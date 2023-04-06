package com.example

import com.example.scenarios.dirtyReadScenario
import com.example.scenarios.lostUpdateScenario
import com.example.scenarios.nonRepeatableReadScenario
import com.example.scenarios.phantomReadScenario
import com.example.util.Helper
import com.example.util.Report
import javax.enterprise.context.ApplicationScoped

/**
 * Logs demo
 */
fun runDemo() {
    val r = Report("REPORT.md")
    r.h1("Logs demo report")
    val mysql = SystemActor("mysql", mysqlConnectionPool, r)
    val postgres = SystemActor("postgres", postgresConnectionPool, r)
    r.text("Creating tables and inserting data")
    r.text("In MySQL:")
    mysql.execute(
        """
            CREATE TABLE IF NOT EXISTS accounts (
                id     INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                name   VARCHAR(255),
                amount INT
            )
        """.trimIndent(),
        "TRUNCATE TABLE accounts",
        "INSERT INTO accounts (name, amount) VALUES ('Alice', 0), ('Bob', 0), ('Charlie', 0), ('Dave', 0), ('Eve', 0), ('Frank', 0)",
    )
    r.text("In PostgreSQL:")
    postgres.execute(
        """
            CREATE TABLE IF NOT EXISTS accounts (
                id     SERIAL PRIMARY KEY,
                name   VARCHAR(255),
                amount INT
            )
        """.trimIndent(),
        "TRUNCATE TABLE accounts",
        "INSERT INTO accounts (name, amount) VALUES ('Alice', 0), ('Bob', 0), ('Charlie', 0), ('Dave', 0), ('Eve', 0), ('Frank', 0)",
    )

    r.h3("[Lost update scenario](LOST_UPDATE.md)")
    r.todo()
    r.h3("[Dirty read scenario](DIRTY_READ.md)")
    r.todo()
    r.h3("[Non-repeatable read scenario](NON_REPEATABLE_READ.md)")
    r.todo()
    r.h3("[Phantom read scenario](PHANTOM_READ.md)")
    r.todo()
    r.writeToFile()

    var scenarioReport = Report("LOST_UPDATE.md")
    lostUpdateScenario(helper, scenarioReport)
    scenarioReport.writeToFile()

    scenarioReport = Report("DIRTY_READ.md")
    dirtyReadScenario(helper, scenarioReport)
    scenarioReport.writeToFile()

    scenarioReport = Report("NON_REPEATABLE_READ.md")
    nonRepeatableReadScenario(helper, scenarioReport)
    scenarioReport.writeToFile()

    scenarioReport = Report("PHANTOM_READ.md")
    phantomReadScenario(helper, scenarioReport)
    scenarioReport.writeToFile()
}

@ApplicationScoped
class Demo(private val helper: Helper) {
    fun start() {
        runDemo(helper)
    }
}

private fun Report.todo() {
    h4("MySQL with `READ UNCOMMITTED`:")
    text("TODO")
    h4("PostgreSQL with `READ UNCOMMITTED`:")
    text("TODO")
    h4("MySQL with `READ COMMITTED`:")
    text("TODO")
    h4("PostgreSQL with `READ COMMITTED`:")
    text("TODO")
    h4("MySQL with `REPEATABLE READ`:")
    text("TODO")
    h4("PostgreSQL with `REPEATABLE READ`:")
    text("TODO")
    h4("MySQL with `SERIALIZABLE`:")
    text("TODO")
    h4("PostgreSQL with `SERIALIZABLE`:")
    text("TODO")
}
