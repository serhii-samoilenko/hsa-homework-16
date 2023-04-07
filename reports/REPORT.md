# Logs demo report

Creating table and inserting data

```sql
CREATE TABLE IF NOT EXISTS data (
  id INT AUTO_INCREMENT PRIMARY KEY,
  col1 VARCHAR(255),
  col2 VARCHAR(255),
  col3 VARCHAR(255)
)
```

Expected 50000 rows, got 0

```sql
TRUNCATE TABLE data;
SET @row_number = 0;
INSERT INTO data (col1, col2, col3)
SELECT
    CONCAT('col1_', t1.num),
    CONCAT('col2_', t2.num),
    CONCAT('col3_', t3.num)
FROM
    (SELECT @row_number := @row_number + 1 AS num FROM information_schema.tables) t1,
    (SELECT @row_number := @row_number + 1 AS num FROM information_schema.tables) t2,
    (SELECT @row_number := @row_number + 1 AS num FROM information_schema.tables) t3
LIMIT 50000;
```

Table contains 50000 rows

Will use query:

```sql
SELECT count(*)
FROM data t1
JOIN data t2 ON t1.id = t2.id - 1
JOIN data t3 ON t1.id = t3.id - 2
WHERE t1.col1 LIKE '%col1_%' AND t2.col2 LIKE '%col2_%' AND t3.col3 LIKE '%col3_%'
```

Will run queries for 30s

### Running queries with 10-second slow query log threshold

```sql
SET GLOBAL long_query_time = 10
```

Run `1`, Result: `7.08` queries per second

Run `2`, Result: `6.72` queries per second

Run `3`, Result: `7.23` queries per second

### Running queries with 1-second slow query log threshold

```sql
SET GLOBAL long_query_time = 1
```

Run `1`, Result: `6.76` queries per second

Run `2`, Result: `6.54` queries per second

Run `3`, Result: `7.53` queries per second

### Running queries with 0-second slow query log threshold

```sql
SET GLOBAL long_query_time = 0
```

Run `1`, Result: `5.97` queries per second

Run `2`, Result: `6.44` queries per second

Run `3`, Result: `6.48` queries per second

