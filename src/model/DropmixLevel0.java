package model;

import util.Helpers;

import java.util.HashSet;

public class DropmixLevel0 {
  public byte[] raw;
  public int startIdx;
  public static String preData = "\u0000\u0000\u0000\u0000\u0002\u0000\u0000\u0000Ä\u0002\u0000\u0000\u0000\u0000\u0000\u0000\u0003\u0000\u0000\u0000\u0002\u0000\u0000\u0000µ\u0002\u0000\u0000\u0000\u0000\u0000\u0000\u0002\u0000\u0000\u0000Á\u0002\u0000\u0000\u0000\u0000\u0000\u0000\u0002\u0000\u0000\u0000Å\u0002\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000¹\u0001\u0000\u0000";
  //public static byte[] header = {7,73,68,44,65,114,116,105,115,116,44,78,97,109,101,44,65,117,100,105,111,44,73,108,108,117,115,116,114,97,116,111,114,44,73,109,97,103,101,44,84,121,112,101,44,78,117,109,32,66,97,114,115,44,84,101,115,116,32,80,111,119,101,114,44,73,110,115,116,114,117,109,101,110,116,44,73,110,115,116,114,117,109,101,110,116,32,50,44,73,110,115,116,114,117,109,101,110,116,32,51,44,73,110,115,116,114,117,109,101,110,116,32,52,44,71,101,110,114,101,44,89,101,97,114,44,83,111,117,114,99,101,44,65,98,105,108,105,116,121,44,83,99,114,101,101,110,32,84,101,120,116,44,77,117,115,105,99,32,69,102,102,101,99,116,44,84,101,109,112,111,44,75,101,121,44,77,111,100,101,44,84,114,97,110,115,105,116,105,111,110,44,87,105,108,100,32,66,101,97,116,32,72,97,115,32,75,101,121,44,65,114,116,32,67,114,111,112,32,67,101,110,116,101,114,44,67,44,68,98,44,68,44,69,98,44,69,44,70,44,71,98,44,71,44,65,98,44,65,44,66,98,44,66,44,67,114,101,100,105,116,115};
  public DropmixLevel0Card[] cards = new DropmixLevel0Card[440];
  public int dataLength;
  public boolean iOS = false;

  public DropmixLevel0(byte[] raw) {
    byte[] header = new byte[preData.length()];
    int i = 0;
    for (char c : preData.toCharArray()) {
      header[i] = (byte) c;
      i++;
    }
    this.startIdx = AbstractDropmixDataRecord.getStartIndex(raw, header);
    this.raw = raw;
    DropmixLevel0Card headingRow = new DropmixLevel0Card(raw, this.startIdx, new HashSet<String>(), this.iOS);
    for (String h: DropmixLevel0Card.headings) {
      if (!h.equals(headingRow.card.data.get(h))) {
        throw new RuntimeException("db-corrupt:bad-heading:"+ h);
      }
    }

    int currentStart = headingRow.startIdx + headingRow.raw.length;
    int counter = 0;
    while (counter < 440) {
      DropmixLevel0Card newCard = new DropmixLevel0Card(raw, currentStart, new HashSet<String>(), this.iOS);
      currentStart = newCard.startIdx + newCard.raw.length;
      cards[counter] = newCard;
      counter++;
    }
    this.dataLength = currentStart - startIdx;
  }

  public byte[] backToByteArray() {
    byte[] rawCopy = this.raw.clone();
    for (DropmixLevel0Card card: this.cards) {
      byte[] cardData = card.backToByteArray();
      for (int i = 0; i < card.dataLength; i++) {
        rawCopy[card.startIdx + i] = cardData[i];
      }
    }
    return rawCopy;
  }
}
