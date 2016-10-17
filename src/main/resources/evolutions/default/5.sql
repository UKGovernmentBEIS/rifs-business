# --- !Ups

create table "application_section" (
  "id" BIGSERIAL NOT NULL PRIMARY KEY,
  "application_id" BIGINT NOT NULL,
  "section_number" INTEGER NOT NULL,
  "answers" jsonb NOT NULL
);
create index "applicationsection_application_idx" on "application_section" ("application_id");
create table "application" ("id" BIGSERIAL NOT NULL PRIMARY KEY,"application_form_id" BIGINT NOT NULL);
create index "application_application_form_idx" on "application" ("application_form_id");
alter table "application_section" add constraint "applicationsection_application_fk" foreign key("application_id") references "application"("id") on update NO ACTION on delete CASCADE;
alter table "application" add constraint "application_application_form_fk" foreign key("application_form_id") references "application_form"("id") on update NO ACTION on delete CASCADE;

# --- !Downs

drop table application;
drop table application_section;