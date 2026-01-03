# Tectonic Au Naturel

A performance-optimized fork of [Tectonic](https://github.com/Apollounknowndev/tectonic), a Minecraft mod that transforms terrain generation.

## About This Fork

This fork includes performance optimizations and C2ME compatibility improvements:

- **Thread Safety**: Volatile state for proper visibility across C2ME worker threads
- **Memory Management**: LRU cache eviction, array pooling, ThreadLocal cleanup hooks
- **Noise Optimization**: Reduced octave counts for continentalness/erosion parameters
- **GC Pressure**: Cached empty collections, pooled arrays to reduce allocations

## Original Project

**Tectonic** by [Apollo](https://github.com/Apollounknowndev) is a Minecraft mod and datapack that transforms how terrain is shaped.

- Original repository: https://github.com/Apollounknowndev/tectonic
- Modrinth page: https://modrinth.com/mod/tectonic

## Building

```bash
./gradlew build
```

For Forge 1.20.1:
```bash
./gradlew forge1201IncludeJar --no-daemon -Dorg.gradle.jvmargs="-Xmx8G"
```

## Third-Party Licenses

This project includes the following third-party components:

### FastNoiseLite
- **License**: MIT License
- **Copyright**: (c) 2023 Jordan Peck and Contributors
- **Source**: https://github.com/Auburn/FastNoiseLite

## License

This fork maintains the same licensing as the original Tectonic project. The original project does not specify an explicit license file; please refer to the original repository for licensing terms.
