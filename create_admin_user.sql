-- Script para crear usuario ADMIN inicial
-- Ejecutar este script en PostgreSQL (pgAdmin o psql)

-- Conexión: music_player database

-- Password: admin123
-- Hash BCrypt generado con: BCrypt.hashpw("admin123", BCrypt.gensalt())
-- Este es solo un ejemplo, el hash real se generará al ejecutar el register

-- Insertar usuario ADMIN manualmente
-- Nota: El UUID debe ser generado, este es solo un ejemplo

INSERT INTO users (id, username, password, email, role, created_at, updated_at)
VALUES (
    gen_random_uuid(),  -- Genera UUID automáticamente
    'superadmin',
    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',  -- admin123
    'superadmin@example.com',
    'ADMIN',
    NOW(),
    NOW()
);

-- Verificar que se creó correctamente
SELECT id, username, email, role, created_at FROM users WHERE username = 'superadmin';

-- Notas importantes:
-- 1. El password hash mostrado arriba es un ejemplo. 
-- 2. En producción, usa un password más seguro
-- 3. Puedes registrar usuarios normales via API y luego actualizar su rol a ADMIN:
--    UPDATE users SET role = 'ADMIN' WHERE username = 'tu_usuario';
