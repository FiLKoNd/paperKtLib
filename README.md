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
dependencies {
    compileOnly("com.filkond:paperktlib-adventure:1.0.1")
    compileOnly("com.filkond:paperktlib-config:1.0.1")
    compileOnly("com.filkond:paperktlib-paper:1.0.1")
    compileOnly("com.filkond:paperktlib-towny:1.0.1")
}
```
