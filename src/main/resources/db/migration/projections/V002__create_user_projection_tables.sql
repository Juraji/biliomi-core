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
    username       varchar(255) not null,
    passwordHash varchar(255),
    displayName     varchar(255),
    primary key (username)
) engine = InnoDB;

create table UserProjection_AuthorityGroupProjection
(
    UserProjection_username   varchar(255) not null,
    authorityGroups_groupId varchar(255) not null,
    primary key (UserProjection_username, authorityGroups_groupId)
) engine = InnoDB;
