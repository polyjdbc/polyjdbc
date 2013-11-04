-- psql -U postgres -f init-psql.sql
create database polly;
create user polly with password 'polly';
grant all on database polly to polly;