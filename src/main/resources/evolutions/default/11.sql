# --- !Ups

alter table "section" ADD COLUMN text varchar(4096);

# -- !Downs

alter table "section" drop COLUMN text;
