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
    │               │   ├── ClientSetup.java
    │               │   └── DeathEventHandler.java
    │               ├── mixin
    │               │   └── MixinBrewingStandScreen.java
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
        │                   ├── guide1.png
        │                   └── guide2.png
        ├── META-INF
        │   ├── accesstransformer.cfg
        │   └── mods.toml
        ├── pack.mcmeta
        └── repack.mixins.json

19 directories, 17 files
```
