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
  public TreeMap<Integer, DropmixSharedAssetsSeason> seasons;
  // card data for quicker reference; needs to be the same references as in seasons
  public TreeMap<String, DropmixSharedAssetsCard> cards;

  public DropmixSharedAssets(byte[] rawData) {
    this.rawData = rawData;
    this.seasons = new TreeMap<>();
    this.cards = new TreeMap<>();
    int seasonIdx = 0;
    int cardCounter = 0;
    for (byte[] header: headers) {
      int startIdx = AbstractDropmixDataRecord.getStartIndex(this.rawData, header);
      DropmixSharedAssetsSeason season = new DropmixSharedAssetsSeason(this.rawData, header, startIdx, seasonIdx);
      seasons.put(seasonIdx, season);
      for (DropmixSharedAssetsCard c: season.cards) {
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
  public boolean csvMatchesByteArray(String csv, DropmixSharedAssetsSeason season, byte[] newData) {
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
      DropmixSharedAssetsCard c1 = modifiedAssets.cards.get(s1);
      DropmixSharedAssetsCard c2 = modifiedAssets.cards.get(s2);
      String c1CardCTI = c1.getCardCTI();
      String c2CardCTI = c2.getCardCTI();

      if (!alreadySwapped.contains(c1CardCTI) && !alreadySwapped.contains(c2CardCTI)) {

        changedSeasons.add("" + c1.getCardSeason());
        changedSeasons.add("" + c2.getCardSeason());
        // if both are from the same season there's no need to worry about the output table length
        boolean isSameSeason = c1.getCardSeason() == (c2.getCardSeason());
        c1.setCTI(c2CardCTI, isSameSeason);
        c2.setCTI(c1CardCTI, isSameSeason);
        alreadySwapped.add(c1CardCTI);
        alreadySwapped.add(c2CardCTI);
      }
    });
    Counter databaseModified = new Counter(0);
    Counter databaseSize = new Counter(0);
    modifiedAssets.seasons.forEach(new BiConsumer<Integer, DropmixSharedAssetsSeason>() {
      @Override
      public void accept(Integer seasonIdx, DropmixSharedAssetsSeason seasonTable) {
        byte[] seasonByteArray = seasonTable.backToByteArray(false);

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
    int sCount = 0;
    for (DropmixSharedAssetsSeason s: modifiedAssets.seasons.values()) {
      int size = s.length;
      StringBuilder start = new StringBuilder();
      StringBuilder end = new StringBuilder();
      StringBuilder all = new StringBuilder();
      StringBuilder old = new StringBuilder();
      int printRange = 1600;
      if (s.length < printRange) {
        continue;
      }
      for (int i = 0; i < printRange; i++) {
        all.append((char) s.rawDb[i]);
      }
      DropmixSharedAssetsSeason oldS= seasons.get(sCount);
      for (int i = 0; i < size; i++) {
        try {
          all.append((char) s.rawDb[i]);
          old.append((char) oldS.rawDb[i]);
        } catch (ArrayIndexOutOfBoundsException | NullPointerException e) {}
      }
    }
    DropmixSharedAssets ds = new DropmixSharedAssets(clonedAssetsFile);
    for (int i = 0; i < 3; i++) {
      DropmixSharedAssetsSeason s1 = modifiedAssets.seasons.get(i);
      DropmixSharedAssetsSeason s2 = ds.seasons.get(i);
      System.out.printf("\nSize: %d %d; db len: %d %d", s1.length, s2.length, s1.rawDb.length, s2.rawDb.length);
    }
    return clonedAssetsFile;
  }
}
