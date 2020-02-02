@file:JvmName("Routes")
@file:JvmMultifileClass

package org.recyclica.backend.route

import io.ktor.application.call
import io.ktor.response.respond
import io.ktor.routing.Routing
import io.ktor.routing.post
import org.jetbrains.exposed.sql.SizedCollection
import org.jetbrains.exposed.sql.lowerCase
import org.jetbrains.exposed.sql.or
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.recyclica.backend.*
import org.recyclica.backend.helper.checkField
import org.recyclica.backend.helper.withRequest

fun Routing.register() {
    post(ROUTE_REGISTER) {
        call.withRequest<RequestRegister> { request ->
            if (call.checkField(RecyclicaField.USERNAME, request.username) &&
                call.checkField(RecyclicaField.FULLNAME, request.fullname) &&
                call.checkField(RecyclicaField.PASSWORD, request.password)
            ) {
                val hasEmail = request.email != null
                val hasPhone = request.phone != null
                if (hasEmail || hasPhone) {
                    if ((!hasEmail || call.checkField(RecyclicaField.EMAIL, request.email)) &&
                        (!hasPhone || call.checkField(RecyclicaField.PHONE, request.phone))
                    ) {
                        val usernameLower = request.username.toLowerCase()
                        val emailLower = request.email?.toLowerCase()
                        val existingUser = transaction {
                            User.find {
                                var q = (Users.username.lowerCase() eq usernameLower)
                                if (hasEmail)
                                    q = q or (Users.email.lowerCase() eq emailLower)
                                if (hasPhone)
                                    q = q or (Users.phone.lowerCase() eq request.phone)
                                q
                            }.limit(1).firstOrNull()
                        }
                        if (existingUser != null) {
                            when {
                                existingUser.username.toLowerCase() == usernameLower -> call.respond(responseError("existing_username"))
                                existingUser.email?.toLowerCase() == emailLower -> call.respond(responseError("existing_email"))
                                existingUser.phone == request.phone -> call.respond(responseError("existing_phone"))
                            }
                        } else {
                            val salt = PasswordHasher.generateScryptSalt()
                            val password = PasswordHasher.scrypt(request.password, salt)
                            val newUser = transaction {
                                User.new {
                                    username = request.username
                                    fullname = request.fullname
                                    passwordScrypt = password
                                    passwordSalt = salt
                                    registeredOn = DateTime.now(DateTimeZone.UTC)
                                    email = request.email
                                    phone = request.phone
                                }
                            }
                            transaction {
                                newUser.groups = SizedCollection(GroupManager.DEFAULT) // can't be done in one transaction because чё за хуйня
                            }
                            call.respond(responseOf())
                        }
                    }
                } else
                    call.respond(responseError("no_phone_or_email"))
            }
        }
    }
}