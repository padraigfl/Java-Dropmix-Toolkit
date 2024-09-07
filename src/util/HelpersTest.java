package util;

import model.AbstractDropmixDataRecord;
import model.DropmixSharedAssets;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

  @Test
  void getStartIndexTest() {
    byte[] bytes = Helpers.loadFile("sharedassets0.assets.split194");
    int startIdx = AbstractDropmixDataRecord.getStartIndex(bytes, DropmixSharedAssets.s0Header);
    Assertions.assertEquals(startIdx, 808232);
  }

  @Test
  void getDBLengthTest() {
    byte[] bytes = Helpers.loadFile("sharedassets0.assets.split194");
    int startIdx = AbstractDropmixDataRecord.getStartIndex(bytes, DropmixSharedAssets.s0Header);
    int crude = Helpers.intFromByteArray(Arrays.copyOfRange(bytes, startIdx, startIdx+4));
    System.out.println();
    Assertions.assertEquals(873, crude);
  }
}