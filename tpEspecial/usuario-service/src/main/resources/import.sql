INSERT INTO usuario (nombre, apellido, email, numero_celular, password_hash, rol, fecha_alta) VALUES ('Ana', 'Perez', 'ana.perez@admin.com', '1122334455', '$2a$10$hash..dummy1', 'ADMINISTRADOR', '2025-10-30');
INSERT INTO usuario (nombre, apellido, email, numero_celular, password_hash, rol, fecha_alta) VALUES ('Bruno', 'Diaz', 'bruno.diaz@maint.com', '1199887766', '$2a$10$hash..dummy2', 'MANTENIMIENTO', '2025-10-30');
INSERT INTO usuario (nombre, apellido, email, numero_celular, password_hash, rol, fecha_alta) VALUES ('Carlos', 'Gomez', 'carlos.gomez@user.com', '1111111111', '$2a$10$hash..dummy3', 'USUARIO', '2025-10-30');
INSERT INTO usuario (nombre, apellido, email, numero_celular, password_hash, rol, fecha_alta) VALUES ('Daniela', 'Fernandez', 'daniela.f@user.com', '2222222222', '$2a$10$hash..dummy4', 'USUARIO', '2025-10-30');
INSERT INTO usuario (nombre, apellido, email, numero_celular, password_hash, rol, fecha_alta) VALUES ('Esteban', 'Lopez', 'esteban.lopez@user.com', '3333333333', '$2a$10$hash..dummy5', 'USUARIO', '2025-10-30');
INSERT INTO usuario (nombre, apellido, email, numero_celular, password_hash, rol, fecha_alta) VALUES ('Florencia', 'Martinez', 'flor.martinez@user.com', '4444444444', '$2a$10$hash..dummy6', 'USUARIO', '2025-10-30');
INSERT INTO usuario (nombre, apellido, email, numero_celular, password_hash, rol, fecha_alta) VALUES ('Gabriel', 'Sanchez', 'gabi.sanchez@user.com', '5555555555', '$2a$10$hash..dummy7', 'USUARIO', '2025-10-30');
INSERT INTO usuario (nombre, apellido, email, numero_celular, password_hash, rol, fecha_alta) VALUES ('Laura', 'Torres', 'laura.torres@user.com', '6666666666', '$2a$10$hash..dummy8', 'USUARIO', '2025-10-30');
INSERT INTO usuario (nombre, apellido, email, numero_celular, password_hash, rol, fecha_alta) VALUES ('Juan', 'Romero', 'juan.romero@user.com', '7777777777', '$2a$10$hash..dummy9', 'USUARIO', '2025-10-30');
INSERT INTO usuario (nombre, apellido, email, numero_celular, password_hash, rol, fecha_alta) VALUES ('Maria', 'Suarez', 'maria.suarez@user.com', '8888888888', '$2a$10$hash..dummy10', 'USUARIO', '2025-10-30');

INSERT INTO cuenta (numero_identificatorio, fecha_alta, saldo, habilitada, id_cuenta_mercado_pago) VALUES ('CBU-001-A', '2025-10-30', 15000.00, true, 'MP-ID-001');
INSERT INTO cuenta (numero_identificatorio, fecha_alta, saldo, habilitada, id_cuenta_mercado_pago) VALUES ('CBU-002-B', '2025-10-30', 5000.00, true, 'MP-ID-002');
INSERT INTO cuenta (numero_identificatorio, fecha_alta, saldo, habilitada, id_cuenta_mercado_pago) VALUES ('CBU-003-C', '2025-10-30', 0.00, true, 'MP-ID-003');
INSERT INTO cuenta (numero_identificatorio, fecha_alta, saldo, habilitada, id_cuenta_mercado_pago) VALUES ('CBU-004-D', '2025-10-30', 750.50, true, 'MP-ID-004');
INSERT INTO cuenta (numero_identificatorio, fecha_alta, saldo, habilitada, id_cuenta_mercado_pago) VALUES ('CBU-005-E', '2025-10-30', 100.00, false, 'MP-ID-005');

-- Ana (ID 1) se asocia a Cuenta 'CBU-001-A' (ID 1)
INSERT INTO usuario_cuenta (id_usuario, id_cuenta, fecha_asociacion) VALUES (1, 1, '2025-10-31 10:00:00');

-- Carlos (ID 3) se asocia a Cuenta 'CBU-001-A' (ID 1)
INSERT INTO usuario_cuenta (id_usuario, id_cuenta, fecha_asociacion) VALUES (3, 1, '2025-10-31 10:01:00');

-- Bruno (ID 2) se asocia a Cuenta 'CBU-002-B' (ID 2)
INSERT INTO usuario_cuenta (id_usuario, id_cuenta, fecha_asociacion) VALUES (2, 2, '2025-10-31 10:02:00');

-- Daniela (ID 4) se asocia a Cuenta 'CBU-003-C' (ID 3)
INSERT INTO usuario_cuenta (id_usuario, id_cuenta, fecha_asociacion) VALUES (4, 3, '2025-10-31 10:03:00');

-- Esteban (ID 5) se asocia a Cuenta 'CBU-003-C' (ID 3)
INSERT INTO usuario_cuenta (id_usuario, id_cuenta, fecha_asociacion) VALUES (5, 3, '2025-10-31 10:04:00');

-- Florencia (ID 6) se asocia a Cuenta 'CBU-004-D' (ID 4)
INSERT INTO usuario_cuenta (id_usuario, id_cuenta, fecha_asociacion) VALUES (6, 4, '2025-10-31 10:05:00');

-- Carlos (ID 3) se asocia tambien a Cuenta 'CBU-004-D' (ID 4)
INSERT INTO usuario_cuenta (id_usuario, id_cuenta, fecha_asociacion) VALUES (3, 4, '2025-10-31 10:07:00');

-- Gabriel (ID 7) se asocia a Cuenta 'CBU-005-E' (ID 5)
INSERT INTO usuario_cuenta (id_usuario, id_cuenta, fecha_asociacion) VALUES (7, 5, '2025-10-31 10:06:00');