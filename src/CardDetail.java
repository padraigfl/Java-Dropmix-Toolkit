import org.antlr.runtime.tree.Tree;

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
    GenreRef
  };
  public TreeMap<String, String> cardData;
  public boolean needsQuotes;
  public int dataLength;

  public CardDetail(String[] data) {
    cardData = new TreeMap<String, String>();
    for (int i = 0; i < heading.length; i++) {
      cardData.put(
        heading[i],
        data[i]
      );
    }
    this.needsQuotes = this.cardData.get("Season").equals("2");
    this.dataLength = String.join(",", data).length();
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

  public String rowToDataString() {
    ArrayList<String> sb = new ArrayList<String>();
    for (String h: heading) {
      sb.add(cardData.get(h));
    }
    return String.join(",", sb);
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