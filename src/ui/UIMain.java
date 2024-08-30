package ui;

import model.AppState;
import util.UtilAdb;

import javax.swing.*;
import java.awt.*;

import static ui.components.Tools.getScrollPane;

public class UIMain extends JFrame {
  public static final String SCREEN_SETUP = "setup";
  public static final String SCREEN_CARDS = "DB Preview";
  public static final String SCREEN_PLAYLISTS = "Playlist Swap Mod";
  public static final String SCREEN_TOOLS = "Misc Tools";
  public static String[] screens = new String[]{ SCREEN_SETUP, SCREEN_CARDS, SCREEN_PLAYLISTS, SCREEN_TOOLS };

  private Insets i = new Insets(3, 3, 3, 3);
  public static int width = 960;
  public static int height = 720;
  JPanel setupPanel;
  public JPanel cardsPanel;
  public JPanel playlistPanel;
  JTabbedPane menu;
  AppState as;
  UILog log;
  String addApkTabMsg = "Please add and verify an APK on the setup screen to use this tab";
  public UIMain() {
    // Warning message
    JOptionPane.showMessageDialog(
      this,
      "This application is quite a rough implementation and should only be used if you are okay with the risks that involves." + "\n" +
        "These include harm to your android device and wasting space on your PC with poor file management\n" +
        "The ADB server initialisation process may have issues"
    );
    as = AppState.getInstance(false, this);
    setLayout(new GridBagLayout());

    log = new UILog(UIMain.width, UIMain.height);

    menu = new JTabbedPane();
    setupPanel = new UISetup(this);
    menu.setBounds(0,0,width,height);
    menu.add(screens[0], setupPanel);
    JPanel test = new JPanel();
    JTextField tf = new JTextField();
      tf.setText("test");
    test.add(tf);
    test.setVisible(true);
    menu.add(screens[1], getEmptyPanel(addApkTabMsg));
    menu.add(screens[2], getEmptyPanel(addApkTabMsg));
    // menu.add(screens[3], getEmptyPanel("Tool for simple setup of Archive zip to go here"));
    GridBagConstraints c = new GridBagConstraints();
    c.insets = i;
    c.gridx = 0;
    c.gridy = 0;
    c.gridheight = 2;
    c.weightx = c.weighty = 1;
    c.fill = GridBagConstraints.BOTH;
    add(menu, c);
    c.gridheight = 1;
    c.gridy = 2;
    add(log, c);

    setSize(width, height);

    setVisible(true);
  }
  public void addPanel(int idx, JPanel panel) {
    panel.setVisible(true);
    menu.setComponentAt(idx, panel);
  }
  public void addPlaceholders() {
    try {
      addPanel(1, getEmptyPanel(addApkTabMsg));
      addPanel(2, getEmptyPanel(addApkTabMsg));
    } catch (Exception e) {}
  }
  public void addCardsPanel() {
    cardsPanel = new UICards(this);
    JPanel jp = new JPanel();
    JScrollPane scrollPane = getScrollPane(cardsPanel);
    jp.add(scrollPane);
    addPanel(1, jp);
  }
  public void addPlaylistsPanel() {
    playlistPanel = new UIPlaylists(this);
    addPanel(2, playlistPanel);
  }
  public JPanel getEmptyPanel(String text) {
    JPanel p = new JPanel();
    p.add(SwingFactory.buildText(text));
    JScrollPane scrollPane = getScrollPane(p);
    JPanel jp = new JPanel();
    jp.add(scrollPane);
    return jp;
  }
}
