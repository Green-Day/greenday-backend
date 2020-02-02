package org.recyclica.backend

import com.lambdaworks.crypto.SCrypt
import java.security.SecureRandom

object PasswordHasher {
    private const val SCRYPT_N = 16384
    private const val SCRYPT_R = 8
    private const val SCRYPT_P = 1
    private const val SCRYPT_DK = 64
    private val random = SecureRandom()

    fun generateScryptSalt() = ByteArray(8).also { random.nextBytes(it) }

    fun scrypt(password: String, salt: ByteArray) =
        SCrypt.scrypt(password.toByteArray(), salt, SCRYPT_N, SCRYPT_R, SCRYPT_P, SCRYPT_DK)
}