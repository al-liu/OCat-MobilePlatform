select * from resource;
delete from resource where version_code = 1;
select count(1) from resource;