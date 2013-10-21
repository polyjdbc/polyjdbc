# PolyJDBC

PolyJDBC is a polyglot, lightweight wrapper around standard JDBC drivers with
schema inspection/creation capabilities.

## Why?

When developing [SmartParam](http://smartparam.org) JDBC repository i realized
that i was writing polyglot JDBC wrapper instead of focusing on core JDBC repository responsibilities.
PolyJDBC came to life by extracting low-level wrapping code to separate project.

Before starting work on SmartParam JDBC repo i was looking for a good JDBC wrapper
that would do more than suppress SQLExceptions. Specifically i was looking for
transaction-aware wrapper that would also make it easy to perform DDL operations.

## Target

PolyJDBC primary target are libraries that need light and cross-platform JDBC
persistence. Hibernate is great, but it leaves little place for end-user customization,
which is important when offering a library. It is also quite heavy. PolyJDBC size is
only 75kB, no dependencies except for slf4j logging API.

## Features

* polyglot (or better poly-dialect? multiple DB dialect support, including id generation stategies)
* transaction-oriented
* resources (Statements, ResultSets) are managed inside transaction scope
* intiutive transaction commit/rollback support
* DDL operation DSL (CREATE TABLE/INDEX/SEQUENCE with constraints, DROP *)
* SQL query DSL (INSERT, SELECT, UPDATE, DELETE)
* lightweight
* schema inspection (table/sequence exists?)
* schema alteration

## Supported database engines

* H2
* PostgreSQL
* MySQL

Thanks to [testng](http://testng.org/) magic PolyJDBC runs integration tests
on each database. This way i can be sure that all features all supported across
all engines.

## How to get it?

```xml
<dependency>
    <groupId>org.polyjdbc</groupId>
    <artifactId>polyjdbc</artifactId>
    <version>0.1.0</version>
</dependency>
```

## Enough, show me the code

Simple usage of low-level query runner:

```java
Dialect dialect = DialectRegistry.dialect("H2");
TransactionManager manager = new DataSourceTransactionManager(dialect, dataSource);

QueryRunner queryRunner = new TransactionalQueryRunner(manager.openTransaction());

try {
    SelectQuery query = QueryFactory.select().query("select * from test where year = :year")
        .withArgument("year", 2013).limit(10);
    List<Test> tests = queryRunner.selectList(query, new TestMapper());
}
finally {
    queryRunner.close();
}
```

**QueryRunner** is created per transaction and should always be closed.
 **try-finally** is there to make sure that resources are really freed. If you want to
perform operations without runner create/close boilerplate use reusable **SimpleQueryRunner**:

```java
Dialect dialect = DialectRegistry.dialect("H2");
TransactionManager manager = new DataSourceTransactionManager(dialect, dataSource);

SimpleQueryRunner simpleRunner = new SimpleQueryRunner(manager);

SelectQuery query = QueryFactory.select().query("select * from test where name = :name")
        .withArgument("name", "test");

Test test = simpleRunner.queryUnique(query, new TestMapper());
```

**SimpleQueryRunner** performs each query in new transaction. If you need to perform
custom (or multiple) operations use reusable **TransactionRunner**:

```java
Dialect dialect = DialectRegistry.dialect("H2");
TransactionManager manager = new DataSourceTransactionManager(dialect, dataSource);

TransactionRunner transactionRunner = new TransactionRunner(manager);

Test test = transactionRunner.run(new TransactionWrapper<Test>() {
    @Override
    public Test perform(QueryRunner queryRunner) {
        SelectQuery query = QueryFactory.select().query("select * from test where name = :name")
            .withArgument("name", "test");
        return queryRunner.queryUnique(query, new TestMapper());
    }
});

transactionRunner.run(new VoidTransactionWrapper() {
    @Override
    public void performVoid(QueryRunner queryRunner) {
        DeleteQuery query = QueryFactory.delete().from("test").where("year < :year")
            .withArgument("year", 2012);
        queryRunner.delete(query);
    }
});
```

## License

PolyJDBC is published under [Apache License 2.0](http://www.apache.org/licenses/LICENSE-2.0).