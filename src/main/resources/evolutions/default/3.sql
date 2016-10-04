# --- !Ups

create table "application_section" ("id" BIGINT NOT NULL PRIMARY KEY,"application_id" BIGINT NOT NULL,"section_number" INTEGER NOT NULL,"title" VARCHAR(255) NOT NULL,"started" BOOLEAN NOT NULL);
create index "applicationsection_application_idx" on "application_section" ("application_id");
create table "application" ("id" BIGINT NOT NULL PRIMARY KEY,"opportunity_id" BIGINT NOT NULL);
create index "application_opportunity_idx" on "application" ("opportunity_id");
alter table "application_section" add constraint "applicationsection_application_fk" foreign key("application_id") references "application"("id") on update NO ACTION on delete CASCADE;
alter table "application" add constraint "application_opportunity_fk" foreign key("opportunity_id") references "opportunity"("id") on update NO ACTION on delete CASCADE;

# --- !Downs

drop table "application_section";
drop table "application";
