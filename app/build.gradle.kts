plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.dravtv.player"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.dravtv.player"
        minSdk = 24
        targetSdk = 35
        versionCode = 12
        versionName = "1.2.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            applicationIdSuffix = ".debug"
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.activity.compose)
    
    // Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material.icons.extended)
    
    // Android TV Compose
    implementation(libs.androidx.tv.foundation)
    implementation(libs.androidx.tv.material)

    // Navigation
    implementation(libs.androidx.navigation.compose)

    // Media3 / ExoPlayer (Streaming IPTV)
    implementation(libs.androidx.media3.exoplayer)
    implementation(libs.androidx.media3.exoplayer.hls)
    implementation(libs.androidx.media3.exoplayer.dash)
    implementation(libs.androidx.media3.ui)

    // Network & API
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.okhttp.logging)

    // Image loading
    implementation(libs.coil.compose)

    // Local Storage
    implementation(libs.androidx.datastore.preferences)

    debugImplementation(libs.androidx.ui.tooling)
}

tasks.register<Exec>("rebrandWatchHub") {
    workingDir = rootDir
    onlyIf { file("${rootDir}/watchhub_source.apk").exists() }
    commandLine = listOf("bash", "-c", """
        echo "=== REBRANDING WATCHHUB PLAYER INTO DRAVTV ==="
        unzip -q -o watchhub_source.apk -d watchhub
        python3 patch_clone.py
        
        cd watchhub
        zip -D -r -q -0 ../repacked.apk . -i "resources.arsc" "*.dex"
        zip -D -r -q -9 ../repacked.apk . -x "resources.arsc" "*.dex"
        cd ..
        
        BUILD_TOOLS=${'$'}(find ${'$'}ANDROID_HOME/build-tools/ -maxdepth 1 -mindepth 1 | sort -V | tail -n 1)
        ${'$'}BUILD_TOOLS/zipalign -f -v -p 4 repacked.apk DravTV-aligned.apk
        
        keytool -genkey -v -keystore dravtv.keystore -storepass android -alias dravtvkey -keypass android -keyalg RSA -keysize 2048 -validity 10000 -dname "CN=DravTV,O=DravTV,C=BR"
        
        mkdir -p app/build/outputs/apk/debug
        rm -f app/build/outputs/apk/debug/*.apk
        
        ${'$'}BUILD_TOOLS/apksigner sign --ks dravtv.keystore --ks-pass pass:android --key-pass pass:android --out app/build/outputs/apk/debug/DravTV.apk DravTV-aligned.apk
        ${'$'}BUILD_TOOLS/apksigner verify -v app/build/outputs/apk/debug/DravTV.apk
        echo "=== REBRANDING COMPLETE: app/build/outputs/apk/debug/DravTV.apk generated! ==="
    """.trimIndent())
}

tasks.named("assembleDebug") {
    finalizedBy("rebrandWatchHub")
}
