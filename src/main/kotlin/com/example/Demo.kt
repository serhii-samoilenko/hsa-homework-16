package com.example

import com.example.util.ConnectionPool
import com.example.util.Database
import com.example.util.Report
import java.lang.Math.round
import java.util.concurrent.Executors
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit.SECONDS
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

/**
 * Logs demo
 */
fun runDemo() {
    val r = Report("REPORT.md")
    r.h1("Logs demo report")
    val concurrency = 10
    val db = Database(ConnectionPool("jdbc:mysql://localhost:3306/test", "root", "root", concurrency))
    r.text("Creating table and inserting data")
    db.execute(
        """
        CREATE TABLE IF NOT EXISTS data (
          id INT AUTO_INCREMENT PRIMARY KEY,
          col1 VARCHAR(255),
          col2 VARCHAR(255),
          col3 VARCHAR(255)
        )
        """.trimIndent(),
        report = r,
    )
    val desiredCount = 100000L

    var count = db.querySingleValue("SELECT COUNT(*) FROM data") as Long
    if (count != desiredCount) {
        r.text("Expected $desiredCount rows, got $count")
        db.execute(
            "TRUNCATE TABLE data",
            "SET @row_number = 0",
            """
            INSERT INTO data (col1, col2, col3)
            SELECT
                CONCAT('col1_', t1.num),
                CONCAT('col2_', t2.num),
                CONCAT('col3_', t3.num)
            FROM
                (SELECT @row_number := @row_number + 1 AS num FROM information_schema.tables) t1,
                (SELECT @row_number := @row_number + 1 AS num FROM information_schema.tables) t2,
                (SELECT @row_number := @row_number + 1 AS num FROM information_schema.tables) t3
            LIMIT $desiredCount
            """.trimIndent(),
            report = r,
        )
    }
    count = db.querySingleValue("SELECT COUNT(*) FROM data") as Long
    r.text("Table contains $count rows")

    val queries = listOf(
//        "SELECT col1 FROM data WHERE col1 = 'col1_1'",
//        "SELECT col1 FROM data WHERE col2 = 'col2_2'",
        "SELECT count(*) FROM data t1 JOIN data t2 ON t1.id = t2.id - 1 WHERE t1.col1 LIKE '%col1_' || ROUND(RAND() * 100) || '%' " +
            "AND t2.col2 LIKE '%col2_' || ROUND(RAND() * 100) || '%'",
        "SELECT col1 FROM data WHERE col3 = 'col3_' || ROUND(RAND() * 100)",
        "SELECT col1 FROM data WHERE id = ROUND(RAND() * 10000)",
    )

    val duration = 30.seconds

    r.text("Will use random queries of:")
    r.sql(queries)
    r.text("Will run queries for $duration")

    fun runBenchmark(repeats: Int) {
        for (i in 1..repeats) {
            benchmark(duration, concurrency) {
                Runnable { db.query(queries.random()) }
            }.also {
                r.text("Run `$i`, result: `$it`")
            }
        }
    }

    r.h3("Running queries with 10-second slow query log threshold")
    db.execute("SET GLOBAL long_query_time = 10", report = r)
    runBenchmark(3)

    r.h3("Running queries with 1-second slow query log threshold")
    db.execute("SET GLOBAL long_query_time = 1", report = r)
    runBenchmark(3)

    r.h3("Running queries with 0-second slow query log threshold")
    db.execute("SET GLOBAL long_query_time = 0", report = r)
    runBenchmark(3)

    r.writeToFile()
}

data class Result(
    val count: Long,
    val duration: Duration,
) {
    fun opsPerSecond(): Double {
        val ops = count * 1000.0 / duration.inWholeMilliseconds
        return when {
            ops > 10 -> round(10.0 * ops) / 10.0
            ops > 1 -> round(100.0 * ops) / 100.0
            else -> round(1000.0 * count * 1000.0 / duration.inWholeMilliseconds) / 1000.0
        }
    }

    override fun toString() = "$count ops in $duration - ${opsPerSecond()} ops/sec"
}

fun benchmark(
    duration: Duration,
    concurrency: Int,
    taskSupplier: () -> Runnable,
): Result {
    val startTime = System.currentTimeMillis()
    val endTime = startTime + duration.inWholeMilliseconds
    val executor = Executors.newFixedThreadPool(concurrency) as ThreadPoolExecutor

    while (System.currentTimeMillis() < endTime) {
        val totalCount = executor.activeCount + executor.queue.size
        val capacity = concurrency - totalCount - 1
        for (i in 0 until capacity) {
            executor.submit(taskSupplier())
        }
    }
    executor.shutdown()
    executor.awaitTermination(10, SECONDS)
    val actuaTime = System.currentTimeMillis() - startTime
    return Result(executor.completedTaskCount, actuaTime.milliseconds)
}
