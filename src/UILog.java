import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

public class UILog extends JPanel {
  StringBuilder log = new StringBuilder();
  JTextPane tp;
  public UILog() {
    tp = new JTextPane();
    logSetup();
    add(tp);
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
  }

  private void logSetup() {
    PrintStream cmd = System.out;
    log.delete(0, log.length());
    PrintStream out = new PrintStream(new OutputStream() {
      @Override
      public void write(int b) throws IOException {
        log.append((char) b);
        tp.setText(log.toString());
        cmd.print((char) b);
      }
    });
    System.setOut(out);
  }
}
