plugins { id("com.android.application") }
android { namespace = "com.example.galaxystudy"; compileSdk = 35
 defaultConfig { applicationId = "com.example.galaxystudy"; minSdk = 26; targetSdk = 35; versionCode = 1; versionName = "1.0" }
 compileOptions { sourceCompatibility = JavaVersion.VERSION_17; targetCompatibility = JavaVersion.VERSION_17 }
}
dependencies {
 implementation("com.google.mlkit:text-recognition:16.0.1")
 implementation("com.google.mlkit:translate:17.0.3")
}
