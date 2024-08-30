package model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.TreeMap;

public class AppStateTest {
  AppState as = AppState.getInstance(true);

  @Test
  public void getCardSwapFromPlaylist() {
    TreeMap<String, String> plSwap = new TreeMap<>();
    plSwap.put("highness", "sweets");
    plSwap.put("sweets", "highness");

    TreeMap<String, String> cardSwap = AppState.getCardSwapFromPlaylist(plSwap);
    Assertions.assertEquals(cardSwap.size(), 30);
    Assertions.assertTrue(cardSwap.containsKey("LIC_0058_Wild"));
    Assertions.assertTrue(cardSwap.containsValue("LIC_0058_Wild"));
    Assertions.assertEquals(cardSwap.get("LIC_0058_Wild"), "LIC_0137_Wild");

    String errorMessage = "";
    try {
      plSwap.clear();
      plSwap.put("baffler", "promo");
      plSwap.put("promo", "baffler");
      cardSwap = AppState.getCardSwapFromPlaylist(plSwap);
    } catch (RuntimeException e) {
      errorMessage = e.getMessage();
    }
    Assertions.assertEquals(errorMessage, "invalid-playlist-swap");

    try {
      errorMessage = "";
      plSwap.clear();
      plSwap.put("instinct", "bomb");
      plSwap.put("chiller", "instinct");
      plSwap.put("bomb", "chiller");
      cardSwap = AppState.getCardSwapFromPlaylist(plSwap);
    } catch (RuntimeException e) {
      errorMessage = e.getMessage();
    }
    Assertions.assertEquals(errorMessage, "playlist-swap-sync-issue");
  }
}
