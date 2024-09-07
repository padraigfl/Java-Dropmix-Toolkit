package util;

import brut.common.BrutException;
import com.android.apksigner.ApkSignerTool;
import model.AppState;
import model.DropmixSharedAssets;
import model.Process;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class UtilApk implements Runnable {
  private String returnValue;
  private Process currentProcess;
  static String keyPath;
  static String certPath;
  public String input;
  public String output;
  public Process proc;

  public UtilApk(Process p, String input, String output) {
    this.proc = p;
    this.input = input;
    this.output = output;
  }

  // experimental multithreaded solution
  @Override
  public synchronized void run() {
    long id = Thread.currentThread().getId();
    try {
      currentProcess = this.proc;
      // Displaying the thread that is running
      System.out.println(
        this.proc + ": " + id
          + " is running");
      if (this.proc == Process.DECOMPILING) {
        returnValue = decompileApk(input, output);
      }
      if (this.proc == Process.RECOMPILING) {
        returnValue = recompile(input, output);
      }
      notifyAll();
    }
    catch (Exception e) {
      e.printStackTrace();
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
      brut.apktool.Main.main(new String[]{"d", "-rf", apkPath, "-o", outputPath});
      byte[] assetsFile = util.Helpers.loadLocalFile(outputPath + "/" + DropmixSharedAssets.assetsRelativePath);

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
    getKeyAndCert();
    try {
      brut.apktool.Main.main(new String[]{"b", inputPath, "-o", DropmixSharedAssets.unsignedPath});
      // TODO is this worth splitting into its own function?
      ApkSignerTool.main(new String[]{
        "sign",
        "--key",
        UtilApk.keyPath, // TODO add valid key
        "--cert",
        UtilApk.certPath,// TODO add valid cert
        "--in",
        DropmixSharedAssets.unsignedPath,
        "--out",
        outputPath,
      });
      Files.deleteIfExists(Paths.get(DropmixSharedAssets.unsignedPath));
      System.out.println("Signed: "+ outputPath);
      return outputPath;
    } catch (BrutException | IOException e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
  public static void getKeyAndCert() {
    if (UtilApk.keyPath == null) {
      UtilApk.keyPath = Helpers.saveTempFile("/key.pk8", "key.pk8");
    }
    if (UtilApk.certPath == null) {
      UtilApk.certPath = Helpers.saveTempFile("/certificate.pem", "cert.pem");
    }
  }
}
