DELETE FROM user_roles where 1=1;
DELETE FROM roles where id=1;
DELETE FROM users where email='dev@meteor.fr';

INSERT INTO roles(id, name) VALUES(1, 'ROLE_USER');

INSERT INTO users(email, password, compte_valide) VALUES ('dev@meteor.fr', '$2a$10$hGtiUhI5Fycm.dSZLXqnNuZXx.ewaVPGfHU70f8tfs3rN5q/KJJDe', true);

INSERT INTO user_roles (user_id, role_id) values(
 (SELECT  users.id FROM users WHERE users.email = 'dev@meteor.fr'), 1
);
