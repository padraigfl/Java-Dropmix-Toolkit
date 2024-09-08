package ui;

import model.AppState;
import model.DropmixLevel0;
import model.DropmixSharedAssets;
import model.Process;
import util.UtilAdb;
import util.UtilApk;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.TreeMap;

public class UIPlaylistActions extends JPanel {
  public String verifiedModApk;
  public static final String modDir = "dropmix_modded_src";
  public boolean includeBafflers = false;
  public UIPlaylistActions() {
    setLayout(new GridLayout(7, 1));
    try {
      Files.deleteIfExists(Paths.get(modDir));
    } catch (IOException e) { }
    renderActions();
  }
  public void renderActions() {
    removeAll();
    AppState as = AppState.getInstance();
    UIPlaylistActions that = this;
    JButton resignedApkBtn = SwingFactory.buildButton("Build Re-Signed APK", new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        freshDecompile(false, false);
      }
    });
    resignedApkBtn.setEnabled(verifiedModApk == null && as.currentProcess.equals(Process.NONE) && as.playlistSwap.isEmpty());
    add(resignedApkBtn);

    JCheckBox cb = new JCheckBox("swap bafflers if possible");
    cb.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        that.includeBafflers = !that.includeBafflers;
      }
    });
    cb.setEnabled(as.currentProcess.equals(Process.NONE));
    add(cb);

    JButton modApkBtn = SwingFactory.buildButton("Full Swap", new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        freshDecompile(true, false);
      }
    });
    modApkBtn.setEnabled(!as.playlistSwap.isEmpty() && verifiedModApk == null && as.currentProcess.equals(Process.NONE));
    add(modApkBtn);
    JButton safeModApkBtn = SwingFactory.buildButton("Safe Swap", new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        freshDecompile(true, true);
      }
    });
    safeModApkBtn.setEnabled(!as.playlistSwap.isEmpty() && verifiedModApk == null);
    add(safeModApkBtn);

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
            savePath = Paths.get(savePath + ".apk");
          }
          Files.write(savePath, Files.readAllBytes(Paths.get(verifiedModApk)));
        } catch (IOException ex) {}
      }
    });
    saveApk.setEnabled(this.verifiedModApk != null);

    add(installApkBtn);
    add(saveApk);
  }
  private void freshDecompile(boolean useMod, boolean safeMod) {
    AppState as = AppState.getInstance();
    AppState.setCurrentProcess(Process.DECOMPILING);
    SwingWorker decompile = new SwingWorker() {
      @Override
      protected Object doInBackground() throws Exception {
        renderActions();
        UtilApk.decompileApk(as.apkFile.getAbsolutePath(), getTempDir().toString());
        if (useMod) {
          AppState.switchCurrentProcess(Process.DECOMPILING, Process.GENERATING_MOD);
          freshModify(safeMod);
        } else {
          AppState.switchCurrentProcess(Process.DECOMPILING, Process.RECOMPILING);
          recompile(useMod);
        }
        return null;
      }
    };
    decompile.execute();
  }
  // apply mod
  private void freshModify(boolean safeMod) {
    AppState as = AppState.getInstance();
    UIPlaylistActions that = this;
    SwingWorker modify = new SwingWorker() {
      @Override
      protected Object doInBackground() throws Exception {
        renderActions();
        TreeMap<String, String> swapObj = AppState.getCardSwapFromPlaylist(as.playlistSwap, that.includeBafflers);

        byte[] modBytes = safeMod ? as.level0Handler.applySwap(swapObj) : as.assetsHandler.applySwap(swapObj);
        Path assetsPath = Paths.get(
          getTempDir().toString() +
            ( safeMod ? DropmixLevel0.relativePath : DropmixSharedAssets.assetsRelativePath)
        );
        try {
          System.out.println("writing to "+ assetsPath.toAbsolutePath().toString());
          Files.deleteIfExists(assetsPath);
          Files.write(assetsPath, modBytes);
          Files.write(Paths.get("moddedFile"), modBytes);
          System.out.println("mod applied");
          AppState.switchCurrentProcess(Process.GENERATING_MOD, Process.RECOMPILING);
          recompile(true);
        } catch (IOException e) {
          System.out.println("mod write fail");
        }
        return null;
      }
    };
    modify.execute();
  }
  // recompile and sign
  public void recompile(boolean useMod) {
    UIPlaylistActions that = this;
    SwingWorker sw = new SwingWorker() {
      @Override
      protected Object doInBackground() throws Exception {
        renderActions();
        String output = UtilApk.recompile(getTempDir().toAbsolutePath().toString(), "Dropmix190mod.apk");
        that.verifiedModApk = output;
        that.renderActions();
        AppState.switchCurrentProcess(Process.RECOMPILING, Process.NONE);
        if (useMod) {
          System.out.println("Modified APK is now ready");
        } else {
          System.out.println("Re-signed APK generated");
        }
        return output;
      }
    };
    sw.execute();
  }

  public void clearState() {
    try {
      if (this.verifiedModApk != null) {
        Files.deleteIfExists(Paths.get(this.verifiedModApk));
      }
    } catch (IOException e) { }
    this.verifiedModApk = null;
    renderActions();
  }
  public static Path getTempDir() {
    return Paths.get(modDir).toAbsolutePath();
  }
}
