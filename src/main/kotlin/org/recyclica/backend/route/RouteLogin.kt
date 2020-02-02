@file:JvmName("Routes")
@file:JvmMultifileClass

package org.recyclica.backend.route

import io.ktor.application.*
import io.ktor.response.respond
import io.ktor.routing.*
import org.jetbrains.exposed.sql.transactions.*
import org.recyclica.backend.*
import org.recyclica.backend.helper.withRequest

fun Routing.login() {
    post(ROUTE_LOGIN) {
        call.withRequest<RequestLogin> { request ->
            if (RecyclicaField.PASSWORD.isValidLength(request.password) && request.loginType.field.isValidLength(request.loginString)) {
                val user = transaction { User.find { request.loginType.column eq request.loginString }.firstOrNull() }
                if (user != null) {
                    val scrypt = PasswordHasher.scrypt(request.password, user.passwordSalt)
                    if(user.passwordScrypt.contentEquals(scrypt)) {
                        call.respond(responseOf(Authenticator.newToken(user)))
                        return@post
                    }
                }
            }
            call.respond(responseError("invalid_credentials"))
        }
    }
}