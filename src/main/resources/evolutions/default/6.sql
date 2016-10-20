# --- !Ups

alter table "application_section" ADD COLUMN completed_at_dt TIMESTAMP;

# -- !Downs

alter table "application_section" DROP COLUMN completed_at_dt;
