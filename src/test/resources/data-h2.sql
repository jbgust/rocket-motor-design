INSERT INTO roles(id, name) VALUES(1, 'ROLE_USER');

INSERT INTO users(email, password, compte_valide, donator, receive_newsletter) VALUES
    ('test@meteor.fr', '$2a$10$hGtiUhI5Fycm.dSZLXqnNuZXx.ewaVPGfHU70f8tfs3rN5q/KJJDe', true, false, false);
SET @userTestId = (SELECT  users.id FROM users WHERE users.email = 'test@meteor.fr');
INSERT INTO user_roles (user_id, role_id) values(@userTestId, 1);


INSERT INTO users(email, password, compte_valide, donator, receive_newsletter) VALUES
    ('another-user@meteor.fr', '$2a$10$hGtiUhI5Fycm.dSZLXqnNuZXx.ewaVPGfHU70f8tfs3rN5q/KJJDe', true, false, false);
SET @anotherUserId = (SELECT  users.id FROM users WHERE users.email = 'another-user@meteor.fr');
INSERT INTO user_roles (user_id, role_id) values(@anotherUserId, 1);
