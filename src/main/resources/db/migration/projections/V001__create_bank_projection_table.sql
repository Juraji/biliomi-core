create table BankProjection
(
    accountId varchar(36) not null,
    userId    varchar(36) not null,
    balance   bigint      not null,
    primary key (accountId)
);

alter table BankProjection
    add constraint UK_16qsfdbdkllofdoixdav5hfpm unique (userId);
