# kotlinx.serialization — keep @Serializable companion + $$serializer
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
-keepclassmembers @kotlinx.serialization.Serializable class ** {
    static <1>$Companion Companion;
}
-keep,allowshrinking class **$$serializer { *; }
-keepclasseswithmembers class ** {
    kotlinx.serialization.KSerializer serializer(...);
}

# App-owned @Serializable models (DTOs + domain models)
-keep,includedescriptorclasses class app.devper.pharm.**$$serializer { *; }
-keep class app.devper.pharm.data.remote.dto.** { *; }
-keep class app.devper.pharm.data.storage.** { *; }
-keep class app.devper.pharm.domain.model.** { *; }
-keep class app.devper.pharm.domain.param.** { *; }

# Koin reflection — keep app constructors so factoryOf / singleOf can resolve
-keep class app.devper.pharm.** {
    public <init>(...);
}
-keep class org.koin.** { *; }

# Ktor — coroutine internal factories
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keep class io.ktor.** { *; }
-keep class kotlinx.coroutines.** { *; }

# Ktor debug detector references java.lang.management on JVM — absent on Android.
-dontwarn java.lang.management.**
-dontwarn io.ktor.util.debug.**

# OkHttp / okio — referenced but optional bits absent at runtime.
-dontwarn org.bouncycastle.**
-dontwarn org.conscrypt.**
-dontwarn org.openjsse.**

# Kotlin reflect referenced indirectly by serializer factories.
-dontwarn kotlin.reflect.jvm.internal.**

# Compose runtime — keep state-holders and recomposition scopes
-keep class androidx.compose.runtime.** { *; }

# Keep Application + Activity entry points
-keep class app.devper.pharm.PharmacyApplication { *; }
-keep class app.devper.pharm.MainActivity { *; }

# Drop debug/log statements in release
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
}
