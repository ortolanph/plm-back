create table generated_codes (
    id serial primary key,
    code varchar(6) not null unique,
    created_at timestamp not null default now()
);

create index idx_generated_codes_created_at on generated_codes(created_at);