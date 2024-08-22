import javax.swing.*;
import java.awt.*;

public class UIMain extends JFrame {
  public static final String SCREEN_SETUP = "setup";
  public static final String SCREEN_CARDS = "cardswap";
  public static final String SCREEN_PLAYLISTS = "playlistswap";
  public static final String SCREEN_TOOLS = "tools";
  public static String[] screens = new String[]{ SCREEN_SETUP, SCREEN_CARDS, SCREEN_PLAYLISTS, SCREEN_TOOLS };

  static int width = 800;
  static int height = 600;
  JPanel setupPanel;
  JPanel cardsPanel;
  JPanel playlistPanel;
  JPanel toolsPanel;
  JPanel logs;
  JPanel app;
  JTabbedPane menu;
  AppState as;
  public UIMain() {
    as = AppState.getInstance();
    setLayout(null);

    menu = new JTabbedPane();
    setupPanel = new UISetup(this);
    menu.setBounds(0,0,width,height);
    menu.add(screens[0], setupPanel);
    JPanel test = new JPanel();
    JTextField tf = new JTextField();
      tf.setText("test");
    test.add(tf);
    test.setVisible(true);
    menu.add(screens[1], test);
    menu.add(screens[2], new JPanel());
    menu.add(screens[3], new JPanel());
    add(menu);

    setSize(width, height);
    setVisible(true);
  }
  public void addPanel(int idx, JPanel panel) {
    panel.setVisible(true);
    menu.setComponentAt(idx, panel);
  }
  public void addCardsPanel() {
    cardsPanel = new UICards(this);
    addPanel(1, cardsPanel);
  }
  public void addPlaylistsPanel() {
    playlistPanel = new UIPlaylists(this);
    JPanel jp = new JPanel();
    JScrollPane scrollPane = new JScrollPane(
      playlistPanel,
      JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
      JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
    );
    scrollPane.setMinimumSize(new Dimension(300, height-400));
    scrollPane.setPreferredSize(new Dimension(width - 50, height - 200));
    jp.add(scrollPane);
    addPanel(2, jp);
  }
  public void addToolsPanel() {
    toolsPanel = new UITools(this);
  }
}
