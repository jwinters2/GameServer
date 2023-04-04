create table if not exists users  (
    uid int primary key Generated Always as Identity,
    username varchar unique,
    password_hash varchar,
    salt varchar
);

create index if not exists uid_index on users (uid);