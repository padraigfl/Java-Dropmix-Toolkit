import model.AppState;
import ui.UIMain;
import util.UtilAdb;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
  public static void main(String[] args) {
    // String signKey = "./SignApk-master/key.pk8";
    // String signCert = "./certificate.pem";
    for (int i = 0; i < args.length; i++) {
      System.out.println(args[i]);
    }
    UtilAdb.setAdbPath();

    new UIMain();

    UtilAdb.startServer();
  }

  public static String adbDevice() {
    try {
      List<String> cmd = new LinkedList<>();
      cmd.add("external_tools/adb");
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