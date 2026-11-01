import java.util.Properties

// The installable Android app: a thin shell around :composeApp's shared UI. AGP 9 compiles the
// Kotlin here itself (built-in Kotlin); the Compose compiler plugin still comes from Kotlin.
plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "app.devper.pharm"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "app.devper.pharm"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = libs.versions.app.versionCode.get().toInt()
        versionName = libs.versions.app.version.get()
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        buildConfig = true // PharmacyApplication reads BuildConfig.DEBUG
    }


    // The release key lives outside the repo. The properties file is named by PHARM_KEYSTORE_PROPERTIES, else
    // ~/.devper/keystores/pharm-release.properties (storeFile, storePassword, keyAlias, keyPassword;
    // backed up in Secret Manager of devperpos). Without it the release build is unsigned.
    val keystoreProperties = releaseKeystoreProperties()
    if (keystoreProperties != null) {
        signingConfigs.create("release") {
            storeFile = file(keystoreProperties.getProperty("storeFile"))
            storePassword = keystoreProperties.getProperty("storePassword")
            keyAlias = keystoreProperties.getProperty("keyAlias")
            keyPassword = keystoreProperties.getProperty("keyPassword")
        }
    }

    buildTypes {
        debug {
            isMinifyEnabled = false
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
            if (keystoreProperties != null) signingConfig = signingConfigs.getByName("release")
        }
    }
}

dependencies {
    implementation(project(":composeApp"))
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.koin.android)
    implementation(libs.ktor.client.okhttp)
}

fun releaseKeystoreProperties(): Properties? {
    val path = System.getenv("PHARM_KEYSTORE_PROPERTIES")
        ?: "${System.getProperty("user.home")}/.devper/keystores/pharm-release.properties"
    val file = File(path)
    if (!file.isFile) {
        logger.lifecycle("Android release signing: no keystore properties at $path, the release APK will be unsigned")
        return null
    }
    return Properties().apply { file.inputStream().use { load(it) } }
}
