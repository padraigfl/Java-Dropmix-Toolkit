package model;

import util.Helpers;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeMap;

public class DropmixLevel0Card extends AbstractDropmixDataRecord {
//  public static final String CID = "CID";
//  public static final String Artist = "Artist";
//  public static final String Name = "Name";
//  public static final String Audio = "Audio";
//  public static final String Illustrator = "Illustrator";
//  public static final String Image = "Image";
//  public static final String Type = "Type";
//  public static final String NumBars = "Num Bars";
//  public static final String TestPower = "Test Power";
//  public static final String Instrument = "Instrument";
//  public static final String Instrument2 = "Instrument 2";
//  public static final String Instrument3 = "Instrument 3";
//  public static final String Instrument4 = "Instrument 4";
//  public static final String Genre = "Genre";
//  public static final String Year = "Year";
//  public static final String Source = "Source";
//  public static final String Ability = "Ability";
//  public static final String ScreenText = "Screen Text";
//  public static final String MusicEffect = "Music Effect";
//  public static final String Tempo = "Tempo";
//  public static final String Key = "Key";
//  public static final String Mode = "Mode";
//  public static final String Transition = "Transition";
//  public static final String WildBeatHasKey = "Wild Beat Has Key";
//  public static final String ArtCropCenter = "Art Crop Center";
//  public static final String C = "C";
//  public static final String Db = "Db";
//  public static final String D = "D";
//  public static final String Eb = "Eb";
//  public static final String E = "E";
//  public static final String F = "F";
//  public static final String Gb = "Gb";
//  public static final String G = "G";
//  public static final String Ab = "Ab";
//  public static final String A = "A";
//  public static final String Bb = "Bb";
//  public static final String B = "B";
public static final String[] headings = new String[]{
  "CID",
  "Artist",
  "Name",
  "Audio",
  "Illustrator",
  "Image",
  "Type",
  "Num Bars",
  "Test Power",
  "Instrument",
  "Instrument 2",
  "Instrument 3",
  "Instrument 4",
  "Genre",
  "Year",
  "Source",
  "Ability",
  "Screen Text",
  "Music Effect",
  "Tempo",
  "Key",
  "Mode",
  "Transition",
  "Wild Beat Has Key",
  "Art Crop Center",
  "C",
  "Db",
  "D",
  "Eb",
  "E",
  "F",
  "Gb",
  "G",
  "Ab",
  "A",
  "Bb",
  "B",
  "Credits",
};
  public static final String Credits = "Credits";
  public DataRow card;
  public Set<String> forceStringFields = new HashSet<>();
  public DropmixLevel0Card(byte[] rawData, int startIdx, Set<String> forceStringFields, boolean iOS) {
    super(rawData, startIdx, iOS);

    String rowString = Helpers.bytetoString(this.recordData);
    if (iOS) {
      rowString = rowString.replace("\r\n", "");// remove padding at end of listing
    }
    this.card = new DataRow(rowString.split(","), headings, forceStringFields);
  }

  public void updateEntry(String key, String newData) {
    if (key.equals(Credits)) {
      throw new Error("cant-update-credits");
    }
    String oldValue = this.card.data.get(key);
    int changeInSize = newData.length() - oldValue.length();
    String fieldToChange = this.card.data.get(Credits);
    if (changeInSize > 0) {
      fieldToChange = fieldToChange.substring(0, fieldToChange.length() - changeInSize);
    } else if (changeInSize < 0) {
      fieldToChange = Helpers.rPad(fieldToChange, fieldToChange.length() + Math.abs(changeInSize), ' ');
    }
    if (changeInSize != 0) {
      this.card.data.put(Credits, fieldToChange);
    }
    // update length of credits
  }
  public byte[] backToByteArray() {
    // convert from +4 to i+length
    byte[] newRaw = raw.clone();
    StringBuilder sb = new StringBuilder();
    for (String h: headings) {
      String field = this.card.data.get(h);

      if (forceStringFields.contains(h) || field.contains(",")) {
        field = Helpers.addQuotes(field);
      } else {
        try {
          Integer.parseInt(field);
        } catch (Exception e) {
          if (field.length() > 0) {
            field = Helpers.addQuotes(field);
          }
        }
      }
      sb.append(field);
      if (!h.equals(Credits)) {
        sb.append(",");
      }
    }
    byte[] rowData = Helpers.stringToByte(sb.toString(), true);
    int count = 4;
    for (byte b : rowData) {
      newRaw[count++] = b;
    }
    return newRaw;
  }
  @Override
  public String[] getHeadings() {
    return DropmixLevel0Card.headings;
  }
}
