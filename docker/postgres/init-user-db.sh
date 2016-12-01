#!/bin/bash
set -e

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" <<-EOSQL
    CREATE USER rifs;
    CREATE DATABASE rifs;
    GRANT ALL PRIVILEGES ON DATABASE rifs TO rifs;
EOSQL
