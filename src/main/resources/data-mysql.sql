SET @oldUserDevId = (SELECT  users.id FROM users WHERE users.email = 'dev@meteor.fr');
SET @oldUserTokenId = (SELECT  users.id FROM users WHERE users.email = 'token-test@meteor.fr');

DELETE FROM user_roles where user_id in (@oldUserDevId, @oldUserTokenId);
DELETE FROM user_roles USING user_roles INNER JOIN users
    ON (users.id = user_roles.user_id) where email like 'cypress-%@meteor.fr';

DELETE FROM user_validation_token where user_id in (@oldUserDevId, @oldUserTokenId);
DELETE FROM user_validation_token USING user_validation_token INNER JOIN users
    ON (users.id = user_validation_token.user_id) where email like 'cypress-%@meteor.fr';

DELETE FROM users where email in ('dev@meteor.fr', 'token-test@meteor.fr');
DELETE FROM users where email like 'cypress-%@meteor.fr';

INSERT IGNORE INTO roles(id, name) VALUES(1, 'ROLE_USER');

INSERT INTO users(email, password, compte_valide) VALUES ('dev@meteor.fr', '$2a$10$hGtiUhI5Fycm.dSZLXqnNuZXx.ewaVPGfHU70f8tfs3rN5q/KJJDe', true);
INSERT INTO users(email, password, compte_valide) VALUES ('token-test@meteor.fr', '$2y$12$RxGGw0RbonhHlf16sIsAVeLOf7L8cbZ5fpqoJxwok8yeEbTrvvDHW', false);

SET @userDevId = (SELECT  users.id FROM users WHERE users.email = 'dev@meteor.fr');
SET @userTokenId = (SELECT  users.id FROM users WHERE users.email = 'token-test@meteor.fr');

INSERT INTO user_roles (user_id, role_id) values(@userDevId, 1);
INSERT INTO user_roles (user_id, role_id) values(@userTokenId, 1);

INSERT INTO user_validation_token(id, expiry_date, token_type, user_id)
    values('TOKEN-validate', DATE_ADD(NOW(), INTERVAL 15 MINUTE), 'CREATION_COMPTE', @userTokenId);

INSERT INTO user_validation_token(id, expiry_date, token_type, user_id)
    values('TOKEN-reset-pwd', DATE_ADD(NOW(), INTERVAL 15 MINUTE), 'RESET_PASSWORD', @userTokenId);

INSERT INTO user_validation_token(id, expiry_date, token_type, user_id)
    values('expired-token', DATE_SUB(NOW(), INTERVAL 1 MINUTE), 'CREATION_COMPTE', @userDevId);
