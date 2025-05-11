plugins {
    id("com.android.application")
}

android {
    namespace = "com.example.crabfood"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.crabfood"
        minSdk = 27
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        manifestPlaceholders["MAPBOX_ACCESS_TOKEN"] = project.properties["MAPBOX_ACCESS_TOKEN"] as? String ?: ""
        buildConfigField("String", "MAPBOX_ACCESS_TOKEN", "\"${project.properties["MAPBOX_ACCESS_TOKEN"]}\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    buildFeatures {
        viewBinding = true
        dataBinding = true
        buildConfig = true
    }
}

dependencies {

    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.2.1")
    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    implementation("androidx.activity:activity:1.8.0")
    implementation("androidx.paging:paging-guava:3.3.6")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")

    implementation("androidx.navigation:navigation-fragment-ktx:2.5.3")
    implementation("androidx.navigation:navigation-ui-ktx:2.5.3")


    // Firebase
    implementation ("com.google.firebase:firebase-bom:32.2.2")
    implementation ("com.google.firebase:firebase-auth:22.3.1")
    implementation ("com.google.firebase:firebase-storage:20.2.1")
    implementation ("com.google.firebase:firebase-messaging:24.1.0")
    implementation ("com.google.firebase:firebase-firestore:25.1.0")

    //mapbox
    implementation("com.mapbox.maps:android:11.11.0")

    // Glide for image loading
    implementation("com.github.bumptech.glide:glide:4.15.1")
    annotationProcessor("com.github.bumptech.glide:compiler:4.15.1")

    // Retrofit for networking
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")

    // Room for local database
    implementation("androidx.room:room-runtime:2.5.2")
    annotationProcessor ("androidx.room:room-compiler:2.5.2")
    implementation ("androidx.room:room-rxjava2:2.4.3")

    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")

    // Other libraries
    implementation("com.google.android.gms:play-services-location:21.0.1")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("de.hdodenhof:circleimageview:3.1.0")
    implementation("com.airbnb.android:lottie:6.1.0")

    implementation ("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // Paging 3
    implementation ("androidx.paging:paging-runtime:3.2.1")
    // Guava
    implementation ("com.google.guava:guava:32.1.3-android")

    implementation ("androidx.concurrent:concurrent-futures:1.1.0")

    // Coroutines for background operations
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")

}