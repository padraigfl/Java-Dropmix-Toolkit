package ui.components;

import ui.UIMain;

import javax.swing.*;
import java.awt.*;

public class Tools {
  public static JScrollPane getScrollPane(JPanel panel, int width, int height) {
    JScrollPane scrollPane = new JScrollPane(
      panel,
      JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
      JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
    );
    scrollPane.setMinimumSize(new Dimension(width - 100, (int) (height * 0.4)));
    scrollPane.setPreferredSize(new Dimension(width - 100, (int) (height * 0.4)));
    scrollPane.setMaximumSize(new Dimension(width - 100, (int) (height * 0.4)));
    return scrollPane;
  }
  public static JScrollPane getScrollPane(JPanel panel) {
    return getScrollPane(panel, UIMain.width, UIMain.height);
  }
}
