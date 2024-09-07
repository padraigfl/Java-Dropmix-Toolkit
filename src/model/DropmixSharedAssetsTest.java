package model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import util.Helpers;

import java.util.TreeMap;

class DropmixSharedAssetsTest {
  Helpers helper = new Helpers();
  DropmixSharedAssets dropmixSharedAssets = new DropmixSharedAssets(helper.loadFile("sharedassets0.assets.split194"));

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
    String[][] cardSwaps = new String[][]{
            // two swaps of just season 1, different lengths
            new String[]{ "LIC_0058_Wild", "FX_0022" },
            new String[]{ "FX_0044", "HMX_0030_Loop" },
            // season swaps, different lengths
            // s0 to s1
            new String[]{ "LIC_0031_Wild", "FX_0024" },
            //    // s0 to s2
            new String[]{ "LIC_0185_Wild", "FX_0057", },
            //    // s1 to s2
            new String[]{ "HMX_0001_Loop", "FX_0074_FX" },
    };

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
  void applySwapOfPlaylists() {

    AppState as = AppState.getInstance();
    as.assetsHandler = dropmixSharedAssets;
    TreeMap<String, String> playlists = new TreeMap<>();
    String[] names = new String[]{"derby", "ouroboros", "mirrors", "astro", "energy", "city", "flora", "verdant", "rhymer", "phase", "instinct", "fantastic" };
    for (int i=0; i< names.length; i++) {
      playlists.put(names[i], names[names.length - 1 -i]);
    }
    TreeMap<String, String> swapCards = AppState.getCardSwapFromPlaylist(playlists, false);

    int oldLength = dropmixSharedAssets.rawData.length;
    byte[] moddedFile = dropmixSharedAssets.applySwap(swapCards);
    DropmixSharedAssets newVersion = new DropmixSharedAssets(moddedFile);
    Assertions.assertEquals(moddedFile.length, oldLength);
    Assertions.assertNotEquals(moddedFile, dropmixSharedAssets.rawData);
    for (int i = 0; i < 3; i++) {
      DropmixSharedAssetsSeason s1 = dropmixSharedAssets.seasons.get(i);
      DropmixSharedAssetsSeason s2 = newVersion.seasons.get(i);
      byte[] original = Helpers.getNRange(s1.rawDb, 4, s1.rawDb.length - 4);
      byte[] modified = s2.backToByteArray(i == 2);
      Assertions.assertEquals(s1.length, s2.length);
      Assertions.assertEquals(s1.startIdx, s2.startIdx);
      Assertions.assertNotEquals(original, modified);
      int range = 80;
      byte[] endRangeA = Helpers.getNRange(original, original.length - range, range);
      byte[] endRangeB = Helpers.getNRange(modified, modified.length - range, range);
      int lastIdx = 0;
      for (int k = 0; k < original.length; k++) {
        if (original[k] != modified[k] && (k - lastIdx) > range) {
          lastIdx = k;
          byte[] r1 = Helpers.getNRange(original, k - range, range * 2);
          byte[] r2 = Helpers.getNRange(modified, k - range, range * 2);
          StringBuilder a1 = new StringBuilder();
          StringBuilder a2 = new StringBuilder();
          for (int l = 0; l < range*2; l++) {
            a1.append((char) r1[l]);
            a2.append((char) r2[l]);
          }
          System.out.println(a1);
          System.out.println(a2);
          return;
        }
      }
      System.out.println(endRangeA[0]);
      System.out.println(endRangeB[0]);
      Assertions.assertArrayEquals(endRangeA, endRangeB);
    }
  }
  @Test
  void replaceRogueCommas() {
    String rt = "\"Flo Rida\",\"I Don't Like It, I Love It (ft. Robin Thicke, Verdine White)\",\"Loop\"";
    String expect =  "\"Flo Rida\",\"I Don't Like It£ I Love It (ft. Robin Thicke£ Verdine White)\",\"Loop\"";
    String formatted = DropmixSharedAssetsSeason.replaceRogueCommas(rt, ",");
    Assertions.assertEquals(expect, formatted);
  }
}