create TABLE blog
(
    id          INT auto_increment primary key,
    user_id     bigint,
    title       varchar(100),
    content     TEXT,
    description varchar(100),
    created_at  datetime,
    updated_at  datetime
);
