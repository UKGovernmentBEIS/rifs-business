# --- !Ups

create table "paragraph" ("id" BIGINT NOT NULL PRIMARY KEY,"paragraph_number" INTEGER NOT NULL,"section_id" BIGINT NOT NULL,"text" VARCHAR(255) NOT NULL);
create index "paragraph_section_idx" on "paragraph" ("section_id");
create table "section" ("id" BIGINT NOT NULL PRIMARY KEY,"section_number" INTEGER NOT NULL,"opportunity_id" BIGINT NOT NULL,"title" VARCHAR(255) NOT NULL);
create index "section_opportunity_idx" on "section" ("opportunity_id");
create table "opportunity" ("id" BIGINT NOT NULL PRIMARY KEY,"title" VARCHAR(255) NOT NULL,"start_date" VARCHAR(255) NOT NULL,"duration" INTEGER,"duration_units" VARCHAR(255),"value" decimal(9, 2) NOT NULL,"value_units" VARCHAR(255) NOT NULL);
alter table "paragraph" add constraint "paragraph_section_fk" foreign key("section_id") references "section"("id") on update NO ACTION on delete CASCADE;
alter table "section" add constraint "section_opportunity_fk" foreign key("opportunity_id") references "opportunity"("id") on update NO ACTION on delete CASCADE;


# --- !Downs

drop table "paragraph";
drop table "section";
drop table "opportunity";