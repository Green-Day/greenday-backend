package org.recyclica.backend.helper

import com.fasterxml.jackson.core.JsonProcessingException
import io.ktor.application.ApplicationCall
import io.ktor.http.HttpStatusCode
import io.ktor.request.httpMethod
import io.ktor.request.receive
import io.ktor.request.uri
import io.ktor.response.respond
import org.jetbrains.exposed.dao.id.EntityID
import org.recyclica.backend.*

suspend inline fun <reified T : Any> ApplicationCall.tryReceive(): T? {
    try {
        return receive()
    } catch (e: JsonProcessingException) {
        application.environment.log.error("Bad Request: ${request.httpMethod.value} - ${request.uri}", e)
        respond(HttpStatusCode.BadRequest)
    }
    return null
}

suspend inline fun <reified T : Any> ApplicationCall.withRequest(block: (request: T) -> Unit) =
    tryReceive<T>()?.let(block)

suspend inline fun <reified T : RequestToken> ApplicationCall.withToken(block: (request: T, userId: EntityID<Int>) -> Unit) {
    withRequest<T> { request ->
        if (RecyclicaField.TOKEN.isValid(request.token)) {
            try {
                block(request, Authenticator.getTokenUser(request.token))
            } catch (e: InvalidTokenException) {
                respond(responseError("invalid_token"))
            } catch (e: ExpiredTokenException) {
                respond(responseError("expired_token"))
            }
        } else
            respond(responseError("invalid_token"))
    }
}

suspend fun ApplicationCall.checkField(field: RecyclicaField, input: String?): Boolean {
    val result = field.checkString(input)
    return if (result == null)
        true
    else {
        respond(responseError(result))
        false
    }
}
