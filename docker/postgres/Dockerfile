# Setup postgres for developer usage with a `rifs` db and a `rifs` user
FROM postgres:alpine
MAINTAINER js@ovix.co.uk
COPY ./init-user-db.sh /docker-entrypoint-initdb.d/
EXPOSE 5432
