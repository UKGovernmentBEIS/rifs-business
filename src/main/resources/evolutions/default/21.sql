# --- !Ups

alter table "opportunity" add column "published_at_dtime" timestamptz;
alter table "opportunity" add column "duplicated_from_id" BIGINT;

alter table "opportunity" add constraint "duplicated_opportunity_fk" foreign key("duplicated_from_id") references "opportunity"("id") on update NO ACTION on delete CASCADE;

update "opportunity" set "published_at_dtime" = '2016-11-28 00:00:00' where id = 1;

ALTER TABLE "application_section" ALTER COLUMN "completed_at_dt" TYPE TIMESTAMPTZ;

CREATE SEQUENCE opportunity_id_seq START WITH 2;
ALTER TABLE "opportunity" ALTER column "id" SET DEFAULT NEXTVAL('opportunity_id_seq');

CREATE SEQUENCE section_id_seq START WITH 4;
ALTER TABLE "section" ALTER column "id" SET DEFAULT NEXTVAL('section_id_seq');

CREATE SEQUENCE applicationform_id_seq START WITH 2;
ALTER TABLE "application_form" ALTER column "id" SET DEFAULT NEXTVAL('applicationform_id_seq');

CREATE SEQUENCE applicationformsection_id_seq START WITH 7;
ALTER TABLE "application_form_section" ALTER column "id" SET DEFAULT NEXTVAL('applicationformsection_id_seq');

CREATE SEQUENCE applicationformquestion_id_seq START WITH 8;
ALTER TABLE "application_form_question" ALTER column "id" SET DEFAULT NEXTVAL('applicationformquestion_id_seq');

# --- !Downs

ALTER TABLE "section" ALTER column "id" SET DEFAULT null;
drop sequence "section_id_seq";
delete from section where id > 3;

ALTER TABLE "opportunity" ALTER column "id" SET DEFAULT null;
drop sequence "opportunity_id_seq";
delete from opportunity where id > 1;

ALTER TABLE "application_form" ALTER column "id" SET DEFAULT null;
drop sequence "applicationform_id_seq";
delete from application_form where id > 1;

ALTER TABLE "application_form_section" ALTER column "id" SET DEFAULT null;
drop sequence "applicationformsection_id_seq";
delete from application_form_section where id > 6;

ALTER TABLE "application_form_question" ALTER column "id" SET DEFAULT null;
drop sequence "applicationformquestion_id_seq";
delete from application_form_question where id > 7;


alter table "opportunity" drop constraint "duplicated_opportunity_fk";
alter table "opportunity" drop column "duplicated_from_id";
alter table "opportunity" drop column "published_at_dtime";
