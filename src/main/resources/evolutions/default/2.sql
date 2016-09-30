# --- !Ups

insert into "opportunity" values (1, 'Research priorities in health care', '4 March 2017', null, null, 2000, 'per event maximum');

insert into "section" values (1, 1, 1, 'About this opportunity');
insert into "section" values (2, 2, 1, 'The events we will fund');
insert into "section" values (3, 3, 1, 'What events should cover');
insert into "section" values (4, 4, 1, 'How to get funding');
insert into "section" values (5, 5, 1, 'Assessment Criteria');
insert into "section" values (6, 6, 1, 'Further Information');

insert into "paragraph" values (1, 1, 1, 'We want to achieve the widest benefit to society and the economy from the research we fund.');
insert into "paragraph" values (2, 2, 1, 'As part of this, we want to help you to develop innovative ways of building on the research they carry out.');
insert into "paragraph" values (3, 3, 1, 'This may be by sharing knowledge, commercialising ideas, exploring social benefits or other ways to increase the impact of your research.');
insert into "paragraph" values (4, 4, 1, 'Under the Exploring Innovation Seminars programme, we will pay up to £2,000 for each event promoting innovation and collaboration. We will not pay for food or drink.');
insert into "paragraph" values (5, 5, 1, 'Only organisations which receive funding from UK Research Councils may apply.');


insert into "paragraph" values (6, 1, 2, 'To receive funding for the event, your research organisation must receive funding from the research council and must aim to attract research council supported researchers to the event.');
insert into "paragraph" values (7, 2, 2, 'We encourage applications that are coordinated across departments within a research organisation or between different research organisations.');
insert into "paragraph" values (8, 3, 2, 'We advise that attendees are invited from relevant faculties, colleges or departments, and where the primary aim is knowledge exchange, relevant stakeholders should be invited e.g. representatives from industry.');


# --- !Downs

delete from "paragraph";
delete from "section";
delete from "opportunity";
