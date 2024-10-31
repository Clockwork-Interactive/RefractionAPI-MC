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

### All big things that Refraction offers: <br>
- Flood Filler—Highly customizable flood filler
- Camera shake—Provides a camera shake interface 
- Cutscenes—Customizable cutscenes through code (future command support) 
- Interactions—NPC Interaction handler
- Quests—Quest handler system, doesn't offer quest stacking by default (future change)
- Server Screens—Full server side control of screens without extra packets including closing, opening and sharing data between client ↔ server
- Registrable tracking sounds—Ticking sounds that can be registered on the client and called from the server
- Custom debuggers—Similar to Minecraft's system of debugging, Refraction has custom debug support <br>
And a lot more, just take a look for yourself :D