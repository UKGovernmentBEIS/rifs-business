# --- !Ups

create table "application_form_section" ("id" BIGINT NOT NULL PRIMARY KEY,"application_form_id" BIGINT NOT NULL,"section_number" INTEGER NOT NULL,"title" VARCHAR(255) NOT NULL,"started" BOOLEAN NOT NULL);
create index "applicationformsection_application_idx" on "application_form_section" ("application_form_id");
create table "application_form" ("id" BIGINT NOT NULL PRIMARY KEY,"opportunity_id" BIGINT NOT NULL);
create index "application_form_opportunity_idx" on "application_form" ("opportunity_id");
alter table "application_form_section" add constraint "applicationformsection_application_fk" foreign key("application_form_id") references "application_form"("id") on update NO ACTION on delete CASCADE;
alter table "application_form" add constraint "application_form_opportunity_fk" foreign key("opportunity_id") references "opportunity"("id") on update NO ACTION on delete CASCADE;

# --- !Downs

drop table "application_form_section";
drop table "application_form";
