-- psql -U postgres -f init-psql.sql
create database polly;
create user polly with password 'polly';
grant all privileges on polly to polly;