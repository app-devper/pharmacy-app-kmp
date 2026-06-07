package app.devper.pharm.platform

import app.devper.pharm.common.StorageException
import app.devper.pharm.common.platform.SecureStorage
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardOpenOption
import java.nio.file.attribute.PosixFilePermission
import java.nio.file.attribute.PosixFilePermissions
import java.security.SecureRandom
import java.util.Base64
import java.util.Properties
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec

private const val DEFAULT_DIR = ".pharmacy-app"
private const val KEY_FILE = "secure.key"
private const val DATA_FILE = "secure.data"
private const val TRANSFORMATION = "AES/GCM/NoPadding"
private const val GCM_TAG_BITS = 128
private const val GCM_IV_BYTES = 12
private const val AES_KEY_SIZE_BITS = 256

class JvmSecureStorage(
    baseDir: Path = Paths.get(System.getProperty("user.home"), DEFAULT_DIR),
) : SecureStorage {

    private val dir: Path = baseDir
    private val keyPath: Path = baseDir.resolve(KEY_FILE)
    private val dataPath: Path = baseDir.resolve(DATA_FILE)

    @Synchronized
    override fun put(key: String, value: String) {
        try {
            ensureDir()
            val props = readProperties()
            val secretKey = SecretKeySpec(getOrCreateKeyBytes(), "AES")
            val cipher = Cipher.getInstance(TRANSFORMATION).apply {
                init(Cipher.ENCRYPT_MODE, secretKey)
            }
            val iv = cipher.iv
            require(iv.size == GCM_IV_BYTES)
            val ciphertext = cipher.doFinal(value.toByteArray(StandardCharsets.UTF_8))
            val combined = ByteArray(iv.size + ciphertext.size).apply {
                iv.copyInto(this, 0)
                ciphertext.copyInto(this, iv.size)
            }
            props.setProperty(key, Base64.getEncoder().encodeToString(combined))
            writeProperties(props)
        } catch (t: Throwable) {
            throw StorageException("Failed to write secure storage value", cause = t)
        }
    }

    @Synchronized
    override fun get(key: String): String? {
        val props = readProperties()
        val encoded = props.getProperty(key) ?: return null
        return try {
            val combined = Base64.getDecoder().decode(encoded)
            val iv = combined.copyOfRange(0, GCM_IV_BYTES)
            val ciphertext = combined.copyOfRange(GCM_IV_BYTES, combined.size)
            val secretKey = SecretKeySpec(getOrCreateKeyBytes(), "AES")
            val cipher = Cipher.getInstance(TRANSFORMATION).apply {
                init(Cipher.DECRYPT_MODE, secretKey, GCMParameterSpec(GCM_TAG_BITS, iv))
            }
            cipher.doFinal(ciphertext).toString(StandardCharsets.UTF_8)
        } catch (_: Throwable) {
            removeUnsafe(key)
            null
        }
    }

    @Synchronized
    override fun remove(key: String) {
        removeUnsafe(key)
    }

    private fun removeUnsafe(key: String) {
        val props = readProperties()
        if (props.remove(key) != null) {
            writeProperties(props)
        }
    }

    private fun ensureDir() {
        if (Files.notExists(dir)) {
            try {
                val perms = PosixFilePermissions.fromString("rwx------")
                Files.createDirectories(
                    dir,
                    PosixFilePermissions.asFileAttribute(perms),
                )
            } catch (_: UnsupportedOperationException) {
                Files.createDirectories(dir)
            }
        }
    }

    private fun readProperties(): Properties {
        val props = Properties()
        if (Files.exists(dataPath)) {
            Files.newInputStream(dataPath).use { props.load(it) }
        }
        return props
    }

    private fun writeProperties(props: Properties) {
        Files.newOutputStream(
            dataPath,
            StandardOpenOption.CREATE,
            StandardOpenOption.TRUNCATE_EXISTING,
            StandardOpenOption.WRITE,
        ).use { out -> props.store(out, null) }
        applyOwnerOnlyPermissions(dataPath)
    }

    private fun getOrCreateKeyBytes(): ByteArray {
        if (Files.exists(keyPath)) {
            return Files.readAllBytes(keyPath)
        }
        val keyGen = KeyGenerator.getInstance("AES").apply { init(AES_KEY_SIZE_BITS, SecureRandom()) }
        val rawKey = keyGen.generateKey().encoded
        Files.write(
            keyPath,
            rawKey,
            StandardOpenOption.CREATE_NEW,
            StandardOpenOption.WRITE,
        )
        applyOwnerOnlyPermissions(keyPath)
        return rawKey
    }

    private fun applyOwnerOnlyPermissions(path: Path) {
        try {
            Files.setPosixFilePermissions(
                path,
                setOf(PosixFilePermission.OWNER_READ, PosixFilePermission.OWNER_WRITE),
            )
        } catch (_: UnsupportedOperationException) {
        } catch (_: IOException) {
        }
    }
}
