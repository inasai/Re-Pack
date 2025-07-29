# Architecture
```text
.
└── main
    ├── java
    │   └── com
    │       └── inasai
    │           └── repack
    │               ├── config
    │               │   ├── category
    │               │   │   ├── DeathConfig.java
    │               │   │   └── GuideConfig.java
    │               │   └── RePackConfig.java
    │               ├── effect
    │               │   ├── GifEffect.java
    │               │   ├── ParticleEffect.java
    │               │   └── ScreenShakeEffect.java
    │               ├── event
    │               │   ├── category
    │               │   │   ├── DeathEvents.java
    │               │   │   └── GuideEvents.java
    │               │   └── ClientSetup.java
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
        │       │   ├── witch_calls.ogg
        │       │   └── witch_whispers.ogg
        │       ├── sounds.json
        │       └── textures
        │           └── gui
        │               ├── brewing_guide
        │               │   ├── guide_1.png
        │               │   └── guide_2.png
        │               └── death_gif_frames
        │                   ├── frame_000.png
        │                   ├── frame_001.png
        │                   ├── frame_002.png
        │                   ├── frame_003.png
        │                   ├── frame_004.png
        │                   └── frame_005.png
        ├── META-INF
        │   ├── accesstransformer.cfg
        │   └── mods.toml
        ├── pack.mcmeta
        └── repack.mixins.json

23 directories, 30 files
```
