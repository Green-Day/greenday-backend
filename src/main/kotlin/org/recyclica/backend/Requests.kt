package org.recyclica.backend

import org.jetbrains.exposed.sql.Column

class RequestRegister(
    val username: String,
    val fullname: String,
    val password: String,
    val email: String?,
    val phone: String?
)

class RequestLogin(
    val loginType: Type,
    val loginString: String,
    val password: String
) {
    enum class Type(val value: String, val column: Column<in String>, val field: RecyclicaField) {
        PHONE("phone", Users.phone, RecyclicaField.PHONE),
        EMAIL("email", Users.email, RecyclicaField.EMAIL),
        USERNAME("username", Users.username, RecyclicaField.USERNAME) }
}

open class RequestToken {
    lateinit var token: String
}

class RequestGetUserFields(
    val fields: List<String>
) : RequestToken()