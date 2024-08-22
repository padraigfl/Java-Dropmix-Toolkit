import java.util.Arrays;

public class SeasonTable {
  public String[][] rows;
  public byte[] raw;
  public int length;
  public CardDetail[] cards;

  public SeasonTable(byte[] rawTableData, int startIdx, boolean isSeason2) {
    this.raw = rawTableData;
    this.length = Helpers.intFromByteArray((Arrays.copyOfRange(this.raw, startIdx, startIdx + 4)));
    this.rows = Helpers.getByteRowSplit(this.raw, startIdx + 4, this.length, isSeason2);
    this.cards = new CardDetail[this.rows.length - 1];
    for (int i = 0; i < this.cards.length; i++) {
//      System.out.println(this.rows.length);
//      System.out.println(this.cards.length);
      this.cards[i] = new CardDetail(this.rows[i+1]);
    }
  }

  public byte[] backToByteArray() {
    byte[] newByteArray = new byte[this.length];

    int counter = 0;

    for (int i = 0; i < 4; i++) {
      newByteArray[counter] = this.raw[i];
      counter++;
    }
    String heading = String.join(",", CardDetail.heading);
    for (int i = 0; i < heading.length(); i++) {
      newByteArray[counter] = (byte) heading.charAt(i);
      counter++;
    }
    for (CardDetail card: this.cards) {
      String cardRow = card.rowToDataString();
      for (int i = 0; i < cardRow.length(); i++) {
        newByteArray[counter] = (byte) cardRow.charAt(i);
        counter++;
      }
    }
    if (newByteArray.length != this.raw.length) {
      throw new Error("database-length-error");
    }
    return newByteArray;
  }
}
