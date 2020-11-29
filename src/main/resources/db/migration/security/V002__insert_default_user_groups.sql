set @administrators_group_uuid = UUID();
set @users_group_uuid = UUID();
set @admin_user_uuid = UUID();

insert into AuthorityGroup (groupId, name, protected)
VALUES (@administrators_group_uuid, 'Administrators', true);

insert into AuthorityGroup_authorities (AuthorityGroup_groupId, authorities)
VALUES (@administrators_group_uuid, 'ROLE_GROUPS_READ_ALL'),
       (@administrators_group_uuid, 'ROLE_GROUPS_CREATE'),
       (@administrators_group_uuid, 'ROLE_GROUPS_UPDATE'),
       (@administrators_group_uuid, 'ROLE_GROUPS_DELETE'),
       (@administrators_group_uuid, 'ROLE_USERS_READ_ME'),
       (@administrators_group_uuid, 'ROLE_USERS_UPDATE_ME_USERNAME'),
       (@administrators_group_uuid, 'ROLE_USERS_UPDATE_ME_PASSWORD'),
       (@administrators_group_uuid, 'ROLE_USERS_READ_ALL'),
       (@administrators_group_uuid, 'ROLE_USERS_CREATE'),
       (@administrators_group_uuid, 'ROLE_USERS_DELETE'),
       (@administrators_group_uuid, 'ROLE_USERS_ADD_GROUP'),
       (@administrators_group_uuid, 'ROLE_USERS_REMOVE_GROUP'),
       (@administrators_group_uuid, 'ROLE_BANK_READ_ME'),
       (@administrators_group_uuid, 'ROLE_BANK_READ_ALL'),
       (@administrators_group_uuid, 'ROLE_BANK_ADD_POINTS'),
       (@administrators_group_uuid, 'ROLE_BANK_TAKE_POINTS'),
       (@administrators_group_uuid, 'ROLE_SSE_CONNECT');

insert into AuthorityGroup (groupId, name, protected)
VALUES (@users_group_uuid, 'Users', true);

insert into AuthorityGroup_authorities (AuthorityGroup_groupId, authorities)
VALUES (@users_group_uuid, 'ROLE_BANK_READ_ME'),
       (@users_group_uuid, 'ROLE_USERS_READ_ME'),
       (@users_group_uuid, 'ROLE_USERS_UPDATE_ME_PASSWORD');

# Username: admin, password: admin
insert into UserPrincipal (userId, enabled, username, password)
VALUES (@admin_user_uuid, true, 'admin',
        '36c770b7e32307756d270af2721e86b4cbe483c0e744fbfd4f1a373b6d43a79cd5e704c634468a15');

insert into UserPrincipal_AuthorityGroup (UserPrincipal_userId, authorityGroups_groupId)
VALUES (@admin_user_uuid, @administrators_group_uuid)
