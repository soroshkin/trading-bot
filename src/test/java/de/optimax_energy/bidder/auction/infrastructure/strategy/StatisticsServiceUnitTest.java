package de.optimax_energy.bidder.auction.infrastructure.strategy;

import de.optimax_energy.bidder.UnitTest;
import de.optimax_energy.bidder.auction.api.dto.RoundResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class StatisticsServiceUnitTest extends UnitTest {

  private final StatisticsService statisticsService = new StatisticsService();

  private final int myWonQuantity = 2;

  private final int opponentRemainingCash = 100;

  private final int myBid = 5;

  private final int opponentBid = 9;

  private final int opponentWonQuantity = 12;

  @Test
  @DisplayName("Should calculate opponent quantity")
  void shouldCalculateOpponentQuantity() {
    // given
    List<RoundResult> roundResults = givenRoundResults();

    // when
    int bid = statisticsService.calculateOpponentQuantity(roundResults);

    // then
    assertThat(bid).isEqualTo(opponentWonQuantity);
  }

  @Test
  @DisplayName("Should calculate opponent quantity when results are empty")
  void shouldCalculateOpponentQuantityWhenResultsAreEmpty() {
    // given
    List<RoundResult> roundResults = Collections.emptyList();

    // when
    int bid = statisticsService.calculateOpponentQuantity(roundResults);

    // then
    assertThat(bid).isZero();
  }

  @Test
  @DisplayName("Should calculate my quantity")
  void shouldCalculateMyQuantity() {
    // given
    List<RoundResult> roundResults = givenRoundResults();

    // when
    int bid = statisticsService.calculateMyQuantity(roundResults);

    // then
    assertThat(bid).isEqualTo(myWonQuantity);
  }

  @Test
  @DisplayName("Should calculate my quantity when results are empty")
  void shouldCalculateMyQuantityWhenResultsAreEmpty() {
    // given
    List<RoundResult> roundResults = Collections.emptyList();

    // when
    int bid = statisticsService.calculateMyQuantity(roundResults);

    // then
    assertThat(bid).isZero();
  }

  @Test
  @DisplayName("Should calculate my remaining cash")
  void shouldCalculateMyRemainingCash() {
    // given
    List<RoundResult> roundResults = givenRoundResults();

    // when
    int bid = statisticsService.calculateMyRemainingCash(roundResults, INITIAL_CASH);

    // then
    assertThat(bid).isEqualTo(INITIAL_CASH - myBid);
  }

  @Test
  @DisplayName("Should calculate my remaining cash when results are empty")
  void shouldCalculateMyRemainingCashWhenResultsAreEmpty() {
    // given
    List<RoundResult> roundResults = Collections.emptyList();

    // when
    int bid = statisticsService.calculateMyRemainingCash(roundResults, INITIAL_CASH);

    // then
    assertThat(bid).isEqualTo(INITIAL_CASH);
  }

  @Test
  @DisplayName("Should calculate opponent remaining cash")
  void shouldCalculateOpponentRemainingCash() {
    // given
    List<RoundResult> roundResults = givenRoundResults();

    // when
    int bid = statisticsService.calculateOpponentRemainingCash(roundResults, INITIAL_CASH);

    // then
    assertThat(bid).isEqualTo(opponentRemainingCash);
  }

  @Test
  @DisplayName("Should calculate opponent remaining cash when results are empty")
  void shouldCalculateOpponentRemainingCashWhenResultsAreEmpty() {
    // given
    List<RoundResult> roundResults = Collections.emptyList();

    // when
    int bid = statisticsService.calculateOpponentRemainingCash(roundResults, INITIAL_CASH);

    // then
    assertThat(bid).isEqualTo(INITIAL_CASH);
  }

  @Test
  @DisplayName("Should calculate opponent average bid")
  void shouldCalculateOpponentAverageBid() {
    // given
    int firstBid = 10;
    int secondBid = 20;
    List<RoundResult> roundResults = List.of(RoundResult.builder().withOpponentBid(firstBid).build(),
      RoundResult.builder().withOpponentBid(secondBid).build());

    // when
    int bid = statisticsService.calculateOpponentAverageBid(roundResults);

    // then
    assertThat(bid).isEqualTo((firstBid + secondBid) / 2);
  }

  @Test
  @DisplayName("Should calculate opponent average bid when results are empty")
  void shouldCalculateOpponentAverageBidWhenResultsAreEmpty() {
    // given
    List<RoundResult> roundResults = Collections.emptyList();

    // when
    int bid = statisticsService.calculateOpponentAverageBid(roundResults);

    // then
    assertThat(bid).isZero();
  }

  private List<RoundResult> givenRoundResults() {
    return List.of(RoundResult.builder()
      .withMyWonQuantity(myWonQuantity)
      .withOpponentRemainingCash(opponentRemainingCash)
      .withMyBid(myBid)
      .withOpponentBid(opponentBid)
      .withOpponentWonQuantity(opponentWonQuantity)
      .build());
  }
}
