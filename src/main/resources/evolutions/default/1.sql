# --- !Ups
create table "section" ("id" BIGSERIAL NOT NULL PRIMARY KEY,"section_number" INTEGER NOT NULL,"opportunity_id" BIGINT NOT NULL,"title" VARCHAR(255) NOT NULL,"text" VARCHAR(8192),"description" VARCHAR(8192) NOT NULL,"help_text" VARCHAR(8192),"section_type" VARCHAR(30) NOT NULL);
create index "section_opportunity_idx" on "section" ("opportunity_id");
create table "opportunity" ("id" BIGSERIAL NOT NULL PRIMARY KEY,"title" VARCHAR(255) NOT NULL,"start_date" VARCHAR(255) NOT NULL,"end_date" VARCHAR(255),"value" decimal(9, 2) NOT NULL,"value_units" VARCHAR(255) NOT NULL,"published_at_dtime" timestamptz,"duplicated_from_id" BIGINT);
create index "duplicated_opportunity_idx" on "opportunity" ("duplicated_from_id");
create table "application_form_question" ("id" BIGSERIAL NOT NULL PRIMARY KEY,"application_form_section_id" BIGINT NOT NULL,"key" VARCHAR(255) NOT NULL,"text" VARCHAR(255) NOT NULL,"description" VARCHAR(255),"help_text" VARCHAR(255));
create index "applicationformquestion_application_form_section_idx" on "application_form_question" ("application_form_section_id");
create table "application_form_section" ("id" BIGSERIAL NOT NULL PRIMARY KEY,"application_form_id" BIGINT NOT NULL,"section_number" INTEGER NOT NULL,"title" VARCHAR(255) NOT NULL,"section_type" VARCHAR(50) NOT NULL,"fields" jsonb NOT NULL);
create index "applicationformsection_applicationform_idx" on "application_form_section" ("application_form_id");
create table "application_form" ("id" BIGSERIAL NOT NULL PRIMARY KEY,"opportunity_id" BIGINT NOT NULL);
create index "applicationform_opportunity_idx" on "application_form" ("opportunity_id");
create table "application_section" ("id" BIGSERIAL NOT NULL PRIMARY KEY,"application_id" BIGINT NOT NULL,"section_number" INTEGER NOT NULL,"answers" jsonb NOT NULL,"completed_at_dt" timestamptz);
create index "applicationsection_application_idx" on "application_section" ("application_id");
create unique index "unique_section_number_per_application" on "application_section" ("application_id","section_number");
create table "application" ("id" BIGSERIAL NOT NULL PRIMARY KEY,"application_form_id" BIGINT NOT NULL,"personal_reference" VARCHAR(255));
create index "application_application_form_idx" on "application" ("application_form_id");
alter table "section" add constraint "section_opportunity_fk" foreign key("opportunity_id") references "opportunity"("id") on update NO ACTION on delete CASCADE;
alter table "opportunity" add constraint "duplicated_opportunity_fk" foreign key("duplicated_from_id") references "opportunity"("id") on update NO ACTION on delete CASCADE;
alter table "application_form_question" add constraint "applicationformquestion_application_form_section_fk" foreign key("application_form_section_id") references "application_form_section"("id") on update NO ACTION on delete CASCADE;
alter table "application_form_section" add constraint "applicationformsection_applicationform_fk" foreign key("application_form_id") references "application_form"("id") on update NO ACTION on delete CASCADE;
alter table "application_form" add constraint "applicationform_opportunity_fk" foreign key("opportunity_id") references "opportunity"("id") on update NO ACTION on delete CASCADE;
alter table "application_section" add constraint "applicationsection_application_fk" foreign key("application_id") references "application"("id") on update NO ACTION on delete CASCADE;
alter table "application" add constraint "application_application_form_fk" foreign key("application_form_id") references "application_form"("id") on update NO ACTION on delete CASCADE;

# --- !Downs
alter table "application" drop constraint "application_application_form_fk";
alter table "application_section" drop constraint "applicationsection_application_fk";
alter table "application_form" drop constraint "applicationform_opportunity_fk";
alter table "application_form_section" drop constraint "applicationformsection_applicationform_fk";
alter table "application_form_question" drop constraint "applicationformquestion_application_form_section_fk";
alter table "opportunity" drop constraint "duplicated_opportunity_fk";
alter table "section" drop constraint "section_opportunity_fk";
drop table "application";
drop table "application_section";
drop table "application_form";
drop table "application_form_section";
drop table "application_form_question";
drop table "opportunity";
drop table "section";