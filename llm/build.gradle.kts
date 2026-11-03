plugins {
    id("com.android.library")
}

android {
    namespace = "com.sermonprep.llm"
    compileSdk = 35

    defaultConfig {
        minSdk = 26
        consumerProguardFiles("consumer-rules.pro")
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    // Not exposed to :app at compile time (implementation, not api). It reaches the APK at runtime only.
    implementation("com.google.ai.edge.litertlm:litertlm-android:0.18.0")
}
