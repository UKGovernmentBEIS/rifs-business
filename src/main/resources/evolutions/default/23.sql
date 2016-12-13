# --- !Ups

ALTER TABLE "application"
  ADD COLUMN "personal_reference" VARCHAR(255) NULL;


# --- !Downs

ALTER TABLE "application"
  DROP COLUMN "personal_reference";
