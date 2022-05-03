package com.vadmark223.service

import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

object DatabaseFactory {
    fun connect() {
        Database.connect(
            url = "jdbc:postgresql://localhost:5432/vmm",
            driver = "org.postgresql.Driver",
            user = "postgres",
            password = "postgres"
        )
    }

    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }
}