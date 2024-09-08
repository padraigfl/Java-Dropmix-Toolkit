package model;

import java.util.ArrayList;
import java.util.Set;
import java.util.TreeMap;

public class DataRow {
  public String[] headings;
  public TreeMap<String, String> data = new TreeMap<String, String>();
  boolean iOS;
  boolean keepQuotesOnAllStrings;
  static int dataStartIdx = 0;
  String[] src;
  Set<String> forceStringFields;

  public DataRow(String[] data, String[] headings, Set<String> forceStringFields) {
    this.src = data;
    this.headings = headings;
    this.forceStringFields = forceStringFields;
    this.parseDataRow();
  }
  public void parseDataRow() {
    ArrayList<String> finalEntry = new ArrayList<>();
    this.data = new TreeMap<String, String>();
    for (int i = 0; i < src.length; i++) {
      if (i >= (headings.length - 1)) {
        if (i == headings.length - 1 || i == src.length - 1) {
          finalEntry.add(src[i].replaceAll("\"", ""));
        } else {
          finalEntry.add(src[i]);
        }
      } else {
        data.put(headings[i], src[i].replaceAll("\"", ""));
      }
    }
    data.put(headings[headings.length - 1], String.join(",", finalEntry.toArray(new String[0])).replaceAll("\n", ""));
  };
  public String writeDataRow() {
    ArrayList<String> row = new ArrayList<>();
    for (int i = 0; i < headings.length; i++) {
      if (forceStringFields.contains(headings[i])) {
        row.add("\"" + data.get(headings[i]) + "\"");
        continue;
      }
      try {
        Integer.parseInt(this.data.get(i));
        row.add(this.data.get(i));
      } catch(Exception e) {
        row.add(this.data.get(i));
      }
    }
    return String.join(",", row);
  };
  public String[] getHeadings() {
    return new String[]{};
  };
}
