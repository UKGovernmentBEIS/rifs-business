## The business validation tier for the RIFS system

[![Codacy Badge](https://api.codacy.com/project/badge/Grade/79c11c91bca345b9857902f637c46d2e)](https://www.codacy.com/app/doug/rifs-business?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=UKGovernmentBEIS/rifs-business&amp;utm_campaign=Badge_Grade)
[![CircleCI](https://circleci.com/gh/UKGovernmentBEIS/rifs-business.svg?style=svg)](https://circleci.com/gh/UKGovernmentBEIS/rifs-business)


### Dependencies

This application requires the postgres database.  You can either install this yourself 
 or use docker-compose to get the [latest docker hub postgres image.](https://hub.docker.com/_/postgres/)
 
 Some basic docker command lines:
 
```
 # Bring postgres up (and build the image if needed) on localhost:5432
 docker-compose up -d

```

```
 # shutdown postgres
 docker-compose stop
```

```
 # remove the container
 docker-compose rm   
```
