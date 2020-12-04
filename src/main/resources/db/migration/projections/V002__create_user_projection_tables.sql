create table AuthorityGroupProjection
(
    groupId   varchar(36) not null,
    groupName varchar(255),
    primary key (groupId)
) engine = InnoDB;

create table AuthorityGroupProjection_authorities
(
    AuthorityGroupProjection_groupId varchar(36) not null,
    authorities                      varchar(255)
) engine = InnoDB;

create table UserProjection
(
    userId       varchar(36) not null,
    passwordHash varchar(255),
    username     varchar(255),
    primary key (userId)
) engine = InnoDB;

create table UserProjection_AuthorityGroupProjection
(
    UserProjection_userId   varchar(255) not null,
    authorityGroups_groupId varchar(255) not null,
    primary key (UserProjection_userId, authorityGroups_groupId)
) engine = InnoDB;

alter table UserProjection
    add constraint UK_16qtpgaoa1rea8awxdav5hfpm unique (username);
