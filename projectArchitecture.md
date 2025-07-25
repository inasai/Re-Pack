# Architecture
```text
.
└── main
    ├── java
    │   └── com
    │       └── inasai
    │           └── repack
    │               ├── config
    │               │   └── RePackConfig.java
    │               ├── event
    │               │   └── ClientSetup.java
    │               ├── mixin
    │               │   ├── accessor
    │               │   │   └── ILivingEntityAccessor.java
    │               │   ├── MixinBrewingStandScreen.java
    │               │   └── MixinLocalPlayer.java
    │               ├── RePack.java
    │               └── sound
    │                   └── ModSounds.java
    └── resources
        ├── assets
        │   └── repack
        │       ├── lang
        │       │   ├── en_us.json
        │       │   └── uk_ua.json
        │       ├── sounds
        │       │   ├── custom_death.ogg
        │       │   └── special_death.ogg
        │       ├── sounds.json
        │       └── textures
        │           └── gui
        │               └── brewing_guide
        │                   ├── default.png
        │                   └── simple.png
        ├── META-INF
        │   └── mods.toml
        ├── pack.mcmeta
        └── repack.mixins.json

20 directories, 17 files
```
