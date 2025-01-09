# Bitnero

## Overview

Bitnero is an non custodial and no kyc wallet for bitcoin and monero.

## 🌟 Contributions

If you wanna contribute, Please make sure to add new features & Then make a PR.Feel free to
contribute to the project and stay tuned for more exciting updates!

# Tech Stack Highlights

- **Kotlin Multiplatform**: `2.0.0`
- **AGP (Android Gradle Plugin)**: `8.2.2`
- **Compose**: `1.7.1`
    - `androidx-appcompat`: `1.6.1`
    - `androidx-activityCompose`: `1.9.3`
    - `compose-uitooling`: `1.6.7`
    - `composeImageLoader`: `1.7.1`
    - `composeIcons`: `1.1.0`
- **Core Libraries**: `12.1.0`
- **Kotlinx Libraries**:
    - `kotlinx-coroutines`: `1.9.0-RC`
    - `kotlinx-serialization`: `1.6.3`
    - `kotlinx-datetime`: `0.5.0`
- **Networking**:
    - `coilNetwork`: `3.0.0-alpha01`
    - `ktor`: `2.3.12`
- **Dependency Injection**:
    - `koin`: `3.6.0-Beta4`
- **UI/UX**:
    - `compose-uitooling`: `1.6.3`
    - `composeIcons`: `1.1.0`
- **Logging**:
    - `napier`: `2.7.1`
- **Database/Storage**:
    - `sql-delight`: `2.0.2`
- **JSON Parsing**:
    - `json`: `1.6.3`
- **Build Configurations**:
    - `buildConfig`: `4.1.1`
- **Cross-Platform Image Loading**:
    - `kamelImage`: `0.9.4`
- **Size Measurement**:
    - `size`: `0.4.1`
- **Voyager Navigation**: `1.1.0-Beta05`
- **Other Utilities**:
    - `official-viewModel`: `2.8.4`

## Android Screen Shots

## iOS Screen Shots

## Desktop Screen Shots

## Before running!

- check your system with [KDoctor](https://github.com/Kotlin/kdoctor)
- install JDK 17 or higher on your machine
- add `local.properties` file to the project root and set a path to Android SDK there

### Android

To run the application on android device/emulator:

- open project in Android Studio and run imported android run configuration

To build the application bundle:

- run `./gradlew :composeApp:assembleDebug`
- find `.apk` file in `composeApp/build/outputs/apk/debug/composeApp-debug.apk`
  Run android simulator UI tests: `./gradlew :composeApp:pixel5Check`

### Desktop

Run the desktop application: `./gradlew :composeApp:run`
Run desktop UI tests: `./gradlew :composeApp:jvmTest`

### iOS

To run the application on iPhone device/simulator:

- Open `iosApp/iosApp.xcproject` in Xcode and run standard configuration
- Or
  use [Kotlin Multiplatform Mobile plugin](https://plugins.jetbrains.com/plugin/14936-kotlin-multiplatform-mobile)
  for Android Studio
  Run iOS simulator UI tests: `./gradlew :composeApp:iosSimulatorArm64Test`

### Experimental Browser (JS)

Run the browser application: `./gradlew :composeApp:jsBrowserDevelopmentRun --continue`
Run browser UI tests: `./gradlew :composeApp:jsBrowserTest`

## Community Resources

## Acknowledgments

We'd like to thank the following individuals and organizations for their contributions to the
YouTube Clone project:

- No Contributor Yet.
- Checkout Other cool projects on my profile as well.
