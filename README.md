```kotlin
repositories {
    maven("https://maven.pkg.github.com/FiLKoNd/paperKtLib") {
        credentials {
            username = System.getenv("GITHUB_USERNAME")
            password = System.getenv("GITHUB_TOKEN")
        }
    }
}
```

```kotlin
val paperKtLibVersion = "1.3.4"
dependencies {
    compileOnly("com.filkond:paperktlib-adventure:$paperKtLibVersion")
    compileOnly("com.filkond:paperktlib-config:$paperKtLibVersion")
    compileOnly("com.filkond:paperktlib-paper:$paperKtLibVersion")
    compileOnly("com.filkond:paperktlib-towny:$paperKtLibVersion")
}
```
