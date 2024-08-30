package ui;

import javax.swing.*;
import java.awt.*;

import static ui.components.Tools.getScrollPane;

public class UIPlaylists extends JPanel {
  public UIPlaylists(UIMain frame) {
    setLayout(new GridBagLayout());
    refresh();
  }
  public void refresh() {
    removeAll();
    GridBagConstraints c = new GridBagConstraints();
    UIPlaylistActions actions = new UIPlaylistActions();
    UIPlaylistsPanel panel = new UIPlaylistsPanel(this, actions);
    JPanel jp = new JPanel();
    JScrollPane scrollPane = getScrollPane(panel, UIMain.width * 4 / 5, UIMain.height);
    c.gridx = 0;
    c.gridy = 0;
    c.gridwidth = 4;
    jp.add(scrollPane);
    add(jp, c);
    c.gridx = 4;
    c.gridwidth = 1;
    add(actions, c);
  }
}
