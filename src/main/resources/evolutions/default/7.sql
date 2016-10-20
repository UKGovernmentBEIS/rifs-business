# --- !Ups

create table "keystore" (
  "id" VARCHAR(36) PRIMARY KEY NOT NULL,
  "expiry_dtime" TIMESTAMP NOT NULL,
  "document" JSONB NOT NULL
);

# --- !Downs

drop table keystore;