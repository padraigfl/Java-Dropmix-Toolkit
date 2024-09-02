package model;

import util.Helpers;

public class DropmixSharedAssetsPlaylist {
  public String[] cards;
  public int playlistCount;
  public String baffler;
  public String playlistType;
  public String season;
  public String name;
  public String cardId;
  public String itemId;
  public DropmixSharedAssetsPlaylist(String iconName) {
    String playlistName = Helpers.removeQuotes(iconName);
    AppState as = AppState.getInstance();
    int counter = 0;
    this.name = playlistName;
    for (DropmixSharedAssetsCard c : as.getCards()) {
      String cardSeriesIcon = c.data.get(DropmixSharedAssetsCard.SeriesIcon);
      // awkward handling for different quotation handling in db
      if (cardSeriesIcon.equals(iconName) || cardSeriesIcon.equals(playlistName)) {
        if (this.cards == null) {
          try {
            this.playlistCount = Integer.parseInt(c.data.get(DropmixSharedAssetsCard.SeriesCount));
            String deckId = c.data.get(DropmixSharedAssetsCard.DeckID);
            if (deckId != null) {
              this.cardId = deckId;
            }
            String itemId = c.data.get(DropmixSharedAssetsCard.ItemID);
            if (itemId != null) {
              this.itemId = itemId;
            }
          } catch (Exception e) {
            this.playlistCount = playlistName.equals("promo") ? 4 : 12;
          }
          this.cards = new String[this.playlistCount];
          this.playlistType = c.data.get(DropmixSharedAssetsCard.ItemType);
          this.season = Helpers.removeQuotes(c.data.get(DropmixSharedAssetsCard.Season));
        }
        if (counter < this.playlistCount) {
          this.cards[counter] = c.data.get(DropmixSharedAssetsCard.SourceCID);
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
