package model;

import util.Helpers;

/*
Can be used for:
- shared assets card data bases (each season is one record)
- level0 in-game card data (each row in table is one record)
- probably other locations
 */
public abstract class AbstractDropmixDataRecord {
  int startIdx;
  int dataStartIdx;
  int dataLength;
  int dataSpace; // the space occupied by the data (does not include the specifier before the data, does include whitespace at end)
  byte[] raw;// everything including the 32 bit length specifier and whitespace
  byte[] recordData; //
  boolean iOS;
  public AbstractDropmixDataRecord(byte[] data, int startIdx, boolean iOS) {
    this.startIdx = startIdx;
    this.dataStartIdx = startIdx + 4;
    this.dataLength = Helpers.intFromByteArray(Helpers.getNRange(data, startIdx, 4));
    int remainder = (dataLength) % 4;
    this.dataSpace = dataLength + (remainder == 0 ? 0 : 4 - remainder);
    this.raw = Helpers.getNRange(data, startIdx, dataSpace + 4);
    this.recordData = Helpers.getNRange(data, startIdx + 4, dataLength);
    this.iOS = iOS;
  }

  public String toString() {
    return Helpers.byteArrayToString(Helpers.getNRange(raw, 4, dataLength));
  }

  public boolean validateUpdatedData(byte[] newData) {
    if (
      newData.length != dataLength
    ) {
      return false;
    }
    return true;
  }

  public static int getStartIndex(byte[] rawData, byte[] startSequence) {
    outer:
    for (int i = 0; i < rawData.length; i++) {
      if (rawData[i] == startSequence[0]) {
        inner:
        for (int j = 1; j < startSequence.length &&  (j+i) < rawData.length; j++ ) {
          if (rawData[i + j] != startSequence[j]) {
            continue outer;
          }
        }
        return i + startSequence.length;
      }
    }
    return -1;
  }

  public abstract String[] getHeadings();
}