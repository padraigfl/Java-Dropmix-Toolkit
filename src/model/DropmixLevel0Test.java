package model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import util.Helpers;

public class DropmixLevel0Test {
  Helpers helper = new Helpers();
  DropmixLevel0 dropmixSharedAssets = new DropmixLevel0(helper.loadFile("level0.split3"));

  @Test
  public void testAssets() {
    Assertions.assertEquals(dropmixSharedAssets.cards.length, 440);
  }
}
