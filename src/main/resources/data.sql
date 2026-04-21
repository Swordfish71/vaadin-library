-- The hash below is the BCrypt encryption for the word 'password'
-- It was generated using BCrypt with a strength of 10
INSERT INTO app_users (username, password)
VALUES ('admin', '$2a$10$yIgkZHQUlX2En18XVvLeTeYa125IqenCMDjMOkSMe46xNOoy/1djm')
ON CONFLICT (username) DO NOTHING;

INSERT INTO user_roles (user_id, role)
SELECT u.id, 'ADMIN'
FROM app_users u
WHERE u.username = 'admin'
  AND NOT EXISTS (
    SELECT 1 FROM user_roles ur
    WHERE ur.user_id = u.id AND ur.role = 'ADMIN'
);

-- Adding a standard user with the same password ('password')
INSERT INTO app_users (username, password)
VALUES ('user', '$2a$10$yIgkZHQUlX2En18XVvLeTeYa125IqenCMDjMOkSMe46xNOoy/1djm')
ON CONFLICT (username) DO NOTHING;

INSERT INTO user_roles (user_id, role)
SELECT u.id, 'USER'
FROM app_users u
WHERE u.username = 'user'
  AND NOT EXISTS (
    SELECT 1 FROM user_roles ur
    WHERE ur.user_id = u.id AND ur.role = 'USER'
);