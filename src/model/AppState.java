package model;

import se.vidstige.jadb.JadbDevice;
import ui.UIMain;
import util.Helpers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class AppState {
  public static AppState instance = new AppState();
  public File apkFile;
  public File dataZip;
  public byte[] rawData;
  public TreeMap<String, String> swapOptions = new TreeMap<String, String>();;
  public TreeMap<String, String> playlistSwap = new TreeMap<>();
  public JadbDevice adbDevice;
  public DropmixSharedAssets assetsHandler;
  public DropmixLevel0 level0Handler;
  public LogOptions logState = LogOptions.ERROR;
  public Process currentProcess = Process.NONE;
  public UIMain appFrame; // for forcing refreshes
  public boolean isNestedLog;
  public boolean iOS = false;
  private AppState() {
  }
  public static AppState getInstance() {
    return instance;
  }
  public static AppState getInstance(boolean isTest) {
    AppState instance = getInstance();
    if (isTest) {
      instance.setData(Helpers.loadFile("sharedassets0.assets.split194"), Helpers.loadFile("level0.split3"));
    }
    return instance;
  }
  public static AppState getInstance(boolean isTest, UIMain appFrame) {
    AppState instance = getInstance(isTest);
    instance.appFrame = appFrame;
    return instance;
  }
  public void setData(byte[] sharedAssets, byte[] level0) {
    this.assetsHandler = new DropmixSharedAssets(sharedAssets);
    this.level0Handler = new DropmixLevel0(level0);
  }

  public DropmixSharedAssetsCard[] getCards() {
    ArrayList<DropmixSharedAssetsCard> cards = new ArrayList<DropmixSharedAssetsCard>();
    try {
      int seasonIdx = 0;
      DropmixSharedAssetsSeason season = this.assetsHandler.seasons.get(seasonIdx++);
      while (season != null) {
        cards.addAll(Arrays.asList(season.cards));
        season = this.assetsHandler.seasons.get(seasonIdx++);
      }
      return cards.toArray(new DropmixSharedAssetsCard[0]);
    } catch (Exception e) {
      e.printStackTrace();
      return new DropmixSharedAssetsCard[0];
    }
  }
  public DropmixSharedAssetsPlaylist[] getPlaylists() {
    Set<String> playlistNames = new HashSet<String>();
    for (DropmixSharedAssetsCard c: AppState.getInstance().getCards()) {
      playlistNames.add(c.data.get(DropmixSharedAssetsCard.SeriesIcon));
    }
    String[] playlistNamesArray = playlistNames.toArray(new String[0]);


    ArrayList<DropmixSharedAssetsPlaylist> seasons = new ArrayList<>();

    for (int i=0; i < playlistNames.size(); i++) {
      DropmixSharedAssetsPlaylist playlist = new DropmixSharedAssetsPlaylist(playlistNamesArray[i]);
      seasons.add(playlist);
    }
    // this is required to sort the playlists in the common order
    Collections.sort(seasons, new Comparator<DropmixSharedAssetsPlaylist>(){
      public int compare(DropmixSharedAssetsPlaylist o1, DropmixSharedAssetsPlaylist o2)
      {
        int val = o1.season.compareTo(o2.season);
        if (val == 0) {
          // baffler and promo are both empty
          if (o1.cardId == null) {
            return -1;
          }
          if (o2.cardId == null) {
            return 1;
          }
          int card1 = Integer.parseInt(o1.cardId);
          int card2 = Integer.parseInt(o2.cardId);
          return card1 > card2 ? 1 : -1;
        }
        return val;
      }
    });
    return seasons.toArray(new DropmixSharedAssetsPlaylist[0]);
  }
  public void removePlaylistSwap(String p1) {
    String p2 = this.playlistSwap.get(p1);
    this.playlistSwap.remove(p1);
    this.playlistSwap.remove(p2);
  }
  public void setPlaylistSwap(String p1, String p2) throws Exception {
    if (p1.contains("---") || p2.contains("---")) {
      // for handling reset of field
      this.playlistSwap.remove(p1);
      this.playlistSwap.remove(p2);
      return;
    }
    if (this.playlistSwap.get(p1) != null || this.playlistSwap.get(p2) != null) {
      throw new Exception("key-in-use");
    }
    if (this.playlistSwap.containsValue(p1) || this.playlistSwap.containsValue(p2)) {
      throw new Exception("value-in-use");
    }
    for(DropmixSharedAssetsPlaylist pl : this.getPlaylists()) {
      if (pl.name == p1 || pl.name == p2) {
        if (pl.playlistCount != 15) {
          throw new Exception("invalid-playlist");
        }
      }
    }
    this.playlistSwap.put(p1, p2);
    this.playlistSwap.put(p2, p1);
  }

  public static void setCurrentProcess(Process p) {
    AppState as = AppState.getInstance();
    if (p == as.currentProcess) {
      return;
    }
    as.appFrame.repaint();
    if (as.currentProcess != Process.NONE && p != Process.NONE) {
      String message = "Process cancelled: " + as.currentProcess + "is still active";
      throw new RuntimeException(message);
    }
    switch (p) {
      case SIGNING:
      case INSTALLING:
      case DECOMPILING:
      case RECOMPILING:
        as.isNestedLog = true;
        break;
      default:
        as.isNestedLog = false;
    }
    if (p != Process.NONE) {
      System.out.println("Beginning process: " + p);
    }
    as.currentProcess = p;
  }
  public static void endCurrentProcess(Process p) {
    AppState as = AppState.getInstance();
    if (as.currentProcess != p) {
      System.out.printf("Current process mismatch: %s, not %s", as.currentProcess, p);
      return;
    }
    as.isNestedLog = false;
    setCurrentProcess(Process.NONE);
    System.out.println("Process complete: "+p);
  }
  public static void switchCurrentProcess(Process prev, Process next) {
    AppState as = AppState.getInstance();
    if (as.currentProcess == prev) {
      endCurrentProcess(prev);
      setCurrentProcess(next);
    }
  }

  // basically just trying to wipe the slate
  public void reset() {
    this.playlistSwap = new TreeMap<>();
    this.assetsHandler = null;

    this.dataZip = null;
    this.apkFile = null;
    this.adbDevice = null;
    this.appFrame.validate();
    this.appFrame.repaint();
    this.appFrame.addPlaceholders();
  }
  // builds a card based swap from the playlist swap; may require more careful refinement as assumptions about order persistence exist
  public static TreeMap<String, String> getCardSwapFromPlaylist(TreeMap<String, String> plSwap, boolean includeBafflers) {
    for (String key: plSwap.values()) {
      String value = plSwap.get(key);
      String validator = plSwap.get(value);
      if (value == null || validator == null || !key.equals(validator)) {
        throw new RuntimeException("playlist-swap-sync-issue");
      }
    }
    String[] playlistNames = plSwap.keySet().toArray(new String[0]);
    DropmixSharedAssetsPlaylist[] playlists = getInstance().getPlaylists();
    TreeMap<String, DropmixSharedAssetsPlaylist> dropmixSharedAssetsPlaylistTreeMap = new TreeMap<String, DropmixSharedAssetsPlaylist>();
    TreeMap<String, String> generatedCardSwap = new TreeMap<>();
    Set<String> alreadySwappedPlaylists = new HashSet<>();
    for (DropmixSharedAssetsPlaylist pl: playlists) {
      dropmixSharedAssetsPlaylistTreeMap.put(pl.name, pl);
    }
    for (String playlist: playlistNames) {
      DropmixSharedAssetsPlaylist srcPl = dropmixSharedAssetsPlaylistTreeMap.get(playlist);
      DropmixSharedAssetsPlaylist swapPl = dropmixSharedAssetsPlaylistTreeMap.get(plSwap.get(playlist));
      if (alreadySwappedPlaylists.contains(swapPl.name)) {
        continue;
      }
      if (swapPl.cards.length != srcPl.cards.length) {
        throw new RuntimeException("invalid-playlist-swap");
      }
      for (int i = 0; i < srcPl.cards.length; i++) {
        try {
          generatedCardSwap.put(srcPl.cards[i], swapPl.cards[i]);
          generatedCardSwap.put(swapPl.cards[i], srcPl.cards[i]);
        } catch (Exception e) {
          continue;
        }
      }
      if (includeBafflers) {
        String srcBaffler = srcPl.getBaffler();
        String swapBaffler = swapPl.getBaffler();
        if (srcBaffler != null && swapBaffler != null) {
          generatedCardSwap.put(srcBaffler, swapBaffler);
          generatedCardSwap.put(swapBaffler, srcBaffler);
        }
      }
      alreadySwappedPlaylists.add(playlist);
      alreadySwappedPlaylists.add(swapPl.name);
    }
    return generatedCardSwap;
  }
}
