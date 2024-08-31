# DropmixModdingTool

I previously done work on Dropmix at https://github.com/padraigfl/dropmix-card-swap-utility but hit a brick wall in the browser. As the project being modded is an APK it made sense to use Java instead (although C# may also make sense for the Unity side of things).

This project currently replicates the playlist swap functionality of the previous project but the idea is to make a bit of a dumping ground for useful utilites.

## Requirements

### Needed

- Java 1.8 or later (broke a few things getting it down to this one but it should make it more accessible)
- Dropmix 1.9.0 APK file: this program was designed with only 1.9.0 in mind and relies on asset data which may be unique to that version
- ONE phone with ADB enabled and connected: without this you'll have to copy APK files to the phone and install them manually repeatedly
- A bunch of scary permissions screens to accept: directory access on computer, installing unverified APKs on android device, probably others. You are taking risks running this code, hopefully not loads of risks but I can't guarantee it
- Dropmix being deleted from your device: the signing process of the modified APKs means you can't swap between the legit Dropmix APK and modded ones. You can however install one modded APK directly over another and have immediate access to a whole fresh range of card data
- Patience

### Included

This project relies on the following jars, which are included in the build:

- [apksigner](https://developer.android.com/tools/apksigner): from android dev tools, for signing
- [apktool](https://apktool.org/): for decompiling and recompiling
- [jadb](https://github.com/vidstige/jadb): for connecting to android device
- [sl4j](https://www.slf4j.org/): ehh... jadb depends on it

adb is also baked in to simplify the process of connecting to an Android device; this has mainly been tested on macOS.


## Setup:

1. Add a key and certificate to the same directory as you run the jar from for signing the new APK (I may include a dummy one to remove this step)
1. Run `java -jar [app path]/DropmixModdingTool.jar` (depending on your device you may only need to click on the icon in your Finder/Explorer)
1. Add a valid Dropmix APK file (must be v1.9.0) and verify it
1. (if desired) connect Android device (will fail if more than one available)
1. Go to playlists tab and select the playlists you wish to swap
1. Build the modded version of the APK
1. Either install it directly to you ADB connected device or save it to your hard drive

## Troubleshooting

I need to know the version of Java you're using and the output of the log panel within the application. If you want to test more extensively you can get additional and/or clearer logs by running the jar file from the terminal.

- is java working on your system? (check for `java -version` in the terminal)
- what version of java is it?
- do you have multiple adb devices conneccted to your computer?
- is the Dropmix APK file definitely 1.9.0

### Known Issues:

1. A fresh install of all card data is required after each install currently [I'm guessing this is some kind of auto refresh function baked into the app]
1. I raced through this so the UI is janky as hell. I expect frequent restarts. Some of it looks pretty bad with the Java 1.8 compilation settings but it'll do
1. Logs aren't updating correctly; I think I need to add some multithreading functionality
1. UI in general has weird lags tbh
1. Need to figure out the licensing stuff fully. My understanding is that all the libraries I depend on here use Apache 2.0 so I will use that too
1. The included keys for signing the APK currently have had less than zero thought put into them. This is a rough and insecure project. If you want to improve anything around the security of this project I'd love the help!
1. No data is saved between instances of the program so you need to readd the apk each time
1. The app does a terrible job cleaning up after itself, the directory you run the app from will likely have some junk folders in it, they'll overwrite each time you run the app and it's safe to delete them

## Current functionality:

1. Parse app's key card database
1. Generates a modified version of the application with playlists swapped
1. Directly installs modified APK onto connected android device (requires ADB server instance)

## Future work:

1. Alternative swap process using `level0` data (will not require fresh data downloads so future-proof but won't have 100% accurate card behaviour)
1. Include associated baffler cards in playlist swaps
1. Mod iOS app on M1 devices
1. Verify working on Windows
1. Tools for straightforward installing of APK without mods
1. Mayyyyybe custom cards? This involves modifying the level0 assets file instead and injecting custom data files post-install so it's not so easy
