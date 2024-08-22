import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.TreeMap;
import java.util.regex.Pattern;

public class Helpers {
  static byte[] assetsFile;

//  public static int intFromByteArray(byte[] bytes) {
//    return ByteBuffer.wrap(bytes).getInt();
//  }
  // Crude handling of byte array to get unsigned int value, required for calculating dataset lengths
  public static int intFromByteArray(byte[] bytes) {
    double counter = 0;
    double multiplier = 1;
    for (int i = 0; i < bytes.length; i++) {
      int byteConverted = bytes[i] & 0xff;
      counter += (byteConverted * multiplier);
      multiplier = multiplier * Math.pow(2, 8);
    }
    return (int) counter;
  }
  public static String rPad(String str, Integer length, char car) {
    return (str + String.format("%" + length + "s", "").replace(" ", String.valueOf(car))).substring(0, length);
  }
  public static int getStartIndex(byte[] rawData, byte[] startSequence) {
    outer:
    for (int i = 0; i < rawData.length; i++) {
      if (rawData[i] == startSequence[0]) {
        inner:
        for (int j = 1; j < startSequence.length &&  (j+i) < rawData.length; j++ ) {
          if (rawData[i + j] != startSequence[j]) {
            continue outer;
          }
        }
        return i + startSequence.length;
      }
    }
    return -1;
  }
  public static byte[] get4Range(byte[] field, int idx) {
    return Arrays.copyOfRange(field, idx, idx + 4);
  }

  public static byte[] loadLocalFile(String strPath) {
    try {
      return Files.readAllBytes(Paths.get(strPath));
    } catch (IOException e) {
      throw new Error("invalid-assets-file");
    }
  }

  public byte[] loadFile() {
    ClassLoader classLoader = getClass().getClassLoader();

    try {
      String fileByteArrayPathString = classLoader.getResource("sharedassets0.assets.split194").getFile();
      assetsFile = Files.readAllBytes(Path.of(fileByteArrayPathString));
      return assetsFile;
    } catch (IOException | NullPointerException e) {
      throw new Error(e);
    }
  }

  public static TreeMap<String, String> getSampleTreeMap() {
    TreeMap<String, String> test = new TreeMap<>();
    test.put("p01_c001_i01_maintitlesong_wild", "p01_c002_i01_flutterfly_wild");
    test.put("p01_c002_i01_flutterfly_wild", "p01_c001_i01_maintitlesong_wild");
    return test;
  }

  // Each seasons data table has some quirks which need to be handled here
  // TODO document quirks
  public static String[][] getByteRowSplit(byte[] bytes, int dataStartIdx, int arrayLength, boolean isSeason2) {
    // dataStartIdx should correspond to the first piece of valid text
    ArrayList<String[]> data = new ArrayList<String[]>();
    StringBuilder rawString = new StringBuilder();
    for (int i = 0; i < arrayLength; i++) {
      char byteCastChar = (char)(char) bytes[dataStartIdx + i];
      rawString.append(byteCastChar);
    }
    String commaRegEx = "(\"[A-Za-z\\s]+\\s),";
    String fillInString = ";";
    String[] rawStringSplit = rawString.toString().split("\n");
    for (int i = 0; i < rawStringSplit.length; i++) {
      String s = rawStringSplit[i];
      String rawRow = s;
      if (isSeason2 && s.contains("\"")) {
        rawRow = rawRow.replaceAll(commaRegEx, "$1"+fillInString+" ");
      } else if (i > 0) { // Robin Thicke has a comma in the title, heading follows different format
        rawRow = rawRow.replaceAll(commaRegEx, "$1"+fillInString+ " ");
      }
      String[] dataRow = rawRow.split(",");
      for (int j = 0; j < dataRow.length; j++) {
        if (dataRow[j].contains(fillInString)) {
          dataRow[j] = dataRow[j].replaceAll(fillInString, ";");
        }
      }
      data.add(dataRow);
    }
    return data.toArray(new String[data.size()][data.getFirst().length]);
  }

  static byte[] stringToByteArray(String str) {
    byte[] byteArray = new byte[str.length()];
    for (int i = 0; i < str.length(); i++) {
      byteArray[i] = (byte) str.charAt(i);
    }
    return byteArray;
  }
  public static byte[] getNewBytesetFromData(String[][] data, int season, int arrayLength) {
    byte[] byteData = new byte[arrayLength];
    int indexCounter = 0;
    for (int i = 0; i < data.length; i++) {
      String combined = String.join(",", data[i]);
      for (int j = 0; j < data[i].length; j++) {
        byteData[indexCounter] = (byte) combined.charAt(j);
        indexCounter++;
      }
    }
    return byteData;
  }

  public static String addQuotes(String str) {
    String quoteChar = "\"";
    StringBuilder sb = new StringBuilder();
    if (!str.startsWith(quoteChar)) {
      sb.append(quoteChar);
    }
    sb.append(str);
    if (!str.endsWith(quoteChar)) {
      sb.append(quoteChar);
    }
    return sb.toString();
  }
  public static String removeQuotes(String str) {
    String quoteChar = "\"";
    int len = str.length();
    return str.substring(str.startsWith(quoteChar) ? 1 : 0, str.endsWith(quoteChar) ? len - 1 : len);
  }
  public static boolean deleteDirectory(File directoryToBeDeleted) {
    System.out.println("Deleting directory " + directoryToBeDeleted.getName()+ "...");
    File[] allContents = directoryToBeDeleted.listFiles();
    if (allContents != null) {
      for (File file : allContents) {
        deleteDirectory(file);
      }
    }
    return directoryToBeDeleted.delete();
  }
}
