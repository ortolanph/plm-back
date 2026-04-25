alter table generated_codes
    add column email varchar(255) not null;

create index idx_generated_codes_email on generated_codes (email);