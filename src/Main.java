import com.android.apksigner.ApkSignerTool;
import se.vidstige.jadb.JadbConnection;
import se.vidstige.jadb.JadbDevice;
import se.vidstige.jadb.JadbException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
  public static void main(String[] args) {
    // String signKey = "./SignApk-master/key.pk8";
    // String signCert = "./certificate.pem";
    AppState appState = AppState.getInstance();

    String decompiledPath = "decompiled";
    String unsignedPath = "unsigned.apk";
    String assetsPath = decompiledPath + "/assets/bin/Data/sharedassets0.assets.split194";
    for (int i = 0; i < args.length; i++) {
      System.out.println(args[i]);
    }

    // adbDevice();

//    try {
//      JadbConnection jadb = new JadbConnection();
//      List<JadbDevice> devices = jadb.getDevices();
//      System.out.println("size: " + devices.size() + devices.get(0));
//    } catch (JadbException | IOException e) {
//      e.printStackTrace();
//      System.out.println(e);
//      throw new Error(e);
//    }
    new UIMain();

//    try {
//      Parameters params = new Parameters(args);
//      // decompile
//      brut.apktool.Main.main(new String[]{"d", "-rf", params.sourceApk, "-o", decompiledPath});
//
//      byte[] assetsFile = Helpers.loadLocalFile(assetsPath);
//      AssetsHandler assetsHandler0 = new AssetsHandler(assetsFile, AssetsHandler.s0Header);
//      byte[] updatedAssetsFile = assetsHandler0.processSwapAction(Helpers.getSampleTreeMap());
//      System.out.println("assets file" + assetsHandler0.dbLength);
//      Path sharedAssets = Path.of(assetsPath);
//      Files.deleteIfExists(sharedAssets);
//      Files.write(sharedAssets, updatedAssetsFile);
//      // compile
//      brut.apktool.Main.main(new String[]{"b", decompiledPath, "-o", unsignedPath});
//
//      Helpers.deleteDirectory(Path.of(decompiledPath).toFile());
//      // sign
//      System.out.println("Signing APK");
//      ApkSignerTool.main(new String[]{
//        "sign",
//        "--key",
//        params.key,
//        "--cert",
//        params.cert,
//        "--in",
//        unsignedPath,
//        "--out",
//        params.destApk,
//      });
//      Files.deleteIfExists(Path.of(unsignedPath));
//      System.out.println("Signed");
//    } catch (Exception e) {
//      e.printStackTrace();
//      throw new RuntimeException(e);
//    }

  }

  public static String adbDevice() {
    try {
      List<String> cmd = new LinkedList<>();
      cmd.add("adb");
      cmd.add("devices");
      ProcessBuilder pb = new ProcessBuilder(cmd);
      pb.redirectErrorStream(true);
      Process p = pb.start();
      BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
      String line;
      String agg = "";
      while ((line = in.readLine()) != null) {
        System.out.println(line);
        agg += line;
      }
      p.waitFor();
      System.out.println("ok!");

      in.close();
      return agg;

    } catch (IOException | InterruptedException e) {
      e.printStackTrace();
    }
    return "";
  }
}