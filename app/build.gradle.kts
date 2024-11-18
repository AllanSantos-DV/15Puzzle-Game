plugins {
    alias(libs.plugins.android.application) // Define que este módulo é um app Android
}

android {
    namespace = "com.allan.puzzlegame" // Pacote base do app
    compileSdk = 34 // Versão do SDK usada para compilar o app

    defaultConfig {
        applicationId = "com.allan.puzzlegame" // ID do aplicativo
        minSdk = 24 // Versão mínima suportada
        targetSdk = 34 // Versão de destino
        versionCode = 1 // Código de versão
        versionName = "1.0" // Nome da versão

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner" // Runner para testes
    }

    buildTypes {
        release {
            isMinifyEnabled = false // Não minificar no release
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("debug")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    implementation(libs.appcompat) // AppCompat para compatibilidade entre versões
    implementation(libs.material) // Material Design Components
    implementation(libs.gridlayout) // GridLayout para o tabuleiro 4x4
    testImplementation(libs.junit) // Dependência para testes unitários
    androidTestImplementation(libs.ext.junit) // Dependência para testes de integração
    androidTestImplementation(libs.espresso.core) // Dependência para testes de UI
}
