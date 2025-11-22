-- TARIFAS
-- Nota: aunque el campo se llama 'precio_por_minuto', en la nueva lógica:
-- - NORMAL: se usa como precio por KILÓMETRO
-- - PAUSA: se usa como precio por MINUTO de pausa
-- - PAUSA_EXTENDIDA: se usa como precio por MINUTO de pausa extendida (>15 min)

INSERT INTO tarifa (tipo_tarifa, precio_por_minuto, fecha_vigencia_desde) VALUES ('NORMAL', 50.00, '2024-11-01');
-- Tarifa NORMAL: $50 por kilómetro

INSERT INTO tarifa (tipo_tarifa, precio_por_minuto, fecha_vigencia_desde) VALUES ('PAUSA', 5.00, '2024-11-01');
-- Tarifa PAUSA: $5 por minuto de pausa normal (≤15 min)

INSERT INTO tarifa (tipo_tarifa, precio_por_minuto, fecha_vigencia_desde, fecha_vigencia_hasta) VALUES ('PAUSA_EXTENDIDA', 8.00, '2024-01-01', '2024-10-31');
INSERT INTO tarifa (tipo_tarifa, precio_por_minuto, fecha_vigencia_desde) VALUES ('PAUSA_EXTENDIDA', 10.00, '2024-11-01');
-- Tarifa PAUSA_EXTENDIDA: $10 por minuto de pausa extendida (>15 min, solo el excedente)

-- FACTURACIONES DE EJEMPLO
INSERT INTO facturacion (id_viaje, id_cuenta, fecha, monto_total, tiempo_total, tiempo_pausado, id_tarifa_normal, id_tarifa_pausa, id_tarifa_extendida) VALUES (1, 1, '2024-10-01 10:30:00', 350.50, 30, 5, 1, 2, null);
INSERT INTO facturacion (id_viaje, id_cuenta, fecha, monto_total, tiempo_total, tiempo_pausado, id_tarifa_normal, id_tarifa_pausa, id_tarifa_extendida) VALUES (2, 1, '2024-10-02 14:50:00', 520.00, 35, 10, 1, 2, null);
INSERT INTO facturacion (id_viaje, id_cuenta, fecha, monto_total, tiempo_total, tiempo_pausado, id_tarifa_normal, id_tarifa_pausa, id_tarifa_extendida) VALUES (3, 2, '2024-10-03 09:45:00', 420.75, 45, 20, 1, 2, 4);
INSERT INTO facturacion (id_viaje, id_cuenta, fecha, monto_total, tiempo_total, tiempo_pausado, id_tarifa_normal, id_tarifa_pausa, id_tarifa_extendida) VALUES (4, 2, '2024-10-04 17:10:00', 680.25, 50, 10, 1, 2, null);
INSERT INTO facturacion (id_viaje, id_cuenta, fecha, monto_total, tiempo_total, tiempo_pausado, id_tarifa_normal, id_tarifa_pausa, id_tarifa_extendida) VALUES (5, 3, '2024-10-05 12:15:00', 510.00, 45, 5, 1, 2, null);



