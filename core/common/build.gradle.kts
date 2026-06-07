import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.INT
import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING

plugins {
    id("pharmacy.kmp.library")
    alias(libs.plugins.buildkonfig)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.koin.core)
        }
        commonTest.dependencies {
            implementation(libs.kotlinx.coroutines.test)
        }
    }
}

android {
    namespace = "app.devper.pharm.core.common"
}

buildkonfig {
    packageName = "app.devper.pharm.common"
    objectName = "BuildKonfig"

    defaultConfigs {
        buildConfigField(STRING, "VERSION_NAME", libs.versions.app.version.get())
        buildConfigField(INT, "VERSION_CODE", libs.versions.app.versionCode.get())
    }
}
