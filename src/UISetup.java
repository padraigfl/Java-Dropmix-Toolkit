import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.concurrent.TimeUnit;

public class UISetup extends JPanel {
  AppState as;
  UIFilePicker apkFind;
  UIFilePicker dataZipFind;
  int updateCount = 0;
  StringBuilder log = new StringBuilder("Log: ");
  JTextPane logText;
  UIMain parentFrame;
  public UISetup(UIMain frame) {
    parentFrame = frame;
    as = AppState.getInstance();

    apkFind = new UIFilePicker("apk", "Android Package", "apk");
    dataZipFind = new UIFilePicker("zip", "data zip", "zip");
    logText = new JTextPane();
    logText.setText(log.toString());
    refresh();
  }
  public void refresh() {
    removeAll();
    if (as.apkFile == null) {
      add(apkFind);
      addFilePicker(apkFind, "Add APK File");
    } else {
      JTextPane text = new JTextPane();
      text.setText("APK: " + as.apkFile.getAbsolutePath());
      text.setEditable(false);
      add(text);
    }
    if (as.dataZip == null) {
      add(dataZipFind);
      addFilePicker(dataZipFind, "Add Data Zip");
    } else {
      JTextPane text = new JTextPane();
      text.setText("Data zip: " + as.apkFile.getAbsolutePath());
      text.setEditable(false);
      add(text);
    }

    JTextPane tp = new JTextPane();
    tp.setText("D/L apk");
    add(tp);
    if (as.apkFile != null || as.dataZip != null) {
      // Clear data button
      JButton clearBtn = buildButton(
        "Clear data",
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent ev) {
            as.dataZip = null;
            as.apkFile = null;
            apkFind.clearFileSelection();
            dataZipFind.clearFileSelection();
            refresh();
          }
        }
      );
      add(clearBtn);
    }

    if (as.apkFile != null) {
      // verify APK button
      JButton decompileBtn = buildButton(
        "Verify APK",
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent ev) {
            decompileApk();
          }
        }
      );
      add(decompileBtn);
    }
    add(logText);
    updateCount++;
    validate();
    repaint();;
  }

  public void addFilePicker(UIFilePicker picker, String buttonText) {
    JButton b = buildButton(buttonText, new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        File selectedFile = picker.openFileChooser();
        if (picker == apkFind) {
          as.apkFile = selectedFile;
        }
        if (picker == dataZipFind) {
          as.dataZip = selectedFile;
        }
        refresh();
      }
    });
    add(b);
  }
  public JButton buildButton(String buttonText, ActionListener action) {
    JButton btn = new JButton();
    btn.setText(buttonText);
    btn.addActionListener(action);
    return btn;
  }

  public boolean decompileApk() {
    try {
      // decompile
      PrintStream cmd = System.out;
      log.delete(0, log.length());
      PrintStream out = new PrintStream(new OutputStream() {
        @Override
        public void write(int b) throws IOException {
          log.append((char) b);
          logText.setText(log.toString());
          cmd.print((char) b);
          refresh();
        }
      });
      System.setOut(out);
      brut.apktool.Main.main(new String[]{"d", "-rf", as.apkFile.getAbsolutePath(), "-o", AssetsHandler.decompiledPath});
      System.out.println("Decompile completed");
      System.setOut(cmd);
      byte[] assetsFile = Helpers.loadLocalFile(AssetsHandler.assetsPath);
      AssetsHandler assetsHandler0 = new AssetsHandler(assetsFile, AssetsHandler.s0Header);
      as.setData(assetsFile);

      parentFrame.addCardsPanel();
      parentFrame.addPlaylistsPanel();
      return assetsFile.length > 100000; // file will be bigger than this, a bit arbitrary but should error here anyway
    } catch (Exception e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    }

  }
}
