DELETE FROM roles;
DELETE FROM users;

INSERT INTO roles(id, name) VALUES(1, 'ROLE_USER');

INSERT INTO users(id, email, password, compte_valide) VALUES (0, 'dev@meteor.fr', '$2a$10$hGtiUhI5Fycm.dSZLXqnNuZXx.ewaVPGfHU70f8tfs3rN5q/KJJDe', true);

INSERT INTO user_roles(user_id, role_id) VALUES(0, 1);
