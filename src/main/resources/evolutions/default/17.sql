# --- !Ups

# --- Add maxWords values to the text and textArea fields

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

# --- !Downs

# --- No need for any downs - the extra maxWords attributes will be ignored