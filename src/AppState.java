import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class AppState {
  public static AppState instance;
  public File apkFile;
  public File dataZip;
  public byte[] rawData;
  public String currentState;
  public TreeMap<String, String> swapOptions;
  public TreeMap<String, String> playlistSwap;
  public SeasonTable[] seasons;
  public String adbDevice;
  public PlaylistDetail[] playlists;
  private AppState() {
    this.swapOptions = new TreeMap<String, String>();;
    this.playlistSwap = new TreeMap<>();
  }
  private AppState(boolean isTest) {
    if (isTest) {
      setData(loadFile());
    }
    this.swapOptions = new TreeMap<String, String>();
    this.playlistSwap = new TreeMap<>();
    setData(new byte[0]);
  }

  public TreeMap<String, String> getPlaylistSwap() {
    return playlistSwap;
  }

  public static AppState getInstance() {
    if (instance == null) {
      instance = new AppState();
    }
    return instance;
  }
  public static AppState getInstance(boolean isTest) {
    if (instance == null) {
      instance = new AppState(isTest);
    }
    return instance;
  }
  public void setData(byte[] fileData) {
    // get s1, s2, s0
    byte[][] headers = new byte[][]{AssetsHandler.s0Header, AssetsHandler.s1Header, AssetsHandler.s2Header};
    seasons = new SeasonTable[3];
    ArrayList<CardDetail> cards = new ArrayList<CardDetail>();
    ArrayList<PlaylistDetail> pl = new ArrayList<PlaylistDetail>();
    System.out.println(fileData.length);
    for(int i = 0; i < headers.length; i++) {
      byte[] tablePrefix = headers[i];
      int startIdx = Helpers.getStartIndex(fileData, tablePrefix);
      SeasonTable st = new SeasonTable(fileData, startIdx, tablePrefix == AssetsHandler.s2Header);
      seasons[i] = st;
      for (CardDetail c: st.cards) {
        System.out.print(st.cards[1]);
        System.out.println(c.toString());
      }
    }
  }
  public CardDetail[] getCards() {
    ArrayList<CardDetail> cards = new ArrayList<CardDetail>();
    try {
      for (SeasonTable st: seasons) {
        for (CardDetail c: st.cards) {
          cards.add(c);
        }
      }
      return cards.toArray(new CardDetail[0]);
    } catch (Exception e) {
      e.printStackTrace();
      return new CardDetail[0];
    }
  }

  public void setApkFile(File f) {
    this.apkFile = f;
  }
  public void setDataZip(File f) {
    this.dataZip = f;
  }

  public PlaylistDetail[] getPlaylists() {
    Set<String> playlistNames = new HashSet<String>();
    for (CardDetail c: AppState.getInstance().getCards()) {
      playlistNames.add(c.cardData.get(CardDetail.SeriesIcon));
    }
    String[] playlistNamesArray = playlistNames.toArray(new String[0]);
    PlaylistDetail[] playlists = new PlaylistDetail[playlistNames.size()];
    for (int i=0; i < playlistNames.size(); i++) {
      playlists[i] = new PlaylistDetail(playlistNamesArray[i]);
    }
    return playlists;
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
    if (this.playlistSwap.get(p1) != null || this.playlistSwap.get(p2) != null) {
      throw new Exception("key-in-use");
    }
    if (this.playlistSwap.values().contains(p1) || this.playlistSwap.values().contains(p2)) {
      throw new Exception("value-in-use");
    }
    int i = 0;
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
}
