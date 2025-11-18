package edu.tudai.arq.viajeservice.controller;

import edu.tudai.arq.viajeservice.dto.ChatResponseDTO;
import edu.tudai.arq.viajeservice.exception.ApiError;
import edu.tudai.arq.viajeservice.service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/viajes/chat")
@Tag(name = "Chat IA", description = "API de chat con IA para consultas sobre viajes (solo usuarios PREMIUM)")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping
    @Operation(
            summary = "Consultar viajes usando lenguaje natural (SOLO PREMIUM)",
            description = "Permite a usuarios PREMIUM hacer consultas en lenguaje natural sobre sus viajes. " +
                    "La IA de Groq convierte la pregunta en SQL, ejecuta la consulta y genera una respuesta en lenguaje natural. " +
                    "Automáticamente filtra los resultados por el usuario autenticado.\n\n" +
                    "**Proceso:**\n" +
                    "1. Tu pregunta se envía a Groq\n" +
                    "2. Groq genera SQL automáticamente\n" +
                    "3. Se ejecuta el SQL en la base de datos\n" +
                    "4. Los resultados se envían de vuelta a Groq\n" +
                    "5. Groq genera una respuesta en lenguaje natural\n\n" +
                    "**Ejemplos de preguntas:**\n" +
                    "- ¿Cuántos viajes hice este mes?\n" +
                    "- ¿Cuál fue mi viaje más largo?\n" +
                    "- ¿Cuántos kilómetros recorrí en total?\n" +
                    "- Muéstrame mis viajes de octubre\n" +
                    "- ¿Cuánto gasté en viajes esta semana?\n" +
                    "- ¿Cuántas veces pausé durante mis viajes?\n\n" +
                    "**Nota:** El campo 'mensaje' contiene la respuesta en lenguaje natural, " +
                    "y 'datos' contiene los resultados SQL crudos por si los necesitas.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Pregunta en lenguaje natural",
                    required = true,
                    content = @Content(
                            mediaType = "text/plain",
                            examples = {
                                    @ExampleObject(name = "Viajes del mes", value = "¿Cuántos viajes hice este mes?"),
                                    @ExampleObject(name = "Kilómetros totales", value = "¿Cuántos kilómetros he recorrido en total?"),
                                    @ExampleObject(name = "Viaje más largo", value = "¿Cuál fue mi viaje más largo?"),
                                    @ExampleObject(name = "Gasto semanal", value = "¿Cuánto gasté en viajes esta semana?"),
                                    @ExampleObject(name = "Pausas", value = "¿Cuántas pausas hice en total?"),
                                    @ExampleObject(name = "Viajes recientes", value = "Muéstrame mis últimos 5 viajes")
                            }
                    )
            )
    )
    @ApiResponse(responseCode = "200", description = "Consulta ejecutada exitosamente",
            content = @Content(schema = @Schema(implementation = ChatResponseDTO.class)))
    @ApiResponse(responseCode = "400", description = "SQL inválido o prompt mal formado",
            content = @Content(schema = @Schema(implementation = ChatResponseDTO.class)))
    @ApiResponse(responseCode = "401", description = "No autenticado - Requiere JWT",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(responseCode = "403", description = "Acceso denegado - Solo usuarios PREMIUM",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(responseCode = "500", description = "Error al procesar la consulta",
            content = @Content(schema = @Schema(implementation = ChatResponseDTO.class)))
    public ResponseEntity<?> consultar(
            @RequestBody String prompt,
            @RequestHeader(value = "X-User-Id", required = false)
            @Parameter(description = "ID del usuario autenticado (inyectado por Gateway)", hidden = true)
            Long userId
    ) {
        // Validar que el usuario esté autenticado
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ChatResponseDTO(false, "Usuario no autenticado. Se requiere JWT.", null));
        }

        // Procesar la consulta
        return chatService.procesarPrompt(prompt, userId);
    }

    @GetMapping("/ping")
    @Operation(
            summary = "Verificar que el servicio de chat esté funcionando",
            description = "Endpoint de health check para el servicio de chat IA"
    )
    @ApiResponse(responseCode = "200", description = "Servicio funcionando correctamente")
    public ResponseEntity<ChatResponseDTO> ping() {
        return ResponseEntity.ok(new ChatResponseDTO(
                true,
                "Chat IA con Groq está operativo. Servicio disponible para usuarios PREMIUM.",
                "OK"
        ));
    }
}

