create table user_projection
(
    id             varchar(64)  not null,
    username       varchar(255) not null,
    created_at     timestamp    not null,
    points_balance long         not null
)
