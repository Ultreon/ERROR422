# ERROR 422
[![](https://jitpack.io/v/dev.ultreon/ERROR422.svg)](https://jitpack.io/#dev.ultreon/ERROR422)

## How to Compile
1. Download and install Adoptium JDK from https://adoptium.net/
2. Open your terminal (Windows: cmd.exe, Mac: Terminal, Linux: bash)
3. Change directory to the root of the project:
    ```bash
    cd /path/to/project
    ```
4. Run the following command:
    ```bash
    ./gradlew build
    ```
5. The built mods are in `forge/build/libs` and `fabric/build/libs` folders.

## Downloads
 * Modrinth: https://modrinth.com/mod/error422

## Use as Dependency
1. First add JitPack to the repository section in your project's `build.gradle`:
   ```gradle
   repositories {
       maven { url "https://jitpack.io" }
   }
   ```
2. And now add the dependency to your project's `build.gradle`:
   * <details>
     <summary>Fabric Loom</summary>
     
     ```gradle
     dependencies {
         modImplementation "com.github.ultreon.ERROR422:error-422-ported-fabric:0.1.1"

         modApi("com.ultreon.mods:ultreon-lib-fabric:1.1.0")
         modApi('com.github.Ultreon.advanced-debug:advanced-debug-fabric:9b626785f7')
     }
     ```
     </details>
   * <details>
     <summary>ForgeGradle</summary>
     
     ```gradle
     dependencies {
         implementation fg.deobf("com.github.ultreon.ERROR422:error-422-ported-forge:0.1.1")
     
         implementation fg.deobf("com.ultreon.mods:ultreon-lib-forge:1.1.0")
         implementation fg.deobf("com.github.Ultreon.advanced-debug:advanced-debug-forge:9b626785f7")
     }
     ```
     </details>
   * <details>
     <summary>Forge Loom</summary>
     
     ```gradle
     dependencies {
         modImplementation "com.github.ultreon.ERROR422:error-422-ported-forge:0.1.1"

         modApi("com.ultreon.mods:ultreon-lib-forge:1.1.0") {
             exclude group: "org.lwjgl"
             exclude group: "org.lwjgl.lwjgl"
             exclude group: "com.google.code"
             exclude group: "com.google.guava"
             exclude group: "com.google.protobuf"
             exclude group: "com.google.gson"
             exclude group: "com.google"
         }
         modApi('com.github.Ultreon.advanced-debug:advanced-debug-forge:9b626785f7') {
             exclude group: "org.lwjgl"
             exclude group: "org.lwjgl.lwjgl"
             exclude group: "com.google.code"
             exclude group: "com.google.guava"
             exclude group: "com.google.protobuf"
             exclude group: "com.google.gson"
             exclude group: "com.google"
         }
     }
     ```
     </details>
   * <details>
     <summary>Architectury Loom</summary>
     
     ```gradle
     dependencies {
         modImplementation "com.github.ultreon.ERROR422:error-422-ported:0.1.1"
     
         modApi("com.ultreon.mods:ultreon-lib:1.1.0")
         modApi("com.github.Ultreon.advanced-debug:advanced-debug:9b626785f7")
     }
     ```
     </details>
   

