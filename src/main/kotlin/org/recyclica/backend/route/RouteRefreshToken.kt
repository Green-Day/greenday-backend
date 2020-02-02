@file:JvmName("Routes")
@file:JvmMultifileClass

package org.recyclica.backend.route

import io.ktor.application.*
import io.ktor.response.respond
import io.ktor.routing.*
import org.recyclica.backend.*
import org.recyclica.backend.helper.withRequest

fun Routing.refreshToken() {
    post(ROUTE_REFRESH_TOKEN) {
        call.withRequest<RequestToken> { request ->
            val result = Authenticator.refreshToken(request.token)
            if (result == null)
                call.respond(responseError("invalid_token"))
            else
                call.respond(responseOf(result))
        }
    }
}