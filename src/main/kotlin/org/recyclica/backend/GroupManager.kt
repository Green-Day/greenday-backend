package org.recyclica.backend

import org.jetbrains.exposed.sql.transactions.transaction

object GroupManager {
    lateinit var DEFAULT: Group private set

    fun setup() {
        transaction {
            DEFAULT = Group.findById(0) ?: Group.new(0) {}
        }
    }
}