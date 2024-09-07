package util;

import model.AbstractDropmixDataRecord;
import model.DropmixSharedAssets;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

class HelpersTest {
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