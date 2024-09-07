package model;

import util.Helpers;

public class DropmixSharedAssetsPlaylist {
  public String[] cards;
  public int playlistCount;
  public String playlistType;
  public String season;
  public String name;
  public String cardId;
  public String itemId;

  // TODO is there a better way to embed this info
  public String getBaffler() {
    switch (this.name) {
      case "derby":
        return "HMX_0049_Lead";
      case "mirrors":
        return "HMX_0050_Beat";
      case "ouroboros":
        return "LIC_0129_Lead";
      case "astro":
        return "LIC_0131_Lead";
      case "lucky":
        return "HMX_0051_Loop";
      case "flawless":
        return "LIC_0133_Loop";
      case "bomb":
        return "HMX_0052_Bass";
      case "chiller":
        return "LIC_0144_Lead";
      case "energy":
        return "HMX_0055_Beat";
      case "city":
        return "LIC_0103_Loop";
      case "flora":
        return "LIC_0024_Loop";
      case "verdant":
        return "LIC_0165_Lead";
      case "rhymer":
        return "HMX_0075_Lead";
      case "phase":
        return "HMX_0064_Beat";
      case "instinct":
        return "LIC_0149_Loop";
      case "fantastic":
        return "HMX_0074_Bass";
      default:
        return null;
    }
  }
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
}
