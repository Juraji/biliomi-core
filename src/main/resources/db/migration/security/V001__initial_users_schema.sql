create table AuthorityGroup
(
    groupId   varchar(36)  not null,
    name      varchar(100) not null,
    protected bit          not null,
    primary key (groupId)
);

create table AuthorityGroup_authorities
(
    AuthorityGroup_groupId varchar(36) not null,
    authorities            varchar(50) not null
);

create table UserPrincipal
(
    userId   varchar(36)  not null,
    enabled  bit          not null,
    password varchar(100) not null,
    username varchar(100) not null,
    primary key (userId)
);

create table UserPrincipal_AuthorityGroup
(
    UserPrincipal_userId    varchar(36) not null,
    authorityGroups_groupId varchar(36) not null,
    primary key (UserPrincipal_userId, authorityGroups_groupId)
);

create unique index IDX_4b1ca534bff747985227 on AuthorityGroup (name);

create unique index IDX_4c6e917c1eabe627774c on UserPrincipal (username);

alter table AuthorityGroup_authorities
    add constraint FK3cf9x01p3vdtaxkmbi8thgynd foreign key (AuthorityGroup_groupId) references AuthorityGroup (groupId);

alter table UserPrincipal_AuthorityGroup
    add constraint FKm8t7f5meu04ydnv88y5odbu9x foreign key (authorityGroups_groupId) references AuthorityGroup (groupId);

alter table UserPrincipal_AuthorityGroup
    add constraint FKnfoj627hhk4i7fhgavdghjfpc foreign key (UserPrincipal_userId) references UserPrincipal (userId);

