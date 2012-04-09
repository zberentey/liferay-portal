alter table Contact_ add classNameId LONG;
alter table Contact_ add classPK LONG;
alter table Contact_ add emailAddress VARCHAR(75) null;

drop table Groups_Permissions;

drop table OrgGroupPermission;

drop table Permission_;

drop table Resource_;

drop table ResourceCode;

drop table Roles_Permissions;

drop table Users_Permissions;