create table BankProjection
(
    accountId varchar(36)  not null,
    username  varchar(255) not null,
    balance   bigint       not null,
    primary key (accountId)
);
