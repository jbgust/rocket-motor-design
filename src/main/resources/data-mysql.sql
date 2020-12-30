SET @oldUserDevId = (SELECT  users.id FROM users WHERE users.email = 'dev@meteor.fr');
SET @oldUserTokenId = (SELECT  users.id FROM users WHERE users.email = 'token-test@meteor.fr');

DELETE FROM motor;

DELETE FROM user_roles where user_id in (@oldUserDevId, @oldUserTokenId);
DELETE FROM user_roles USING user_roles INNER JOIN users
    ON (users.id = user_roles.user_id) where email like 'cypress-%@meteor.fr';

DELETE FROM user_validation_token where user_id in (@oldUserDevId, @oldUserTokenId);
DELETE FROM user_validation_token USING user_validation_token INNER JOIN users
    ON (users.id = user_validation_token.user_id) where email like 'cypress-%@meteor.fr';

DELETE FROM users where email in ('dev@meteor.fr', 'token-test@meteor.fr');
DELETE FROM users where email like 'cypress-%@meteor.fr';

DELETE FROM propellant where id =  UUID_TO_BIN('df3b7cb7-6a95-11e7-8846-b05adad3f0ae');
INSERT INTO propellant(id, name, description, json_propellant)
values(UUID_TO_BIN('df3b7cb7-6a95-11e7-8846-b05adad3f0ae'), 'CUSTOM KNDX SI', 'Custom propellant de test', '{"k": "1.131", "k2ph": "1.043", "cstar": null, "density": "1.879", "molarMass": "42.39", "burnRateDataSet": [{"pressureExponent": "0.6193", "toPressureExcluded": "0.779", "burnRateCoefficient": "8.87544496778536", "fromPressureIncluded": "0.1"}, {"pressureExponent": "-0.0087", "toPressureExcluded": "2.572", "burnRateCoefficient": "7.55278442387944", "fromPressureIncluded": "0.779"}, {"pressureExponent": "0.6882", "toPressureExcluded": "5.930", "burnRateCoefficient": "3.84087990499602", "fromPressureIncluded": "2.572"}, {"pressureExponent": "-0.1481", "toPressureExcluded": "8.502", "burnRateCoefficient": "17.2041864098062", "fromPressureIncluded": "5.930"}, {"pressureExponent": "0.4417", "toPressureExcluded": "11.20", "burnRateCoefficient": "4.77524086347659", "fromPressureIncluded": "8.502"}], "pressureExponent": null, "chamberTemperature": "1710", "burnRateCoefficient": null}');



INSERT IGNORE INTO roles(id, name) VALUES(1, 'ROLE_USER');

INSERT INTO users(email, password, compte_valide) VALUES ('dev@meteor.fr', '$2a$10$hGtiUhI5Fycm.dSZLXqnNuZXx.ewaVPGfHU70f8tfs3rN5q/KJJDe', true);
INSERT INTO users(email, password, compte_valide) VALUES ('token-test@meteor.fr', '$2y$12$RxGGw0RbonhHlf16sIsAVeLOf7L8cbZ5fpqoJxwok8yeEbTrvvDHW', false);

SET @userDevId = (SELECT  users.id FROM users WHERE users.email = 'dev@meteor.fr');
SET @userTokenId = (SELECT  users.id FROM users WHERE users.email = 'token-test@meteor.fr');

/* TODO gérer le cas des versions */
INSERT INTO motor(id, name, description, owner_id, json_motor)
values(UUID_TO_BIN('334f4399-1ff8-4a68-8192-8e4ebe0b3751'), 'Moteur Autre user', 'test pour le dev', @userTokenId, '{"version":2,"configs":[{"computationHash":"f9911b07f1cf0c80f5512507ff3a75f1","throatDiameter":"6","propellantType":"KNSB_COARSE","chamberInnerDiameter":28,"chamberLength":81,"extraConfig":{"densityRatio":0.9529,"nozzleErosionInMillimeter":1,"combustionEfficiencyRatio":0.9,"ambiantPressureInMPa":0.101,"erosiveBurningAreaRatioThreshold":6,"erosiveBurningVelocityCoefficient":0,"nozzleEfficiency":0.85,"nozzleExpansionRatio":"3.95","optimalNozzleDesign":false},"nozzleDesign":{"divergenceAngle":24,"convergenceAngle":60},"grainType":"HOLLOW","grainConfig":{"outerDiameter":27.5,"coreDiameter":14.1,"segmentLength":80.5,"numberOfSegment":1,"outerSurface":"INHIBITED","endsSurface":"INHIBITED","coreSurface":"EXPOSED"}}],"measureUnit":"SI"}');

INSERT INTO motor(id, name, description, owner_id, json_motor)
values(UUID_TO_BIN('9048315a-a8de-4f3c-ba79-52f5d8f25e49'), 'Moteur Meteor V3', 'test pour le dev', @userDevId, '{"version":2,"configs":[{"computationHash":"f9911b07f1cf0c80f5512507ff3a75f1","throatDiameter":"6","propellantType":"KNSB_COARSE","chamberInnerDiameter":28,"chamberLength":81,"extraConfig":{"densityRatio":0.9529,"nozzleErosionInMillimeter":1,"combustionEfficiencyRatio":0.9,"ambiantPressureInMPa":0.101,"erosiveBurningAreaRatioThreshold":6,"erosiveBurningVelocityCoefficient":0,"nozzleEfficiency":0.85,"nozzleExpansionRatio":"3.95","optimalNozzleDesign":false},"nozzleDesign":{"divergenceAngle":24,"convergenceAngle":60},"grainType":"HOLLOW","grainConfig":{"outerDiameter":27.5,"coreDiameter":14.1,"segmentLength":80.5,"numberOfSegment":1,"outerSurface":"INHIBITED","endsSurface":"INHIBITED","coreSurface":"EXPOSED"}}],"measureUnit":"SI"}');

INSERT INTO motor(id, name, description, owner_id, json_motor)
values(UUID_TO_BIN('9048315a-a8de-4f3c-ba79-52f5d8f25e40'), 'Moteur Meteor V4', 'test pour le dev', @userDevId, '{"version":2,"configs":[{"computationHash":"f9911b07f1cf0c80f5512507ff3a75f1","throatDiameter":"6","propellantType":"KNSB_COARSE","chamberInnerDiameter":28,"chamberLength":81,"extraConfig":{"densityRatio":0.9529,"nozzleErosionInMillimeter":1,"combustionEfficiencyRatio":0.9,"ambiantPressureInMPa":0.101,"erosiveBurningAreaRatioThreshold":6,"erosiveBurningVelocityCoefficient":0,"nozzleEfficiency":0.85,"nozzleExpansionRatio":"3.95","optimalNozzleDesign":false},"nozzleDesign":{"divergenceAngle":24,"convergenceAngle":60},"grainType":"HOLLOW","grainConfig":{"outerDiameter":27.5,"coreDiameter":14.1,"segmentLength":80.5,"numberOfSegment":1,"outerSurface":"INHIBITED","endsSurface":"INHIBITED","coreSurface":"EXPOSED"}}],"measureUnit":"SI"}');

INSERT INTO motor(id, name, description, owner_id, json_motor)
values(UUID_TO_BIN('9048315a-a8de-4f3c-ba79-52f5d8f25e41'), 'Moteur Meteor V5', 'test pour le dev', @userDevId, '{"version":2,"configs":[{"computationHash":"f9911b07f1cf0c80f5512507ff3a75f1","throatDiameter":"6","propellantType":"KNSB_COARSE","chamberInnerDiameter":28,"chamberLength":81,"extraConfig":{"densityRatio":0.9529,"nozzleErosionInMillimeter":1,"combustionEfficiencyRatio":0.9,"ambiantPressureInMPa":0.101,"erosiveBurningAreaRatioThreshold":6,"erosiveBurningVelocityCoefficient":0,"nozzleEfficiency":0.85,"nozzleExpansionRatio":"3.95","optimalNozzleDesign":false},"nozzleDesign":{"divergenceAngle":24,"convergenceAngle":60},"grainType":"HOLLOW","grainConfig":{"outerDiameter":27.5,"coreDiameter":14.1,"segmentLength":80.5,"numberOfSegment":1,"outerSurface":"INHIBITED","endsSurface":"INHIBITED","coreSurface":"EXPOSED"}}],"measureUnit":"SI"}');

INSERT INTO motor(id, name, description, owner_id, json_motor)
values(UUID_TO_BIN('9048315a-a8de-4f3c-ba79-52f5d8f25e42'), 'Moteur Meteor V6', 'test pour le dev', @userDevId, '{"version":2,"configs":[{"computationHash":"f9911b07f1cf0c80f5512507ff3a75f1","throatDiameter":"6","propellantType":"KNSB_COARSE","chamberInnerDiameter":28,"chamberLength":81,"extraConfig":{"densityRatio":0.9529,"nozzleErosionInMillimeter":1,"combustionEfficiencyRatio":0.9,"ambiantPressureInMPa":0.101,"erosiveBurningAreaRatioThreshold":6,"erosiveBurningVelocityCoefficient":0,"nozzleEfficiency":0.85,"nozzleExpansionRatio":"3.95","optimalNozzleDesign":false},"nozzleDesign":{"divergenceAngle":24,"convergenceAngle":60},"grainType":"HOLLOW","grainConfig":{"outerDiameter":27.5,"coreDiameter":14.1,"segmentLength":80.5,"numberOfSegment":1,"outerSurface":"INHIBITED","endsSurface":"INHIBITED","coreSurface":"EXPOSED"}}],"measureUnit":"SI"}');

INSERT INTO motor(id, name, description, owner_id, json_motor)
values(UUID_TO_BIN('9048315a-a8de-4f3c-ba79-52f5d8f25e43'), 'Moteur Meteor V7', 'test pour le dev', @userDevId, '{"version":2,"configs":[{"computationHash":"f9911b07f1cf0c80f5512507ff3a75f1","throatDiameter":"6","propellantType":"KNSB_COARSE","chamberInnerDiameter":28,"chamberLength":81,"extraConfig":{"densityRatio":0.9529,"nozzleErosionInMillimeter":1,"combustionEfficiencyRatio":0.9,"ambiantPressureInMPa":0.101,"erosiveBurningAreaRatioThreshold":6,"erosiveBurningVelocityCoefficient":0,"nozzleEfficiency":0.85,"nozzleExpansionRatio":"3.95","optimalNozzleDesign":false},"nozzleDesign":{"divergenceAngle":24,"convergenceAngle":60},"grainType":"HOLLOW","grainConfig":{"outerDiameter":27.5,"coreDiameter":14.1,"segmentLength":80.5,"numberOfSegment":1,"outerSurface":"INHIBITED","endsSurface":"INHIBITED","coreSurface":"EXPOSED"}}],"measureUnit":"SI"}');

INSERT INTO user_roles (user_id, role_id) values(@userDevId, 1);
INSERT INTO user_roles (user_id, role_id) values(@userTokenId, 1);

INSERT INTO user_validation_token(id, expiry_date, token_type, user_id)
    values('TOKEN-validate', DATE_ADD(NOW(), INTERVAL 15 MINUTE), 'CREATION_COMPTE', @userTokenId);

INSERT INTO user_validation_token(id, expiry_date, token_type, user_id)
    values('TOKEN-reset-pwd', DATE_ADD(NOW(), INTERVAL 15 MINUTE), 'RESET_PASSWORD', @userTokenId);

INSERT INTO user_validation_token(id, expiry_date, token_type, user_id)
    values('expired-token', DATE_SUB(NOW(), INTERVAL 1 MINUTE), 'CREATION_COMPTE', @userDevId);
