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
    │               │   ├── DeathEventHandler.java
    │               │   └── SpecialScreenEffects.java
    │               ├── mixin
    │               │   ├── MixinBrewingStandScreen.java
    │               │   └── MixinGameRenderer.java
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
        │       │   ├── witchCalls.ogg
        │       │   └── witchWhispers.ogg
        │       ├── sounds.json
        │       └── textures
        │           ├── effects
        │           │   ├── witchCalls001.png
        │           │   ├── witchCalls002.png
        │           │   ├── witchCalls003.png
        │           │   ├── witchCalls004.png
        │           │   ├── witchCalls005.png
        │           │   └── witchCalls006.png
        │           └── gui
        │               └── brewing_guide
        │                   ├── guide1.png
        │                   └── guide2.png
        ├── META-INF
        │   ├── accesstransformer.cfg
        │   └── mods.toml
        ├── pack.mcmeta
        └── repack.mixins.json

20 directories, 25 files
```
