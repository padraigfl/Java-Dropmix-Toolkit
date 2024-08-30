package util;

import brut.common.BrutException;
import com.android.apksigner.ApkSignerTool;
import model.AppState;
import model.DropmixSharedAssets;
import model.Process;
import se.vidstige.jadb.JadbDevice;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;

public class UtilApk {
  public static boolean decompileApk(String apkPath, String outputPath) {
    try {
      AppState.setCurrentProcess(Process.DECOMPILING);
      brut.apktool.Main.main(new String[]{"d", "-rf", apkPath, "-o", outputPath});
      byte[] assetsFile = util.Helpers.loadLocalFile(outputPath + "/" + DropmixSharedAssets.assetsRelativePath);

      AppState.endCurrentProcess(Process.DECOMPILING);
      if (assetsFile.length > 100000) {
        AppState.getInstance().setData(assetsFile);
        return true;
      }
      return false;
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
