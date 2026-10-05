plugins {
	alias(libs.plugins.android.application)
	id("com.google.gms.google-services")
}

android {
	namespace = "pucmm.args.icc451_firebase_chat"
	compileSdk {
		version = release(37)
	}

	defaultConfig {
		applicationId = "pucmm.args.icc451_firebase_chat"
		minSdk = 26
		//noinspection OldTargetApi
		targetSdk = 36
		versionCode = 1
		versionName = "1.0"

		testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
	buildFeatures {
		viewBinding = true
	}
	compileOptions {
		sourceCompatibility = JavaVersion.VERSION_11
		targetCompatibility = JavaVersion.VERSION_11
	}
}

dependencies {
	implementation(libs.androidx.recyclerview)
	implementation(libs.androidx.lifecycle.viewmodel)
	implementation(libs.androidx.lifecycle.livedata)
	implementation(libs.androidx.fragment.ktx)
	implementation(libs.coil)
	implementation(libs.kotlinx.coroutines.play.services)
	implementation(platform(libs.firebase.bom))
	implementation(libs.firebase.auth)
	implementation(libs.firebase.firestore)
	implementation(libs.firebase.messaging)
	implementation(libs.firebase.storage)


	implementation(libs.androidx.core.ktx)
	implementation(libs.androidx.appcompat)
	implementation(libs.material)
	implementation(libs.androidx.activity)
	implementation(libs.androidx.constraintlayout)
	testImplementation(libs.junit)
	androidTestImplementation(libs.androidx.junit)
	androidTestImplementation(libs.androidx.espresso.core)
}