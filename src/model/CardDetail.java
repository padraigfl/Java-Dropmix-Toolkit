package model;

import util.Helpers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.TreeMap;

public class CardDetail {
  public static final String CID = "CID";
  public static final String SourceCID = "Source CID";
  public static final String CTI = "CTI";
  public static final String ItemID = "Item ID";
  public static final String ItemType = "Item Type";
  public static final String Power = "Power";
  public static final String DeckID = "Deck ID";
  public static final String SeriesIcon = "Series Icon";
  public static final String SeriesIndex = "Series Index";
  public static final String SeriesCount = "Series Count";
  public static final String Season = "Season";
  public static final String ArtistRef = "ArtistRef";
  public static final String SongRef = "SongRef";
  public static final String TypeRef = "TypeRef";
  public static final String GenreRef = "GenreRef";
  public static final String PrintId = "Print ID";
  public static String[] heading = new String[]{
    CID,
    SourceCID,
    CTI,
    ItemID,
    ItemType,
    Power,
    DeckID,
    SeriesIcon,
    SeriesIndex,
    SeriesCount,
    Season,
    ArtistRef,
    SongRef,
    TypeRef,
    GenreRef,
    PrintId,
  };
  public TreeMap<String, String> cardData;
  public boolean needsQuotes;
  public int dataLength;

  public CardDetail(String[] data, String[] columns) {
    this.cardData = new TreeMap<String, String>();
    for (int i = 0; i < columns.length; i++) {
      this.setCardField(columns[i], data[i]);
    }
//    if (this.cardData.get(CardDetail.SongRef).contains("Days Ahead")) {
//      System.out.println(this.cardData);
//    }
    this.needsQuotes = this.cardData.get(CardDetail.Season).equals("2");
    this.dataLength = String.join(",", data).length();
  }

  public void setCardField(String key, String data) {
    this.cardData.put(key, data.replaceAll("\"", "").replaceAll("£", ","));
  }
  // don't need length adjust if swapping between the same season
  public void dataSwap(CardDetail swapCard, boolean cardClone) {
    String oldValue = cardData.get("Source CID");
    String newValue = swapCard.cardData.get("Source CID");
    String newPower = swapCard.cardData.get("Power");
    String newValueFormatted = this.needsQuotes ? Helpers.addQuotes(newValue) : newValue;
    cardData.put("Source CID", newValue);
    cardData.put("Power", newPower);
    if (cardClone || !swapCard.cardData.get("Season").equals(cardData.get("Season"))) {
      int diffLength = newValueFormatted.length() - oldValue.length();
      String songRef = Helpers.removeQuotes(this.cardData.get("SongRef"));
      if (diffLength > 0) {
        songRef = songRef.substring(0, songRef.length() - diffLength);
        if (this.needsQuotes) {
          songRef = Helpers.addQuotes(songRef);
        }
      }
      if (diffLength < 0) {
        songRef = Helpers.rPad(songRef, diffLength, ' ');
      }
      this.cardData.put(
        "SongRef",
        songRef
      );
    }
  }

  public String getCardId() {
    return this.cardData.get(CardDetail.SourceCID);
  }
  private void setCardId(String newId) {
    this.cardData.put(CardDetail.SourceCID, newId);
  }
  public int getCardSeason() {
    try {
      return Integer.parseInt(
        this.cardData.get(CardDetail.Season).replaceAll("\"", "")
      );
    } catch (Exception e) {
      return 0;
    }
  }
  private void shortedDBRowForModification(int changeRequired) {
          /*
        SongRef would be preferable here as it will generally be longer but may cause issues with FX cards
        With this in mind it makes sense to use the Artist name instead
        TODO Investigate whether FX songRef values need to be swapped over when swap is applied
       */
    String fieldToChange =
      changeRequired < 0
        && this.cardData.get(CardDetail.SongRef).length() < Math.abs(changeRequired) + 1
        ? CardDetail.SongRef
        : CardDetail.ArtistRef;
    String songTitle = this.cardData.get(fieldToChange);
    if (changeRequired > 0) {
      songTitle = Helpers.rPad(songTitle, songTitle.length() + Math.abs(changeRequired), ' ');
    } else {
      songTitle = songTitle.substring(0, songTitle.length() - Math.abs(changeRequired) );
    }
    this.cardData.put(fieldToChange, songTitle);
  }
  public void setSourceCID(String newCardId, boolean preserveLength) {
    String currentId = getCardId();
    int songTitleLengthChangeRequired = currentId.length() - newCardId.length();
    if (!preserveLength && songTitleLengthChangeRequired != 0) {
      shortedDBRowForModification(songTitleLengthChangeRequired);
    }
    this.setCardId(newCardId);
  }

  public String toString() {
    StringBuilder sb = new StringBuilder();
    for (String heading: CardDetail.heading) {
      sb.append(heading).append(":").append(cardData.get(heading)).append(";");
    }
    return sb.toString();
  }
}
// CIDSource CIDCTIItem IDItem TypePowerDeck IDSeries IconSeries IndexSeries CountSeasonArtistRefSongRefTypeRefGenreRefPrint ID