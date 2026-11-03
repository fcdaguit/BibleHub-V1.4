# APK build instructions

## A. GitHub Actions (no local setup)
1. Create a GitHub repository and push this folder to `main`.
2. **Actions → Build APK** runs automatically on push (also manually via *Run workflow*). It runs unit tests, then builds the debug APK and uploads it as an artifact.
3. To publish a release APK on GitHub Releases: `git tag v1.0.0 && git push origin v1.0.0`.

The workflow installs Gradle itself, so no `gradle-wrapper.jar` is committed. If you prefer a wrapper, run `gradle wrapper` once locally and commit the result.

### Signed release APK (optional)
Add these repository secrets (**Settings → Secrets and variables → Actions**):

| Secret | Value |
|---|---|
| `KEYSTORE_BASE64` | `base64 -w0 release.jks` |
| `KEYSTORE_PASSWORD`, `KEY_ALIAS`, `KEY_PASSWORD` | your keystore details |

Create the keystore once: `keytool -genkeypair -v -keystore release.jks -alias sermonprep -keyalg RSA -keysize 2048 -validity 10000`. **Back it up**; losing it means users cannot update the app. Never commit it (`.gitignore` already excludes `*.jks`).

With the secrets present, tag builds (`v*`) also produce a signed release APK.

## B. Local build
Requirements: JDK 17, Android SDK (platform 35), Gradle 8.10+ (or Android Studio).

```
gradle :app:testDebugUnitTest
gradle :app:assembleDebug
# → app/build/outputs/apk/debug/app-debug.apk
```
Or open the folder in Android Studio and press Run.

## If the first build fails
This code has never been compiled (see README). Likely causes, in order:
1. **Compile errors** from API or import mistakes: fix as reported by Gradle.
2. **Dependency not found**, most likely `com.google.ai.edge.litertlm:litertlm-android:0.18.0`: set a version listed on Google Maven.
3. **Kotlin version errors** ("Module was compiled with an incompatible version of Kotlin"): the LiteRT-LM AI library is built with Kotlin 2.4, so the project uses Kotlin 2.4.21, KSP 2.3.10, Hilt 2.58 and Room 2.8.5, plus `ksp("org.jetbrains.kotlin:kotlin-metadata-jvm:2.4.21")` so Hilt and Room can read it. Keep these together if you upgrade any of them.
4. **Room/KSP schema errors** around the FTS table: see `data/db/Entities.kt`.

## Updating versions
Library versions are set directly in `app/build.gradle.kts` and root `build.gradle.kts`.
