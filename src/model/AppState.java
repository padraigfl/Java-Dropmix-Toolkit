package model;

import se.vidstige.jadb.JadbDevice;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class AppState {
  public static AppState instance = new AppState();
  public File apkFile;
  public File dataZip;
  public File obbZip;
  public byte[] rawData;
  public TreeMap<String, String> swapOptions = new TreeMap<String, String>();;
  public TreeMap<String, String> playlistSwap = new TreeMap<>();
  public JadbDevice adbDevice;
  public DropmixSharedAssets assetsHandler;
  public LogOptions logState = LogOptions.ERROR;
  public Process currentProcess = Process.NONE;
  public JFrame appFrame; // for forcing refreshes
  public boolean isNestedLog;
  private AppState() {
  }
  public static AppState getInstance() {
    return instance;
  }
  public static AppState getInstance(boolean isTest) {
    AppState instance = getInstance();
    if (isTest) {
      instance.setData(instance.loadFile());
    }
    return instance;
  }
  public static AppState getInstance(boolean isTest, JFrame appFrame) {
    AppState instance = getInstance(isTest);
    instance.appFrame = appFrame;
    return instance;
  }
  public void setData(byte[] fileData) {
    this.assetsHandler = new DropmixSharedAssets(fileData);
  }

  public CardDetail[] getCards() {
    ArrayList<CardDetail> cards = new ArrayList<CardDetail>();
    try {
      int seasonIdx = 0;
      SeasonTable season = this.assetsHandler.seasons.get(seasonIdx++);
      while (season != null) {
        cards.addAll(Arrays.asList(season.cards));
        season = this.assetsHandler.seasons.get(seasonIdx++);
      }
      return cards.toArray(new CardDetail[0]);
    } catch (Exception e) {
      e.printStackTrace();
      return new CardDetail[0];
    }
  }
  public PlaylistDetail[] getPlaylists() {
    Set<String> playlistNames = new HashSet<String>();
    for (CardDetail c: AppState.getInstance().getCards()) {
      playlistNames.add(c.cardData.get(CardDetail.SeriesIcon));
    }
    String[] playlistNamesArray = playlistNames.toArray(new String[0]);


    ArrayList<PlaylistDetail> seasons = new ArrayList<>();

    for (int i=0; i < playlistNames.size(); i++) {
      PlaylistDetail playlist = new PlaylistDetail(playlistNamesArray[i]);
      seasons.add(playlist);
    }
    // this is required to sort the playlists in the common order
    Collections.sort(seasons, new Comparator<PlaylistDetail>(){
      public int compare(PlaylistDetail o1, PlaylistDetail o2)
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
    return seasons.toArray(new PlaylistDetail[0]);
  }
  byte[] loadFile() {
    if (rawData != null) {
      return rawData;
    }
    ClassLoader classLoader = getClass().getClassLoader();

    try {
      String fileByteArrayPathString = classLoader.getResource("sharedassets0.assets.split194").getFile();
      rawData = Files.readAllBytes(Path.of(fileByteArrayPathString));
      return rawData;
    } catch (IOException | NullPointerException e) {
      throw new Error(e);
    }
  }
  public void setPlaylistSwap(String p1, String p2) throws Exception {
    if (p1.contains("---") || p2.contains("---")) {
      // for handling reset of field
      return;
    }
    if (this.playlistSwap.get(p1) != null || this.playlistSwap.get(p2) != null) {
      throw new Exception("key-in-use");
    }
    if (this.playlistSwap.containsValue(p1) || this.playlistSwap.containsValue(p2)) {
      throw new Exception("value-in-use");
    }
    for(PlaylistDetail pl : this.getPlaylists()) {
      if (pl.name == p1 || pl.name == p2) {
        if (pl.playlistCount != 15) {
          throw new Exception("invalid-playlist");
        }
      }
    }
    this.playlistSwap.put(p1, p2);
    this.playlistSwap.put(p2, p1);
  }

  public static boolean setCurrentProcess(Process p) {
    AppState as = AppState.getInstance();
    if (p == as.currentProcess) {
      return false;
    }
    as.appFrame.repaint();
    System.out.println("New Process" + p);
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
    System.out.println("Beginning process: " + p);
    System.out.println(as.isNestedLog);
    as.currentProcess = p;
    return true;
  }
  public static boolean endCurrentProcess(Process p) {
    AppState as = AppState.getInstance();
    if (as.currentProcess != p) {
      System.out.printf("Current process mismatch: %s, not %s", as.currentProcess, p);
      return false;
    }
    as.isNestedLog = false;
    setCurrentProcess(Process.NONE);
    System.out.println("Process complete: "+p);
    return true;
  }
  public static boolean switchCurrentProcess(Process prev, Process next) {
    AppState as = AppState.getInstance();
    if (as.currentProcess == prev) {
      endCurrentProcess(prev);
      setCurrentProcess(next);
      return true;
    }
    return false;
  }

  // basically just trying to wipe the slate
  public void reset() {
    this.swapOptions = new TreeMap<String, String>();
    this.playlistSwap = new TreeMap<>();
    this.assetsHandler = null;

    this.dataZip = null;
    this.apkFile = null;
    this.obbZip = null;
    this.adbDevice = null;
    this.appFrame.validate();
    this.appFrame.repaint();
  }
  // builds a card based swap from the playlist swap; may require more careful refinement as assumptions about order persistence exist
  public static TreeMap<String, String> getCardSwapFromPlaylist(TreeMap<String, String> plSwap) {
    for (String key: plSwap.values()) {
      String value = plSwap.get(key);
      String validator = plSwap.get(value);
      if (value == null || validator == null || !key.equals(validator)) {
        throw new RuntimeException("playlist-swap-sync-issue");
      }
    }
    String[] playlistNames = plSwap.keySet().toArray(new String[0]);
    PlaylistDetail[] playlists = getInstance().getPlaylists();
    TreeMap<String, PlaylistDetail> playlistDetailTreeMap = new TreeMap<String, PlaylistDetail>();
    TreeMap<String, String> generatedCardSwap = new TreeMap<>();
    Set<String> alreadySwappedPlaylists = new HashSet<>();
    for (PlaylistDetail pl: playlists) {
      playlistDetailTreeMap.put(pl.name, pl);
    }
    for (String playlist: playlistNames) {
      PlaylistDetail srcPl = playlistDetailTreeMap.get(playlist);
      PlaylistDetail swapPl = playlistDetailTreeMap.get(plSwap.get(playlist));
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
      alreadySwappedPlaylists.add(playlist);
      alreadySwappedPlaylists.add(swapPl.name);
    }
    return generatedCardSwap;
  }
}
