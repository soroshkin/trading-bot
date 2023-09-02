package de.optimax_energy.bidder.auction.infrastructure.strategy;

import de.optimax_energy.bidder.UnitTest;
import de.optimax_energy.bidder.auction.api.dto.RoundResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

class AggressiveBiddingStrategyUnitTest extends UnitTest {

  private static final int INITIAL_CASH = 1000;

  private static final int INITIAL_QUANTITY = 10;

  @Mock
  private StatisticsService statisticsService;

  private AggressiveBiddingStrategy aggressiveBiddingStrategy;

  @BeforeEach
  void setUp() {
    aggressiveBiddingStrategy = new AggressiveBiddingStrategy(statisticsService);
  }

  @Test
  @DisplayName("Should return bid, which bigger by 1 than opponent remaining cash, if opponent needs one QU to win")
  void shouldReturnBidWhichMoreThanOpponentRemainingCash() {
    // given
    List<RoundResult> roundResults = givenRoundResults();
    int opponentRemainingCash = 1;
    when(statisticsService.calculateOpponentRemainingCash(roundResults, INITIAL_CASH)).thenReturn(opponentRemainingCash);
    when(statisticsService.calculateOpponentQuantity(roundResults)).thenReturn(INITIAL_QUANTITY / 2);
    when(statisticsService.calculateMyRemainingCash(roundResults, INITIAL_CASH)).thenReturn(INITIAL_CASH);

    // when
    int bid = aggressiveBiddingStrategy.placeBid(roundResults, INITIAL_QUANTITY, INITIAL_CASH);

    // then
    assertThat(bid).isEqualTo(opponentRemainingCash + 1);
    verify(statisticsService).calculateOpponentRemainingCash(roundResults, INITIAL_CASH);
    verify(statisticsService).calculateOpponentQuantity(roundResults);
    verify(statisticsService).calculateMyRemainingCash(roundResults, INITIAL_CASH);
    verify(statisticsService).calculateMyQuantity(roundResults);
    verifyNoMoreInteractions(statisticsService);
  }

  @Test
  @DisplayName("Should return bid, which equals tot remaining cash, if opponent needs one QU to win")
  void shouldReturnBidWhichEqualsToRemainingCash() {
    // given
    List<RoundResult> roundResults = givenRoundResults();
    when(statisticsService.calculateOpponentRemainingCash(roundResults, INITIAL_CASH)).thenReturn(INITIAL_CASH);
    when(statisticsService.calculateOpponentQuantity(roundResults)).thenReturn(INITIAL_QUANTITY / 2);
    when(statisticsService.calculateMyRemainingCash(roundResults, INITIAL_CASH)).thenReturn(INITIAL_CASH);

    // when
    int bid = aggressiveBiddingStrategy.placeBid(roundResults, INITIAL_QUANTITY, INITIAL_CASH);

    // then
    assertThat(bid).isEqualTo(INITIAL_CASH);
    verify(statisticsService).calculateOpponentRemainingCash(roundResults, INITIAL_CASH);
    verify(statisticsService).calculateOpponentQuantity(roundResults);
    verify(statisticsService).calculateMyRemainingCash(roundResults, INITIAL_CASH);
    verify(statisticsService).calculateMyQuantity(roundResults);
    verifyNoMoreInteractions(statisticsService);
  }

  @Test
  @DisplayName("Should adjust bid and make it larger by 1 than opponent remaining cash, if opponent remaining cash less than calculated bid")
  void shouldDecreaseBidIfItIsLargerThanOpponentRemainingCash() {
    // given
    List<RoundResult> roundResults = givenRoundResults();
    int opponentRemainingCash = 1;
    when(statisticsService.calculateOpponentRemainingCash(roundResults, INITIAL_CASH)).thenReturn(opponentRemainingCash);
    when(statisticsService.calculateOpponentQuantity(roundResults)).thenReturn(0);
    when(statisticsService.calculateMyRemainingCash(roundResults, INITIAL_CASH)).thenReturn(INITIAL_CASH);
    when(statisticsService.calculateOpponentAverageBid(roundResults)).thenReturn(INITIAL_CASH / INITIAL_QUANTITY);

    // when
    int bid = aggressiveBiddingStrategy.placeBid(roundResults, INITIAL_QUANTITY, INITIAL_CASH);

    // then
    assertThat(bid).isEqualTo(opponentRemainingCash + 1);
    verify(statisticsService).calculateOpponentRemainingCash(roundResults, INITIAL_CASH);
    verify(statisticsService).calculateOpponentQuantity(roundResults);
    verify(statisticsService).calculateMyRemainingCash(roundResults, INITIAL_CASH);
    verify(statisticsService).calculateMyQuantity(roundResults);
    verify(statisticsService).calculateOpponentAverageBid(roundResults);
    verifyNoMoreInteractions(statisticsService);
  }

  @Test
  @DisplayName("Should increase bid in 1.34 times opponent's average bid")
  void shouldIncreaseBidDependingOnOpponentAverageBid() {
    // given
    List<RoundResult> roundResults = givenRoundResults();
    double bidMultiplier = 1.34;
    when(statisticsService.calculateOpponentRemainingCash(roundResults, INITIAL_CASH)).thenReturn(INITIAL_CASH);
    when(statisticsService.calculateOpponentQuantity(roundResults)).thenReturn(1);
    when(statisticsService.calculateMyRemainingCash(roundResults, INITIAL_CASH)).thenReturn(INITIAL_CASH);
    when(statisticsService.calculateOpponentAverageBid(roundResults)).thenReturn(INITIAL_CASH / INITIAL_QUANTITY);

    // when
    int bid = aggressiveBiddingStrategy.placeBid(roundResults, INITIAL_QUANTITY, INITIAL_CASH);

    // then
    assertThat(bid).isEqualTo((int) (bidMultiplier * INITIAL_CASH / INITIAL_QUANTITY));
    verify(statisticsService).calculateOpponentRemainingCash(roundResults, INITIAL_CASH);
    verify(statisticsService).calculateOpponentQuantity(roundResults);
    verify(statisticsService).calculateMyRemainingCash(roundResults, INITIAL_CASH);
    verify(statisticsService).calculateMyQuantity(roundResults);
    verify(statisticsService).calculateOpponentAverageBid(roundResults);
    verifyNoMoreInteractions(statisticsService);
  }

  private List<RoundResult> givenRoundResults() {
    return List.of(RoundResult.builder().build());
  }
}
