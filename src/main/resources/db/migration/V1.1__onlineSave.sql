create table propellant
(
    id               binary(16),
    name            varchar(256) not null,
    description      varchar(1000),
    json_propellant JSON,
    owner_id bigint  not null,
    primary key (id),
    foreign key (owner_id) references users (id),
    UNIQUE `unique_name_user_constraint_propellant`(`name`, `owner_id`)
);


create table motor
(
    id               binary(16),
    name            varchar(256) not null,
    description      varchar(1000),
    json_motor JSON,
    owner_id bigint  not null,
    primary key (id),
    foreign key (owner_id) references users (id),
    UNIQUE `unique_name_user_constraint_motor`(`name`, `owner_id`)
);
