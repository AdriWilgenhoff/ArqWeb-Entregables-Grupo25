package edu.tudai.arq.viajeservice.service;

import edu.tudai.arq.viajeservice.client.GroqClient;
import edu.tudai.arq.viajeservice.dto.ChatResponseDTO;
import edu.tudai.arq.viajeservice.feignclient.CuentaFeignClient;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ChatService {

    private static final Logger log = LoggerFactory.getLogger(ChatService.class);

    @PersistenceContext
    private EntityManager entityManager;

    private final GroqClient groqClient;
    private final CuentaFeignClient cuentaFeignClient;
    private final String CONTEXTO_SQL;

    private static final Pattern SQL_ALLOWED = Pattern.compile("(?is)\\b(SELECT|INSERT|UPDATE|DELETE)\\b[\\s\\S]*?;");

    private static final Pattern SQL_FORBIDDEN = Pattern.compile("(?i)\\b(DROP|TRUNCATE|ALTER|CREATE|GRANT|REVOKE)\\b");

    public ChatService(GroqClient groqClient, CuentaFeignClient cuentaFeignClient) {
        this.groqClient = groqClient;
        this.cuentaFeignClient = cuentaFeignClient;
        this.CONTEXTO_SQL = cargarEsquemaSQL("schema_viajes.sql");
    }

    private String cargarEsquemaSQL(String nombreArchivo) {
        try (InputStream inputStream = new ClassPathResource(nombreArchivo).getInputStream()) {
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Error al leer el archivo SQL desde resources: " + e.getMessage(), e);
        }
    }

    @Transactional
    public ResponseEntity<?> procesarPrompt(String promptUsuario, Long idUsuario) {
        try {
            var cuentasResponse = cuentaFeignClient.getCuentasByUsuario(idUsuario);

            if (cuentasResponse == null || cuentasResponse.getBody() == null || cuentasResponse.getBody().isEmpty()) {
                log.warn("Usuario {} no tiene cuentas asociadas", idUsuario);
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ChatResponseDTO(false, "No tienes ninguna cuenta asociada. Este servicio requiere una cuenta PREMIUM.", null));
            }

            boolean tienePremium = cuentasResponse.getBody().stream()
                    .anyMatch(cuenta -> "PREMIUM".equalsIgnoreCase(cuenta.tipoCuenta()));

            if (!tienePremium) {
                log.warn("Usuario {} no tiene cuenta PREMIUM", idUsuario);
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ChatResponseDTO(false, "Acceso denegado. Este servicio solo está disponible para usuarios con cuenta PREMIUM.", null));
            }

            log.info("Usuario {} tiene cuenta PREMIUM - acceso concedido al chat IA", idUsuario);

        } catch (Exception e) {
            log.error("Error al validar cuenta del usuario {}: {}", idUsuario, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ChatResponseDTO(false, "Error al validar tu cuenta: " + e.getMessage(), null));
        }

        try {
            String promptFinal = String.format("""
                Este es el esquema de mi base de datos MySQL para el sistema de viajes en monopatín:
                
                %s
                
                IMPORTANTE:
                - El usuario que consulta tiene el ID: %d
                - SIEMPRE debes filtrar por id_usuario = %d en las consultas SELECT
                - Devolveme ÚNICAMENTE una sentencia SQL MySQL completa y VÁLIDA
                - Sin texto adicional, sin markdown (```sql), sin comentarios
                - La sentencia debe terminar con punto y coma (;)
                - Solo puedes usar SELECT, INSERT, UPDATE o DELETE
                
                Pregunta del usuario: %s
                """, CONTEXTO_SQL, idUsuario, idUsuario, promptUsuario);

            log.info("==== PROMPT ENVIADO A GROQ ====\n{}", promptFinal);

            String respuestaIa = groqClient.preguntar(promptFinal);
            log.info("==== RESPUESTA IA ====\n{}", respuestaIa);

            String sql = extraerConsultaSQL(respuestaIa);

            if (sql == null || sql.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ChatResponseDTO(false, "No se encontró una sentencia SQL válida en la respuesta de la IA.", null));
            }

            log.info("==== SQL EXTRAÍDA ====\n{}", sql);

            String sqlToExecute = sql.endsWith(";") ? sql.substring(0, sql.length() - 1) : sql;

            try {
                Object data;
                String respuestaNatural;

                if (sql.trim().regionMatches(true, 0, "SELECT", 0, 6)) {
                    @SuppressWarnings("unchecked")
                    List<Object> resultadosRaw = entityManager.createNativeQuery(sqlToExecute).getResultList();

                    List<Object[]> resultados = normalizarResultados(resultadosRaw);
                    data = resultados;

                    respuestaNatural = generarRespuestaNatural(promptUsuario, sql, resultados);

                    return ResponseEntity.ok(new ChatResponseDTO(true, respuestaNatural, data));
                } else {
                    int rows = entityManager.createNativeQuery(sqlToExecute).executeUpdate();
                    data = rows;

                    respuestaNatural = generarRespuestaNaturalDML(promptUsuario, sql, rows);

                    return ResponseEntity.ok(new ChatResponseDTO(true, respuestaNatural, data));
                }

            } catch (Exception e) {
                log.warn("Error al ejecutar SQL: {}", e.getMessage(), e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new ChatResponseDTO(false, "Error al ejecutar la sentencia: " + e.getMessage(), null));
            }

        } catch (Exception e) {
            log.error("Fallo al procesar prompt", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ChatResponseDTO(false, "Error al procesar el prompt: " + e.getMessage(), null));
        }
    }

    private String extraerConsultaSQL(String respuesta) {
        if (respuesta == null) return null;

        Matcher m = SQL_ALLOWED.matcher(respuesta);
        if (!m.find()) return null;

        String sql = m.group().trim();

        int first = sql.indexOf(';');
        if (first > -1) {
            sql = sql.substring(0, first + 1);
        }

        if (SQL_FORBIDDEN.matcher(sql).find()) {
            log.warn("Sentencia bloqueada por contener DDL prohibido: {}", sql);
            return null;
        }

        return sql;
    }

    private String generarRespuestaNatural(String preguntaOriginal, String sqlEjecutado, List<Object[]> resultados) {
        try {
            String resultadosFormateados = formatearResultados(resultados);

            String promptRespuesta = String.format("""
                Eres un asistente virtual amigable de un sistema de alquiler de monopatines.
                
                El usuario preguntó: "%s"
                
                Se ejecutó esta consulta SQL: %s
                
                Los resultados fueron: %s
                
                Genera una respuesta en lenguaje natural, amigable y concisa que responda la pregunta original del usuario.
                - Usa un tono conversacional y cercano
                - Sé breve y directo
                - Si no hay resultados, dilo de forma amable
                - NO menciones SQL ni aspectos técnicos
                - NO agregues explicaciones adicionales no solicitadas
                - Responde SOLO con la respuesta natural, sin texto adicional
                """, preguntaOriginal, sqlEjecutado, resultadosFormateados);

            log.info("==== PROMPT PARA RESPUESTA NATURAL ====\n{}", promptRespuesta);

            String respuestaNatural = groqClient.preguntar(promptRespuesta);

            log.info("==== RESPUESTA NATURAL GENERADA ====\n{}", respuestaNatural);

            return respuestaNatural.trim();

        } catch (Exception e) {
            log.warn("Error al generar respuesta natural, usando respuesta por defecto: {}", e.getMessage());
            return "Consulta ejecutada con éxito. Se encontraron " + resultados.size() + " resultados.";
        }
    }

    /**
     * Genera una respuesta en lenguaje natural para operaciones DML
     */
    private String generarRespuestaNaturalDML(String preguntaOriginal, String sqlEjecutado, int filasAfectadas) {
        try {
            String promptRespuesta = String.format("""
                Eres un asistente virtual amigable de un sistema de alquiler de monopatines.
                
                El usuario preguntó: "%s"
                
                Se ejecutó esta operación SQL: %s
                
                Resultado: %d fila(s) afectada(s)
                
                Genera una respuesta en lenguaje natural, amigable y concisa confirmando la operación.
                - Usa un tono conversacional
                - Sé breve
                - NO menciones SQL
                - Responde SOLO con la confirmación, sin texto adicional
                """, preguntaOriginal, sqlEjecutado, filasAfectadas);

            String respuestaNatural = groqClient.preguntar(promptRespuesta);
            return respuestaNatural.trim();

        } catch (Exception e) {
            log.warn("Error al generar respuesta natural para DML: {}", e.getMessage());
            return "Operación completada. " + filasAfectadas + " registro(s) afectado(s).";
        }
    }

    private List<Object[]> normalizarResultados(List<Object> resultadosRaw) {
        List<Object[]> resultadosNormalizados = new ArrayList<>();

        for (Object resultado : resultadosRaw) {
            if (resultado instanceof Object[]) {
                resultadosNormalizados.add((Object[]) resultado);
            } else {
                resultadosNormalizados.add(new Object[]{resultado});
            }
        }

        return resultadosNormalizados;
    }

    private String formatearResultados(List<Object[]> resultados) {
        if (resultados == null || resultados.isEmpty()) {
            return "No se encontraron resultados";
        }

        StringBuilder sb = new StringBuilder();

        if (resultados.size() == 1 && resultados.get(0).length == 1) {
            sb.append(resultados.get(0)[0]);
        } else {
            sb.append("[\n");
            for (int i = 0; i < Math.min(resultados.size(), 10); i++) { // Limitar a 10 filas
                Object[] fila = resultados.get(i);
                sb.append("  Fila ").append(i + 1).append(": ");
                for (int j = 0; j < fila.length; j++) {
                    if (j > 0) sb.append(", ");
                    sb.append(fila[j]);
                }
                sb.append("\n");
            }
            if (resultados.size() > 10) {
                sb.append("  ... y ").append(resultados.size() - 10).append(" filas más\n");
            }
            sb.append("]\nTotal: ").append(resultados.size()).append(" resultados");
        }

        return sb.toString();
    }
}

