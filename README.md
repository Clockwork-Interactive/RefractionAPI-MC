# You might be wondering how to add this project without the maven repository.
Build Refraction and get the jar file from the target folder. Add it to your project lib folder (make one if you haven't).
Then add the jar file as a flatdir dependency in your build.gradle file.
```gradle
repositiories {
    flatDir {
        dirs 'lib'
    }
}
dependencies {
    include "blank:refraction-MOD_LOADER-MINECRAFT_VERSION:REFRACTION_VERSION"
}
```
