# --- !Ups

--- Add maxWords values to the text and textArea fields

update "application_form_section"
set fields = '[{"name": "title", "type": "text", "isNumeric": false, "maxWords": 20}]'
where "section_number" = 1;

update "application_form_section"
set fields = '[{"name": "eventObjectives", "type": "textArea", "maxWords": 500}]'
where "section_number" = 3;

update "application_form_section"
set fields = '[{"name": "topicAndSpeaker", "type": "textArea", "maxWords": 500}]'
where "section_number" = 4;

update "application_form_section"
set fields = '[{"name": "eventAudience", "type": "textArea", "maxWords": 500}]'
where "section_number" = 5;

alter table "application_form_section" drop column "started";

alter table "application_form_section" add column "section_type" varchar(50) not null default 'form';
update "application_form_section" set "section_type" = 'list' where "section_number" = 6;
update "application_form_section" set "fields" = '[]' where "section_number" = 6;

alter table "opportunity" drop column duration;
alter table "opportunity" drop column duration_units;
alter table "opportunity" add column end_date varchar(255);

# --- !Downs

--- the extra maxWords attributes will be ignored
--- the started column was never used to no need to restore it

alter table "application_form_section" drop column "section_type";
update "application_form_section"
set fields = '[{"name": "", "type": "costList"}]'
where "section_number" = 6;