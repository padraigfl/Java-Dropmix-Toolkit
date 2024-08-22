import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.Arrays;

public class AssetsHandler {
  public static final byte[] s0Header = {99,97,114,100,115,95,112,48,49,0,0,0};
  public static final byte[] s1Header =  { 99,97,114,100,115,0,0,0};
  public static final byte[] s2Header = {99,97,114,100,115,95,115,48,50,0,0,0};
  public static final String decompiledPath = "decompiled";
  public static final String assetsPath = decompiledPath + "/assets/bin/Data/sharedassets0.assets.split194";
  int startIdx;
  int dbLength;
  byte[] rawData;
  final static int TOTAL_CARDS = 441;
  String dbRaw;

  public AssetsHandler(byte[] rawData, byte[] startHeader) {
    this.rawData = rawData;
    this.startIdx = Helpers.getStartIndex(this.rawData, startHeader);
    this.dbLength = Helpers.intFromByteArray(
      Arrays.copyOfRange(this.rawData, this.startIdx, this.startIdx + 4));
    this.dbRaw = new String(
      Arrays.copyOfRange(this.rawData, this.startIdx + 4, this.startIdx + 4 + this.dbLength),
      StandardCharsets.UTF_8
    );
  }

  // TODO: this current requires matching key value pairs to keep data at same size
  /*
    TreeMap<String, String> test = new TreeMap<>();
    test.put("p01_c001_i01_maintitlesong_wild", "p01_c002_i01_flutterfly_wild");
    test.put("p01_c002_i01_flutterfly_wild", "p01_c001_i01_maintitlesong_wild");
   */
  public byte[] processSwapAction(TreeMap<String, String> swapPairs ) {
    String[] dbRows = (this.dbRaw).split("\n");

    rows:
    for (int i = 0; i < dbRows.length; i++) {
      entries:
      for (Map.Entry<String, String>
              entry : swapPairs.entrySet()) {
        if (dbRows[i].contains(entry.getKey())) {
          System.out.println("swapping");
          dbRows[i] = dbRows[i].replace(entry.getKey(), entry.getValue());
          continue rows;
        }
      }
    }

    String newDb = String.join("\n", dbRows);
    byte[] cloneRaw = this.rawData.clone();
    for (int i = 0; i < this.dbLength; i++) {
      cloneRaw[startIdx + i] = (byte) newDb.charAt(i);
    }
    return cloneRaw;
  }
}
