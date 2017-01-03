# --- !Ups

ALTER TABLE "section"
  ADD COLUMN "description" VARCHAR(8192) NOT NULL default 'no description';
ALTER TABLE "section"
  ADD COLUMN "help_text" VARCHAR(8192) NULL;

UPDATE section
SET
  description = 'Be as specific as possible so that applicants fully understand the aim of the opportunity. This will help ensure that applications meet the criteria and objectives.',
  help_text = E'There are no fixed rules about content;; however the most successful events have involved senior academics working with colleagues to develop the research programme and share their strategic vision.

Feedback from previous events has shown that it is important to keep the demands on time modest, with most seminars scheduled over a half day.'
WHERE section_number = 1;

UPDATE section
SET
  description = 'Describe the questions the applicant will see on their application form.',
  help_text = NULL
WHERE section_number = 2;

UPDATE section
SET
  description = 'Which criteria will be used to assess applications?',
  help_text = E'Criteria will depend on the opportunity, but may include quality, objectives, collaboration between teams and organisations, and value for money. You may weight criteria equally or set priorities.'
WHERE section_number = 3;

# --- !Downs

ALTER TABLE "section"
  DROP COLUMN "description";
ALTER TABLE "section"
  DROP COLUMN "help_text";