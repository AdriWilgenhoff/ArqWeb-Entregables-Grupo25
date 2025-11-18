-- Schema SQL para la base de datos de viajes
-- Este archivo contiene el esquema completo de las tablas viaje y pausa
-- Usado como contexto para la IA Groq

CREATE TABLE viaje (
    id_viaje BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_usuario BIGINT NOT NULL COMMENT 'ID del usuario que realizó el viaje',
    id_cuenta BIGINT NOT NULL COMMENT 'ID de la cuenta que pagó el viaje',
    id_monopatin BIGINT NOT NULL COMMENT 'ID del monopatín utilizado',
    fecha_hora_inicio DATETIME NOT NULL COMMENT 'Fecha y hora de inicio del viaje',
    fecha_hora_fin DATETIME COMMENT 'Fecha y hora de finalización del viaje',
    kilometros_recorridos DOUBLE COMMENT 'Kilómetros totales recorridos en el viaje',
    estado VARCHAR(20) NOT NULL COMMENT 'Estado del viaje: EN_CURSO, PAUSADO, FINALIZADO',
    costo_total DOUBLE COMMENT 'Costo total del viaje en pesos',
    id_parada_inicio BIGINT COMMENT 'ID de la parada donde comenzó el viaje',
    id_parada_fin BIGINT COMMENT 'ID de la parada donde finalizó el viaje'
) COMMENT='Tabla que almacena información de los viajes realizados por los usuarios';

CREATE TABLE pausa (
    id_pausa BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_viaje BIGINT NOT NULL COMMENT 'ID del viaje al que pertenece esta pausa',
    hora_inicio DATETIME NOT NULL COMMENT 'Fecha y hora de inicio de la pausa',
    hora_fin DATETIME COMMENT 'Fecha y hora de fin de la pausa',
    extendida BOOLEAN DEFAULT FALSE COMMENT 'Indica si la pausa superó los 15 minutos',
    FOREIGN KEY (id_viaje) REFERENCES viaje(id_viaje)
) COMMENT='Tabla que almacena las pausas realizadas durante un viaje';

-- NOTAS IMPORTANTES PARA LA IA:
-- 1. SIEMPRE filtrar por id_usuario cuando se consulten viajes de un usuario específico
-- 2. El campo 'estado' puede tener los valores: 'EN_CURSO', 'PAUSADO', 'FINALIZADO'
-- 3. Los viajes finalizados tienen fecha_hora_fin y kilometros_recorridos completados
-- 4. Las pausas extendidas (>15 min) tienen el campo extendida=TRUE
-- 5. Para calcular tiempos de viaje usar TIMESTAMPDIFF(MINUTE, fecha_hora_inicio, fecha_hora_fin)
-- 6. La base de datos se llama 'viaje' y usa MySQL

