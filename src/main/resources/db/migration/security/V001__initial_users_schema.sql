create table UserGroup
(
    groupId varchar(36) not null,
    name    varchar(100),
    primary key (groupId)
) engine = InnoDB;

create table UserGroup_authorities
(
    UserGroup_groupId varchar(36) not null,
    authorities       varchar(100)
) engine = InnoDB;

create table UserPrincipal
(
    userId                varchar(36) not null,
    accountNonExpired     bit          not null,
    accountNonLocked      bit          not null,
    credentialsNonExpired bit          not null,
    enabled               bit          not null,
    password              varchar(100),
    username              varchar(100),
    primary key (userId)
) engine = InnoDB;

create table UserPrincipal_authorities
(
    UserPrincipal_userId varchar(36) not null,
    authorities          varchar(100)
) engine = InnoDB;

create table UserPrincipal_UserGroup
(
    UserPrincipal_userId varchar(36) not null,
    userGroups_groupId   varchar(36) not null,
    primary key (UserPrincipal_userId, userGroups_groupId)
) engine = InnoDB;

alter table UserGroup_authorities
    add constraint FK3cf9x01p3vdtaxkmbi8thgynd foreign key (UserGroup_groupId) references UserGroup (groupId);

alter table UserPrincipal_authorities
    add constraint FKkyo8w12ji6icd5xhkw18rld8e foreign key (UserPrincipal_userId) references UserPrincipal (userId);

alter table UserPrincipal_UserGroup
    add constraint FKm8t7f5meu04ydnv88y5odbu9x foreign key (userGroups_groupId) references UserGroup (groupId);

alter table UserPrincipal_UserGroup
    add constraint FKnfoj627hhk4i7fhgavdghjfpc foreign key (UserPrincipal_userId) references UserPrincipal (userId);
