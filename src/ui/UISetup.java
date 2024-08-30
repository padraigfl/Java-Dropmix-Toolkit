package ui;

import brut.common.BrutException;
import com.android.apksigner.ApkSignerTool;
import model.AppState;
import model.Process;
import net.lingala.zip4j.ZipFile;
import se.vidstige.jadb.JadbConnection;
import se.vidstige.jadb.JadbDevice;
import se.vidstige.jadb.JadbException;
import ui.components.UIFilePicker;
import model.DropmixSharedAssets;
import util.UtilApk;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.zip.ZipException;

import static util.UtilApk.decompileApk;

public class UISetup extends JPanel {
  AppState as;
  UIFilePicker apkFind;
  UIFilePicker dataZipFind;
  UIFilePicker obbZipFind;
  int updateCount = 0;
  UIMain parentFrame;
  public boolean apkVerified;
  public UISetup(UIMain frame) {
    parentFrame = frame;
    as = AppState.getInstance();
    setLayout(new GridBagLayout());

    refresh();
  }
  private GridBagConstraints getDefaultGridConstraints(int y) {
    GridBagConstraints c = new GridBagConstraints();
    c.gridheight = 1;
    c.gridy = y;
    c.fill = GridBagConstraints.HORIZONTAL;
    c.weighty = 1.0;
    c.weightx = 1.0;
    return c;
  }
  public void refresh() {
    removeAll();
    apkFind = new UIFilePicker("apk", "Android Package", "apk");
    dataZipFind = new UIFilePicker("zip", "data zip", "zip");
    obbZipFind = new UIFilePicker("zip", "obb zip", "zip");
    int y = 0;
    GridBagConstraints c = getDefaultGridConstraints(y);

    renderApkInput(y++);
    renderDataZipInput(y++);
    renderObbZipInput(y++);
//    if (AppState.getInstance().apkFile == null) {
//      renderArchiveInput(y++);
//    }
    renderAddAdb(y++);
    renderClearInput(y++);

    c.gridheight = GridBagConstraints.REMAINDER;
    c.gridy = y;
    add(new JPanel(), c);
    updateCount++;

    validate();
    repaint();;
  }

  public void renderAddAdb(int row) {
    GridBagConstraints c = getDefaultGridConstraints(row);
    JadbDevice device = AppState.getInstance().adbDevice;
    if (device != null) {
      add(SwingFactory.buildText("Device: "+device), c);
      return;
    }
    c.gridwidth = 2;
    add(SwingFactory.buildText("No device added"), c);
    c.gridwidth = 1;
    c.gridx = 2;
    JButton connectDeviceBtn = SwingFactory.buildButton(
      "Add ADB device",
      new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent ev) {
          try {
            JadbConnection jadb = new JadbConnection();
            List<JadbDevice> devices = jadb.getDevices();
            if (devices.size() == 1) {
              JadbDevice device = devices.getFirst();
              AppState.getInstance().adbDevice = device;
              System.out.println("Android device connected: " + device.toString() );
              refresh();
            } else if (devices.size() > 1) {
              System.out.println("Please connect only one ADB device (this restriction can hopefully be refined in the future)");
            } else {
              System.out.println("No ADB connected device found");
            }
          } catch (JadbException | IOException e) {
            e.printStackTrace();
            System.out.println(e);
            throw new Error(e);
          }
        }
      });
      add(connectDeviceBtn, c);
  }
  public void renderClearInput(int row) {
    GridBagConstraints c = getDefaultGridConstraints(row);
    c.gridwidth = 3;
    c.gridx = 0;
    if (as.apkFile != null || as.dataZip != null) {
      // Clear data button
      UISetup that = this;
      JButton clearBtn = SwingFactory.buildButton(
        "Clear data",
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent ev) {
            apkFind.clearFileSelection();
            dataZipFind.clearFileSelection();
            obbZipFind.clearFileSelection();
            that.apkVerified = false;
            as.reset();
            util.Helpers.logAction("Setup cleared");
            refresh();
          }
        }
      );
      add(clearBtn, c);
    }
  }
  // people might just want a single file install function??
//  public void renderArchiveInput(int row) {
//    GridBagConstraints c = getDefaultGridConstraints(row);
//    c.gridwidth = 2;
//    c.gridx = 0;
//    add(SwingFactory.buildText("Use bundled Archive file"), c);
//    c.gridwidth = 1;
//    c.gridx = 2;
//    add(SwingFactory.buildButton("use Archive", new ActionListener() {
//      @Override
//      public void actionPerformed(ActionEvent e) {
//        System.out.println("not implemented yet");
//      }
//    }), c);
//  }
  public void renderApkInput(int row) {
    GridBagConstraints c = getDefaultGridConstraints(row);
    if (as.apkFile == null) {
      c.gridx = 0;
      c.gridwidth = 2;
      add(SwingFactory.buildText("No APK added yet"), c);
      c.gridx = 2;
      c.gridwidth = 1;
      add(addFilePicker(apkFind, "Add APK File"), c);
    }
    else {
      c.gridx = 0;
      c.gridwidth = 2;
      add(SwingFactory.buildText("APK: " + as.apkFile.getAbsolutePath()), c);
      c.gridx = 2;
      c.gridwidth = 1;
      UISetup that = this;
      JButton decompileBtn = SwingFactory.buildButton(
        "Verify APK",
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent ev) {
            System.out.println("Verifying APK: Setting up database...");
            if (decompileApk(as.apkFile.getAbsolutePath(), DropmixSharedAssets.decompiledPath)) {
              parentFrame.addCardsPanel();
              parentFrame.addPlaylistsPanel();
              that.apkVerified = true;
              refresh();
            }
          }
        }
      );
      decompileBtn.setEnabled(!that.apkVerified);
      add(decompileBtn, c);
    }
  }
  public void renderDataZipInput(int row) {
    GridBagConstraints c = getDefaultGridConstraints(row);
    if (as.dataZip == null) {
      // add(dataZipFind);
      c.gridwidth = 2;
      add(SwingFactory.buildText("No data zip added yet"), c);
      c.gridwidth = 1;
      c.gridx = 2;
      add(addFilePicker(dataZipFind, "Add Data Zip"), c);
    }
    else {
      c.gridwidth = 2;
      add(SwingFactory.buildText("Data zip: " + as.dataZip.getAbsolutePath()), c);
    }
  }

  public void renderObbZipInput(int row) {
    GridBagConstraints c = getDefaultGridConstraints(row);
    if (as.obbZip == null) {
      // add(dataZipFind);
      c.gridwidth = 2;
      add(SwingFactory.buildText("No obb zip added yet"), c);
      c.gridwidth = 1;
      c.gridx = 2;
      add(addFilePicker(obbZipFind, "Add Obb Zip"), c);
    }
    else {
      c.gridwidth = 2;
      add(SwingFactory.buildText("Obb zip: " + as.obbZip.getAbsolutePath()), c);
    }
  }

  public JButton addFilePicker(UIFilePicker picker, String buttonText) {
    return SwingFactory.buildButton(buttonText, new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        File selectedFile = picker.openFileChooser();
        String text = "";
        AppState as = AppState.getInstance();
        if (picker == apkFind) {
          text = "APK Selected: " + selectedFile.getAbsolutePath();
          as.apkFile = selectedFile;
        }
        if (picker == dataZipFind) {
          text = "Data Zip Selected: " + selectedFile.getAbsolutePath();
          as.dataZip = selectedFile;
        }
        if (picker == obbZipFind) {
          as.obbZip = selectedFile;
          try {
            ZipFile zf = new ZipFile(selectedFile.getAbsolutePath());
            zf.extractAll("test");
            System.out.println();
          } catch (IOException ex) {
            ex.printStackTrace();
          }
          text = "Obb zip selected" + selectedFile.getAbsolutePath();
        }
        System.out.println(text);
        refresh();
      }
    });
  }
}