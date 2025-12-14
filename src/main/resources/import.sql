INSERT INTO `provincia` (`codigo`, `comunidad`, `nombre`) VALUES ('29', 'Andalucía', 'Málaga');
INSERT INTO `provincia` (`codigo`, `comunidad`, `nombre`) VALUES ('23', 'Andalucía', 'Jaén');



INSERT INTO `usuario` (`email`, `nif`, `password`, `rol`, `telefono`, `username`, `enabled` ) VALUES ('pepe@sincorreo.com', '123456789F', '$2a$10$LhG/8HBWdMfK5lXd9x3Hy.xZBRU7OrQeLRvsscnIuEFF4ohZ16ZlG', 'ADMIN', '555123456', 'admin', 1);
INSERT INTO `cod_pos` (`cp`, `provincia_codigo`, `municipio`) VALUES (23150, 23, 'Valdepeñas de Jaén');
