create table token
(
    id                 serial primary key,
    created_at         timestamp not null default current_timestamp,
    updated_at         timestamp not null default current_timestamp,
    hedera_token_id    text      not null,
    name               text      not null,
    symbol             text      not null,
    supply_public_key  text      not null,
    supply_private_key text      not null
);

create table nft
(
    id              serial primary key,
    created_at      timestamp                                   not null default current_timestamp,
    updated_at      timestamp                                   not null default current_timestamp,
    token_id        int references token (id) on delete cascade not null,
    hedera_token_id text                                        not null,
    serials         bigint[]                                    not null,
    data            text[]                                      not null -- ipfs hashes
);

create table image
(
    id         serial primary key,
    created_at timestamp not null default current_timestamp,
    updated_at timestamp not null default current_timestamp,
    ipfs       text      not null
);
