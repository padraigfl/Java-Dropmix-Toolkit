package util;

import model.DropmixSharedAssets;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

class HelpersTest {
  static byte[] header = { 67,73,68,44,83,111,117,114,99,101,32,67,73,68,44,67,84,73,44,73,116,101,109,32,73,68,44,73,116,101,109,32,84,121,112,101,44,80,111,119,101,114,44,68,101,99,107,32,73,68,44,83,101,114,105,101,115,32,73,99,111,110,44,83,101,114,105,101,115,32,73,110,100,101,120,44,83,101,114,105,101,115,32,67,111,117,110,116,44,83,101,97,115,111,110,44,65,114,116,105,115,116,82,101,102,44,83,111,110,103,82,101,102,44,84,121,112,101,82,101,102,44,71,101,110,114,101,82,101,102,44,80,114,105,110,116,32,73,68 };
  static byte[] assetsFile;

  @Test
  void fromByteArrayTest() {
    byte[] barray = { 0x01, 0x01, 0x0000, 0x0000 };
    int val = Helpers.intFromByteArray(barray);
    Assertions.assertEquals(val, 257);
  }

  @Test
  void rPadTest() {
    Assertions.assertEquals(Helpers.rPad("Hello", 10, '!'), "Hello!!!!!");
  }

  byte[] loadFile() {
    if (assetsFile != null) {
      return assetsFile;
    }
    ClassLoader classLoader = getClass().getClassLoader();

    try {
      String fileByteArrayPathString = classLoader.getResource("sharedassets0.assets.split194").getFile();
      assetsFile = Files.readAllBytes(Path.of(fileByteArrayPathString));
      return assetsFile;
    } catch (IOException | NullPointerException e) {
      throw new Error(e);
    }
  }

  @Test
  void getStartIndexTest() {
    byte[] bytes = loadFile();
    int startIdx = DropmixSharedAssets.getStartIndex(bytes, DropmixSharedAssets.s0Header);
    Assertions.assertEquals(startIdx, 808232);
  }

  @Test
  void getDBLengthTest() {
    byte[] bytes = loadFile();
    int startIdx = DropmixSharedAssets.getStartIndex(bytes, DropmixSharedAssets.s0Header);
    int crude = Helpers.intFromByteArray(Arrays.copyOfRange(bytes, startIdx, startIdx+4));
    System.out.println();
    Assertions.assertEquals(873, crude);
  }

  @Test
  void getDataArray() {
    byte[] bytes = loadFile();
    int startIdx = DropmixSharedAssets.getStartIndex(bytes, DropmixSharedAssets.s1Header);
    int crude = Helpers.intFromByteArray(Arrays.copyOfRange(bytes, startIdx, startIdx+4));
    String[][] rowData = Helpers.getByteRowSplit(bytes, startIdx + 4, crude, false);

    for (int i = 1; i < rowData.length; i++) {
      Assertions.assertEquals(rowData[0].length, rowData[i].length);
    }
  }
}