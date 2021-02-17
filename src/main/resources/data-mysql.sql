SET @oldUserDevId = (SELECT  users.id FROM users WHERE users.email = 'dev@meteor.fr');
SET @oldUserTokenId = (SELECT  users.id FROM users WHERE users.email = 'token-test@meteor.fr');
DELETE FROM propellant;
DELETE FROM motor;

DELETE FROM user_roles where user_id in (@oldUserDevId, @oldUserTokenId);
DELETE FROM user_roles USING user_roles INNER JOIN users
    ON (users.id = user_roles.user_id) where email like 'cypress-%@meteor.fr';

DELETE FROM user_validation_token where user_id in (@oldUserDevId, @oldUserTokenId);
DELETE FROM user_validation_token USING user_validation_token INNER JOIN users
    ON (users.id = user_validation_token.user_id) where email like 'cypress-%@meteor.fr';

DELETE FROM users where email in ('dev@meteor.fr', 'token-test@meteor.fr');
DELETE FROM users where email like 'cypress-%@meteor.fr';


INSERT IGNORE INTO roles(id, name) VALUES(1, 'ROLE_USER');
INSERT IGNORE INTO roles(id, name) VALUES(2, 'ROLE_ADMIN');

INSERT INTO users(email, password, compte_valide, donator) VALUES ('dev@meteor.fr', '$2a$10$hGtiUhI5Fycm.dSZLXqnNuZXx.ewaVPGfHU70f8tfs3rN5q/KJJDe', true, true);
INSERT INTO users(email, password, compte_valide) VALUES ('token-test@meteor.fr', '$2y$12$RxGGw0RbonhHlf16sIsAVeLOf7L8cbZ5fpqoJxwok8yeEbTrvvDHW', false);

SET @userDevId = (SELECT  users.id FROM users WHERE users.email = 'dev@meteor.fr');
SET @userTokenId = (SELECT  users.id FROM users WHERE users.email = 'token-test@meteor.fr');

INSERT INTO user_roles (user_id, role_id) values(@userDevId, 1);
INSERT INTO user_roles (user_id, role_id) values(@userDevId, 2);

INSERT INTO user_roles (user_id, role_id) values(@userTokenId, 1);

INSERT INTO user_validation_token(id, expiry_date, token_type, user_id)
values('TOKEN-validate', DATE_ADD(NOW(), INTERVAL 15 MINUTE), 'CREATION_COMPTE', @userTokenId);

INSERT INTO user_validation_token(id, expiry_date, token_type, user_id)
values('TOKEN-reset-pwd', DATE_ADD(NOW(), INTERVAL 15 MINUTE), 'RESET_PASSWORD', @userTokenId);

INSERT INTO user_validation_token(id, expiry_date, token_type, user_id)
values('expired-token', DATE_SUB(NOW(), INTERVAL 1 MINUTE), 'CREATION_COMPTE', @userDevId);


INSERT INTO propellant(id, name, unit, description, owner_id, json_propellant)
values(UUID_TO_BIN('df3b7cb7-6a95-11e7-8846-b05adad3f0ae'), 'CUSTOM Propelant dextrose SI', 'SI', 'Custom propellant de test', @userDevId, '{"name":"CUSTOM KNDX SI","burnRateCoefficient":"0.0665","pressureExponent":"0.319","cstar":null,"density":"1.879","k":"1.131","k2ph":"1.043","chamberTemperature":"1710","molarMass":"42.39","burnRateDataSet":[{"fromPressureIncluded":"0.1","toPressureExcluded":"0.779","burnRateCoefficient":"8.87544496778536","pressureExponent":"0.6193"},{"fromPressureIncluded":"0.779","toPressureExcluded":"2.572","burnRateCoefficient":"7.55278442387944","pressureExponent":"-0.0087"},{"fromPressureIncluded":"2.572","toPressureExcluded":"5.930","burnRateCoefficient":"3.84087990499602","pressureExponent":"0.6882"},{"fromPressureIncluded":"5.930","toPressureExcluded":"8.502","burnRateCoefficient":"17.2041864098062","pressureExponent":"-0.1481"},{"fromPressureIncluded":"8.502","toPressureExcluded":"11.20","burnRateCoefficient":"4.77524086347659","pressureExponent":"0.4417"}]}');

INSERT INTO propellant(id, name, unit, description, owner_id, json_propellant)
values(UUID_TO_BIN('6176e4b4-b1f8-49f0-b658-1b4493bed730'), 'CUSTOM Propelant dextrose IMPERIAL', 'IMPERIAL', 'Custom propellant de test', @userDevId, '{"name":"CUSTOM KNDX SI","burnRateCoefficient":"0.0665","pressureExponent":"0.319","cstar":null,"density":"1.879","k":"1.131","k2ph":"1.043","chamberTemperature":"1710","molarMass":"42.39","burnRateDataSet":[{"fromPressureIncluded":"0.1","toPressureExcluded":"0.779","burnRateCoefficient":"8.87544496778536","pressureExponent":"0.6193"},{"fromPressureIncluded":"0.779","toPressureExcluded":"2.572","burnRateCoefficient":"7.55278442387944","pressureExponent":"-0.0087"},{"fromPressureIncluded":"2.572","toPressureExcluded":"5.930","burnRateCoefficient":"3.84087990499602","pressureExponent":"0.6882"},{"fromPressureIncluded":"5.930","toPressureExcluded":"8.502","burnRateCoefficient":"17.2041864098062","pressureExponent":"-0.1481"},{"fromPressureIncluded":"8.502","toPressureExcluded":"11.20","burnRateCoefficient":"4.77524086347659","pressureExponent":"0.4417"}]}');

INSERT INTO propellant(id, name, unit, description, owner_id, json_propellant)
values(UUID_TO_BIN('f20feb38-ee4d-49a4-8131-78eb8472c06c'), 'ANOTHER PROPELLANT', 'Custom propellant de test', 'SI',  @userTokenId, '{"name":"CUSTOM KNDX SI","burnRateCoefficient":"0.0665","pressureExponent":"0.319","cstar":null,"density":"1.879","k":"1.131","k2ph":"1.043","chamberTemperature":"1710","molarMass":"42.39","burnRateDataSet":[{"fromPressureIncluded":"0.1","toPressureExcluded":"0.779","burnRateCoefficient":"8.87544496778536","pressureExponent":"0.6193"},{"fromPressureIncluded":"0.779","toPressureExcluded":"2.572","burnRateCoefficient":"7.55278442387944","pressureExponent":"-0.0087"},{"fromPressureIncluded":"2.572","toPressureExcluded":"5.930","burnRateCoefficient":"3.84087990499602","pressureExponent":"0.6882"},{"fromPressureIncluded":"5.930","toPressureExcluded":"8.502","burnRateCoefficient":"17.2041864098062","pressureExponent":"-0.1481"},{"fromPressureIncluded":"8.502","toPressureExcluded":"11.20","burnRateCoefficient":"4.77524086347659","pressureExponent":"0.4417"}]}');


/* TODO gérer le cas des versions */
INSERT INTO motor(id, name, description, owner_id, json_motor)
values(UUID_TO_BIN('9048315a-a8de-4f3c-ba79-52f5d8f25e49'), 'Moteur custom prop.', 'test pour le dev', @userDevId, '{"grainType": "HOLLOW", "extraConfig": {"densityRatio": 0.9529, "nozzleEfficiency": 0.85, "optimalNozzleDesign": false, "ambiantPressureInMPa": 0.101, "nozzleExpansionRatio": 3.95, "combustionEfficiencyRatio": 0.9, "nozzleErosionInMillimeter": 1, "erosiveBurningAreaRatioThreshold": 6, "erosiveBurningVelocityCoefficient": 0}, "grainConfig": {"coreSurface": "EXPOSED", "endsSurface": "INHIBITED", "coreDiameter": 14.1, "outerSurface": "INHIBITED", "outerDiameter": 27.5, "segmentLength": 80.5, "numberOfSegment": 1}, "nozzleDesign": {"divergenceAngle": 24, "convergenceAngle": 60}, "chamberLength": 81, "throatDiameter": 6, "computationHash": "f9911b07f1cf0c80f5512507ff3a75f1", "propellantId": "df3b7cb7-6a95-11e7-8846-b05adad3f0ae", "chamberInnerDiameter": 28, "version": 3, "measureUnit": "SI"}');

INSERT INTO motor(id, name, description, owner_id, json_motor)
values(UUID_TO_BIN('9048315a-a8de-4f3c-ba79-52f5d8f25e40'), 'Moteur custom prop inexistant', 'test pour le dev', @userDevId, '{"version":3, "grainType": "HOLLOW", "extraConfig": {"densityRatio": 0.9529, "nozzleEfficiency": 0.85, "optimalNozzleDesign": false, "ambiantPressureInMPa": 0.101, "nozzleExpansionRatio": 3.95, "combustionEfficiencyRatio": 0.9, "nozzleErosionInMillimeter": 1, "erosiveBurningAreaRatioThreshold": 6, "erosiveBurningVelocityCoefficient": 0}, "grainConfig": {"coreSurface": "EXPOSED", "endsSurface": "INHIBITED", "coreDiameter": 14.1, "outerSurface": "INHIBITED", "outerDiameter": 27.5, "segmentLength": 80.5, "numberOfSegment": 1}, "nozzleDesign": {"divergenceAngle": 24, "convergenceAngle": 60}, "chamberLength": 81, "throatDiameter": 6, "computationHash": "f9911b07f1cf0c80f5512507ff3a75f1", "propellantId": "dddd7cb7-6a95-11e7-8846-b05adad3f0ae", "chamberInnerDiameter": 28, "measureUnit": "SI"}');

INSERT INTO motor(id, name, description, owner_id, json_motor)
values(UUID_TO_BIN('9048315a-a8de-4f3c-ba79-52f5d8f25e41'), 'Moteur Meteor V1', 'test pour le dev', @userDevId, '{"version":3,"throatDiameter":"6","propellantId":"KNSB_COARSE","chamberInnerDiameter":28,"chamberLength":81,"extraConfig":{"densityRatio":0.9529,"nozzleErosionInMillimeter":1,"combustionEfficiencyRatio":0.9,"ambiantPressureInMPa":0.101,"erosiveBurningAreaRatioThreshold":6,"erosiveBurningVelocityCoefficient":0,"nozzleEfficiency":0.85,"nozzleExpansionRatio":"3.95","optimalNozzleDesign":false},"nozzleDesign":{"divergenceAngle":24,"convergenceAngle":60},"grainType":"HOLLOW","grainConfig":{"outerDiameter":27.5,"coreDiameter":14.1,"segmentLength":80.5,"numberOfSegment":1,"outerSurface":"INHIBITED","endsSurface":"INHIBITED","coreSurface":"EXPOSED"},"measureUnit":"SI"}');

INSERT INTO motor(id, name, description, owner_id, json_motor)
values(UUID_TO_BIN('9048315a-a8de-4f3c-ba79-52f5d8f25e42'), 'Moteur Meteor V2', 'test pour le dev', @userDevId, '{"version":3,"throatDiameter":"6","propellantId":"KNSB_COARSE","chamberInnerDiameter":28,"chamberLength":81,"extraConfig":{"densityRatio":0.9529,"nozzleErosionInMillimeter":1,"combustionEfficiencyRatio":0.9,"ambiantPressureInMPa":0.101,"erosiveBurningAreaRatioThreshold":6,"erosiveBurningVelocityCoefficient":0,"nozzleEfficiency":0.85,"nozzleExpansionRatio":"3.95","optimalNozzleDesign":false},"nozzleDesign":{"divergenceAngle":24,"convergenceAngle":60},"grainType":"HOLLOW","grainConfig":{"outerDiameter":27.5,"coreDiameter":14.1,"segmentLength":80.5,"numberOfSegment":1,"outerSurface":"INHIBITED","endsSurface":"INHIBITED","coreSurface":"EXPOSED"},"measureUnit":"SI"}');

INSERT INTO motor(id, name, description, owner_id, json_motor)
values(UUID_TO_BIN('9048315a-a8de-4f3c-ba79-52f5d8f25e43'), 'Moteur Meteor V3', 'test pour le dev', @userDevId, '{"version":3,"throatDiameter":"6","propellantId":"KNSB_COARSE","chamberInnerDiameter":28,"chamberLength":81,"extraConfig":{"densityRatio":0.9529,"nozzleErosionInMillimeter":1,"combustionEfficiencyRatio":0.9,"ambiantPressureInMPa":0.101,"erosiveBurningAreaRatioThreshold":6,"erosiveBurningVelocityCoefficient":0,"nozzleEfficiency":0.85,"nozzleExpansionRatio":"3.95","optimalNozzleDesign":false},"nozzleDesign":{"divergenceAngle":24,"convergenceAngle":60},"grainType":"HOLLOW","grainConfig":{"outerDiameter":27.5,"coreDiameter":14.1,"segmentLength":80.5,"numberOfSegment":1,"outerSurface":"INHIBITED","endsSurface":"INHIBITED","coreSurface":"EXPOSED"},"measureUnit":"SI"}');

INSERT INTO motor(id, name, description, owner_id, json_motor)
values(UUID_TO_BIN('334f4399-1ff8-4a68-8192-8e4ebe0b3751'), 'Moteur Autre user', 'test pour le dev', @userTokenId, '{"version":3,"throatDiameter":"6","propellantId":"KNSB_COARSE","chamberInnerDiameter":28,"chamberLength":81,"extraConfig":{"densityRatio":0.9529,"nozzleErosionInMillimeter":1,"combustionEfficiencyRatio":0.9,"ambiantPressureInMPa":0.101,"erosiveBurningAreaRatioThreshold":6,"erosiveBurningVelocityCoefficient":0,"nozzleEfficiency":0.85,"nozzleExpansionRatio":"3.95","optimalNozzleDesign":false},"nozzleDesign":{"divergenceAngle":24,"convergenceAngle":60},"grainType":"HOLLOW","grainConfig":{"outerDiameter":27.5,"coreDiameter":14.1,"segmentLength":80.5,"numberOfSegment":1,"outerSurface":"INHIBITED","endsSurface":"INHIBITED","coreSurface":"EXPOSED"},"measureUnit":"SI"}');
