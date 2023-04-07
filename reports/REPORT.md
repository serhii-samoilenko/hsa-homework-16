# Logs demo report

Creating table and inserting data

```sql
CREATE TABLE IF NOT EXISTS data (
  id INT AUTO_INCREMENT PRIMARY KEY,
  col1 VARCHAR(255),
  col2 VARCHAR(255),
  col3 VARCHAR(255)
);
```

Expected 100000 rows, got 10000

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
LIMIT 100000;
```

Table contains 100000 rows

Will use random queries of:

```sql
SELECT count(*) FROM data t1 JOIN data t2 ON t1.id = t2.id - 1 WHERE t1.col1 LIKE '%col1_' || ROUND(RAND() * 100) || '%' AND t2.col2 LIKE '%col2_' || ROUND(RAND() * 100) || '%';
SELECT col1 FROM data WHERE col3 = 'col3_' || ROUND(RAND() * 100);
SELECT col1 FROM data WHERE id = ROUND(RAND() * 10000);
```

Will run queries for 30s

### Running queries with 10-second slow query log threshold

```sql
SET GLOBAL long_query_time = 10;
```

Run `1`, result: `466 ops in 30.505s - 15.3 ops/sec`

Run `2`, result: `470 ops in 30.295s - 15.5 ops/sec`

Run `3`, result: `465 ops in 30.469s - 15.3 ops/sec`

### Running queries with 1-second slow query log threshold

```sql
SET GLOBAL long_query_time = 1;
```

Run `1`, result: `472 ops in 30.384s - 15.5 ops/sec`

Run `2`, result: `473 ops in 30.506s - 15.5 ops/sec`

Run `3`, result: `454 ops in 30.391s - 14.9 ops/sec`

### Running queries with 0-second slow query log threshold

```sql
SET GLOBAL long_query_time = 0;
```

Run `1`, result: `420 ops in 30.342s - 13.8 ops/sec`

Run `2`, result: `443 ops in 30.326s - 14.6 ops/sec`

Run `3`, result: `428 ops in 30.318s - 14.1 ops/sec`

