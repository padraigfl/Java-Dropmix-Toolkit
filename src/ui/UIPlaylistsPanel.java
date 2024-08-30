package ui;

import model.AppState;
import model.PlaylistDetail;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.stream.Collectors;

public class UIPlaylistsPanel extends JPanel {
  JPanel parentFrame;
  UIPlaylistActions actions;
  public UIPlaylistsPanel(JPanel parent, UIPlaylistActions actions) {
    parentFrame = parent;
    this.actions = actions;
    setGrid();
  }

  public void setGrid() {
    removeAll();
    PlaylistDetail[] playlists = AppState.getInstance().getPlaylists();
    String[] headings = new String[]{ "Name", "Count", "Type", "Playlist#", "Action" };
    setLayout(new GridLayout(playlists.length + 1, 5));
    int width = (int) ( UIMain.width * 0.65);
    setMinimumSize(new Dimension(width - 250, UIMain.height));
    setPreferredSize(new Dimension(width - 100, UIMain.height));
    setMaximumSize(new Dimension(width - 100, UIMain.height));

    for (String h: headings) {
      add(getText(h));
    }

    for (PlaylistDetail pl : playlists) {
      add(getText(pl.name));
      add(getText("" + pl.playlistCount));
      add(getText(pl.playlistType));
      add(getText(pl.season + "-" + pl.cardId));
      if (pl.playlistCount == 15) {
        add(getPlaylistComboBox(pl.name));
      } else {
        add(new JPanel());
      }
    }
  }

  public JTextField getText(String text) {
    JTextField tf = new JTextField();
    tf.setText(text);
    return tf;
  }

  public String[] getPlaylistOptions(String playlist) {
    String emptyValue = "-----";
    PlaylistDetail[] playlists = AppState.getInstance().getPlaylists();
    TreeMap<String, String> swap = AppState.getInstance().swapOptions;
    ArrayList<String> validPlaylists = new ArrayList<>();
    validPlaylists.add(emptyValue);
    for (PlaylistDetail pl: playlists) {
      if (
        !pl.name.equals(playlist)
          && pl.playlistCount == 15
          && swap.get(pl.name) == null
      ) {
        validPlaylists.add(pl.name);
      }
    }
    return validPlaylists.toArray(new String[0]);
  }
  public JComboBox<String> getPlaylistComboBox(String playlist) {
    String[] options = getPlaylistOptions(playlist);
    JComboBox<String> box = new JComboBox<>(options);
    TreeMap<String, String> swaps = AppState.getInstance().playlistSwap;
    String selectedValue = swaps.get(playlist);
    if (selectedValue != null) {
      box.setSelectedIndex(Arrays.stream(options).collect(Collectors.toList()).indexOf(selectedValue));
      box.validate();
      box.repaint();
    }
    UIPlaylistsPanel that = this;
    box.addItemListener(new ItemListener() {
      Object oldSelectionItem = 0;
      @Override
      public void itemStateChanged(ItemEvent event) {
        if (event.getStateChange() == ItemEvent.SELECTED) {
          try {
            AppState.getInstance().setPlaylistSwap(playlist, event.getItem().toString());
            // box.setSelectedItem(event.getItem());
            oldSelectionItem = event.getItem();
            that.setGrid();
            actions.validate();
            actions.renderActions();
            that.actions.clearState();
          } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.toString());
            box.setSelectedIndex(0);
          }
//          if (!"Okay".equalsIgnoreCase(jTextField.getText())) {
//            if (oldSelectionIndex < 0) {
//              box.setSelectedIndex(0);
//            } else {
//              jComboBox.setSelectedIndex(oldSelectionIndex);
//            }
//          } else {
//            oldSelectionIndex = jComboBox.getSelectedIndex();
//          }
        }
      }
    });
    return box;
  }
}
