import javax.swing.*;

public class UICards extends JPanel {
  UIMain parentFrame;
  public UICards(UIMain frame) {
    parentFrame = frame;
    JTable table = getTable();
    table.setBounds(0, 50, parentFrame.width, parentFrame.height-50);
    JScrollPane scrollPane = new JScrollPane(table);
    scrollPane.setBounds(0, 50, parentFrame.width, parentFrame.height-50);
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
//      public Object getValueAt(int row, int col) { return cards[row].cardData.get(CardDetail.heading[col]); }
//    };
    JTable table = new JTable(tableData, headings);
    return table;
  }
}
