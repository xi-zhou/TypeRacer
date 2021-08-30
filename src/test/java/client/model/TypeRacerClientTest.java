package client.model;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** Test class for TypeRacer client model. */
public class TypeRacerClientTest {

  private MultiplayerTypeRacerClient multiplayerTypeRacerClient;

  @BeforeEach
  public void setUp() throws Exception {
    multiplayerTypeRacerClient = new MultiplayerTypeRacerClient();
  }

  @Test
  @DisplayName("calculation for words per minute should work")
  public void testCalculateWpm() {
    assertNotNull(multiplayerTypeRacerClient.calculateWpm(),
        "Regular Calculation per minute works should work");
  }
}
