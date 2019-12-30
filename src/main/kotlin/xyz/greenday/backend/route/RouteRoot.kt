@file:JvmName("Routes")
@file:JvmMultifileClass

package xyz.greenday.backend.route

import io.ktor.application.call
import io.ktor.response.respondText
import io.ktor.routing.Routing
import io.ktor.routing.get
import xyz.greenday.backend.ROUTE_ROOT

fun Routing.root() {
    get(ROUTE_ROOT) {
        call.respondText("meow")
    }
}