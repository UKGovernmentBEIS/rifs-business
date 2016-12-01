# --- !Ups

alter table "opportunity" add column "published_at_dtime" timestamptz;
alter table "opportunity" add column "duplicated_from_id" BIGINT;

alter table "opportunity" add constraint "duplicated_opportunity_fk" foreign key("duplicated_from_id") references "opportunity"("id") on update NO ACTION on delete CASCADE;

# --- !Downs

alter table "opportunity" drop constraint "duplicated_opportunity_fk";
alter table "opportunity" drop column "duplicated_from_id";
alter table "opportunity" drop column "published_at_dtime";
