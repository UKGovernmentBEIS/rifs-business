# --- !Ups

update paragraph
set text = 'What is your event called? Wordcount: 20'
where paragraph_number = 2 and section_id = 2;


update paragraph
set text = 'You can''t claim for food or drink. After the event, we''ll need a detailed invoice itemising all costs claimed before we release the funds.  Wordcount: 200 words per item justification.'
where paragraph_number = 15 and section_id = 2;

# -- !Downs

update paragraph
set text = 'What is your event called? Wordcount 20'
where paragraph_number = 2 and section_id = 2;

update paragraph
set text = 'You can''t claim for food or drink. After the event, we''ll need a detailed invoice itemising all costs claimed before we release the funds.  Wordcount 200 words per item justification.'
where paragraph_number = 15 and section_id = 2;

