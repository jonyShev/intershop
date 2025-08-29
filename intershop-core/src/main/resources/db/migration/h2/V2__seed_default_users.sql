-- Создаём дефолтных пользователей для dev/docker:
-- user / password
-- admin / admin

INSERT INTO app_user (id, username, password, enabled) VALUES
  ('00000000-0000-0000-0000-000000000001', 'user',  '$2b$10$a.Tu2Dwwnp79qEEX1NPFZuAFdfmS9/NqvyI2RPFZuTvrCB.rEX6XC', TRUE),
  ('00000000-0000-0000-0000-000000000002', 'admin', '$2b$10$gqfpxURI6Zs8130QUtzxZeXNg4nASJL4QFYobPuWFidFfWXd1q3bi', TRUE);

-- Роли
INSERT INTO user_authority (user_id, authority) VALUES
  ('00000000-0000-0000-0000-000000000001', 'ROLE_USER'),
  ('00000000-0000-0000-0000-000000000002', 'ROLE_ADMIN');
