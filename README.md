# What is Refraction?
This library has no specific purpose, it's just a collection of utilities that I've made for my own projects. <br>
I've decided to make it public so that others can use it as well. <br>
If you have any suggestions or want to contribute, feel free to do so! <br>

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
- ReConfig—Config system with auto syncing support <br>
- Atda—Persistent data storage system <br>
- Syncables—Server → Client class syncing capability <br>
- Channels—Custom data channels between server and client w/ headers <br>
- Runnabler—Handler for delayed and ticking runnables on client / server <br>
- RefractionEvents—Event system with multiloader support <br>
- VoxelUtils—Voxel generation utilities <br>
- Particler—Particle wrapper with render and tick manipulation <br>
- RAABB—Allows for non-axis aligner boxes with collision <br>
- Cutscenes—Customizable cutscenes through code (future command support) <br>
- Custom debuggers—Similar to Minecraft's system of debugging, Refraction has custom debug support <br>
- Interactions—NPC Interaction handler <br>
- Quests—Quest handler system, doesn't offer quest stacking by default (future change) <br>
- Server Screens—Full server side control of screens without extra packets including closing, opening and sharing data between client ↔ server <br>
- Registrable tracking sounds—Ticking sounds that can be registered on the client and called from the server <br>
- Flood Filler—Highly customizable flood filler <br>
- Camera shake—Provides a camera shake interface <br>
- SyncConfig—Automatic syncing for player joins <br>
- Vec3Helper—A lot of vec3 util <br>
- RScanner—File scanner given a package <br>
And a lot more, just take a look for yourself, mainly in @helper and @feature dirs :D