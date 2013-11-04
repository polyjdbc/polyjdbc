-- mysql -u root < init-mysql.sql
create database polly;
create user 'polly'@'localhost' identified by 'polly';
grant all on polly.* to 'polly'@'localhost';