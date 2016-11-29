# --- !Ups

ALTER TABLE "application_form" ADD CONSTRAINT "one_form_per_opportunity" UNIQUE ("id", "opportunity_id");

# --- !Downs

ALTER TABLE "application_form" DROP CONSTRAINT "one_form_per_opportunity";
