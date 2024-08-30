package model;

import util.Helpers;

public class PlaylistDetail {
  public String[] cards;
  public int playlistCount;
  public String baffler;
  public String playlistType;
  public String season;
  public String name;
  public String cardId;
  public String itemId;
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
            String deckId = c.cardData.get(CardDetail.DeckID);
            if (deckId != null) {
              this.cardId = deckId;
            }
            String itemId = c.cardData.get(CardDetail.ItemID);
            if (itemId != null) {
              this.itemId = itemId;
            }
          } catch (Exception e) {
            this.playlistCount = playlistName.equals("promo") ? 4 : 12;
          }
          this.cards = new String[this.playlistCount];
          this.playlistType = c.cardData.get(CardDetail.ItemType);
          this.season = Helpers.removeQuotes(c.cardData.get(CardDetail.Season));
        }
        if (counter < this.playlistCount) {
          this.cards[counter] = c.cardData.get(CardDetail.SourceCID);
          counter++;
        }
      }
    }
  }
  public String getBaffler(String iconName) {
    baffler = "";
    return baffler;
  }
}
