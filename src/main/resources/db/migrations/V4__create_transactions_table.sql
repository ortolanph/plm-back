create table transactions
(
    id                       uuid primary key,
    id_lender                uuid           not null,
    transaction_date         timestamp      not null default now(),
    transaction_value        numeric(19, 2) not null,
    transaction_type         varchar(20)    not null,
    transaction_payment_type varchar(20),
    constraint fk_transactions_lender foreign key (id_lender) references lenders (id)
);

create index idx_transactions_lender on transactions (id_lender);
create index idx_transactions_date on transactions (transaction_date);
create index idx_transactions_type on transactions (transaction_type);