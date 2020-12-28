create table propellant
(
    id               binary(16),
    title            varchar(256),
    description      varchar(1000),
    json_propellant JSON,
    primary key (id)
);