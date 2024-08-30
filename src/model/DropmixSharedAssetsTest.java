package model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import util.Helpers;

import java.util.TreeMap;

class DropmixSharedAssetsTest {
  Helpers helper = new Helpers();
  DropmixSharedAssets dropmixSharedAssets = new DropmixSharedAssets(helper.loadFile());

//  @Test
//  void validateConstructor() {
//    Assertions.assertEquals(dropmixSharedAssets.seasons.size(), 3);
//    Assertions.assertEquals(dropmixSharedAssets.cards.size(), DropmixSharedAssets.TOTAL_CARDS);
////    TreeMap<String, String> test = new TreeMap<>();
////    test.put("p01_c001_i01_maintitlesong_wild", "p01_c002_i01_flutterfly_wild");
////    test.put("p01_c002_i01_flutterfly_wild", "p01_c001_i01_maintitlesong_wild");
////    byte[] newDb = DropmixSharedAssets.processSwapAction(test);
////    byte[] dbParsed = Arrays.copyOfRange(newDb, DropmixSharedAssets.startIdx, DropmixSharedAssets.startIdx + DropmixSharedAssets.dbLength);
////    Assertions.assertEquals(dbParsed.length, DropmixSharedAssets.dbLength);
////    Assertions.assertEquals(newDb.length, DropmixSharedAssets.rawData.length);
//  }

  @Test
  void applySwap() {
    TreeMap<String, String> swapCards = new TreeMap<>();
    String[][] cardSwaps = new String[5][2];

    // cardSwaps[0]  = new String[]{ "LIC_0031_Wild", "LIC_0185_Wild" };
    // cardSwaps[0] = new String[]{ "FX_0044", "LIC_0031_Wild" };

    // two swaps of just season 1, different lengths
    cardSwaps[0] = new String[]{ "LIC_0058_Wild", "FX_0022" };
    cardSwaps[1] = new String[]{ "FX_0044", "HMX_0030_Loop" };
    // season swaps, different lengths
    // s0 to s1
    cardSwaps[2] = new String[]{ "LIC_0031_Wild", "FX_0024" };
//    // s0 to s2
    cardSwaps[3] = new String[]{ "LIC_0185_Wild", "FX_0057", };
//    // s1 to s2
    cardSwaps[4] = new String[]{ "HMX_0001_Loop", "FX_0074_FX" };

    for (String[] swap: cardSwaps) {
      if (swap[0] == null) {
        continue;
      }
      swapCards.put(swap[0], swap[1]);
      swapCards.put(swap[1], swap[0]);
    }
    byte[] moddedFile = dropmixSharedAssets.applySwap(swapCards);
    Assertions.assertEquals(moddedFile.length, dropmixSharedAssets.rawData.length);
    Assertions.assertNotEquals(moddedFile, dropmixSharedAssets.rawData);
  }

  @Test
  void replaceRogueCommas() {
    String rt = "\"Flo Rida\",\"I Don't Like It, I Love It (ft. Robin Thicke, Verdine White)\",\"Loop\"";
    String expect =  "\"Flo Rida\",\"I Don't Like It£ I Love It (ft. Robin Thicke£ Verdine White)\",\"Loop\"";
    String formatted = SeasonTable.replaceRogueCommas(rt, ",");
    Assertions.assertEquals(expect, formatted);
  }
}