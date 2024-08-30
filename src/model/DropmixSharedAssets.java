package model;

import util.Counter;
import util.Helpers;

import java.util.*;
import java.util.function.BiConsumer;

// handles the assets file which modifications will be applied to
public class DropmixSharedAssets {
  public static final byte[] s0Header = {99,97,114,100,115,95,112,48,49,0,0,0};
  public static final byte[] s1Header =  { 99,97,114,100,115,0,0,0};
  public static final byte[] s2Header = {99,97,114,100,115,95,115,48,50,0,0,0};
  public static final byte[][] headers = { s0Header, s1Header, s2Header };
  public static final String decompiledPath = "decompiled";
  public static final String assetsRelativePath = "/assets/bin/Data/sharedassets0.assets.split194";
  public static final String unsignedPath = "unsigned.apk";
  public static final String signedPath = "Dropmix 1.9.0-signed";
  public final static int TOTAL_CARDS = 440;
  // raw assets file data, should not modify directly on swaps
  final byte[] rawData;
  // season data pulled directly from tables
  public TreeMap<Integer, SeasonTable> seasons;
  // card data for quicker reference; needs to be the same references as in seasons
  public TreeMap<String, CardDetail> cards;

  public DropmixSharedAssets(byte[] rawData) {
    this.rawData = rawData;
    this.seasons = new TreeMap<>();
    this.cards = new TreeMap<>();
    int seasonIdx = 0;
    int cardCounter = 0;
    for (byte[] header: headers) {
      int startIdx = getStartIndex(this.rawData, header);
      SeasonTable season = new SeasonTable(this.rawData, header, startIdx, seasonIdx);
      seasons.put(seasonIdx, season);
      for (CardDetail c: season.cards) {
        cards.put(c.getCardId(), c);
      }
      cardCounter += season.cards.length;
      seasonIdx++;
    }
    if (cardCounter != TOTAL_CARDS) {
      throw new RuntimeException("card-count-mismatch");
    }
  }

  // validates the data matches with the raw data that will be modified
  public boolean csvMatchesByteArray(String csv, SeasonTable season, byte[] newData) {
    int arrLength = Helpers.intFromByteArray((Arrays.copyOfRange(this.rawData, season.startIdx, season.startIdx)));
    return csv.length() == (arrLength - 4);
  }

  // returns copy of shared assets file to mod apk
  public byte[] applySwap(TreeMap<String, String> cardSwaps) {
    byte[] clonedAssetsFile = this.rawData.clone();
    DropmixSharedAssets modifiedAssets = new DropmixSharedAssets(clonedAssetsFile);
    ArrayList<String> alreadySwapped = new ArrayList<>();

    Set<String> changedSeasons = new HashSet<>();
    cardSwaps.forEach((s1, s2) -> {
      CardDetail c1 = modifiedAssets.cards.get(s1);
      CardDetail c2 = modifiedAssets.cards.get(s2);
      String c1CardId = c1.getCardId();
      String c2CardId = c2.getCardId();
      if (!alreadySwapped.contains(c1CardId) && !alreadySwapped.contains(c2CardId)) {
        // if both are from the same season there's no need to worry about the output table length
        boolean isSameSeason = c1.getCardSeason().equals(c2.getCardSeason());
        c1.setSourceCID(c2CardId, isSameSeason);
        c2.setSourceCID(c1CardId, isSameSeason);
        alreadySwapped.add(c1CardId);
        alreadySwapped.add(c2CardId);
        changedSeasons.add(c1.getCardSeason());
        changedSeasons.add(c2.getCardSeason());
      }
    });
    Counter databaseModified = new Counter(0);
    Counter databaseSize = new Counter(0);
    modifiedAssets.seasons.forEach(new BiConsumer<Integer, SeasonTable>() {
      @Override
      public void accept(Integer seasonIdx, SeasonTable seasonTable) {
        String seasonArray = SeasonTable.csvWriter(seasonTable.toNestedString(), ",", "\"", seasonIdx);
        byte[] seasonByteArray = new byte[seasonArray.length() + 4];
        for (int i = 0; i < 4; i++) {
          seasonByteArray[i] = seasonTable.rawDb[i];
        }
        char[] seasonCharArray = seasonArray.toCharArray();
        for (int i = 0; i < seasonArray.length(); i++) {
          seasonByteArray[i + 4] = (byte) seasonCharArray[i];
        }
        if (seasonByteArray.length != seasonTable.length) {
          if (seasonIdx == 0) {
            StringBuilder sb = new StringBuilder();
            for (byte b: seasonTable.rawDb) {
              sb.append((char) b);
            }
          }

          // TODO be consistent about lengths and start indexes
          if (seasonByteArray.length != seasonTable.rawDb.length) {
            throw new RuntimeException("modified-database-size-wrong:" + seasonByteArray.length + " " + seasonTable.rawDb.length);
          }
        }
        databaseSize.iterate(seasonTable.length);
        for (int i = 0; i < seasonByteArray.length; i++) {
          int currentIdx = seasonTable.startIdx + i;
          if (clonedAssetsFile[currentIdx] != seasonByteArray[i]) {
            clonedAssetsFile[currentIdx] = seasonByteArray[i];
            databaseModified.iterate();
          }
        }
      }
    });
    System.out.printf("Updated %d Seasons; %d of %d (%.2f) bytes", changedSeasons.size(), databaseModified.getCounter(), databaseSize.getCounter(), ((double) databaseModified.getCounter() / (double) databaseSize.getCounter()));
    return clonedAssetsFile;
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
}
