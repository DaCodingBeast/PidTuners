//buildscript {
//    repositories {
//        mavenCentral()
//        google()
//    }
//    dependencies {
//        classpath 'com.android.tools.build:gradle:7.2.2'
//    }
//}
plugins {
    alias(libs.plugins.jetbrains.kotlin.android)
    id("maven-publish")
    id("com.android.library")
}

android {
    namespace = "com.dacodingbeast.pidtuners"
    compileSdk = 35

    defaultConfig {
        minSdk = 24
        targetSdk = 34

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation("org.firstinspires.ftc:Hardware:10.2.0")
    implementation("org.firstinspires.ftc:RobotCore:10.2.0")
    implementation ("com.acmerobotics.dashboard:dashboard:0.4.16")
    implementation(libs.androidx.core.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation("androidx.compose.runtime:runtime:1.5.1")
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {
                groupId = "com.dacodingbeast"
                artifactId = "dacodingbeast"

                afterEvaluate {
                    from(components["release"])
                }
            }
        }
        repositories {
            maven {
                name = "dacodingbeast"
                url = uri("${project.buildDir}/release")
            }
        }
    }
}