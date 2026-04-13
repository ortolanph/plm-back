create table genereated_codes (
    id serial primary key,
    name varchar(255) not null,
    generated_code varchar(6) not null,
    created_at timestamp not null default now()
);