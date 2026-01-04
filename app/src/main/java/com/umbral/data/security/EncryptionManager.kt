package com.umbral.data.security

import android.content.Context
import android.util.Base64
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.Mac
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec
import javax.inject.Inject
import javax.inject.Singleton

interface EncryptionManager {
    fun encrypt(plainText: String): String
    fun decrypt(cipherText: String): String
    fun hmac(data: String): String
}

@Singleton
class EncryptionManagerImpl @Inject constructor(
    private val context: Context
) : EncryptionManager {

    private val masterKey by lazy {
        MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
    }

    private val sharedPrefs by lazy {
        EncryptedSharedPreferences.create(
            context,
            "umbral_qr_keys",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    private val secretKey: SecretKey by lazy {
        getOrCreateSecretKey()
    }

    override fun encrypt(plainText: String): String {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")

        // Generate random IV for each encryption
        val iv = ByteArray(12)
        SecureRandom().nextBytes(iv)

        val spec = GCMParameterSpec(128, iv)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, spec)

        val encrypted = cipher.doFinal(plainText.toByteArray(Charsets.UTF_8))

        // Combine IV + encrypted data
        val combined = iv + encrypted
        return Base64.encodeToString(combined, Base64.URL_SAFE or Base64.NO_WRAP)
    }

    override fun decrypt(cipherText: String): String {
        val combined = Base64.decode(cipherText, Base64.URL_SAFE or Base64.NO_WRAP)

        // Extract IV and encrypted data
        val iv = combined.sliceArray(0 until 12)
        val encrypted = combined.sliceArray(12 until combined.size)

        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        val spec = GCMParameterSpec(128, iv)
        cipher.init(Cipher.DECRYPT_MODE, secretKey, spec)

        val decrypted = cipher.doFinal(encrypted)
        return String(decrypted, Charsets.UTF_8)
    }

    override fun hmac(data: String): String {
        val mac = Mac.getInstance("HmacSHA256")
        mac.init(secretKey)
        val hash = mac.doFinal(data.toByteArray(Charsets.UTF_8))
        return Base64.encodeToString(hash, Base64.URL_SAFE or Base64.NO_WRAP)
    }

    private fun getOrCreateSecretKey(): SecretKey {
        val existingKey = sharedPrefs.getString("qr_secret_key", null)

        return if (existingKey != null) {
            val keyBytes = Base64.decode(existingKey, Base64.DEFAULT)
            SecretKeySpec(keyBytes, "AES")
        } else {
            val keyGenerator = KeyGenerator.getInstance("AES")
            keyGenerator.init(256)
            val newKey = keyGenerator.generateKey()

            sharedPrefs.edit()
                .putString(
                    "qr_secret_key",
                    Base64.encodeToString(newKey.encoded, Base64.DEFAULT)
                )
                .apply()

            newKey
        }
    }
}
