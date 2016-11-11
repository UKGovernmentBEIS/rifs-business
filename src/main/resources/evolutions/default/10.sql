# --- !Ups

delete from paragraph where paragraph_number = 1 and section_id = 3;

# -- !Downs

insert into paragraph values (24, 1, 3, 'Assessment Criteria');
