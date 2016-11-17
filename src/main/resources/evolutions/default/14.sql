# --- !Ups

update "application_form_section"
set fields = '[{"name": "title", "type": "text", "isNumeric": false}]'
where "section_number" = 1;

update "application_form_section"
set fields = '[{"name": "provisionalDate", "type": "dateWithDays", "allowPast":false, "minValue":1, "maxValue":9}]'
where "section_number" = 2;

update "application_form_section"
set fields = '[{"name": "eventObjectives", "type": "textArea"}]'
where "section_number" = 3;

update "application_form_section"
set fields = '[{"name": "topicAndSpeaker", "type": "textArea"}]'
where "section_number" = 4;

update "application_form_section"
set fields = '[{"name": "eventAudience", "type": "textArea"}]'
where "section_number" = 5;

update "application_form_section"
set fields = '[{"name": "", "type": "costList"}]'
where "section_number" = 6;


# --- !Downs

update "application_form_section" set fields = '[]';
