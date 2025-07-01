# Installation Instructions

**Adding it to your project**

In your `build.gradle` file in the TeamCode module, add the following line to the dependencies:

```gradle
dependencies {
    implementation 'com.github.DaCodingBeast:PidTuners:version'
}
```

Replace the version with the latest version here : [![](https://jitpack.io/v/DaCodingBeast/PidTuners.svg)](https://jitpack.io/#DaCodingBeast/PidTuners)

In your `build.dependencies.gradle` file, add the following to the repositories:

```gradle
repositories {
    maven {url = 'https://jitpack.io'}
}
```

Inside your `build.common.gradle` file, add the following to your packaging options in your `android` block:

```gradle
packagingOptions {
    exclude 'META-INF/LICENSE-notice.md'
    exclude 'META-INF/LICENSE.md'
}
```
