package org.recyclica.backend

import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.recyclica.backend.UserTokens.updatedOn
import org.recyclica.backend.UserTokens.user
import org.recyclica.backend.helper.nextString
import java.security.SecureRandom

object Authenticator {
    private val random = SecureRandom()

    private fun SqlExpressionBuilder.updatedOnNotExpired(hours: Int) =
        updatedOn greaterEq DateTime.now(DateTimeZone.UTC).minusHours(hours)

    private fun DateTime.expired(hours: Int) = this < DateTime.now(DateTimeZone.UTC).minusHours(hours)

    fun generateToken() = random.nextString(32)

    fun newToken(userIn: User): String {
        val generatedToken = generateToken()
        transaction {
            UserToken.new {
                token = generatedToken
                updateOn = DateTime.now(DateTimeZone.UTC)
                user = userIn
            }
            UserTokens.deleteWhere {
                UserTokens.id inList UserTokens
                    .slice(UserTokens.id)
                    .select { user eq userIn.id }
                    .orderBy(updatedOn, SortOrder.DESC)
                    .limit(100, offset = Config.authentication.maxUserTokens)
                    .map { it[UserTokens.id] }
            }
        }
        return generatedToken
    }

    fun refreshToken(userToken: String): String? {
        val newToken = generateToken()
        var result = false
        transaction {
            if (UserTokens.update({ (UserTokens.token eq userToken) and updatedOnNotExpired(Config.authentication.userTokenIrrevocablyExpirationHours) }) {
                    it[token] = newToken
                    it[updatedOn] = DateTime.now(DateTimeZone.UTC)
                } > 0)
                result = true
        }
        return if (result) newToken else null
    }

    fun invalidateToken(userToken: String): Boolean {
        return transaction {
            UserTokens.deleteWhere { UserTokens.token eq userToken } > 0
        }
    }

    fun getTokenUser(userToken: String): EntityID<Int> {
        return transaction {
            val row = UserTokens.select { UserTokens.token eq userToken }.firstOrNull() ?: throw InvalidTokenException
            if (row[updatedOn].expired(Config.authentication.userTokenIrrevocablyExpirationHours))
                throw InvalidTokenException
            if (row[updatedOn].expired(Config.authentication.userTokenExpirationHours))
                throw ExpiredTokenException
            row[user]
        }
    }
}

open class TokenException : Exception()
object ExpiredTokenException : TokenException()
object InvalidTokenException : TokenException()