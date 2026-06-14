package app.devper.pharm.platform

import app.devper.pharm.common.StorageException
import app.devper.pharm.common.platform.SecureStorage
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.alloc
import kotlinx.cinterop.interpretObjCPointer
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.rawValue
import kotlinx.cinterop.reinterpret
import kotlinx.cinterop.value
import platform.CoreFoundation.CFDictionaryRef
import platform.CoreFoundation.CFTypeRefVar
import platform.Foundation.CFBridgingRelease
import platform.Foundation.CFBridgingRetain
import platform.Foundation.NSData
import platform.Foundation.NSMutableDictionary
import platform.Foundation.NSString
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.create
import platform.Foundation.dataUsingEncoding
import platform.Security.SecItemAdd
import platform.Security.SecItemCopyMatching
import platform.Security.SecItemDelete
import platform.Security.errSecSuccess
import platform.Security.kSecAttrAccessible
import platform.Security.kSecAttrAccessibleAfterFirstUnlockThisDeviceOnly
import platform.Security.kSecAttrAccount
import platform.Security.kSecAttrService
import platform.Security.kSecClass
import platform.Security.kSecClassGenericPassword
import platform.Security.kSecMatchLimit
import platform.Security.kSecMatchLimitOne
import platform.Security.kSecReturnData
import platform.Security.kSecValueData
import platform.Foundation.NSCopyingProtocol
import platform.darwin.NSObject
import platform.darwin.OSStatus

private const val SERVICE = "app.devper.pharm.secure"

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
class KeychainSecureStorage : SecureStorage {

    override fun put(key: String, value: String) {
        baseQuery(key).useCFDictionary { SecItemDelete(it) }
        val data = (NSString.create(string = value) as NSString)
            .dataUsingEncoding(NSUTF8StringEncoding)
            ?: throw StorageException("Failed to encode keychain value")
        val query = baseQuery(key).apply {
            setObject(data, forKey = kSecValueData.bridgeKey())
            setObject(
                kSecAttrAccessibleAfterFirstUnlockThisDeviceOnly.bridgeValue(),
                forKey = kSecAttrAccessible.bridgeKey(),
            )
        }
        val status: OSStatus = query.useCFDictionary { SecItemAdd(it, null) }
        if (status != errSecSuccess) {
            throw StorageException("Keychain SecItemAdd failed: $status")
        }
    }

    override fun get(key: String): String? = memScoped {
        val query = baseQuery(key).apply {
            setObject(kSecMatchLimitOne.bridgeValue(), forKey = kSecMatchLimit.bridgeKey())
            setObject(true, forKey = kSecReturnData.bridgeKey())
        }
        val result = alloc<CFTypeRefVar>()
        val status = query.useCFDictionary { SecItemCopyMatching(it, result.ptr) }
        if (status != errSecSuccess) return@memScoped null
        val data = CFBridgingRelease(result.value) as? NSData ?: return@memScoped null
        val nsString = NSString.create(data = data, encoding = NSUTF8StringEncoding) ?: return@memScoped null
        nsString.toString()
    }

    override fun remove(key: String) {
        baseQuery(key).useCFDictionary { SecItemDelete(it) }
    }

    private fun baseQuery(key: String): NSMutableDictionary {
        val dict = NSMutableDictionary()
        dict.setObject(kSecClassGenericPassword.bridgeValue(), forKey = kSecClass.bridgeKey())
        dict.setObject(SERVICE, forKey = kSecAttrService.bridgeKey())
        dict.setObject(key, forKey = kSecAttrAccount.bridgeKey())
        return dict
    }

    private inline fun <R> NSMutableDictionary.useCFDictionary(block: (CFDictionaryRef?) -> R): R {
        val cf = CFBridgingRetain(this)
        try {
            return block(cf?.reinterpret())
        } finally {
            CFBridgingRelease(cf)
        }
    }

    private fun Any?.bridgeKey(): NSCopyingProtocol =
        interpretObjCPointer<NSString>(this.rawCFPointer())

    private fun Any?.bridgeValue(): NSObject =
        interpretObjCPointer<NSObject>(this.rawCFPointer())

    private fun Any?.rawCFPointer(): kotlinx.cinterop.NativePtr {
        val p = this as kotlinx.cinterop.CPointer<*>
        return p.rawValue
    }
}
