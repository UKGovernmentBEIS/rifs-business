# --- !Ups

alter table section add column "section_type" varchar(30) not null default 'text';
update section set text = null, section_type = 'questions' where section_number = 2;

# --- !Downs

alter table section drop COLUMN "section_type";
UPDATE "section"
SET "text" = E'Event title

What is your event called? Wordcount: 20

Provisional date

You can change this in the future.

Event objectives

What are the objectives of the event? Who will benefit? What will you do to maximise the benefits? Wordcount: 500

Topics and speakers

Who is the event''s target audience?  There may be one or more audiences.  How many people do you expect to attend? Which sectors (for example, academic, industrial, legal) will they represent?  Wordcount: 500

Costs

We will pay up to £2,000 towards the travel and accommodation costs of external speakers, room fees, equipment, time spent in organising the event and any other reasonable costs.

You can''t claim for food or drink. After the event, we''ll need a detailed invoice itemising all costs claimed before we release the funds.  Wordcount: 200 words per item justification.'
WHERE "section_number" = 2;