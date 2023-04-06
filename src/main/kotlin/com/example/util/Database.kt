package com.example.util

import java.sql.Connection
import java.sql.ResultSet.CONCUR_READ_ONLY
import java.sql.ResultSet.TYPE_FORWARD_ONLY
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

class Database(private val pool: ConnectionPool) {
    fun execute(vararg sql: String): Duration {
        val start = System.currentTimeMillis()
        pool.connection().use { connection ->
            sql.forEach {
                connection.createStatement().executeUpdate(it)
            }
        }
        val end = System.currentTimeMillis()
        return (end - start).milliseconds
    }

    fun tryExecute(sql: String) = try {
        execute(sql)
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

    fun querySingleValue(connection: Connection, sql: String): Any = queryData(sql).first().values.first()

    fun queryRowValues(connection: Connection, sql: String): Collection<Any> = queryData(sql).map { it.values.first() }
}
