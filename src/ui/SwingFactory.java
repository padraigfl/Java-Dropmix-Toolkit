package ui;

import model.AppState;
import model.Process;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class SwingFactory {
  public static JButton buildButton(String buttonText, ActionListener action) {
    AppState as = AppState.getInstance();
    JButton btn = new JButton();
    btn.setText(buttonText);
    btn.addActionListener(action);
    btn.setEnabled(as.currentProcess == Process.NONE);

    return btn;
  }
  public static JTextPane buildText(String text) {
    JTextPane tp = new JTextPane();
    tp.setText(text);
    tp.setEditable(false);
    return tp;
  }
  public static GridBagConstraints quickGridBagConstraints(int gridx, int gridy, int gridwidth, int gridheight) {
    GridBagConstraints c = new GridBagConstraints();
    c.gridx = gridx;
    c.gridy = gridy;
    c.gridwidth = gridwidth;
    c.gridheight = gridheight;
    return c;
  }
}
