USE optimak_db;

-- Empresa
INSERT INTO EMPRESA (NIT, Nombre, Direccion, Ciudad)
VALUES ('9018271085', 'MAKAIROS 91 SAS', 'Carrera 47 # 54-13', 'Rionegro')
ON DUPLICATE KEY UPDATE Nombre = VALUES(Nombre);

INSERT INTO MODULO_OPERATIVO (idMODULO_OPERATIVO, EMPRESA_NIT)
VALUES ('01', '9018271085'),
       ('02', '9018271085')
ON DUPLICATE KEY UPDATE EMPRESA_NIT = VALUES(EMPRESA_NIT);

-- ============================================================
-- administrador de prueba
-- ID de usuario: 1 
-- Contraseña: admin123
-- ===================================================

INSERT INTO ADMINISTRADOR (Nombre, Departamento, HashContrasena, EMPRESA_NIT)
VALUES (
    'Adrián Arias',
    'Dirección General',
    '$2a$10$zbfir/HDQME/bB/vU.ruQOZljtEGZolbpa0P6KPOAaV5eue98005u',
    '9018271085'
);

-- ============================================================
-- operario para prueba
-- Cédula (ID de acceso): 1000000001
-- Contraseña: operario123
-- =========================================
INSERT INTO EMPLEADO_OPERATIVO (
    Numero_cedula, Nombre, Telefono, Edad, Sexo,
    Cargo_rol, Correo, HashContrasena, Activo,
    MODULO_OPERATIVO_idMODULO_OPERATIVO,
    MODULO_OPERATIVO_EMPRESA_NIT
)
VALUES (
    '1000000001',
    'Carlos López',
    '3001234567',
    28,
    'M',
    'Operario de Confección',
    'carlos.lopez@prueba.com',
    '$2a$10$QAxO.y4obPLL0/U1HlRjsuG/p1IeEbINye25PzXS7BNfH.xis3DN2',  
    1,
    '01',
    '9018271085'
);

-- Historial de qué modulo es el operario
-- Obligatoriamente el sistema va a esperar que cada operario tenga
-- exactamente un registro activo (FechaFin = NULL).

INSERT INTO HISTORIAL_MODULO_EMPLEADO (
    FechaInicio, FechaFin, Numero_cedula,
    MODULO_OPERATIVO_idMODULO_OPERATIVO,
    MODULO_OPERATIVO_EMPRESA_NIT
)
VALUES (
    CURRENT_DATE(),
    NULL,
    '1000000001',
    '01',
    '9018271085'
);

-- DATOS DE PRUEBA — Referencia, Operaciones y Lote

-- Referencia
INSERT INTO REFERENCIA (ID_REF, Tipo_Prenda, Cliente, Coleccion, Precio_unitario)
VALUES ('29781', 'Leggin', 'DODO', NULL, 3630.00)
ON DUPLICATE KEY UPDATE
    Tipo_Prenda     = VALUES(Tipo_Prenda),
    Cliente         = VALUES(Cliente),
    Precio_unitario = VALUES(Precio_unitario);

-- 8 operaciones de la referencia 29781
INSERT INTO OPERACION (Nombre_corto, Detalle, Maquina, SAM_operacion, REFERENCIA_ID_REF)
VALUES
    ('Preparar fajónes',
     'Unir fajón posterior con fajón delantero',
     'FIL', 0.37, '29781'),

    ('Unir tiros',
     'Unir tiros posteriores y delanteros de ambas piernas',
     'FIL', 0.50, '29781'),

    ('Cerrar entrepierna',
     'Cerrrar entrepierna cuidando dirección de los filetes en el cace',
     'FIL', 0.75, '29781'),

    ('Recubrir tiros',
     'Recubrir filete desde el tiro trasero hasta el delantero',
     'REC', 0.54, '29781'),

    ('Pegar fajón',
     'Pegar fajón a la parte inferior del Leggin',
     'FIL', 0.87, '29781'),

    ('Recubrir cintura',
     'Recubrir cintura - filete de la unión del fajón',
     'REC', 0.75, '29781'),

    ('Dobladillar ruedos',
     'Dobladillar ruedos de ambas piernas',
     'DOB', 0.76, '29781'),

    ('Pulir y Revisar',
     'Pulir hebras de costuras, revisar costuras, marquilla, tela e integridad de la prenda, retirar adhesivos.',
     'Manual', 0.76, '29781');

-- Lote OM 5654 — Negro — 1000 unidades totales — Módulo 01
-- Cant: XXL=0, XL=100, L=200, M=300, S=300, XS=100
INSERT INTO LOTE (
    OM, numero_remision_entrada, OC, Color,
    Cant_XXL, Cant_XL, Cant_L, Cant_M, Cant_S, Cant_XS,
    Fecha_ingreso_planta,
    MODULO_OPERATIVO_idMODULO_OPERATIVO,
    MODULO_OPERATIVO_EMPRESA_NIT,
    REFERENCIA_ID_REF
)
VALUES (
    '5654', '1506', NULL, 'Negro',
    0, 100, 200, 300, 300, 100,
    '2026-04-24',
    '01',
    '9018271085',
    '29781'
)
ON DUPLICATE KEY UPDATE
    Color                = VALUES(Color),
    Cant_XXL             = VALUES(Cant_XXL),
    Cant_XL              = VALUES(Cant_XL),
    Cant_L               = VALUES(Cant_L),
    Cant_M               = VALUES(Cant_M),
    Cant_S               = VALUES(Cant_S),
    Cant_XS              = VALUES(Cant_XS),
    Fecha_ingreso_planta = VALUES(Fecha_ingreso_planta);
