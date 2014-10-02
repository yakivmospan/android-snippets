### Gradle: Setup

**main_build.gradle**
```java
// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath 'com.android.tools.build:gradle:0.12.2'

        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}

allprojects {
    repositories {
        mavenCentral()
    }
}
```

**settings.gradle**

```java
include ':app', ':some-lib-project'

project(':some-lib-project').projectDir = new File('../some-lib-project')
```

**app_build.gradle**
```java
apply plugin: 'android'

android {
    compileSdkVersion Integer.parseInt(project.COMPILE_SDK_VERSION)
    buildToolsVersion project.BUILD_TOOLS_VERSION

    defaultConfig {
	    applicationId project.APP_PACKAGE
        minSdkVersion Integer.parseInt(project.MIN_SDK_VERSION)
        targetSdkVersion project.TARGET_SDK_VERSION
    }

    signingConfigs {

        release {
            storeFile file('.../release.keystore')
            keyAlias '..'
            keyPassword '..'
            storePassword '..'
        }

        flavor {
            storeFile file('.../flavor.keystore')
            keyAlias '..'
            keyPassword '..'
            storePassword '..'
        }
    }

    buildTypes {
        release {
            runProguard false
            proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.pro'
            signingConfig signingConfigs.release
        }
    }
    
    productFlavors {
        flavor{
            applicationId project.FLAVOR_PACKAGE
            versionCode Integer.parseInt(project.VERSION_CODE)
            versionName project.VERSION_NAME
            signingConfig signingConfigs.flavor
        }
    }
}

dependencies {
    compile fileTree(dir: 'libs', include: ['*.jar'])
    compile 'org.jetbrains:annotations:13.0'
    compile 'com.android.support:appcompat-v7:20.0.0'
    compile 'com.google.android.gms:play-services:3.2.65'
    compile project(':some-lib-project')
}
```

**gradle.properties**
```properties
# Project-wide Gradle settings.

# IDE (e.g. Android Studio) users:
# Settings specified in this file will override any Gradle settings
# configured through the IDE.

# For more details on how to configure your build environment visit
# http://www.gradle.org/docs/current/userguide/build_environment.html

# Specifies the JVM arguments used for the daemon process.
# The setting is particularly useful for tweaking memory settings.
# Default value: -Xmx10248m -XX:MaxPermSize=256m
# org.gradle.jvmargs=-Xmx2048m -XX:MaxPermSize=512m -XX:+HeapDumpOnOutOfMemoryError -Dfile.encoding=UTF-8

# When configured, Gradle will run in incubating parallel mode.
# This option should only be used with decoupled projects. More details, visit
# http://www.gradle.org/docs/current/userguide/multi_project_builds.html#sec:decoupled_projects
# org.gradle.parallel=true

#Common information

COMPILE_SDK_VERSION=16
BUILD_TOOLS_VERSION=20
MIN_SDK_VERSION=8
TARGET_SDK_VERSION=16
APP_PACKAGE=app.package
```

**ant_project_build.gradle**
```java
apply plugin: 'android-library'

android {
    compileSdkVersion 16
    buildToolsVersion "20.0.0"

    sourceSets {
        main {
            manifest {
                srcFile 'AndroidManifest.xml'
            }
            java {
                srcDir 'src'
            }
            res {
                srcDir 'res'
            }
            assets {
                srcDir 'assets'
            }
            resources {
                srcDir 'src'
            }
        }
    }
}
```
