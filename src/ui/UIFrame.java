package ui;

import model.AppState;
import model.CardDetail;

import javax.swing.*;

public class UIFrame extends JFrame {
  JFrame f;
  String mode;
  static String[] modes = { "setup", "swap", "install" };
  JTabbedPane menu;
  JPanel setupPanel;
  JPanel swapPanel;
  JPanel installPanel;
  JTextField textTest;
  int width = 800;
  int height = 600;
  public UIFrame() {
    menu = new JTabbedPane();
    render();
  }

  public void render() {
    AppState as = AppState.getInstance();
    menu.removeAll();
    setupPanel = new JPanel();
    swapPanel = new JPanel();
    installPanel = new JPanel();
    System.out.println("HI");
    menu.add(modes[0], setupPanel);
    menu.add(modes[1], swapPanel);
    menu.add(modes[2], installPanel);
    menu.setBounds(0,0,width,height);
    textTest = new JTextField();
    as = AppState.getInstance();
    textTest.setText(textTest.getText() + "Length: "+as.getCards().length);
    installPanel.add(textTest);
    JButton b=new JButton("click");//create button
    b.setBounds(130,100,100, 40);
    JTable table = getTable();
    JScrollPane scrollPane = new JScrollPane(table);
    scrollPane.setBounds(0, 50, width, height-50);
    swapPanel.add(scrollPane);

    add(menu);
    setSize(width, height);
    setLayout(null);
    setVisible(true);
    repaint();
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
