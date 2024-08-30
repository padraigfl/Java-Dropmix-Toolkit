package ui.components;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;

//
public class UIFilePicker extends JFileChooser {
  File selectedFile;
  public UIFilePicker(String id, String description, String extensions) {
    FileNameExtensionFilter filter = new FileNameExtensionFilter(description, extensions);
    setVisible(false);
    setFileFilter(filter);

  }
  public File openFileChooser() {
    setVisible(true);
    int returnVal = showOpenDialog(this.getParent());
    if(returnVal == JFileChooser.APPROVE_OPTION) {
      this.selectedFile = getSelectedFile();
    }
    setVisible(false);
    return this.selectedFile;
  }
  public void clearFileSelection() {
    this.selectedFile = null;
  }
}
