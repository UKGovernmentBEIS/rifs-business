# --- !Ups

insert into "application" values (1, 1);

insert into "application_section" values (1, 1, 1, 'Event title', false);
insert into "application_section" values (2, 1, 2, 'Provisional date', false);
insert into "application_section" values (3, 1, 3, 'Event objectives', false);
insert into "application_section" values (4, 1, 4, 'Topics and speakers', false);
insert into "application_section" values (5, 1, 5, 'Event audience', false);
insert into "application_section" values (6, 1, 6, 'Costs', false);

# --- !Downs

drop table "application_section";
drop table "application";
