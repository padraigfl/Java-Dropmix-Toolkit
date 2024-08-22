import java.util.Arrays;
import java.util.regex.Pattern;

public class Parameters {
  String action;
  String sourceApk;
  String destApk;
  String key;
  String cert;
  String modificationFile;
  boolean forceWrite = false;

  public Parameters(String[] args) {
    int startIdx = -1;
    for (int i = 0; i < args.length; i++) {
      if (args[i].startsWith("-")) {
        startIdx = i;
        break;
      }
    }
    if (startIdx == -1) {
      throw new Error("params-malformed");
    }
    for (int i = startIdx; i < args.length; i += 2) {

      System.out.println(i);
      System.out.println(args[i]);
      System.out.println(args[i+1]);
      setParamType(args[i], args[i+1]);
    }
  }

  private void setParamType(String param, String value) {
    String[] actionName = { "--action", "-a" };
    String[] sourceName = { "--source", "-s" };
    String[] destName = { "--destination", "-d" };
    String[] signKeyName = { "--key", "-k" };
    String[] signCertName = { "--cert", "-c" };
    String[] modificationFileName = { "--mod", "-m" };
    String[] forceName = { "--force" };
    if (value.matches("/^-+/")) {
      throw new Error("parameter-as-value");
    }

    if (Arrays.asList(actionName).contains(param)) {
      this.action = value;
      return;
    }

    if (Arrays.asList(sourceName).contains(param)) {
      this.sourceApk = value;
      return;
    }
    if (Arrays.asList(destName).contains(param)) {
      this.destApk = value;
      return;
    }
    if (Arrays.asList(signKeyName).contains(param)) {
      this.key = value;
      return;
    }
    if (Arrays.asList(signCertName).contains(param)) {
      this.cert = value;
      return;
    }
    if (Arrays.asList(modificationFileName).contains(param)) {
      this.modificationFile = value;
      return;
    }
    if (Arrays.asList(forceName).contains(param)) {
      this.forceWrite = true;
      return;
    }
    throw new Error("parameter-invalid");
  }
}
