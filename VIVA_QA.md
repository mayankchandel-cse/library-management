# Viva Questions & Answers — Library Management System (Spring Boot + JDBC)

Written in plain, simple student English.

## 1. What is the difference between JDBC and JdbcTemplate?

JDBC is the basic Java API for talking to a database. With plain JDBC we
have to write a lot of repeated code: open a connection, make a statement,
run the query, read the result, close everything, and handle errors.
`JdbcTemplate` is a Spring class built on top of JDBC. It does all that
repeated work for us — it opens and closes the connection, runs the query,
and turns the result into Java objects. So we only write the SQL and the
row mapping, and the code is much shorter and safer.

## 2. What is the difference between PreparedStatement and Statement? What is SQL injection?

A `Statement` runs SQL as a plain string, so if user input is joined into
the string, a user can type SQL code and change the query. That attack is
called SQL injection. For example, typing `' OR '1'='1` in a search box
could make the query return all rows. A `PreparedStatement` uses `?`
placeholders, like `SELECT * FROM books WHERE title LIKE ?`, and the
values are sent separately. The database treats the value as data only,
never as SQL code, so injection is not possible. In this project
`JdbcTemplate` uses prepared statements for us, for example in the book
search and in issue and return.

## 3. What is a ResultSet?

A `ResultSet` is the table of rows that comes back from a `SELECT` query.
We move through it row by row with `next()`, and we read each column with
methods like `getInt("id")`, `getString("title")` and `getDate("due_date")`.
In this project we do not read a `ResultSet` by hand in most places:
`JdbcTemplate` gives each row to a `RowMapper`, and the mapper reads the
`ResultSet` and builds a `Book`, `Member` or `IssueRecord` object from it.

## 4. What is a DataSource and connection pooling? What is HikariCP?

A `DataSource` is an object that gives us database connections. Opening a
new connection every time is slow, so a connection pool keeps some
connections ready and gives them out again and again. That is called
connection pooling. Spring Boot uses HikariCP as its default pool, so we
do not need to set it up ourselves. We only write the database URL,
username and password in `application.properties`, and Spring Boot makes
the `DataSource` with a HikariCP pool behind it.

## 5. What does @SpringBootApplication do?

`@SpringBootApplication` is put on the main class, `LibraryApplication`.
It is three annotations in one: `@Configuration` (this class can define
beans), `@EnableAutoConfiguration` (Spring Boot sets up things like the
`DataSource` and `JdbcTemplate` for us from the settings), and
`@ComponentScan` (Spring looks for our classes marked `@Service`,
`@Repository` and `@Component` and creates them as beans). So one
annotation starts the whole setup.

## 6. What are @Configuration and @Bean?

`@Configuration` marks a class as a source of bean definitions — it tells
Spring "this class explains how to build some objects". A method marked
`@Bean` inside such a class creates one object and gives it to the Spring
container, so other classes can use it. In this project we mostly do not
need our own `@Bean` methods, because Spring Boot auto-configuration
already creates beans like `DataSource` and `JdbcTemplate`, and our own
classes are found by component scanning. But if we needed a special object,
we would write a `@Bean` method for it.

## 7. What are @Repository, @Service and @Component?

These are all Spring annotations that mark a class so Spring creates it
as a bean and we can inject it where it is needed.
`@Component` is the general one, used for a simple helper class — in this
project `FineCalculator` is a `@Component`.
`@Service` marks the business-logic layer — `LibraryService` has the
rules for issuing, returning, fines and statistics.
`@Repository` marks the database layer — `BookRepository`,
`MemberRepository` and `IssueRepository` run the SQL. Spring also turns
database errors from a `@Repository` into Spring's own exceptions.

## 8. What is @Autowired and dependency injection?

Dependency injection means a class does not create the objects it needs
with `new` — Spring gives them to it instead. `@Autowired` tells Spring
to do that: for example, `LibraryService` has a constructor that takes
the repositories and `FineCalculator`, and Spring passes those beans in
automatically. This makes the code easy to test and to change, because
the service does not depend on how the repositories are built. The GUI
works the same way: `LibraryApplication` gets the `LibraryService` bean
from the Spring context and passes it to `MainFrame`, instead of the GUI
doing `new LibraryService()`.

## 9. What is application.properties?

`application.properties` is the settings file of a Spring Boot project.
It holds values that can change without changing code, like the database
URL (`spring.datasource.url`), the database username and password, and
`spring.sql.init.mode=always`, which tells Spring Boot to run `schema.sql`
and `data.sql` on startup. Spring Boot reads this file when the
application starts and uses the values to set up the `DataSource` and
other beans.

## 10. What are schema.sql and data.sql? How does the database get created?

`schema.sql` holds the `CREATE TABLE` statements for the `books`,
`members` and `issues` tables, and `data.sql` holds `INSERT` statements
with sample books, members and issues. Because
`spring.sql.init.mode=always` is set in `application.properties`, Spring
Boot runs both files every time the application starts. So we only have
to create the empty database `library_db` and the `library` user once;
the tables and sample data are made automatically after that.

## 11. What are transactions? Why do they matter here?

A transaction is a group of database steps that must all succeed or all
fail together. If one step fails, the whole group is rolled back, so the
database is never left half-updated. This matters when we issue a book:
we insert a row in `issues` and we also reduce the book's available
copies. Both must happen together — if the insert worked but the copy
count did not change, the data would be wrong. So `issueBook` and
`returnBook` in `LibraryService` are marked `@Transactional`.

## 12. Why does the Swing GUI get the service from the Spring context?

The GUI is only the screen — it should not know how to connect to the
database or build a service. In `LibraryApplication.main`, Spring Boot
starts first (with no web server), creates all the beans, and then we
ask the context for the `LibraryService` bean with
`context.getBean(LibraryService.class)` and pass it to `MainFrame`,
which passes it to each panel. That way the panels use the same service
Spring built, with the real repositories and database connection inside
it. If the GUI did `new LibraryService()`, the repositories inside it
would be empty and nothing would work.
