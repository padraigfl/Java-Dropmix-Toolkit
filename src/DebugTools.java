public class DebugTools {
  static boolean logOutput = false;
  public static void log(CharSequence s) {
    if (logOutput) {
      System.out.println(s);
    }
  }
}
