use ocat;
select * from resource;
select count(1) from resource;
delete from resource where version_code in (1,2,3);

select * from patch;
delete from patch where new_version in ('1.0.0', '1.0.1', '1.0.2');

select * from resource as a where a.version_cdoe in (select max(resource.version_code) from resource);
SELECT MAX(resource.version_code) FROM resource;

select * from user;
delete from user where user_id in (1,2);

alter table resource add column status SMALLINT NOT NULL  DEFAULT 1;
alter table patch add column status SMALLINT NOT NULL  DEFAULT 1;