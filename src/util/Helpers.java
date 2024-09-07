package util;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.util.*;
import java.util.stream.Collectors;

public class Helpers {
  static byte[] assetsFile;

//  public static int intFromByteArray(byte[] bytes) {
//    return ByteBuffer.wrap(bytes).getInt();
//  }
  // Crude handling of byte array to get unsigned int value, required for calculating dataset lengths
  public static int intFromByteArray(byte[] bytes) {
    double counter = 0;
    double multiplier = 1;
    for (byte aByte : bytes) {
      int byteConverted = aByte & 0xff;
      counter += (byteConverted * multiplier);
      multiplier = multiplier * Math.pow(2, 8);
    }
    return (int) counter;
  }
  public static String rPad(String str, int length, char car) {
    return (str + String.format("%" + length + "s", "").replace(" ", String.valueOf(car))).substring(0, length);
  }
  public static byte[] getNRange(byte[] field, int idx, int len) {
    return Arrays.copyOfRange(field, idx, idx + len);
  }
  public static char[] byteToChars(byte[] field) {
    char[] chars = new char[field.length];
    int i = 0;
    for (byte b: field) {
      chars[i++] = (char) b;
    }
    return chars;
  }
  public static String bytetoString(byte[] field) {
    StringBuilder sb = new StringBuilder();
    for (byte b: field) {
      if (b != 13) {// end of entry character in level0
        sb.append((char) b);
      }
    }
    return sb.toString();
  }
  public static byte[] stringToByte(String row, boolean include13) {
    byte[] output = new byte[row.length() + (include13 ? 1 : 0)];
    int i = 0;
    for (char c : row.toCharArray()) {
      output[i++] = (byte) c;
    }
    if (include13) {
      output[output.length - 1] = 13;
    }
    return output;
  }

  public static byte[] loadLocalFile(String strPath) {
    try {
      return Files.readAllBytes(Paths.get(strPath));
    } catch (IOException e) {
      throw new Error("invalid-assets-file");
    }
  }

  public static byte[] loadFile(String file) {
    ClassLoader classLoader = Helpers.class.getClassLoader();

    try {
      String fileByteArrayPathString = classLoader.getResource(file).getFile();
      assetsFile = Files.readAllBytes(Paths.get(fileByteArrayPathString));
      return assetsFile;
    } catch (IOException | NullPointerException e) {
      throw new Error(e);
    }
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
  public static void logAction(String text) {
    System.out.println("ACTION: "+text);
  }

  public static <T> T[] getArrayFromList(List<T> thing) {
    return (T[]) thing.toArray(new Object[0]);
  }
  public static <T> List<T> getListFromArray(T[] arr) {
    return Arrays.stream(arr).collect(Collectors.toList());
  }

  public static String saveTempFile(String resourcePath, String tempFileName) {
    try {
      String srcFileName = UtilAdb.class.getResource(resourcePath).getFile();
      System.out.println(srcFileName);
      byte[] adbBytes = IOUtils.toByteArray(UtilAdb.class.getResourceAsStream(resourcePath));

      Path tempFilePath = Files.createTempFile(tempFileName, null);
      Files.write(tempFilePath, adbBytes);

      if (System.getProperty("os.name").toLowerCase().contains("mac")) {
        Files.setPosixFilePermissions(tempFilePath,
          EnumSet.of(
            PosixFilePermission.OWNER_READ,
            PosixFilePermission.OWNER_WRITE,
            PosixFilePermission.OWNER_EXECUTE,
            PosixFilePermission.GROUP_READ,
            PosixFilePermission.GROUP_WRITE,
            PosixFilePermission.GROUP_EXECUTE,
            PosixFilePermission.OTHERS_READ,
            PosixFilePermission.OTHERS_EXECUTE)
        );
      }
      System.out.println("Saved to" + tempFilePath.toString() + " " +
        Files.readAllBytes(tempFilePath).length
      );

      return tempFilePath.toAbsolutePath().toString();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static String byteArrayToString(byte[] bytes) {
    StringBuilder sb = new StringBuilder();
    for (byte b:bytes) {
      sb.append((char) b);
    }
    return sb.toString();
  }
}
