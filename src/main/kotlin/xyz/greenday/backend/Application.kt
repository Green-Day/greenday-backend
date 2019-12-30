@file:JvmName("Application")

package xyz.greenday.backend

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.datatype.joda.JodaModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.jackson.jackson
import io.ktor.routing.*
import io.ktor.server.netty.EngineMain
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.*
import org.slf4j.event.Level
import xyz.greenday.backend.route.root

fun main(args: Array<String>) {
    EngineMain.main(args)
}

lateinit var Config: BackendConfig private set


@Suppress("unused") // application.conf
fun Application.module() {
    Config = readConfig()
    Database.connect(
        Config.database.jdbcString,
        user = Config.database.user,
        password = Config.database.password,
        driver = Config.database.jdbcDriver
    )

    transaction {
//        SchemaUtils.create(Users) - todo
    }

    install(XForwardedHeaderSupport)

    install(CallLogging) {
        level = Level.INFO
        format { call ->
            when (val status = call.response.status() ?: "Unhandled") {
                HttpStatusCode.Found -> "[${call.request.origin.remoteHost}] $status: ${call.request.toLogString()} -> ${call.response.headers[HttpHeaders.Location]}"
                else -> "[${call.request.origin.remoteHost}] $status: ${call.request.toLogString()}"
            }
        }
    }

    install(ContentNegotiation) {
        jackson {
            setSerializationInclusion(JsonInclude.Include.NON_NULL)
            enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
            registerModule(KotlinModule())
            registerModule(JodaModule())
        }
    }

    routing {
        root()
    }
}