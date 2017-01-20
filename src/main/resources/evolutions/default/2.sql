# --- !Ups

insert into "opportunity" values (1, 'Research priorities in health care', '4 March 2017', null, null, 2000, 'per event maximum');

insert into "section" values (1, 1, 1, 'About this opportunity');
insert into "section" values (2, 2, 1, 'The events we will fund');
insert into "section" values (3, 3, 1, 'What events should cover');
insert into "section" values (4, 4, 1, 'How to get funding');
insert into "section" values (5, 5, 1, 'Assessment Criteria');
insert into "section" values (6, 6, 1, 'Further Information');

# --- !Downs

delete from "section";
delete from "opportunity";
