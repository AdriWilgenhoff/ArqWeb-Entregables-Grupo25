-- Datos de ejemplo para viaje-service
-- Asumiendo que ya existen usuarios, cuentas y monopatines en sus respectivos servicios

-- Viajes finalizados
INSERT INTO viaje (id_cuenta, id_usuario, id_monopatin, fecha_hora_inicio, fecha_hora_fin, id_parada_inicio, id_parada_fin, kilometros_recorridos, estado, costo_total) VALUES (1, 1, 1, '2024-10-01 10:00:00', '2024-10-01 10:30:00', 1, 5, 5.2, 'FINALIZADO', 350.50);
INSERT INTO viaje (id_cuenta, id_usuario, id_monopatin, fecha_hora_inicio, fecha_hora_fin, id_parada_inicio, id_parada_fin, kilometros_recorridos, estado, costo_total) VALUES (1, 1, 2, '2024-10-02 14:15:00', '2024-10-02 14:50:00', 3, 7, 8.1, 'FINALIZADO', 520.00);
INSERT INTO viaje (id_cuenta, id_usuario, id_monopatin, fecha_hora_inicio, fecha_hora_fin, id_parada_inicio, id_parada_fin, kilometros_recorridos, estado, costo_total) VALUES (2, 2, 3, '2024-10-03 09:00:00', '2024-10-03 09:45:00', 2, 4, 6.5, 'FINALIZADO', 420.75);
INSERT INTO viaje (id_cuenta, id_usuario, id_monopatin, fecha_hora_inicio, fecha_hora_fin, id_parada_inicio, id_parada_fin, kilometros_recorridos, estado, costo_total) VALUES (2, 3, 1, '2024-10-04 16:20:00', '2024-10-04 17:10:00', 5, 1, 10.3, 'FINALIZADO', 680.25);
INSERT INTO viaje (id_cuenta, id_usuario, id_monopatin, fecha_hora_inicio, fecha_hora_fin, id_parada_inicio, id_parada_fin, kilometros_recorridos, estado, costo_total) VALUES (3, 4, 4, '2024-10-05 11:30:00', '2024-10-05 12:15:00', 6, 8, 7.8, 'FINALIZADO', 510.00);

-- Viaje en curso
INSERT INTO viaje (id_cuenta, id_usuario, id_monopatin, fecha_hora_inicio, id_parada_inicio, kilometros_recorridos, estado) VALUES (1, 1, 5, '2024-11-01 08:00:00', 2, 0.0, 'EN_CURSO');

-- Viaje pausado
INSERT INTO viaje (id_cuenta, id_usuario, id_monopatin, fecha_hora_inicio, id_parada_inicio, kilometros_recorridos, estado) VALUES (2, 2, 6, '2024-11-01 07:30:00', 3, 2.5, 'PAUSADO');

-- Pausas para viajes finalizados
INSERT INTO pausa (id_viaje, hora_inicio, hora_fin, extendida) VALUES (1, '2024-10-01 10:10:00', '2024-10-01 10:15:00', false);
INSERT INTO pausa (id_viaje, hora_inicio, hora_fin, extendida) VALUES (2, '2024-10-02 14:25:00', '2024-10-02 14:35:00', false);
INSERT INTO pausa (id_viaje, hora_inicio, hora_fin, extendida) VALUES (3, '2024-10-03 09:15:00', '2024-10-03 09:35:00', true);
INSERT INTO pausa (id_viaje, hora_inicio, hora_fin, extendida) VALUES (4, '2024-10-04 16:45:00', '2024-10-04 16:55:00', false);

-- Pausa activa para el viaje pausado
INSERT INTO pausa (id_viaje, hora_inicio, extendida) VALUES (7, '2024-11-01 07:45:00', false);


