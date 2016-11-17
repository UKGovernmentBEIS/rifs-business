# --- !Ups

update paragraph set text = 'You can change this in the future.'
where
  section_id = (select id from section where title = 'What we will ask you' LIMIT 1) and
  paragraph_number = 5;


# -- !Downs

update paragraph set text = 'You can can change this in the future.'
where
  section_id = (select id from section where title = 'What we will ask you' LIMIT 1) and
  paragraph_number = 5;