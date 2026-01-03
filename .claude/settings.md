# Tectonic Au Naturel - Project Context

## Overview

This is a performance-optimized fork of the Tectonic Minecraft mod. The fork focuses on:
- C2ME compatibility (multi-threaded world generation)
- Memory efficiency and reduced GC pressure
- Noise computation optimization

## Project Structure

```
src/
  common/          - Shared code across all platforms
  shared/          - Version-specific shared code (1.20.1, 1.21.1, 1.21.10)
  forge/           - Forge-specific code
  fabric/          - Fabric-specific code
  neoforge/        - NeoForge-specific code
```

## Key Files

### Density Functions (Core Worldgen)
- `src/common/main/java/dev/worldgen/tectonic/worldgen/densityfunction/ConfigNoise.java`
  - LRU cache for noise values (max 256 entries)
  - ThreadLocal FastNoiseLite instances
  - Note: compute() is rarely called due to mapAll() converting to vanilla

- `src/common/main/java/dev/worldgen/tectonic/worldgen/densityfunction/ConfigClamp.java`
  - ThreadLocal array pooling for fillArray operations

### Configuration
- `src/common/main/java/dev/worldgen/tectonic/config/ConfigHandler.java`
  - Volatile LOADED_STATE for C2ME thread visibility

### Noise Parameters
- `src/common/main/resources/resourcepacks/tectonic/data/tectonic/worldgen/noise/parameter/`
  - continentalness.json - 6 octaves (reduced from 9)
  - erosion.json - 6 octaves (reduced from 9)

## Build Commands

```bash
# Full build
./gradlew build

# Forge 1.20.1 (requires 8GB heap)
./gradlew forge1201IncludeJar --no-daemon -Dorg.gradle.jvmargs="-Xmx8G"

# Clean and rebuild
rm -f build/libs/*.jar && ./gradlew forge1201IncludeJar --no-daemon -Dorg.gradle.jvmargs="-Xmx8G"
```

## Important Notes

1. **FastNoiseLite is essentially dead code** - mapAll() converts ConfigNoise to vanilla DensityFunctions before terrain generation, so ConfigNoise.compute() is never called in the hot path.

2. **DO NOT attempt these optimizations** (they cause terrain corruption):
   - mapAll() preservation (breaks Minecraft's visitor pattern)
   - Cache quantization (causes terrain discontinuities)
   - OpenSimplex2 replacing Perlin (different algorithm characteristics)

3. **Thread safety** - All static mutable state must be volatile or ThreadLocal for C2ME compatibility.

## Upstream

- Original: https://github.com/Apollounknowndev/tectonic
- Author: Apollo (Apollounknowndev)
