use ocat;
select * from patch;
UPDATE patch SET url=replace(`url`, '104', '118') where new_version in ('1.0.0', '1.0.1', '1.0.2');