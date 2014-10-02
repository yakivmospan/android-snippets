### Gradle: Tips

#### Change some class only for one flavor
The task is to add new `SplashActivity.class` for `flavor10` and left old one for the rest flavors.
**Rule**. Main can't contain classes that are in flavors

There are to options to achieve this.

**First one :**
- Remove old `SplashActivity.class` from `main`
- Add copies of it to all `flavors` except `flavor10`
- Then create new `SplashActivity.class` for `flavor10`
- **Change all** old `SplashActivity.class` in flavors that use it when you need to update it

**Second one :**
- Create `common` flavor
- Move old `SplashActivity.class` from `main` inside it
- Change source sets for all `flavors` that should use it
```java
apply plugin: 'android'
android {
    ///...
    signingConfigs {///...}
    buildTypes {///...}
    productFlavors {///...}
    
    sourceSets {
        flavor1 {
            java {
                srcDir 'src/common/java'
            }
        }
        ///...
        flavor9 {
            java {
                srcDir 'src/common/java'
            }
        }
    }
}
```
- Create new `SplashActivity.class` for `flavor10`
- **Change only one** old `SplashActivity.class` in `common` flavor when you need to update it
