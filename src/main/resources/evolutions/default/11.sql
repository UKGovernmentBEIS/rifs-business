# --- !Ups

CREATE TABLE "application_form_question" (
  "id"                          BIGINT       NOT NULL PRIMARY KEY,
  "application_form_section_id" BIGINT       NOT NULL,
  "key"                         VARCHAR(255) NOT NULL,
  "text"                        VARCHAR(255) NOT NULL,
  "description"                 VARCHAR(4096),
  "help_text"                   VARCHAR(4096)
);

CREATE INDEX "applicationformquestion_application_form_section_idx"
  ON "application_form_question" ("application_form_section_id");
ALTER TABLE "application_form_question"
  ADD CONSTRAINT "applicationformquestion_application_form_section_fk" FOREIGN KEY ("application_form_section_id") REFERENCES "application_form_section" ("id") ON UPDATE NO ACTION ON DELETE CASCADE;

INSERT INTO "application_form_question" VALUES (1, 1, 'title', 'What is your event called?', NULL, NULL);

INSERT INTO "application_form_question" VALUES (2, 2, 'provisionalDate.date', 'When do you propose to hold the event?', NULL, NULL);

INSERT INTO "application_form_question" VALUES (3, 2, 'provisionalDate.days', 'How long will it last?', NULL, NULL);

INSERT INTO "application_form_question" VALUES (4, 3, 'eventObjectives', 'What are the objectives of the event?',
                                                E'Explain what outcomes you hope the event will achieve, including who is likely to benefit and the actions you will take to maximise the benefits.',
                                                E'There are no fixed rules about content;; however the most successful events have involved senior academics working with colleagues to develop the research programme and share their strategic vision.\nFeedback from previous events has shown that it is important to keep the demands on time modest, with most seminars scheduled over a half day.');

INSERT INTO "application_form_question" VALUES (5, 4, 'topicAndSpeaker', 'What topics do you intend to cover?',
                                                E'List the subjects and speakers you are planning for the event. It doesn''t matter if they are not confirmed at this stage.',
                                                E'Possible topics for discussion include intellectual asset management, licensing and collaborative R&D.\nSpeakers might include internal or external business development professionals and others such as patent lawyers/agents and KTP advisors.\nWhenever possible, a member of our Swindon office staff will be available to participate in the seminar free of charge.');

INSERT INTO "application_form_question" VALUES (6, 5, 'eventAudience', 'Who is the event''s target audience?',
                                                E'There may be one or more target audiences. How many people do you expect to attend? What sectors (for example, academic, industrial, legal) will they represent?',
                                                E'If possible, examine the audience make-up from previous similar events. Who came to them and who is likely to come to your event?\nIt''s a good idea to invite people from relevant faculties, colleges or departments, and business development offices.');

INSERT INTO "application_form_question" VALUES (7, 6, 'item', 'What will the costs be?',
                                                E'We will pay up to £2,000 towards the travel and accommodation costs of external speakers, room fees, equipment, time spent in organising the event and any other reasonable costs.\nYou can''t claim for food or drink. After the event, we''ll need a detailed invoice itemising all costs claimed before we release the funds.',
                                                E'When you''re listing items, it''s fine to cluster them in groups, for example: printed materials including hand-outs, posters and feedback forms.\nWe''ve left plenty of room for justification, but don''t feel you have to use all of the wordcount, especially if the need for an item is obvious.\nIn terms of who pays for each item, the default setting is 100% payment from the research council. But if your organisation or a partner is covering part of the cost of an item, you can reduce this percentage accordingly.\nFor example, if your organisation is paying 75% of the venue hire, you could reduce the RC percentage to 25%.');


# -- !Downs

DROP TABLE "application_form_question";
