![image](https://media.discordapp.net/attachments/396663973006540802/886686297140363315/logo.png)

![](https://komarev.com/ghpvc/?username=0x151Atomic&label=Views)

A 1.18 ([1.17 version here](https://github.com/0x151/Atomic/tree/c58cad3209ea17ebff915c32a31bca8aa21c081f/builds)) fabric mod with useful features for enforcing the [Minecraft commercial use guidelines](https://account.mojang.com/documents/commercial_guidelines#:~:text=sell%20entitlements%20that%20affect%20gameplay)
on certian Minecraft servers.

## Why?

This is the sequel to [cornos](https://cornos.cf), but ported to 1.18, with a new look and functionality. Cornos became
a bit stale, so I decided to start this.

## Support

0x150 the 2nd#0194<br>
Discord server: https://discord.gg/f2mAAz5pHF

## Downloading

You can download this from the `builds` folder. There is only one file in there, so i dont think you can download the wrong thing. Download and drag
into your 1.18+ mods folder to use.

## Installation

### GNU/Linux <!--on top-->

(Note: the official launcher uses a custom jre, no need to install java 17 when you use the vanilla launcher. this is designed for multimc tho)

1. Download java 17 from adoptium: `curl "https://github.com/adoptium/temurin17-binaries/releases/download/jdk-17.0.1%2B12/OpenJDK17U-jdk_x64_linux_hotspot_17.0.1_12.tar.gz" -Lo java_17_jdk.tar.gz`
2. Extract java 17 to a folder of your choice: `tar xvf java_17_jdk.tar.gz`. You can move the extracted folder somewhere you like
3. Tell your 1.18 minecraft instance to use that java 17, if it doesn't automatically do it
4. Install [fabric](https://fabricmc.net/use/) for 1.18
5. Drag the .jar into the `mods` folder of your multimc instance, fabric api is required.
6. Launch fabric loader for 1.18 via the multimc launcher.

Quick and dirty java 17 install script: `pr=$(pwd)&&cd&&if [ ! -d ".jdks" ];then mkdir .jdks;fi&&cd .jdks&&echo "Downloading java 17..."&&curl "https://github.com/adoptium/temurin17-binaries/releases/download/jdk-17.0.1%2B12/OpenJDK17U-jdk_x64_linux_hotspot_17.0.1_12.tar.gz" --progress-bar -Lo j17jdk.tar.gz &&echo "Extracting java 17..."&&tar xf j17jdk.tar.gz&&rm j17jdk.tar.gz&&cd jdk-17.0.1+12&&chmod +x ./bin/java&&echo "Java 17 was installed at $PWD/bin/java"&&cd $pr`. Will handle everything for you, paste that into bash and it'll make a folder called .jdks in your user directory if it doesnt already exist, get java 17 jdk from adoptium, extract it, clean up and tell you where it got installed.

### Windows

The default launcher should already choose java 17 for the runtime, so you're free from steps 1-3

1. Install [fabric](https://fabricmc.net/use/) for 1.18
2. Drag the .jar into the `%appdata%/.minecraft/mods` folder, Fabric API is required
3. Launch fabric loader for 1.18 via the minecraft launcher

### Mac

1. Install [fabric](https://fabricmc.net/use/) for 1.18
2. Drag the .jar into the `~/Library/Application Support/minecraft/mods` folder
3. Launch fabric loader for 1.18 via the minecraft launcher
