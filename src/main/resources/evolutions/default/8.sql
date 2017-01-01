# --- !Ups

-- Content change in para 2
delete from paragraph where id = 2;
insert into paragraph (id, section_id, paragraph_number, text) values (2, 1, 2, 'As part of this, we want to help you develop innovative ways of building on the research you carry out.');

update opportunity set title = 'Exploring innovation seminars' where title = 'Research priorities in health care';


-- Change the 6 links to just 3.
-- Will cause a delete cascade on the paragraph table
delete from section
where
  opportunity_id in (select id from opportunity where title = 'Exploring innovation seminars')
and
  section_number > 1;

-- insert new sections
insert into section values(2, 2, 1, 'What we will ask you');
insert into section values(3, 3, 1, 'Assessment criteria');

-- insert new section content
insert into paragraph values (9, 1, 2, 'Event title');
insert into paragraph values (10, 2, 2, 'What is your event called? Wordcount 20');
insert into paragraph values (11, 3, 2, ' ');
insert into paragraph values (12, 4, 2, 'Provisional date');
insert into paragraph values (13, 5, 2, 'You can can change this in the future.');
insert into paragraph values (14, 6, 2, ' ');
insert into paragraph values (15, 7, 2, 'Event objectives');
insert into paragraph values (16, 8, 2, 'What are the objectives of the event? Who will benefit? What will you do to maximise the benefits? Wordcount: 500');
insert into paragraph values (17, 9, 2, ' ');
insert into paragraph values (18, 10, 2, 'Topics and speakers');
insert into paragraph values (19, 11, 2, 'Who is the event''s target audience?  There may be one or more audiences.  How many people do you expect to attend? Which sectors (for example, academic, industrial, legal) will they represent?  Wordcount: 500');
insert into paragraph values (20, 12, 2, '');
insert into paragraph values (21, 13, 2, 'Costs');
insert into paragraph values (22, 14, 2, 'We will pay up to £2,000 towards the travel and accommodation costs of external speakers, room fees, equipment, time spent in organising the event and any other reasonable costs.');
insert into paragraph values (23, 15, 2, 'You can''t claim for food or drink. After the event, we''ll need a detailed invoice itemising all costs claimed before we release the funds.  Wordcount 200 words per item justification.');
insert into paragraph values (24, 1, 3, 'Assessment Criteria');
insert into paragraph values (25, 2, 3, 'Applications will be assessed against the following criteria which are all equally weighted: ');
insert into paragraph values (26, 3, 3, '- the research organisation already receives research council funding');
insert into paragraph values (27, 4, 3, '- there’s a fit with the funder’s strategic priorities');
insert into paragraph values (28, 5, 3, '- the programme includes coordination across research organisations or across various departments, schools and central offices in the research organisation');
insert into paragraph values (29, 6, 3, '- the target audience is diverse and includes academics, knowledge exchange professionals, senior management and, where appropriate, representatives from industry or stakeholder groups');
insert into paragraph values (30, 7, 3, '- the objectives are SMART (Specific, Measurable, Achievable, Relevant, Time-bound)');
insert into paragraph values (31, 8, 3, '- the costs are reasonable and the funding could make a difference');

# -- !Downs

delete from paragraph where section_id = 1 and paragraph_number = 2;
insert into paragraph (id, section_id, paragraph_number, text) values (2, 1, 2, 'As part of this, we want to help you to develop innovative ways of building on the research they carry out.');

update opportunity set title = 'Research priorities in health care' where title = 'Exploring innovation seminars' ;

-- Delete the new sections
delete from section
where
  opportunity_id in (select id from opportunity where title = 'Research priorities in health care')
and
  section_number > 1;

-- restore the deleted sections
insert into "section" values (2, 2, 1, 'The events we will fund');
insert into "section" values (3, 3, 1, 'What events should cover');
insert into "section" values (4, 4, 1, 'How to get funding');
insert into "section" values (5, 5, 1, 'Assessment Criteria');
insert into "section" values (6, 6, 1, 'Further Information');

-- restore the deleted content
insert into "paragraph" values (6, 1, 2, 'To receive funding for the event, your research organisation must receive funding from the research council and must aim to attract research council supported researchers to the event.');
insert into "paragraph" values (7, 2, 2, 'We encourage applications that are coordinated across departments within a research organisation or between different research organisations.');
insert into "paragraph" values (8, 3, 2, 'We advise that attendees are invited from relevant faculties, colleges or departments, and where the primary aim is knowledge exchange, relevant stakeholders should be invited e.g. representatives from industry.');
