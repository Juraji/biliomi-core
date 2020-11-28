set @administrators_uuid=UUID();
set @users_uuid=UUID();

insert into AuthorityGroup (groupId, name) VALUES (@administrators_uuid, 'Administrators');
insert into AuthorityGroup_authorities (AuthorityGroup_groupId, authorities) VALUES (@administrators_uuid, 'ROLE_ADMIN');
insert into AuthorityGroup_authorities (AuthorityGroup_groupId, authorities) VALUES (@administrators_uuid, 'ROLE_USER');

insert into AuthorityGroup (groupId, name) VALUES (@users_uuid, 'Users');
insert into AuthorityGroup_authorities (AuthorityGroup_groupId, authorities) VALUES (@users_uuid, 'ROLE_USER');
