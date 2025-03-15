import com.android.build.gradle.internal.api.ApkVariantOutputImpl
import java.text.SimpleDateFormat
import java.util.Locale

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.example.multidemo"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.multidemo"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0.0"
        multiDexEnabled = true
    }

    val excludedFiles = listOf(
        "META-INF/DEPENDENCIES",
    )
    packaging {
        resources.excludes.addAll(excludedFiles)
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

    sourceSets {
        getByName("main") {
            jniLibs.srcDirs("libs")
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
        buildConfig = true
        viewBinding = true
    }

    applicationVariants.all {
        outputs.all {
            if (this is ApkVariantOutputImpl) {
                outputFileName = "X_" + getBuildDate() + "_" + defaultConfig.versionName + ".apk"
            }
        }
    }
}

fun getBuildDate(): String {
    val dateFormat = SimpleDateFormat("yyyyMMdd", Locale.CHINA)
    return dateFormat.format(System.currentTimeMillis())
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    //基础依赖库
    implementation("com.github.AndroidCoderPeng:Kotlin-lite-lib:1.1.4")
    implementation("androidx.core:core-ktx:1.13.0")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.cardview:cardview:1.0.0")
    //Google官方授权库
    implementation("pub.devrel:easypermissions:3.0.0")
    //图片加载库
    implementation("com.github.bumptech.glide:glide:4.12.0")
    //单项/数字、二三级联动、日期/时间等滚轮选择器
    implementation("com.github.gzu-liyujiang.AndroidPicker:WheelPicker:4.1.13")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    implementation("androidx.lifecycle:lifecycle-extensions:2.2.0")
    //查看大图
    implementation("com.github.chrisbanes:PhotoView:2.3.0")
    implementation("com.belerweb:pinyin4j:2.5.0")
    //沉浸式状态栏。基础依赖包，必须要依赖
    implementation("com.gyf.immersionbar:immersionbar:3.0.0")
    //图片选择框架
    implementation("io.github.lucksiege:pictureselector:v3.11.1")
    //大图
    implementation("com.github.chrisbanes:PhotoView:2.3.0")
    //视频播放器
    implementation("com.github.CarGuo.GSYVideoPlayer:GSYVideoPlayer:v8.4.0-release-jitpack")
    //视频压缩
    implementation("com.zolad:videoslimmer:1.0.0")
    //高德地图
    implementation("com.amap.api:3dmap:latest.integration")
    //高德地图搜索
    implementation("com.amap.api:search:8.1.0")
    //Gson
    implementation("com.google.code.gson:gson:2.8.9")
    // CameraX Camera2 extensions
    implementation("androidx.camera:camera-camera2:1.2.3")
    // CameraX Lifecycle library
    implementation("androidx.camera:camera-lifecycle:1.2.3")
    // CameraX View class
    implementation("androidx.camera:camera-view:1.2.3")
    //Google mlkit 人脸检测
    implementation("com.google.mlkit:face-detection:16.1.6")
    //图片压缩
    implementation("top.zibin:Luban:1.1.8")
    //蓝牙
    implementation("com.github.Jasonchenlijian:FastBle:2.4.0")
    //图表
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
}
