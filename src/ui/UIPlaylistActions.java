package ui;

import model.AppState;
import model.CardDetail;
import model.DropmixSharedAssets;
import util.UtilAdb;
import util.UtilApk;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.TreeMap;

public class UIPlaylistActions extends JPanel {
  public String verifiedModApk;
  public static final String modDir = "dropmix_modded_src";
  public UIPlaylistActions() {
    setLayout(new GridLayout(5, 1));
    try {
      Files.deleteIfExists(Path.of(modDir));
    } catch (IOException e) { }
    renderActions();
  }
  public void renderActions() {
    removeAll();
    AppState as = AppState.getInstance();

    JButton modApkBtn = SwingFactory.buildButton("Validate Modified APK", new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        modApk();
        System.out.println("Modified dropmix generated");
      }
    });
    modApkBtn.setEnabled(as.playlistSwap.size() > 0 && verifiedModApk == null);
    add(modApkBtn);

    JButton installApkBtn = SwingFactory.buildButton("Install APK", new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        System.out.println("Installing modded APK");
        boolean result = UtilAdb.installApk(
          as.adbDevice,
          verifiedModApk
        );

        if (result) {
          System.out.println("Modded Dropmix installed to "+as.adbDevice.toString());
        }
      }
    });
    installApkBtn.setEnabled(this.verifiedModApk != null && as.adbDevice != null);

    UIPlaylistActions that = this;
    JButton saveApk = SwingFactory.buildButton("Save modded APK", new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        JFileChooser f = new JFileChooser();
        f.showSaveDialog(that);
        File saved = f.getSelectedFile();
        try {
          System.out.println(verifiedModApk.getBytes().length + " "  + saved.toPath());
          Path savePath = saved.toPath();
          if (!savePath.toString().endsWith(".apk")) {
            savePath = Path.of(savePath.toString() + ".apk");
          }
          Files.write(savePath, Files.readAllBytes(Path.of(verifiedModApk)));
        } catch (IOException ex) {}
      }
    });
    saveApk.setEnabled(this.verifiedModApk != null);

    add(installApkBtn);
    add(saveApk);
  }
  public String modApk() {
    AppState as = AppState.getInstance();
    Path tempDir = Path.of(modDir).toAbsolutePath();
    UtilApk.decompileApk(
      as.apkFile.getAbsolutePath(),
      tempDir.toString()
    );
    TreeMap<String, String> swapObj = AppState.getCardSwapFromPlaylist(as.playlistSwap);

    byte[] modBytes = as.assetsHandler.applySwap(swapObj);
    Path assetsPath = Path.of(tempDir.toString() + DropmixSharedAssets.assetsRelativePath);
    try {
      System.out.println("writing to "+ assetsPath.toAbsolutePath().toString());
      Files.deleteIfExists(assetsPath);
      Files.write(assetsPath, modBytes);
      Files.write(Path.of("moddedFile"), modBytes);
      System.out.println("mod applied");
    } catch (IOException e) {
      System.out.println("mod write fail");
    }
    String output = UtilApk.recompile(tempDir.toAbsolutePath().toString(), "Dropmix190mod.apk");
    this.verifiedModApk = output;
    this.renderActions();
    return output;
  }
  public void clearState() {
    try {
      if (this.verifiedModApk != null) {
        Files.deleteIfExists(Path.of(this.verifiedModApk));
      }
    } catch (IOException e) {

    }
    this.verifiedModApk = null;
    renderActions();
  }
//  public String installApk(String moddedApk) {
//    // get device, validate with data
//    // modApk()
//    // install
//    // return
//    return "";
//  }
//  public String installApk(String moddedApk, boolean transferData) {
//    // installApk()
//    // get data path
//    // transfer data
//    // get obb path
//    // transfer data
//    return "";
//  }
//  public boolean setApk(boolean b) {
//    this.hasModApk = b;
//    return this.hasModApk;
//  }
//  public boolean getApk() {
//    return this.hasModApk;
//  }

}
