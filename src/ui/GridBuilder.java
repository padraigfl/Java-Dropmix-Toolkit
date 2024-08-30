package ui;

import javax.swing.*;
import java.awt.*;

public class GridBuilder extends JPanel {
  public static final String GRID_BAG = "GridBagLayout";
  public static final String GRID = "GridLayout";
  private String layoutType;
  public GridBuilder(GridLayout l) {
    setLayout(l);
    layoutType = GRID;
  }
  public GridBuilder(GridBagLayout l) {
    setLayout(l);
    layoutType = GRID_BAG;
  }
  public JPanel addCell(int gridX, int gridY, int gridWidth, int gridHeight) {
    JPanel panel = new JPanel();
    if (this.layoutType != GRID_BAG) {
      return addCell();
    }
    GridBagConstraints c = new GridBagConstraints();
    c.gridy = gridY;
    c.gridx = gridX;
    c.gridwidth = gridWidth;
    c.gridheight = gridHeight;
    add(panel, c);
    return panel;
  }
  public JPanel addCell(int gridX, int gridY, int gridWidth, int gridHeight, JPanel child) {
    JPanel panel = addCell(gridX, gridY, gridWidth, gridHeight);
    panel.add(child);
    return panel;
  }
  public JPanel addCell() {
    return new JPanel();
  }
}
