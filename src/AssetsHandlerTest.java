import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.*;

class AssetsHandlerTest {
  Helpers helper = new Helpers();
  AssetsHandler assetsHandler = new AssetsHandler(helper.loadFile(), AssetsHandler.s0Header);

  @Test
  void processSwapActionTest() {
    TreeMap<String, String> test = new TreeMap<>();
    test.put("p01_c001_i01_maintitlesong_wild", "p01_c002_i01_flutterfly_wild");
    test.put("p01_c002_i01_flutterfly_wild", "p01_c001_i01_maintitlesong_wild");
    byte[] newDb = assetsHandler.processSwapAction(test);
    byte[] dbParsed = Arrays.copyOfRange(newDb, assetsHandler.startIdx, assetsHandler.startIdx + assetsHandler.dbLength);
    Assertions.assertEquals(dbParsed.length, assetsHandler.dbLength);
    Assertions.assertEquals(newDb.length, assetsHandler.rawData.length);
  }
}