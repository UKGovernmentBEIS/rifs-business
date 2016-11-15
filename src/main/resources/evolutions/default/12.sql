# --- !Ups

delete from "section";

INSERT INTO section (id, section_number, opportunity_id, title, text) VALUES (1, 1, 1, 'About this opportunity', 'We want to achieve the widest benefit to society and the economy from the research we fund.

This may be by sharing knowledge, commercialising ideas, exploring social benefits or other ways to increase the impact of your research.

Under the Exploring Innovation Seminars programme, we will pay up to £2,000 for each event promoting innovation and collaboration. We will not pay for food or drink.

Only organisations which receive funding from UK Research Councils may apply.

As part of this, we want to help you develop innovative ways of building on the research you carry out.');
INSERT INTO section (id, section_number, opportunity_id, title, text) VALUES (2, 2, 1, 'What we will ask you', '### Event title
What is your event called? Wordcount: 20

### Provisional date
You can can change this in the future.

### Event objectives
What are the objectives of the event? Who will benefit? What will you do to maximise the benefits? Wordcount: 500

### Topics and speakers
Who is the event''s target audience?  There may be one or more audiences.  How many people do you expect to attend? Which sectors (for example, academic, industrial, legal) will they represent?  Wordcount: 500

### Costs
We will pay up to £2,000 towards the travel and accommodation costs of external speakers, room fees, equipment, time spent in organising the event and any other reasonable costs.');
INSERT INTO section (id, section_number, opportunity_id, title, text) VALUES (3, 3, 1, 'Assessment criteria', 'Applications will be assessed against the following criteria which are all equally weighted:

* the research organisation already receives research council funding
* there’s a fit with the funder’s strategic priorities
* the programme includes coordination across research organisations or across various departments, schools and central offices in the research organisation
* the target audience is diverse and includes academics, knowledge exchange professionals, senior management and, where appropriate, representatives from industry or stakeholder groups
* the objectives are SMART (Specific, Measurable, Achievable, Relevant, Time-bound)
* the costs are reasonable and the funding could make a difference');

# -- !Downs

update "section" set text = null;