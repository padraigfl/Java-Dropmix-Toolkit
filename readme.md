This project relies on the following jars:

- apksigner: from android dev tools, for signing 
- apktool_2.9.3: for decompiling and recompiling
- jadb: for connecting to android device


Setup:

1. Begin an ADB server instance if you want to transfer direct to a phone (`adb server` or `adb.exe server` in most cases I think)
2. Add a key and certificate to the same directory as you run the jar from for signing the new APK
3. Run `java -jar [app path]/DropmixModdingTool.jar`
4. Add a valid Dropmix APK file (must be v1.9.0) and verify it
5. (if desired) connect Android device (will fail if more than one available)

Current functionality:
1. Parse app's key card database
1. Generates a modified version of the application with playlists swapped
1. Directly installs modified APK onto connected android device (requires ADB server instance)

Future work:
1. Streamline process some more (can I launch the ADB server within the jar code?)
1. Verify working on Windows
1. Tools for straightforward installing of APK without mods
1. Mayyyyybe custom cards? This involves modifying the level0 assets file instead and injecting custom data files post-install so it's not so easy)

Issues:

1. I raced through this so the UI is janky as hell. I expect frequent restarts
1. Need to figure out the licensing stuff fully. My understanding is that all the libraries I depend on here use Apache 2.0 so I will use that too