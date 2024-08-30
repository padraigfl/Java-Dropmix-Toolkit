package util;

import brut.common.BrutException;
import com.android.apksigner.ApkSignerTool;
import model.AppState;
import model.DropmixSharedAssets;
import model.Process;
import org.junit.platform.commons.util.ClassLoaderUtils;

import javax.swing.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class UtilApk extends Thread {
  private String returnValue;
  private Process currentProcess;

  // experimental multithreaded solution
  public void run(Process p, String input, String output) {
    long id = Thread.currentThread().getId();
    try {
      currentProcess = p;
      // Displaying the thread that is running
      System.out.println(
        p + ": " + id
          + " is running");
      if (p == Process.DECOMPILING) {
        returnValue = decompileApk(input, output);
      }
      if (p == Process.RECOMPILING) {
        returnValue = recompile(input, output);
      }
    }
    catch (Exception e) {
      // Throwing an exception
      System.out.println("Exception is caught");
    }
  }
  public String getReturnValue() {
    return this.returnValue;
  }
  public Process getProcess() {
    return this.currentProcess;
  }
  public static String decompileApk(String apkPath, String outputPath) {
    try {
      AppState.setCurrentProcess(Process.DECOMPILING);
      brut.apktool.Main.main(new String[]{"d", "-rf", apkPath, "-o", outputPath});
      byte[] assetsFile = util.Helpers.loadLocalFile(outputPath + "/" + DropmixSharedAssets.assetsRelativePath);

      AppState.endCurrentProcess(Process.DECOMPILING);
      if (assetsFile.length > 100000) {
        AppState.getInstance().setData(assetsFile);
        return outputPath;
      }
      throw new RuntimeException("invalid-asset-size: < 100000");
    } catch (Exception e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    }
  }
  public static String recompile(String inputPath, String outputPath) {
    try {
      AppState as = AppState.getInstance();
      as.setCurrentProcess(Process.RECOMPILING);
      brut.apktool.Main.main(new String[]{"b", inputPath, "-o", DropmixSharedAssets.unsignedPath});
      as.switchCurrentProcess(Process.RECOMPILING, Process.SIGNING);
      // TODO is this worth splitting into its own function?
      ApkSignerTool.main(new String[]{
        "sign",
        "--key",
        "key.pk8", // TODO add valid key
        "--cert",
        "certificate.pem",// TODO add valid cert
        "--in",
        DropmixSharedAssets.unsignedPath,
        "--out",
        outputPath,
      });
      Files.deleteIfExists(Path.of(DropmixSharedAssets.unsignedPath));
      as.endCurrentProcess(Process.SIGNING);
      System.out.println("Signed: "+ outputPath);
      return outputPath;
    } catch (BrutException | IOException e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
