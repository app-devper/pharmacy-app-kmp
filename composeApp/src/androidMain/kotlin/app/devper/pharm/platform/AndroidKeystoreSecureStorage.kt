package app.devper.pharm.platform

import android.content.Context
import android.content.SharedPreferences
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import app.devper.pharm.common.StorageException
import app.devper.pharm.common.platform.SecureStorage
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

private const val ANDROID_KEYSTORE = "AndroidKeyStore"
private const val KEY_ALIAS = "pharmacy.secure.storage.key"
private const val SECURE_PREFS_NAME = "pharmacy.secure.prefs"
private const val GCM_TAG_BITS = 128
private const val GCM_IV_BYTES = 12
private const val AES_KEY_SIZE_BITS = 256
private const val TRANSFORMATION = "AES/GCM/NoPadding"

class AndroidKeystoreSecureStorage(context: Context) : SecureStorage {

    private val prefs: SharedPreferences =
        context.applicationContext.getSharedPreferences(SECURE_PREFS_NAME, Context.MODE_PRIVATE)

    override fun put(key: String, value: String) {
        try {
            val cipher = Cipher.getInstance(TRANSFORMATION).apply {
                init(Cipher.ENCRYPT_MODE, getOrCreateKey())
            }
            val iv = cipher.iv
            require(iv.size == GCM_IV_BYTES)
            val ciphertext = cipher.doFinal(value.encodeToByteArray())
            val combined = ByteArray(iv.size + ciphertext.size).apply {
                iv.copyInto(this, 0)
                ciphertext.copyInto(this, iv.size)
            }
            prefs.edit().putString(key, Base64.encodeToString(combined, Base64.NO_WRAP)).apply()
        } catch (t: Throwable) {
            throw StorageException("Failed to encrypt secure storage value", cause = t)
        }
    }

    override fun get(key: String): String? {
        val encoded = prefs.getString(key, null) ?: return null
        return try {
            val combined = Base64.decode(encoded, Base64.NO_WRAP)
            val iv = combined.copyOfRange(0, GCM_IV_BYTES)
            val ciphertext = combined.copyOfRange(GCM_IV_BYTES, combined.size)
            val cipher = Cipher.getInstance(TRANSFORMATION).apply {
                init(Cipher.DECRYPT_MODE, getOrCreateKey(), GCMParameterSpec(GCM_TAG_BITS, iv))
            }
            cipher.doFinal(ciphertext).decodeToString()
        } catch (_: Throwable) {
            prefs.edit().remove(key).apply()
            null
        }
    }

    override fun remove(key: String) {
        prefs.edit().remove(key).apply()
    }

    private fun getOrCreateKey(): SecretKey {
        val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }
        val existing = keyStore.getKey(KEY_ALIAS, null) as? SecretKey
        if (existing != null) return existing
        val generator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEYSTORE)
        generator.init(
            KeyGenParameterSpec.Builder(
                KEY_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT,
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setKeySize(AES_KEY_SIZE_BITS)
                .setRandomizedEncryptionRequired(true)
                .build(),
        )
        return generator.generateKey()
    }
}
