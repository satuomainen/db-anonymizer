# Database content anonymizer for small data sets

This is a work-in-progress prototype for a command-line utility to anonymize small and simple databases. For
real-life use cases there is a lot of work to be done.

## Suitable for small and simple databases

* Small = fewer than about 1-5 million rows to be updated.
* Simple = primary keys need to be a single column value.

## Instructions

1. Specify the DB columns to be anonymized in a CSV file (see [doc/example.csv](doc/example.csv)) for the syntax
2. Specify the DB connection parameters as environment variables, for example: 
   ```
   JDBC_URL=jdbc:mysql://127.0.0.1:3306/anonimo
   DB_USERNAME=root
   DB_PASSWORD=password
   ```
3. Build the tool with `mvn package`, which creates `target/dbanonymizer.jar`
4. Run the tool (with the environment variables set)
   ```
   java -jar target/dbanonymizer.jar your_column_specifications.csv
   ```

## Anonymization

JavaFaker is used to generate the data that replaces the original data.

These data types are supported (acceptable values in the CSV file Type column):
* FIRST_NAME
* LAST_NAME
* USERNAME
* RANDOM_STRING

## Restrictions

This utility makes no efforts at the moment to ensure that the values  are unique. If the target column has a
unique constraint, the anonymization may fail.

The input file data is not sanitized or escaped in any way, if they contain SQL injections, those will get
through to the target database.
