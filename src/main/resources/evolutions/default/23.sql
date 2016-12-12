# --- !Ups

alter table section add column "section_type" varchar(30) not null default 'text';
update section set section_type = 'questions' where section_number = 2;

# --- !Downs

alter table section drop COLUMN "section_type";