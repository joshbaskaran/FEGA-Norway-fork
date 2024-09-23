package no.uio.ifi.tc;

import org.junit.jupiter.api.*;

public class TSDFileAPIClientTest {

  @Test
  public void test() {
    TSDFileAPIClient tsdFileAPIClient =
        new TSDFileAPIClient.Builder().accessKey("access-key").build();
    Assertions.assertNotNull(tsdFileAPIClient);
  }
}
