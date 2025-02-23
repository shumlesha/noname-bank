create table accounts
(
    id                 uuid primary key     default gen_random_uuid(),
    creation_timestamp timestamp   not null default current_timestamp,
    blocked_timestamp  timestamp,
    client_id          uuid        not null,
    number             varchar(30) not null unique,
    balance            numeric     not null default 0
);

create table transactions
(
    id                    uuid primary key   default gen_random_uuid(),
    transaction_timestamp timestamp not null default current_timestamp,
    account_from          uuid,
    account_to            uuid      not null,
    amount                numeric   not null,
    constraint fk_account_from foreign key (account_from) references accounts (id),
    constraint fk_account_to foreign key (account_to) references accounts (id)
);