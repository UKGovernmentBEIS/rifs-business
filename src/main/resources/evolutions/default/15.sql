# --- !Ups

alter table "section" add column "text" VARCHAR(8192);

# --- !Downs

alter table "section" drop column "text";
