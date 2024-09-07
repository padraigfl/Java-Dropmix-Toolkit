package util;

import model.AppState;
import model.Process;
import se.vidstige.jadb.JadbDevice;
import se.vidstige.jadb.JadbException;
import se.vidstige.jadb.managers.PackageManager;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.util.EnumSet;

public class UtilAdb {
  public static boolean hasAdbInstance = false;
  static boolean initialisedAdb = false;
  public static String adbPath;
  private UtilAdb() {

  }

  public static  void setAdbPath() {
    UtilAdb.adbPath = getAdbPath();
  }
  public static  void startServer () {
    if (UtilAdb.adbPath == null) {
      getAdbPath();
    }
    if (hasAdbInstance) {
      System.out.println("Already has adb instance");
      return;
    }
    try {
      ProcessBuilder pb = new ProcessBuilder(adbPath, "server");
      java.lang.Process p = pb.start();
      int res = p.waitFor();
      int code = p.exitValue(); // 0 == newly started process
      System.out.println("ADB process started from: " + adbPath + res + " " + code);
      initialisedAdb = true;
      hasAdbInstance = true;
    } catch (Exception e) {
      e.printStackTrace();
      // TODO improve this info
      System.out.println("Possible ADB issue; may not be able to transfer directly to Android device");
    }
  }
  public static void killAdbServer() {
    if (initialisedAdb) {
      try {
        ProcessBuilder pb = new ProcessBuilder(getAdbPath(), "kill-server");
        pb.start();
        pb.wait();
      } catch (IOException e){} catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
    }
  }
  private static String getAdbPath() {
    try {
      String adbLocation = "adb-linux";
      String os = System.getProperty("os.name").toLowerCase();
      if (os.contains("mac")) {
        adbLocation = "adb";
      } else if (os.contains("win")) {
        adbLocation = "adb.exe";
      } else {
        System.out.println(os + " issue; assuming linux");
      }
      String jarParent = new File(UtilAdb.class.getProtectionDomain().getCodeSource().getLocation()
        .toURI()).getParent();
      return Helpers.saveTempFile("/" + adbLocation, adbLocation);
    } catch (URISyntaxException e) {
      throw new RuntimeException(e);
    }
  }
  public static boolean installApk(JadbDevice device, String apkPath) {
    startServer();
    try {
      AppState.setCurrentProcess(Process.INSTALLING);
      new PackageManager(device).forceInstall(Paths.get(apkPath).toFile());
      AppState.endCurrentProcess(Process.INSTALLING);
      return true;
    } catch (JadbException | IOException e) {
      e.printStackTrace();
    }
    return false;
  }
  public static boolean transferData(JadbDevice device, String dataPath) {
    return false;
  }
  public static boolean transferObb(JadbDevice device, String obbPath) {
    return false;
  }
  private static String[] getAllRecursiveFiles(String path) {
    return new String[0];
  }
}
