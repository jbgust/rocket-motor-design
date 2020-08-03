create table roles
(
    id   integer,
    name varchar(20),
    primary key (id)
);

create table users
(
    id                 bigint,
    compte_valide      boolean not null,
    date_creation      timestamp,
    derniere_connexion timestamp,
    email              varchar(50),
    password           varchar(120),
    primary key (id)
);

create table user_roles
(
    user_id bigint  not null,
    role_id integer not null,
    primary key (user_id, role_id),
    foreign key (user_id) references users (id),
    foreign key (role_id) references roles (id)
);

create table user_validation_token
(
    id          varchar(255) not null,
    expiry_date timestamp,
    token_type  varchar(255),
    user_id     bigint       not null,
    primary key (id),
    foreign key (user_id) references users (id)
);
