# --- !Ups

update "application_form_section" set "fields" = '[{"name": "item", "type": "costItem"}]' where "section_number" = 6;

# --- !Downs

update "application_form_section" set "fields" = '[]' where "section_number" = 6;