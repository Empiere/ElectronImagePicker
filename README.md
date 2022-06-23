# ElectronImagePicker

[![](https://jitpack.io/v/Empiere/ElectronImagePicker.svg)](https://jitpack.io/#Empiere/ElectronImagePicker)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)


# Setup
**1. Add this to your project level build.gradle file**

```
allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```
**1. Add this to your project level settings.gradle file [For Newer version]**
```
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url 'https://jitpack.io' }
    }
}
```

**2. Add this to your module level build.gradle file**

```
dependencies {
    implementation 'com.github.Empiere:ElectronImagePicker:1.0'
}
```


