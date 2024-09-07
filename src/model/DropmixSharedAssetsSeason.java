package model;

import util.Helpers;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DropmixSharedAssetsSeason {
  public static final String rowDivider = "\r\n";
  public String[][] rows;
  public byte[] rawDb; // the chunk of the main assets that corresponds to this season; includes 32bit length
  public int length;
  public DropmixSharedAssetsCard[] cards;
  public int startIdx;
  public TreeMap<String, Integer> cardIndexRef = new TreeMap<>();
  public byte[] header;
  public String[] columns;

  public DropmixSharedAssetsSeason(byte[] rawTableData, byte[] startHeader, int startIdx, int season) {
    this.startIdx = startIdx;
    this.header = startHeader;
    this.length = Helpers.intFromByteArray((Arrays.copyOfRange(rawTableData, startIdx, startIdx + 4)));
    this.rawDb = getRawDb(rawTableData, startIdx, length);
    this.rows = csvParser(
      Arrays.copyOfRange(this.rawDb, 4, this.rawDb.length), ",", "\"", season
    );

    // TODO add logs of output season data

    this.columns = this.rows[0];

    this.cards = new DropmixSharedAssetsCard[this.rows.length - 1];
    for (int i = 0; i < this.cards.length; i++) {
      this.cards[i] = new DropmixSharedAssetsCard(this.rows[i+1]);
      cardIndexRef.put(this.cards[i].data.get(DropmixSharedAssetsCard.SourceCID), Integer.valueOf(i));
    }
  }

  public byte[] backToByteArray(boolean includeEndNewLine) {
    String seasonCSV = DropmixSharedAssetsSeason.csvWriter(toNestedString(), ",", "\"", cards[0].getCardSeason());

    byte[] seasonByteArray = new byte[rawDb.length];
    // insert DB length
    for (int i = 0; i < 4; i++) {
      seasonByteArray[i] = rawDb[i];
    }
    // build out byte array
    for (int i = 0; i < seasonCSV.length(); i++) {
      seasonByteArray[i + 4] = (byte) seasonCSV.charAt(i);
    }
    for (int i = seasonCSV.length() + 4; i < rawDb.length; i++) {
      seasonByteArray[i] = rawDb[i];
    }
    return seasonByteArray;
  }
  static String getDb(byte[] b) {
    StringBuilder sb = new StringBuilder();
    for (byte a: b) {
      sb.append((char) a);
    }
    return sb.toString();
  }
  /**/
  public String[][] toNestedString() {
    String[][] nestedData = new String[this.cards.length + 1][this.columns.length];
    nestedData[0] = this.columns;

    int i = 1;
    for (DropmixSharedAssetsCard c: this.cards) {
      String[] cardStringList = new String[columns.length];
      int j = 0;
      for(String key: columns) {
        cardStringList[j] = c.data.get(key);
        j++;
      }
      nestedData[i] = cardStringList;
      i++;
    }
    return nestedData;
  }

  /*
  Quirks:
    - headings never use quotes for strings
    - data rows are inconsistent with quotes
    - season 1's "Season" field is a string instead of a number (i.e. has quotes)
    - season 2 only uses quotes for strings which contain commas (e.g. "Days Ahead, Days Behind")
    - commas within quotes need to be caught
   */
  public static String[][] csvParser(String text, String delimiter, String textQualifier, int season) {
    String[] rows = text.split(rowDivider);
    String[][] rowsSplit = new String[0][0];
    int counter = 0;
    int colCount = 0;
    for (String rowText: rows) {
      // System.out.println(rowText); // prints data row
      String processedRow = replaceRogueCommas(rowText, ",");
      String[] splitRow = processedRow.split(delimiter);

      for (int i = 0; i < colCount; i++ ) {
        rowsSplit[counter][i] = splitRow[i].replaceAll(textQualifier, "");
      }
      if (colCount == 0) {
        colCount = splitRow.length;
        rowsSplit = new String[rows.length][colCount];
      }
      rowsSplit[counter++] = splitRow;
    }
    return rowsSplit;
  }
  public static String[][] csvParser(byte[] bytes, String delimiter, String textQualifier, int season) {
    StringBuilder sb = new StringBuilder();

    for (byte b: bytes) {
      sb.append((char) b);
    }
    return csvParser(sb.toString(), delimiter, textQualifier, season);
  }
  public static String csvWriter(String[][] csv, String delimiter, String textQualifier, int season) {
    int rows = csv.length;
    int cols = csv[0].length;
    StringBuilder text = new StringBuilder();
    boolean allStringsTextQualifier = season != 2;
    boolean isSeasonFieldString = season == 1;
    int currentRowIdx = 0;
    for (String[] row: csv) {
      int currentColIdx = 0;
      for (String cell: row) {
        boolean hasComma = cell.contains("£") || cell.contains(",");
        String formattedString = cell.replaceAll("£", ",");
        boolean isLikelyString = true;
        try {
          Integer.valueOf(cell);
          isLikelyString = false;
        } catch (NumberFormatException e) {
          if (cell.isEmpty()) {
            isLikelyString = false;
          }
        }
        if (
          (currentRowIdx != 0
            && (
              (isLikelyString && allStringsTextQualifier)
              || (isSeasonFieldString && Objects.equals(csv[0][currentColIdx], DropmixSharedAssetsCard.Season))
          )) || (hasComma)
        ){
          formattedString = textQualifier + formattedString + textQualifier;
        }
        text.append(formattedString);
        currentColIdx++;
        if (currentColIdx != cols) {
          text.append(delimiter);
        }
      }
      currentRowIdx++;
      if (currentRowIdx != rows) {
        text.append(rowDivider);
      }
    }
    return text.toString();
  }

  // replaces tricky commas in quoted fields on CSV
  public static String replaceRogueCommas(String text, String delimiter) {
    StringBuilder sb = new StringBuilder();
    String[] splitText = text.split(delimiter);
    for (int i = 0; i < splitText.length; i++) {
      String chunk = splitText[i];
      sb.append(chunk);
      // need to find instances such as "I Don't Like It, I Love It (ft. Robin Thicke, Verdine White)"
      Pattern p = Pattern.compile("^\\s.*[a-z}]+$");
      Matcher m = p.matcher(chunk);
      if (
        (!chunk.isEmpty() && chunk.charAt(0) == '"' && chunk.charAt(chunk.length() -1) != '"')
        || m.find()
      ){
        sb.append('£');
      } else if (i < (splitText.length - 1)) {
        sb.append(',');
      }
    }
    return sb.toString();
  }
  public static byte[] getRawDb(byte[] bytes, int startIdx, int length) {
    return Arrays.copyOfRange(bytes, startIdx, startIdx + length + 4);
  }
}
