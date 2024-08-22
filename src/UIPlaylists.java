import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class UIPlaylists extends JPanel {
  UIMain parentFrame;
  String[] swappablePlaylists;
  JTable table;
  int focusedRow;

  public UIPlaylists(UIMain frame) {
    parentFrame = frame;
    setGrid();
  }

  public void setGrid() {
    removeAll();
    PlaylistDetail[] playlists = AppState.getInstance().getPlaylists();
    String[] headings = new String[]{ "Name", "Count", "Type", "Season", "Action" };
    setLayout(new GridLayout(playlists.length + 1, 5));
    for (String h: headings) {
      add(getText(h));
    }

    for (PlaylistDetail pl : playlists) {
      add(getText(pl.name));
      add(getText("" + pl.playlistCount));
      add(getText(pl.playlistType));
      add(getText(pl.season));
      add(getPlaylistComboBox(pl.name));
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
    return validPlaylists.toArray(new String[validPlaylists.size()]);
  }
  public JComboBox<String> getPlaylistComboBox(String playlist) {
    String[] options = getPlaylistOptions(playlist);
    JComboBox<String> box = new JComboBox<>(options);
    TreeMap<String, String> swaps = AppState.getInstance().playlistSwap;
    String selectedValue = swaps.get(playlist);
    System.out.println("Selected value "+selectedValue);
    if (selectedValue != null) {
      System.out.println(Arrays.stream(options).toList().indexOf(selectedValue));
      box.setSelectedIndex(Arrays.stream(options).toList().indexOf(selectedValue));
      box.validate();
      box.repaint();
    }
    UIPlaylists that = this;
    box.addItemListener(new ItemListener() {
      Object oldSelectionItem = 0;
      @Override
      public void itemStateChanged(ItemEvent event) {
        if (event.getStateChange() == ItemEvent.SELECTED) {
          try {
            AppState.getInstance().setPlaylistSwap(playlist, event.getItem().toString());
            // box.setSelectedItem(event.getItem());
            oldSelectionItem = event.getItem();
            System.out.println(event.getStateChange() + " " + event.paramString());
            that.setGrid();
          } catch (Exception e) {
            e.printStackTrace();
            box.setSelectedIndex(0);
          }
          setVisible(false);
          setVisible(true);
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
