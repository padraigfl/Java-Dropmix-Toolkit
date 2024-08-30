package ui;

import model.AppState;
import model.CardDetail;

import javax.swing.*;
import java.awt.*;

public class UICards extends JPanel {
  UIMain parentFrame;
  public UICards(UIMain frame) {
    parentFrame = frame;
    JTable table = getTable();
    Dimension td = new Dimension(parentFrame.width - 100, 300);
    table.setMinimumSize(td);
    // table.setBounds(0, 0, parentFrame.width, parentFrame.height-400);
    Dimension d = new Dimension(parentFrame.width - 200, 300);
    JScrollPane scrollPane = new JScrollPane(table);
    scrollPane.setMaximumSize(d);
    scrollPane.setPreferredSize(d);
    scrollPane.setMinimumSize(d);

    // scrollPane.setBounds(0, 0, parentFrame.width, parentFrame.height-50);
    add(scrollPane);
  }

  public JTable getTable() {
    JTable jt = new JTable();
    CardDetail[] cardDetails = AppState.getInstance().getCards();
    String[] headings = new String[]{ CardDetail.SourceCID, CardDetail.Power, CardDetail.ArtistRef, CardDetail.SongRef };
    String[][] tableData = new String[cardDetails.length][headings.length];
    for (int i = 0; i < cardDetails.length; i++) {
      for (int j = 0; j < headings.length; j++) {
        tableData[i][j] = cardDetails[i].cardData.get(headings[j]);
      }
    }
//    TableModel dataModel = new AbstractTableModel() {
//      public int getColumnCount() { return 10; }
//      public int getRowCount() { return cards.length;}
//      public Object getValueAt(int row, int col) { return cards[row].cardData.get(model.CardDetail.heading[col]); }
//    };
    JTable table = new JTable(tableData, headings);
    return table;
  }
}
