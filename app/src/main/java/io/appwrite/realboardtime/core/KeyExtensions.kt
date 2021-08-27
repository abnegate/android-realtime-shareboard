package io.appwrite.realboardtime.core

import android.util.Base64
import io.appwrite.realboardtime.model.PasswordHash
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom
import java.security.spec.InvalidKeySpecException
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

@Throws(NoSuchAlgorithmException::class, InvalidKeySpecException::class)
fun generateKey(password: String, salt: ByteArray): SecretKey {
    val iterations = 100000
    val outputKeyLength = 512
    val secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512")
    val keySpec = PBEKeySpec(password.toCharArray(), salt, iterations, outputKeyLength)

    return secretKeyFactory.generateSecret(keySpec)
}

@Throws(NoSuchAlgorithmException::class)
fun generateKey(): SecretKey {
    val outputKeyLength = 512
    val secureRandom = SecureRandom()
    val keyGenerator = KeyGenerator.getInstance("AES").apply {
        init(outputKeyLength, secureRandom)
    }

    return keyGenerator.generateKey()
}

fun String.generateKeys(salt: ByteArray? = null): PasswordHash {
    val saltBytes = salt ?: generateKey().encoded

    return PasswordHash(
        Base64.encodeToString(generateKey(this, saltBytes).encoded, Base64.DEFAULT),
        Base64.encodeToString(saltBytes, Base64.DEFAULT)
    )
}