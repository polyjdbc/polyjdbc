# PolyJDBC

[![Build Status](https://travis-ci.org/polyjdbc/polyjdbc.png?branch=master)](https://travis-ci.org/polyjdbc/polyjdbc)

PolyJDBC is a polyglot, lightweight wrapper around standard JDBC drivers with
schema inspection/creation capabilities.

See project wiki for changelog.

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

* polyglot (or better poly-dialect? multiple DB dialect support, including id generation strategies)
* transaction-oriented
* resources (Statements, ResultSets) are managed inside transaction scope
* intiutive transaction commit/rollback support
* option to leave transaction management to external framework (i.e. Spring)
* DDL operation DSL (CREATE TABLE/INDEX/SEQUENCE with constraints, DROP \*)
* SQL query DSL (INSERT, SELECT, UPDATE, DELETE)
* lightweight
* schema inspection (table/sequence exists?)
* schema alteration

## Supported database engines

* H2
* PostgreSQL
* MySQL
* Oracle (without limit/offset in SELECT)

Thanks to [testng](http://testng.org/) magic PolyJDBC runs integration tests
on each database. This way i can be sure that all features all supported across
all engines.

## How to get it?

```xml
<dependency>
    <groupId>org.polyjdbc</groupId>
    <artifactId>polyjdbc</artifactId>
    <version>0.5.0</version>
</dependency>
```

## Enough, show me the code

### Instantiation

By default, PolyJDBC takes care of transaction management on it's own:

```java
Dialect dialect = DialectRegistry.H2.getDialect();
PolyJDBC polyjdbc = PolyJDBCBuilder.polyJDBC(dialect).connectingToDataSource(dataSource).build();
```

But it is possible to leave connection management to any external framework:
 
```java
Dialect dialect = DialectRegistry.H2.getDialect();
PolyJDBC polyjdbc = PolyJDBCBuilder.polyJDBC(dialect).usingManagedConnections(() -> frameworkManager::getConnection).build();
```

### Querying

To ask simple questions, use simple tool. `SimpleQueryRunner` performs each query in new transaction:

```java
SelectQuery query = polyJdbc.query().selectAll().from("test").where("name = :name")
        .withArgument("name", "test");
        
Test test = polyJdbc.simpleQueryRunner().queryUnique(query, new TestMapper());
```

You might want to span your transaction across multiple statements, if so use `TransactionRunner`:

```java
TransactionRunner transactionRunner = polyjdbc.transactionRunner();

Test test = transactionRunner.run(new TransactionWrapper<Test>() {
    @Override
    public Test perform(QueryRunner queryRunner) {
        SelectQuery query = polyJdbc.query().selectAll().from("test").where("name = :name")
            .withArgument("name", "test");
        return queryRunner.queryUnique(query, new TestMapper());
    }
});

transactionRunner.run(new VoidTransactionWrapper() {
    @Override
    public void performVoid(QueryRunner queryRunner) {
        DeleteQuery query = polyJdbc.query().delete().from("test").where("year < :year")
            .withArgument("year", 2012);
        queryRunner.delete(query);
    }
});
```

Or if you need to get your hands dirty, see `QueryRunner`, but remember to free the resources:

```java
QueryRunner queryRunner = null;

try {
    queryRunner = polyjdbc.queryRunner();
    SelectQuery query = polyJdbc.query().selectAll().from("test").where("year = :year")
        .withArgument("year", 2013).limit(10);
    List<Test> tests = queryRunner.selectList(query, new TestMapper());
    queryRunner.commit()
}
catch(Exception exception) {
    polyjdbc.rollback(queryRunner);
    throw exception;
}
finally {
    polyjdbc.close(queryRunner);
}
```

### Schema management

PolyJDBC comes with tools for schema creating and deletion. More options for
schema inspection are planned, although there is no concrete release date.

To check if relation exists:

```java
SchemaInspector schemaInspector = null;
try {
    schemaInspector = polyjdbc.schemaInspector();
    boolean relationExists = schemaInspector.relationExists("testRelation");
} finally {
    polyjdbc.close(schemaManager);
}
```

To create new schema (group of relations):

```java
SchemaManager schemaManager = null;
try {
    schemaManager = polyjdbc.schemaManager();

    Schema schema = new Schema(configuration.getDialect());
    schema.addRelation("test_one")
        .withAttribute().longAttr("id").withAdditionalModifiers("AUTO_INCREMENT").notNull().and()
        .withAttribute().string("name").withMaxLength(200).notNull().unique().and()
        .withAttribute().integer("age").notNull().and()
        .primaryKey("pk_test_one").using("id").and()
        .build();
    schema.addSequence("seq_test_one").build();
    schema.addRelation("test_two")
        .withAttribute().longAttr("id").withAdditionalModifiers("AUTO_INCREMENT").notNull().and()
        .withAttribute().longAttr("fk_test_one").notNull().and()
        .foreignKey("fk_test_one_id").references("test_one", "id").on("fk_test_one").and()
        .build();
    schema.addSequence("seq_test_two").build();

    schemaManager.create(schema);
} finally {
    polyjdbc.close(schemaManager);
}
```

You don't need to define `Schema` object. Single `Relation`, `Sequence` or `Index` can be
created using `SchemaManager` as well.

## License

PolyJDBC is published under [Apache License 2.0](http://www.apache.org/licenses/LICENSE-2.0).

## Changelog

* **0.5.0**
    * added possibility to plug in external framework for transaction management
    * [API change] PolyJDBC is build using PolyJDBCBuilder
* **0.4.0** (24.08.2014)
    * fixed bug with closing transaction on exception in query runners
    * [API change] QueryRunner.close() does not commit, use QueryRunner.commit() explicitly
    * [API change] some queries from QueryFactory.* now need Dialect (better Oracle support in future), use dialect-aware PolyJDBC.query() 
* **previous versions**
    * support for PostgreSQL, MySQL, H2 and partial support for Oracle