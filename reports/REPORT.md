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

Table contains 100000 rows

Will use random queries of:

```sql
SELECT col1 FROM data WHERE col1 = 'col1_1';
SELECT col1 FROM data WHERE col2 = 'col2_2';
SELECT col1 FROM data WHERE col3 = 'col3_3';
```

Will run queries for 30s

### Running queries with 10-second slow query log threshold

```sql
SET GLOBAL long_query_time = 10;
```

Run `1`, result: `1571 ops in 30.137s - 52.1 ops/sec`

Run `2`, result: `1550 ops in 30.225s - 51.3 ops/sec`

Run `3`, result: `1644 ops in 30.162s - 54.5 ops/sec`

### Running queries with 1-second slow query log threshold

```sql
SET GLOBAL long_query_time = 1;
```

Run `1`, result: `1330 ops in 30.263s - 43.9 ops/sec`

Run `2`, result: `1222 ops in 30.211s - 40.4 ops/sec`

Run `3`, result: `1431 ops in 30.178s - 47.4 ops/sec`

### Running queries with 0-second slow query log threshold

```sql
SET GLOBAL long_query_time = 0;
```

Run `1`, result: `362 ops in 40.057s - 9.04 ops/sec`

Run `2`, result: `24 ops in 30.037s - 0.799 ops/sec`

Run `3`, result: `24 ops in 30.004s - 0.8 ops/sec`

