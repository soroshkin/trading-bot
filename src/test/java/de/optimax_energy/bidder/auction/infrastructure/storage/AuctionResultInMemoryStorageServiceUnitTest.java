package de.optimax_energy.bidder.auction.infrastructure.storage;

import de.optimax_energy.bidder.UnitTest;
import de.optimax_energy.bidder.auction.api.dto.RoundResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class AuctionResultInMemoryStorageServiceUnitTest extends UnitTest {

  private static final String UUID = "0";

  private static final String ANOTHER_UUID = "1";

  private final AuctionResultInMemoryStorageService auctionResultInMemoryStorageService = new AuctionResultInMemoryStorageService();

  @Test
  @DisplayName("Should add round result")
  void shouldAddRoundResultForBidder() {
    // given
    RoundResult givenRoundResult = RoundResult.builder().build();

    // when
    auctionResultInMemoryStorageService.addRoundResultForBidder(UUID, givenRoundResult);

    // then
    List<RoundResult> roundResultsForBidder = auctionResultInMemoryStorageService.getRoundResultsForBidder(UUID);
    assertThat(roundResultsForBidder).hasSize(1);
    assertThat(roundResultsForBidder.get(0)).isEqualTo(givenRoundResult);
  }

  @Test
  @DisplayName("Should add round result for different bidders")
  void shouldAddRoundResultForDifferentBidders() {
    // given
    RoundResult givenRoundResult = RoundResult.builder().withMyBid(5).build();
    RoundResult givenSecondRoundResult = RoundResult.builder().withMyBid(999).build();

    // when
    auctionResultInMemoryStorageService.addRoundResultForBidder(UUID, givenRoundResult);
    auctionResultInMemoryStorageService.addRoundResultForBidder(ANOTHER_UUID, givenSecondRoundResult);

    // then
    List<RoundResult> roundResultsForFirstBidder = auctionResultInMemoryStorageService.getRoundResultsForBidder(UUID);
    List<RoundResult> roundResultsForSecondBidder = auctionResultInMemoryStorageService.getRoundResultsForBidder(ANOTHER_UUID);
    assertThat(roundResultsForFirstBidder).hasSize(1);
    assertThat(roundResultsForFirstBidder.get(0)).isEqualTo(givenRoundResult);
    assertThat(roundResultsForSecondBidder).hasSize(1);
    assertThat(roundResultsForSecondBidder.get(0)).isEqualTo(givenSecondRoundResult);
  }

  @Test
  @DisplayName("Should get round results")
  void shouldGetRoundResultsForBidder() {
    // given
    RoundResult givenRoundResult = RoundResult.builder().build();
    auctionResultInMemoryStorageService.addRoundResultForBidder(UUID, givenRoundResult);

    // when
    List<RoundResult> roundResultsForBidder = auctionResultInMemoryStorageService.getRoundResultsForBidder(UUID);

    // then
    assertThat(roundResultsForBidder).hasSize(1);
    assertThat(roundResultsForBidder.get(0)).isEqualTo(givenRoundResult);
  }
}
