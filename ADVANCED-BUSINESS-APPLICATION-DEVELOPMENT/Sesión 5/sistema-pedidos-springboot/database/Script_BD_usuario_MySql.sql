CREATE DATABASE IF NOT EXISTS pedidosdb
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

CREATE USER IF NOT EXISTS
  'spedidos_app'@'localhost'
  IDENTIFIED BY 'userapp_2026!';

ALTER USER
  'spedidos_app'@'localhost'
  IDENTIFIED BY 'userapp_2026!';

GRANT ALL PRIVILEGES
  ON pedidosdb.*
  TO 'spedidos_app'@'localhost';

FLUSH PRIVILEGES;
