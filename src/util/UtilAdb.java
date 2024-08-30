package util;

import model.AppState;
import model.Process;
import se.vidstige.jadb.JadbDevice;
import se.vidstige.jadb.JadbException;
import se.vidstige.jadb.managers.PackageManager;

import java.io.IOException;
import java.nio.file.Path;

public class UtilAdb {
  public static boolean installApk(JadbDevice device, String apkPath) {
    try {
      AppState.setCurrentProcess(Process.INSTALLING);
      new PackageManager(device).forceInstall(Path.of(apkPath).toFile());
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
