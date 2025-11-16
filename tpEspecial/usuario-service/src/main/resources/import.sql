-- Usuarios (IDs serán autogenerados: 1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
-- Contraseña para todos: "password123" (hasheada con BCrypt)
INSERT INTO usuario (nombre, apellido, email, numero_celular, password_hash, rol, fecha_alta) VALUES ('Ana', 'Perez', 'ana.perez@admin.com', '1122334455', '$2a$10$N9qo8uLOickgx2ZMRZoMye1PZXJqmfH4z/XKMG.JcqQdX3wKJFsL6', 'ADMINISTRADOR', '2025-10-30');
INSERT INTO usuario (nombre, apellido, email, numero_celular, password_hash, rol, fecha_alta) VALUES ('Bruno', 'Diaz', 'bruno.diaz@maint.com', '1199887766', '$2a$10$N9qo8uLOickgx2ZMRZoMye1PZXJqmfH4z/XKMG.JcqQdX3wKJFsL6', 'MANTENIMIENTO', '2025-10-30');
INSERT INTO usuario (nombre, apellido, email, numero_celular, password_hash, rol, fecha_alta) VALUES ('Carlos', 'Gomez', 'carlos.gomez@user.com', '1111111111', '$2a$10$N9qo8uLOickgx2ZMRZoMye1PZXJqmfH4z/XKMG.JcqQdX3wKJFsL6', 'USUARIO', '2025-10-30');
INSERT INTO usuario (nombre, apellido, email, numero_celular, password_hash, rol, fecha_alta) VALUES ('Daniela', 'Fernandez', 'daniela.f@user.com', '2222222222', '$2a$10$N9qo8uLOickgx2ZMRZoMye1PZXJqmfH4z/XKMG.JcqQdX3wKJFsL6', 'USUARIO', '2025-10-30');
INSERT INTO usuario (nombre, apellido, email, numero_celular, password_hash, rol, fecha_alta) VALUES ('Esteban', 'Lopez', 'esteban.lopez@user.com', '3333333333', '$2a$10$N9qo8uLOickgx2ZMRZoMye1PZXJqmfH4z/XKMG.JcqQdX3wKJFsL6', 'USUARIO', '2025-10-30');
INSERT INTO usuario (nombre, apellido, email, numero_celular, password_hash, rol, fecha_alta) VALUES ('Florencia', 'Martinez', 'flor.martinez@user.com', '4444444444', '$2a$10$N9qo8uLOickgx2ZMRZoMye1PZXJqmfH4z/XKMG.JcqQdX3wKJFsL6', 'USUARIO', '2025-10-30');
INSERT INTO usuario (nombre, apellido, email, numero_celular, password_hash, rol, fecha_alta) VALUES ('Gabriel', 'Sanchez', 'gabi.sanchez@user.com', '5555555555', '$2a$10$N9qo8uLOickgx2ZMRZoMye1PZXJqmfH4z/XKMG.JcqQdX3wKJFsL6', 'USUARIO', '2025-10-30');
INSERT INTO usuario (nombre, apellido, email, numero_celular, password_hash, rol, fecha_alta) VALUES ('Laura', 'Torres', 'laura.torres@user.com', '6666666666', '$2a$10$N9qo8uLOickgx2ZMRZoMye1PZXJqmfH4z/XKMG.JcqQdX3wKJFsL6', 'USUARIO', '2025-10-30');
INSERT INTO usuario (nombre, apellido, email, numero_celular, password_hash, rol, fecha_alta) VALUES ('Juan', 'Romero', 'juan.romero@user.com', '7777777777', '$2a$10$N9qo8uLOickgx2ZMRZoMye1PZXJqmfH4z/XKMG.JcqQdX3wKJFsL6', 'USUARIO', '2025-10-30');
INSERT INTO usuario (nombre, apellido, email, numero_celular, password_hash, rol, fecha_alta) VALUES ('Maria', 'Suarez', 'maria.suarez@user.com', '8888888888', '$2a$10$N9qo8uLOickgx2ZMRZoMye1PZXJqmfH4z/XKMG.JcqQdX3wKJFsL6', 'USUARIO', '2025-10-30');

INSERT INTO cuenta (numero_identificatorio, fecha_alta, saldo, habilitada, id_cuenta_mercado_pago) VALUES ('CBU-001-A', '2025-10-30', 15000.00, true, 'MP-ID-001');
INSERT INTO cuenta (numero_identificatorio, fecha_alta, saldo, habilitada, id_cuenta_mercado_pago) VALUES ('CBU-002-B', '2025-10-30', 5000.00, true, 'MP-ID-002');
INSERT INTO cuenta (numero_identificatorio, fecha_alta, saldo, habilitada, id_cuenta_mercado_pago) VALUES ('CBU-003-C', '2025-10-30', 0.00, true, 'MP-ID-003');
INSERT INTO cuenta (numero_identificatorio, fecha_alta, saldo, habilitada, id_cuenta_mercado_pago) VALUES ('CBU-004-D', '2025-10-30', 750.50, true, 'MP-ID-004');
INSERT INTO cuenta (numero_identificatorio, fecha_alta, saldo, habilitada, id_cuenta_mercado_pago) VALUES ('CBU-005-E', '2025-10-30', 100.00, false, 'MP-ID-005');

INSERT INTO usuario_cuenta (id_usuario, id_cuenta) VALUES (1, 1);
INSERT INTO usuario_cuenta (id_usuario, id_cuenta) VALUES (3, 1);
INSERT INTO usuario_cuenta (id_usuario, id_cuenta) VALUES (2, 2);
INSERT INTO usuario_cuenta (id_usuario, id_cuenta) VALUES (4, 3);
INSERT INTO usuario_cuenta (id_usuario, id_cuenta) VALUES (5, 3);
INSERT INTO usuario_cuenta (id_usuario, id_cuenta) VALUES (6, 4);
INSERT INTO usuario_cuenta (id_usuario, id_cuenta) VALUES (3, 4);
INSERT INTO usuario_cuenta (id_usuario, id_cuenta) VALUES (7, 5);
