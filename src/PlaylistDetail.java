import java.util.HashSet;
import java.util.Set;

public class PlaylistDetail {
  String[] cards;
  int playlistCount;
  String baffler;
  String playlistType;
  String season;
  String name;
  public PlaylistDetail(String iconName) {
    String playlistName = Helpers.removeQuotes(iconName);
    AppState as = AppState.getInstance();
    int counter = 0;
    this.name = playlistName;
    for (CardDetail c : as.getCards()) {
      String cardSeriesIcon = c.cardData.get(CardDetail.SeriesIcon);
      // awkward handling for different quotation handling in db
      if (cardSeriesIcon.equals(iconName) || cardSeriesIcon.equals(playlistName)) {
        if (this.cards == null) {
          try {
            this.playlistCount = Integer.parseInt(c.cardData.get(CardDetail.SeriesCount));
          } catch (Exception e) {
            this.playlistCount = playlistName.equals("promo") ? 4 : 12;
          }
          this.cards = new String[this.playlistCount];
          this.playlistType = c.cardData.get(CardDetail.ItemType);
          this.season = c.cardData.get(CardDetail.Season);
        }
        this.cards[counter] = c.cardData.get(CardDetail.SourceCID);
      }
    }
  }
  public String getBaffler(String iconName) {
    baffler = "";
    return baffler;
  }
}
