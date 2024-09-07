package model;

import util.Helpers;

import java.util.*;

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
  public String originalValue;
  public DropmixLevel0Card(byte[] rawData, int startIdx, Set<String> forceStringFields, boolean iOS) {
    super(rawData, startIdx, iOS);

    this.originalValue = Helpers.bytetoString(this.recordData);
    if (iOS) {
      this.originalValue = this.originalValue.replace("\r\n", "");// remove padding at end of listing
    }

    // TODO move CSV parser to somewhere more relevant
    String[] parsedRow = rowParser(this.originalValue);
    this.card = new DataRow(parsedRow, headings, forceStringFields);
  }


  public static String[] rowParser(String value) {
    // hack for headings
    if (!value.contains("\"")) {
      return value.split(",");
    }
    // lazy fix for annoying quotes in credits field
    String[] quotesHandled = value
      .replaceAll(" \"", " '").replaceAll("\" ", "' ").replaceAll("\"\\)", "')")
      .split("\"");
    ArrayList<String> builder = new ArrayList<>();
    boolean firstEmptyInstancePassed = false;
    for (String quoteChunk: quotesHandled) {
      // skip first empty value
      if (quoteChunk.equals("") && builder.size() == 0) {
        continue;
      }
      if (!firstEmptyInstancePassed && quoteChunk.length() == 0) {
        firstEmptyInstancePassed = true;
      } else if (quoteChunk.length() > 0 && quoteChunk.charAt(0) == ',') {
        // if comma between quoted fields, skip
        if (quoteChunk.length() == 1) {
          continue;
        }
        String emptyCommaHack = quoteChunk.substring(1).replaceAll(",", ".,");
        String[] followingFields = emptyCommaHack.split(",");
        for (String field: followingFields) {
          String formattedField = field.replaceAll("\\.$", "");
          builder.add(formattedField);
        }
      } else {
        builder.add(Helpers.addQuotes(quoteChunk));
      }
    }
    String[] bits = new String[builder.size()];
    int i = 0;
    for (String b: bits) {
      bits[i] = builder.get(i++);
    }

    return builder.toArray(new String[0]);
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
    this.card.data.put(key, newData);
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
          int intVal = Integer.parseInt(field);
          // FIXME issue for Ana Tijoux 1977
          if (intVal == 1977 && !h.equals("Year")) {
            field = Helpers.addQuotes(field);
          }
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
    try {
      for (byte b : rowData) {
        newRaw[count++] = b;
      }
    } catch (ArrayIndexOutOfBoundsException e) {
      System.out.println(this);
      throw new Error(e);
    }
    return newRaw;
  }
  @Override
  public String[] getHeadings() {
    return DropmixLevel0Card.headings;
  }
  public String toString() {
    StringBuilder sb = new StringBuilder();
    for (String h: headings) {
      if (sb.length() > 0) {
        sb.append(",");
      }
      String data = this.card.data.get(h);
      if (data == null) {
        data = "";
      }
      try {
        Integer.parseInt(data);
        if (!h.equals("Year")) {
          data = Helpers.addQuotes(data);
        }
      } catch (Exception e) {
        if (data.length() > 0) {
          data = Helpers.addQuotes(data);
        }
      }
      sb.append(data);
    }
    return sb.toString();
  }
}
