alter table transaction_history
    add column lender_id uuid references lenders (id);

create index idx_transaction_history_lender_id on transaction_history (lender_id);