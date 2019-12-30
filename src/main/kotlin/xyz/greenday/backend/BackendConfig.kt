package xyz.greenday.backend

import io.ktor.application.Application
import java.io.File
import kotlin.reflect.KClass
import kotlin.reflect.full.createType
import kotlin.reflect.full.primaryConstructor

data class BackendConfig(
    val database: Database,
    val authentication: Authentication
) {
    data class Database(
        val jdbcString: String,
        val jdbcDriver: String,
        val user: String,
        val password: String
    )

    data class Authentication(
        val userTokenExpirationHours: Int,
        val userTokenIrrevocablyExpirationHours: Int,
        val maxUserTokens: Int
    )
}

private const val ROOT_CONFIG_KEY = "greenday"

fun Application.readConfig(): BackendConfig {
    val typeInt = Int::class.createType()
    val typeString = String::class.createType()
    val typeFile = File::class.createType()
    val params = mutableListOf<Any?>()
    val primary = BackendConfig::class.primaryConstructor!!
    val cfg = environment.config.config(ROOT_CONFIG_KEY)
    for (param in primary.parameters) {
        val cCfg = cfg.config(param.name!!)
        val cParams = mutableListOf<Any?>()
        val cPrimary = (param.type.classifier as KClass<*>).primaryConstructor!!
        for (cParam in cPrimary.parameters) {
            val cfgString = cCfg.property(cParam.name!!).getString()
            cParams.add(
                when (cParam.type) {
                    typeInt -> cfgString.toInt()
                    typeString -> cfgString
                    typeFile -> File(cfgString)
                    else -> error("Invalid type!")
                }
            )
        }
        params.add(cPrimary.call(*cParams.toTypedArray()))
    }
    return primary.call(*params.toTypedArray())
}