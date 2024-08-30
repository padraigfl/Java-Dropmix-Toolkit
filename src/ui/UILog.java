package ui;

import model.AppState;
import util.Counter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

public class UILog extends JPanel {
  StringBuilder log = new StringBuilder();
  JTextPane tp;
  int w;
  int h;

  private Insets i = new Insets(3, 3, 3, 3);
  public UILog(int w, int h) {
    setLayout(new GridBagLayout());
    this.w = w;
    this.h = h;
    logSetup();
    renderLog();
  }

  private void renderLog() {
    removeAll();
    GridBagConstraints c = new GridBagConstraints();
    c.insets = i;
    c.gridx = 0;
    c.gridy = 0;
    c.gridheight = GridBagConstraints.RELATIVE;
    tp = new JTextPane();
    JScrollPane scrollPane = new JScrollPane(tp);
    scrollPane.setMinimumSize(new Dimension(w / 2, h / 5));
    scrollPane.setPreferredSize(new Dimension(w - 30, h / 4));
    add(scrollPane, c);
    c.gridx = 0;
    c.gridy = 1;
    JButton btn = new JButton();
    btn.setText("Clear log");
    btn.setVisible(true);
    btn.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        log = new StringBuilder();
        tp.setText(log.toString());
      }
    });
    add(btn, c);
  }

  private void logSetup() {
    PrintStream cmd = System.out;
    log.delete(0, log.length());
    UILog that = this;
    Counter i = new Counter(0);
    PrintStream out = new PrintStream(new OutputStream() {
      @Override
      public void write(int b) throws IOException {
        i.iterate();
        log.append((char) b);
        tp.setText(log.toString());
        cmd.print((char) b);
        if (AppState.getInstance().isNestedLog && (char) b == '\n') {
          log.append("--");
        }
        // that.renderLog();
      }
    });
    System.setOut(out);
  }
}
