# --- !Ups

alter table "application_form_section" add column "fields" JSONB not null default '[]';

# --- !Downs

alter table "application_form_section" drop column "fields";