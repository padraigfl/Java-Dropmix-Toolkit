package model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import util.Helpers;

import java.util.ArrayList;
import java.util.TreeMap;

public class DropmixLevel0Test {
  DropmixLevel0 dropmixLevel0 = new DropmixLevel0(Helpers.loadFile("level0.split3"));

  @Test
  public void testAssets() {
    Assertions.assertEquals(dropmixLevel0.cards.length, 440);
    for (DropmixLevel0Card c : dropmixLevel0.cards) {
      Assertions.assertEquals(38, c.card.data.size());
    }
  }

  // verifies changing fields doesn't result in the database becoming corrupted
  // @Test
  public void testDatabaseConsistency() {
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
    String CID = "CID";

    for (String[] swap: cardSwaps) {
      if (swap[0] == null) {
        continue;
      }
      swapCards.put(swap[0], swap[1]);
      swapCards.put(swap[1], swap[0]);
    }
    int i = 0;
    for (DropmixLevel0Card c: dropmixLevel0.cards) {
      String cardId = c.card.data.get(CID);
      if (cardId.equals(cardSwaps[i][0])) {
        c.updateEntry(CID, cardSwaps[i][1]);
      }
      if (cardId.equals(cardSwaps[i][1])) {
        c.updateEntry(CID, cardSwaps[i][0]);
      }
    }
    byte[] converted = dropmixLevel0.backToByteArray();
    Assertions.assertEquals(dropmixLevel0.raw.length, converted.length);

    int lastIdx = 0;
    int range = 200;
    for (i = 735140; i < 952188; i++) {
      if (dropmixLevel0.raw[i] != converted[i] && (i - lastIdx) > range){
        lastIdx = i;
        for (int j = i - range; j < i + range; j++) {
          System.out.print((char) dropmixLevel0.raw[j]);
        }
        System.out.println();
        for (int j = i - range; j < i + range; j++) {
          System.out.print((char) converted[j]);
        }
        System.out.println("\n-----");
      }
    }
  }

  @Test
  public void testRowParser() {
    String[] texts = new String[]{
      "\"FX_0070_FX\",\"Harmonix Music\",\"With The Pack\",\"fx_225\",\"Marta Sokolowska\",\"animalsfx2_fx\",\"FX\",16,2,,,,,\"genre_fx\",,\"FX\",\"instant:cover(type{Lead},draw{Self,1})\",\"fx_cover_lead_draw1\",\"FxMode\",123,\"D\",\"minor\",,\"No\",,-2,-1,0,1,2,3,4,5,6,7,-4,-3,\"WITH THE PACK AS PERFORMED BY HARMONIX MUSIC. WRITTEN BY HARMONIX MUSIC.\"",
      "\"LIC_0141_Loop\",\"Fall Out Boy\",\"Centuries\",\"Centuries_LOOP\",\"YONIL\",\"centuries_loop\",\"Loop\",32,3,\"Synth\",,,,\"genre_pop genre_rock\",2015,\"LIC\",,,,88,\"E\",\"minor\",,\"No\",\"0.5\",,,,,,,,,,,,,\"CENTURIES AS PERFORMED BY FALL OUT BOY COURTESY OF ISLAND RECORDS UNDER LICENSE FROM UNIVERSAL MUSIC ENTERPRISES. WRITTEN BY SETH RAO, MICHAEL FONSECA, ANDREW JOHN HURLEY, JONATHAN ROTEM, JOSEPH MARK TROHMAN, PATRICK MARTIN STUMPH, PETER WENTZ, JUSTIN TRANTER, RAJA KUMARI, AND SUZANNE VEGA.  PUBLISHED BY GANGES FLOW MUSIC/THESE ARE SONGS OF PULSE (ASCAP), MUSIC OF RADAR OBO ITSELF AND THE GREAT O MUSIC (ASCAP), SONY/ATV SONGS LLC, CHICAGO X SOFTCORE SONGS, JONATHAN ROTEM MUSIC, WB MUSIC CORP. (ASCAP), WAIFERSONGS LTD. (ASCAP), WARNER-TAMERLANE PUBLISHING CORP. (BMI) AND JUSTIN'S SCHOOL FOR GIRLS (BMI). ALL RIGHTS OBO ITSELF AND WAIFERSONGS LTD. ADMINISTERED BY WB MUSIC CORP. ALL RIGHTS OBO ITSELF AND JUSTIN'S SCHOOL FOR GIRLS ADMINISTERED BY WARNER-TAMERLANE PUBLISHING CORP. CONTAINS SAMPLE OF \"TOM'S DINER\" BY SUZANNE VEGA. WB MUSIC CORP AND WAIFERSONGS LTD.\""
    };
    for (String text: texts) {
      String[] output = DropmixLevel0Card.rowParser(text);
      Assertions.assertEquals(38, output.length);
    }
  }

  @Test void testCardSwap() {
    TreeMap<String, String> swap = new TreeMap<>();
    swap.put("LIC_0058_Wild", "FX_0022");
    swap.put("FX_0044", "HMX_0030_Loop");
    swap.put("FX_0022", "LIC_0058_Wild");
    swap.put("HMX_0030_Loop", "FX_0044");
    byte[] newDb = dropmixLevel0.applySwap(swap);
    DropmixLevel0 swappedSet = new DropmixLevel0(newDb);
    Assertions.assertEquals(dropmixLevel0.dataLength, swappedSet.dataLength);
    Assertions.assertArrayEquals(newDb, swappedSet.backToByteArray());
    Assertions.assertEquals(dropmixLevel0.cards.length, swappedSet.cards.length);
    int lastCardIdx = dropmixLevel0.cards.length - 1;
    Assertions.assertEquals(dropmixLevel0.cards[lastCardIdx].toString(), swappedSet.cards[lastCardIdx].toString());
    int testDbRange = 600;
    int testIndexStart = dropmixLevel0.startIdx + dropmixLevel0.dataLength - testDbRange;
    Assertions.assertArrayEquals(
      Helpers.getNRange(dropmixLevel0.raw, testIndexStart, testDbRange),
      Helpers.getNRange(newDb, testIndexStart, testDbRange)
    );
  }
}
