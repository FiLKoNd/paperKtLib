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
plugins {
    id("com.google.devtools.ksp")
}

val paperKtLibVersion = TODO() // 
dependencies {
    implementation("com.filkond:paperktlib-adventure:$paperKtLibVersion")
    implementation("com.filkond:paperktlib-config:$paperKtLibVersion")
    implementation("com.filkond:paperktlib-paper:$paperKtLibVersion")
    implementation("com.filkond:paperktlib-towny:$paperKtLibVersion")
    implementation("com.filkond:paperktlib-event-generator:$paperKtLibVersion")
    
    ksp("com.filkond:paperktlib-event-generator:$paperKtLibVersion")
}
```
