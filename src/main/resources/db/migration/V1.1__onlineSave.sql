create table propellant
(
    id               binary(16),
    name            varchar(256),
    description      varchar(1000),
    json_propellant JSON,
    primary key (id)
);


create table motor
(
    id               binary(16),
    name            varchar(256),
    description      varchar(1000),
    json_motor JSON,
    primary key (id)
);
