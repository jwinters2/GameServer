create table if not exists users (
    uid int primary key Generated Always as Identity,
    username varchar unique,
    password_hash varchar,
    salt varchar
);

create index if not exists uid_index on users (uid);



create table if not exists matches (
    matchid bigint primary key,
    players int[],
    game varchar,
    gamedata varchar
);

create index if not exists matchid_index on matches (matchid);

create table if not exists player_to_matches (
    playerid int references users (uid),
    matchid bigint references matches (matchid),

    primary key (playerid, matchid)
);