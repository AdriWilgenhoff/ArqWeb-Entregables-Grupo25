# Viaje Service - Implementación Completa

## Resumen de la Implementación

Se ha completado la implementación del **viaje-service** siguiendo la estructura y patrones del **usuario-service**.

## Estructura de Archivos Creados/Modificados

### Entidades (entity/)
- ✅ **Viaje.java** - Entidad principal con todos los atributos especificados
- ✅ **Pausa.java** - Entidad para gestionar las pausas de los viajes
- ✅ **EstadoViaje.java** - Enum con estados: EN_CURSO, PAUSADO, FINALIZADO

### DTOs (dto/)
- ✅ **ViajeDTO.java** - Records para Create, Finalizacion, Response y Resumen
- ✅ **PausaDTO.java** - Record para Response de pausas

### Mappers (mapper/)
- ✅ **ViajeMapper.java** - Conversiones entre Entity y DTO
- ✅ **PausaMapper.java** - Conversiones entre Entity y DTO de Pausa

### Repositorios (repository/)
- ✅ **ViajeRepository.java** - JpaRepository con queries personalizadas
- ✅ **PausaRepository.java** - JpaRepository para gestión de pausas

### Servicios (service/)
- ✅ **ViajeService.java** (interface) - Define todos los métodos del servicio
- ✅ **ViajeServiceImpl.java** - Implementación completa de la lógica de negocio

### Controladores (controller/)
- ✅ **ViajeController.java** - REST Controller con documentación Swagger completa

### Excepciones (exception/)
- ✅ **ViajeNotFoundException.java** - Excepción personalizada
- ✅ **PausaNotFoundException.java** - Excepción personalizada
- ✅ **ViajeInvalidoException.java** - Excepción para validaciones de negocio
- ✅ **ApiError.java** - Record para respuestas de error
- ✅ **GlobalExceptionHandler.java** - Manejador global de excepciones

### Configuración
- ✅ **application.properties** - Configuración corregida para MySQL
- ✅ **import.sql** - Datos de prueba iniciales

## Endpoints Implementados

### Gestión de Viajes
- `POST /api/v1/viajes` - Iniciar un nuevo viaje
- `PUT /api/v1/viajes/{id}/finalizar` - Finalizar un viaje
- `GET /api/v1/viajes/{id}` - Obtener viaje por ID
- `GET /api/v1/viajes` - Listar todos los viajes
- `DELETE /api/v1/viajes/{id}` - Eliminar un viaje

### Gestión de Pausas
- `POST /api/v1/viajes/{id}/pausar` - Pausar un viaje
- `POST /api/v1/viajes/{id}/reanudar` - Reanudar un viaje pausado
- `GET /api/v1/viajes/{id}/pausas` - Obtener pausas de un viaje

### Consultas Específicas
- `GET /api/v1/viajes/usuario/{idUsuario}` - Viajes de un usuario
- `GET /api/v1/viajes/cuenta/{idCuenta}` - Viajes de una cuenta
- `GET /api/v1/viajes/monopatin/{idMonopatin}` - Viajes de un monopatín
- `GET /api/v1/viajes/activos` - Todos los viajes activos (en curso o pausados)
- `GET /api/v1/viajes/monopatin/{idMonopatin}/activo` - Viaje activo de un monopatín

## Características Implementadas

### Lógica de Negocio
1. **Inicio de Viaje**: Valida que el monopatín no tenga un viaje activo
2. **Pausas**: 
   - Control de pausas activas (solo una por viaje)
   - Detección automática de pausas extendidas (> 15 minutos)
   - Finalización automática de pausas al finalizar viaje
3. **Finalización de Viaje**: 
   - Cierra pausas activas automáticamente
   - Registra kilómetros y costo total

### Validaciones
- Validación de DTOs con Bean Validation (Jakarta)
- Validaciones de estado de viaje (no pausar finalizados, etc.)
- Validaciones de monopatín disponible

### Documentación
- Swagger/OpenAPI completo en todos los endpoints
- Descripciones detalladas de cada operación
- Ejemplos de request/response
- Códigos de estado HTTP documentados

## Base de Datos

### Tablas Creadas (con JPA auto-create)
- `viaje` - Tabla principal de viajes
- `pausa` - Tabla de pausas relacionada con viaje

### Configuración
- Motor: MySQL
- Base de datos: `viaje`
- Puerto del servicio: 8087
- Auto-creación de esquema: Habilitado
- Datos iniciales: 5 viajes finalizados, 1 en curso, 1 pausado

## Integración con Eureka
- Cliente Eureka configurado (actualmente deshabilitado)
- OpenFeign habilitado para llamadas entre microservicios
- LoadBalancer configurado

## Próximos Pasos Sugeridos

1. Habilitar Eureka cuando esté disponible
2. Implementar comunicación con usuario-service para validar usuarios/cuentas
3. Implementar comunicación con monopatin-service para validar disponibilidad
4. Implementar comunicación con parada-service para validar paradas
5. Agregar seguridad (Spring Security + JWT)
6. Implementar los reportes solicitados en el enunciado

## Pruebas

Para probar el servicio:

1. Iniciar MySQL en localhost:3306
2. Ejecutar el servicio: `mvn spring-boot:run`
3. Acceder a Swagger UI: http://localhost:8087/swagger-ui.html
4. La API REST estará disponible en: http://localhost:8087/api/v1/viajes

## Notas Técnicas

- Todas las clases siguen el patrón de usuario-service
- Uso de Lombok para reducir boilerplate
- Transacciones configuradas en capa de servicio
- Manejo robusto de errores con excepciones personalizadas
- DTOs separados para entrada y salida
- Mappers para conversión entre capas

