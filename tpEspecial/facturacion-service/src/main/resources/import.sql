-- Datos de ejemplo para facturacion-service

-- Tarifas normales
INSERT INTO tarifa (tipo_tarifa, precio_por_minuto, fecha_vigencia_desde, fecha_vigencia_hasta, activa) VALUES ('NORMAL', 50.00, '2024-01-01', '2024-10-31', false);
INSERT INTO tarifa (tipo_tarifa, precio_por_minuto, fecha_vigencia_desde, activa) VALUES ('NORMAL', 60.00, '2024-11-01', true);

-- Tarifas por pausa extendida
INSERT INTO tarifa (tipo_tarifa, precio_por_minuto, fecha_vigencia_desde, fecha_vigencia_hasta, activa) VALUES ('PAUSA_EXTENDIDA', 80.00, '2024-01-01', '2024-10-31', false);
INSERT INTO tarifa (tipo_tarifa, precio_por_minuto, fecha_vigencia_desde, activa) VALUES ('PAUSA_EXTENDIDA', 90.00, '2024-11-01', true);

-- Facturaciones para viajes finalizados (asumiendo que existen viajes con ID 1-5)
INSERT INTO facturacion (id_viaje, id_cuenta, fecha, monto_total, tiempo_total, tiempo_pausado, id_tarifa_normal, id_tarifa_extendida) VALUES (1, 1, '2024-10-01 10:30:00', 350.50, 30, 5, 1, null);
INSERT INTO facturacion (id_viaje, id_cuenta, fecha, monto_total, tiempo_total, tiempo_pausado, id_tarifa_normal, id_tarifa_extendida) VALUES (2, 1, '2024-10-02 14:50:00', 520.00, 35, 10, 1, null);
INSERT INTO facturacion (id_viaje, id_cuenta, fecha, monto_total, tiempo_total, tiempo_pausado, id_tarifa_normal, id_tarifa_extendida) VALUES (3, 2, '2024-10-03 09:45:00', 420.75, 45, 20, 1, 3);
INSERT INTO facturacion (id_viaje, id_cuenta, fecha, monto_total, tiempo_total, tiempo_pausado, id_tarifa_normal, id_tarifa_extendida) VALUES (4, 2, '2024-10-04 17:10:00', 680.25, 50, 10, 1, null);
INSERT INTO facturacion (id_viaje, id_cuenta, fecha, monto_total, tiempo_total, tiempo_pausado, id_tarifa_normal, id_tarifa_extendida) VALUES (5, 3, '2024-10-05 12:15:00', 510.00, 45, 5, 1, null);

