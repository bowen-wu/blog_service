create TABLE user
(
    id                 INT auto_increment primary key,
    username           varchar(15),
    encrypted_password varchar(100),
    avatar             varchar(100),
    created_at         datetime,
    updated_at         datetime
);
