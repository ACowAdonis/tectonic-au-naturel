plugins {
    kotlin("jvm") version "2.1.21"
    id("earth.terrarium.cloche") version "0.13.6"
}

repositories {
    cloche {
        mavenNeoforgedMeta()
        mavenNeoforged()
        mavenForge()
        mavenFabric()
        mavenParchment()
        librariesMinecraft()
        main()
    }
    mavenLocal()
    mavenCentral()
    maven("https://api.modrinth.com/maven")
    maven("https://maven.terraformersmc.com/")
}

group = "dev.worldgen.tectonic"
version = "3.0.17"

cloche {
    mappings {
        official()
    }

    metadata {
        modId = "tectonic"
        name = "Tectonic"
        description = "Terrain shaping brought to new heights, grander and more varied than ever before!"
        license = "MIT"
        icon = "pack.png"

        url = "https://modrinth.com/project/tectonic"
        issues = "https://github.com/Apollounknowndev/tectonic/issues"
        sources = "https://github.com/Apollounknowndev/tectonic"

        author("Apollo")
        contributor("HB Stratos")
        contributor("DawnKiro")
        contributor("Uni")
    }

    common {
        mixins.from(file("src/common/main/tectonic.mixins.json"))

        dependencies {
            compileOnly("org.spongepowered:mixin:0.8.3")
        }

        metadata {
            dependencies {
                dependency {
                    modId = "lithostitched"
                    required = true
                    version("1.4.11")
                }
            }
        }
    }

    val shared1201 = common("shared:1.20.1") {
        mixins.from(file("src/shared/1.20.1/main/tectonic_1.20.1.mixins.json"))
    }
    val shared1211 = common("shared:1.21.1") {
        mixins.from(file("src/shared/1.21.1/main/tectonic_1.21.1.mixins.json"))
    }
    val shared12110 = common("shared:1.21.10") {
        mixins.from(file("src/shared/1.21.10/main/tectonic_1.21.10.mixins.json"))
    }

    /* Temporarily disabled for Forge-only build
    fabric("fabric:1.20.1") {
        dependsOn(shared1201)

        loaderVersion = "0.16.13"
        minecraftVersion = "1.20.1"

        dependencies {
            fabricApi("0.92.6")
            modImplementation("maven.modrinth:lithostitched:1.4.11-fabric-1.20")
            modImplementation("com.terraformersmc:modmenu:7.2.2")
        }

        includedClient()
        runs {
            client()
            server()
        }

        metadata {
            entrypoint("main") {
                value = "dev.worldgen.tectonic.TectonicFabric"
            }
            entrypoint("modmenu") {
                value = "dev.worldgen.tectonic.compat.TectonicModMenuCompat"
            }
        }
    }
    */

    /* Temporarily disabled for Forge-only build
    fabric("fabric:1.21.1") {
        dependsOn(shared1211)

        loaderVersion = "0.17.3"
        minecraftVersion = "1.21.1"

        dependencies {
            fabricApi("0.116.1")
            modImplementation("maven.modrinth:lithostitched:1.5.0-fabric-1.21.1")
            modImplementation("com.terraformersmc:modmenu:11.0.3")
        }

        includedClient()
        runs {
            client()
            server()
        }

        metadata {
            entrypoint("main") {
                value = "dev.worldgen.tectonic.TectonicFabric"
            }
            entrypoint("modmenu") {
                value = "dev.worldgen.tectonic.compat.TectonicModMenuCompat"
            }
        }
    }

    fabric("fabric:1.21.10") {
        dependsOn(shared12110)

        loaderVersion = "0.17.3"
        minecraftVersion = "1.21.10"

        dependencies {
            fabricApi("0.135.0", "1.21.10")
            modImplementation("maven.modrinth:lithostitched:1.5.1-fabric-1.21.9")
            modImplementation("com.terraformersmc:modmenu:16.0.0-rc.1")
        } //accessWidenFabric1218CommonMinecraft

        includedClient()
        runs {
            client()
            server()
        }

        metadata {
            entrypoint("main") {
                value = "dev.worldgen.tectonic.TectonicFabric"
            }
            entrypoint("modmenu") {
                value = "dev.worldgen.tectonic.compat.TectonicModMenuCompat"
            }
        }
    }
    */

    forge("forge:1.20.1") {
        dependsOn(shared1201)

        loaderVersion = "47.4.0"
        minecraftVersion = "1.20.1"

        dependencies {
            modImplementation("maven.modrinth:lithostitched:1.4.11-forge-1.20")
        }

        runs {
            client()
            server()
        }
    }

    neoforge("neoforge:1.21.1") {
        dependsOn(shared1211)

        loaderVersion = "21.1.209"
        minecraftVersion = "1.21.1"

        dependencies {
            modImplementation("maven.modrinth:lithostitched:1.5.0-neoforge-1.21.1")
        }

        runs {
            client()
            server()
        }
    }

    neoforge("neoforge:1.21.10") {
        dependsOn(shared12110)

        loaderVersion = "21.10.12-beta"
        minecraftVersion = "1.21.10"

        dependencies {
            modImplementation("maven.modrinth:lithostitched:1.5.1-neoforge-1.21.9")
        }

        runs {
            client()
            server()
        }
    }
}