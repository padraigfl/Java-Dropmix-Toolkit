package model;

import util.Helpers;

import java.util.HashSet;
import java.util.TreeMap;

public class DropmixSharedAssetsCard extends DataRow {
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

  private static String[] headings = new String[]{
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
  public DropmixSharedAssetsCard(String[] data) {
    super(data, headings, new HashSet<String>());
  }

  public String getCardId() {
    return this.data.get(DropmixSharedAssetsCard.SourceCID);
  }
  private void setCardId(String newId) {
    this.data.put(DropmixSharedAssetsCard.SourceCID, newId);
  }
  public int getCardSeason() {
    try {
      return Integer.parseInt(
        this.data.get(DropmixSharedAssetsCard.Season).replaceAll("\"", "")
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
        && this.data.get(DropmixSharedAssetsCard.SongRef).length() < Math.abs(changeRequired) + 1
        ? DropmixSharedAssetsCard.SongRef
        : DropmixSharedAssetsCard.ArtistRef;
    String songTitle = this.data.get(fieldToChange);
    if (changeRequired > 0) {
      songTitle = Helpers.rPad(songTitle, songTitle.length() + Math.abs(changeRequired), ' ');
    } else {
      songTitle = songTitle.substring(0, songTitle.length() - Math.abs(changeRequired) );
    }
    this.data.put(fieldToChange, songTitle);
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
    for (String heading: getHeadings()) {
      sb.append(heading).append(":").append(data.get(heading)).append(";");
    }
    return sb.toString();
  }
  @Override
  public String[] getHeadings() {
    return this.headings;
  }
}
// CIDSource CIDCTIItem IDItem TypePowerDeck IDSeries IconSeries IndexSeries CountSeasonArtistRefSongRefTypeRefGenreRefPrint ID