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

1. Run `java -jar [app path]/DropmixModdingTool.jar` (depending on your device you may only need to click on the icon in your Finder/Explorer)
1. Add a valid Dropmix APK file (must be v1.9.0) and verify it
1. (if desired) connect Android device (will fail if more than one available)
1. Go to playlists tab and select the playlists you wish to swap
1. Build the modded version of the APK (Safe Swap recommended; see Swap info below)
1. Either install it directly to you ADB connected device or save it to your hard drive
1. Once you have all card data downloaded, you should only run the app with wifi and data disabled to prevent constant server data refreshes

See /docs/setup.md for a visual run through on running this software.

### Recommendations:

- As modifying the apk requires the app to be be signed with a new security key, there will be issues with any data you already have. With this in mind it's recommended to use the "Build Re-Signed APK" function to have a straight copy of the app unmodified but with the same signature as other mods
- The Safe Swap modification process isn't perfect but as it doesn't break the data integrity within the app (requiring all card data to be redownloaded), it's a better long term option

## Troubleshooting

I need to know the version of Java you're using and the output of the log panel within the application. If you want to test more extensively you can get additional and/or clearer logs by running the jar file from the terminal.

- is java working on your system? (check for `java -version` in the terminal)
- what version of java is it?
- do you have multiple adb devices conneccted to your computer?
- is the Dropmix APK file definitely 1.9.0
- download all card data via the app and proceed to never use the app while connected to the internet again

### Known Issues:

1. Full Swap process triggers re-download of all card assets
1. I raced through this so the UI is janky as hell. I expect frequent restarts. Some of it looks pretty bad with the Java 1.8 compilation settings but it'll do. Would greatly appreciate from a more experienced Java developer here.
1. Need to figure out the licensing stuff fully. My understanding is that all the libraries I depend on here use Apache 2.0 so I will use that too
1. The included keys for signing the APK currently have had less than zero thought put into them. This is a rough and insecure project. If you want to improve anything around the security of this project I'd love the help!
1. No data is saved between instances of the program so you need to re-add the apk each time
1. The app does a terrible job cleaning up after itself, the directory you run the app from will likely have some junk folders in it, they'll overwrite each time you run the app and it's safe to delete them

## Current functionality:

1. Parse app's key card database
1. Generates a modified version of the application with playlists swapped (two processes available)
1. Directly installs modified APK onto connected android device (requires ADB server instance)

### Modification Options

#### Full swap

This simply swaps the card IDs on the top level data tables (found in sharedassets), meaning a card should behave exactly like its swapped counterpart.

Unfortunately this means the cards have data which does not match the app data and a fresh download of assets will be triggered before the game can be played; meaning limited long term value

#### Safe Swap

This swaps the IDs in the game level data (found in level0), mostly behaving exactly the same however "Power" appears to come from the top level database and I may be missing other areas where issues exist

#### Future Options

As I've had to update the game level data for the safe swap, I've largely implemented the parsing and writing of the database which would allow custom cards along with more extensive game modifications (e.g. FX cards rules appear to be set here too)

## Future work:

This is the work I intend to do rather than stretch goals and bolder things

1. Output APK alongside CSV file outlining changes from the main one (with same name, possibly?)
1. Verify working on Windows (partially checked, ADB not)
1. Tools for straightforward installing of APK and data without mods (i.e. install APK to device, copy data files over)
1. Mod iOS app on M1 devices 
