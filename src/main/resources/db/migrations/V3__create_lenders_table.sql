create table lenders (
    id uuid primary key,
    lender_name varchar(255) not null,
    lender_phone varchar(50),
    lender_bank_data varchar(255),
    lender_address varchar(500),
    created_at timestamp not null default now(),
    updated_at timestamp not null default now()
);

create index idx_lenders_name on lenders(lender_name);
create index idx_lenders_phone on lenders(lender_phone);