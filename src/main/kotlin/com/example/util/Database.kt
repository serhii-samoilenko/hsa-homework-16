package com.example.util

import java.sql.ResultSet.CONCUR_READ_ONLY
import java.sql.ResultSet.TYPE_FORWARD_ONLY
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

class Database(private val pool: ConnectionPool) {
    fun execute(vararg sql: String, report: Report? = null): Duration = execute(sql.toList(), report)

    fun execute(sql: List<String>, report: Report? = null): Duration {
        report?.sql(sql.toList())
        val start = System.currentTimeMillis()
        pool.connection().use { connection ->
            sql.forEach {
                connection.createStatement().executeUpdate(it)
            }
        }
        val end = System.currentTimeMillis()
        return (end - start).milliseconds
    }

//    fun execute(sql: String, r: Report? = null): Duration = execute(listOf(sql), r)

    fun tryExecute(sql: String, report: Report? = null) = try {
        execute(listOf(sql), report)
    } catch (e: Exception) {
        println("Ignoring exception: ${e.message}")
        null
    }

    fun queryData(sql: String): List<Map<String, Any>> {
        pool.connection().use { connection ->
            connection.createStatement().executeQuery(sql).use { rs ->
                val columns = (1..rs.metaData.columnCount).map { rs.metaData.getColumnName(it) }
                val rows = mutableListOf<Map<String, Any>>()
                while (rs.next()) {
                    rows.add(columns.associateWith { rs.getObject(it) })
                }
                rs.close()
                return rows
            }
        }
    }

    fun query(sql: String) {
        pool.connection().use { connection ->
            connection
                .createStatement(TYPE_FORWARD_ONLY, CONCUR_READ_ONLY)
                .apply { fetchSize = 2000 }
                .executeQuery(sql).use { rs ->
                    while (rs.next()) {
                        // do nothing
                    }
                    rs.close()
                }
        }
    }

    fun querySingleValue(sql: String): Any = queryData(sql).first().values.first()

    fun queryRowValues(sql: String): Collection<Any> = queryData(sql).map { it.values.first() }
}
