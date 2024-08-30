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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class UILog extends JPanel {
  String log = "";
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
        log = "";
        tp.setText(log);
      }
    });
    add(btn, c);
  }

  private void refreshText() {
    tp.setText(log);
    tp.repaint();
//    CompletableFuture.delayedExecutor(5, TimeUnit.SECONDS).execute(() -> {
//      if (!tp.getText().equals(log)) {
//        refreshText();
//      }
//    });
  }
  private void logSetup() {
    PrintStream cmd = System.out;
    log = "";
    Counter i = new Counter(0);
    PrintStream out = new PrintStream(new OutputStream() {
      @Override
      public void write(int b) throws IOException {
        log = log + ((char) b);
        boolean isNewLine = (char) b == '\n';

        if (AppState.getInstance().isNestedLog && isNewLine) {
          log = log + "- ";
        }
        if (isNewLine) {
          refreshText();
        }
        cmd.print((char) b);
        // that.renderLog();
      }
    });
    System.setOut(out);
  }
}
