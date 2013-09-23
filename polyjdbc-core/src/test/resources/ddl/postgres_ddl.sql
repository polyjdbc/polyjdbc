CREATE TABLE test (
    id BIGINT NOT NULL,
    name VARCHAR(200) NOT NULL,
    count INTEGER NOT NULL,
    countable BOOLEAN DEFAULT true NOT NULL,
    separator CHAR(1) DEFAULT ';' NOT NULL,
    CONSTRAINT pk_test PRIMARY KEY(id),
    CONSTRAINT u_test_name UNIQUE(name)
);
CREATE SEQUENCE seq_test;
CREATE INDEX idx_test_name ON test(name);