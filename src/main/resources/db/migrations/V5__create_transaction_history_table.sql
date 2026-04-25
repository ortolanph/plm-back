create table transaction_history
(
    id                       uuid primary key,
    history_date             timestamp    not null default now(),
    lender_name              varchar(255) not null,
    lender_phone             varchar(50),
    lender_bank_data         varchar(255),
    transaction_date         timestamp,
    transaction_value        numeric(19, 2),
    transaction_type         varchar(20)  not null,
    transaction_payment_type varchar(20),
    history_type             varchar(20)  not null
);

create index idx_transaction_history_lender_name on transaction_history (lender_name);
create index idx_transaction_history_history_date on transaction_history (history_date);
create index idx_transaction_history_history_type on transaction_history (history_type);