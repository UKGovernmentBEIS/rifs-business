# --- !Ups

alter table "application_section" add constraint "unique_section_number_per_application" unique (application_id, section_number);

# -- !Downs

alter table "application_section" drop constraint "unique_section_number_per_application";
