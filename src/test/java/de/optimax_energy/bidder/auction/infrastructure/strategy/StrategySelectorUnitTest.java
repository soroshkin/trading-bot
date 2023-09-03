package de.optimax_energy.bidder.auction.infrastructure.strategy;

import de.optimax_energy.bidder.UnitTest;
import de.optimax_energy.bidder.auction.api.BiddingStrategy;
import de.optimax_energy.bidder.auction.api.dto.RoundResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

class StrategySelectorUnitTest extends UnitTest {

  private static final int AMOUNT_OF_PRODUCTS_IN_ONE_ROUND = 2;

  private static final int INITIAL_CASH = 100;

  @Mock
  private StatisticsService statisticsService;

  @InjectMocks
  private StrategySelector strategySelector;

  @Test
  @DisplayName("Should select MaximumBiddingStrategy when initial quantity is equal to 2")
  void shouldSelectMaximumBiddingStrategyWhenInitialQuantityIsEqualToTwo() {
    // given
    List<RoundResult> roundResults = Collections.emptyList();
    when(statisticsService.calculateMyQuantity(roundResults)).thenReturn(0);

    // when
    Optional<BiddingStrategy> biddingStrategy = strategySelector.select(roundResults, AMOUNT_OF_PRODUCTS_IN_ONE_ROUND, 0);

    // then
    assertThat(biddingStrategy).containsInstanceOf(MaximumBidStrategy.class);
    verify(statisticsService).calculateOpponentQuantity(roundResults);
    verify(statisticsService).calculateMyQuantity(roundResults);
    verifyNoMoreInteractions(statisticsService);
  }

  @Test
  @DisplayName("Should select MaximumBiddingStrategy when last round left")
  void shouldSelectMaximumBiddingStrategyWhenOnlyOneRoundLeft() {
    // given
    int initialQuantity = 20;
    List<RoundResult> roundResults = givenRoundResults(initialQuantity - AMOUNT_OF_PRODUCTS_IN_ONE_ROUND);
    when(statisticsService.calculateMyQuantity(roundResults)).thenReturn(initialQuantity - AMOUNT_OF_PRODUCTS_IN_ONE_ROUND);

    // when
    Optional<BiddingStrategy> biddingStrategy = strategySelector.select(roundResults, initialQuantity, 0);

    // then
    assertThat(biddingStrategy).containsInstanceOf(MaximumBidStrategy.class);
    verify(statisticsService).calculateOpponentQuantity(roundResults);
    verify(statisticsService).calculateMyQuantity(roundResults);
    verifyNoMoreInteractions(statisticsService);
  }

  @ParameterizedTest(name = "Should select ZeroBiddingStrategy, myWonQuantity={0}, opponentWonQuantity={1}, leftQuantity={2}")
  @CsvSource({"7, 1, 4", "1, 7, 4"})
  void shouldSelectZeroBiddingStrategy(int myWonQuantity, int opponentWonQuantity, int leftQuantity) {
    // given
    int initialQuantity = calculateInitialQuantity(myWonQuantity, leftQuantity, opponentWonQuantity);
    List<RoundResult> roundResults = givenRoundResults(myWonQuantity);
    when(statisticsService.calculateOpponentQuantity(roundResults)).thenReturn(opponentWonQuantity);
    when(statisticsService.calculateMyQuantity(roundResults)).thenReturn(myWonQuantity);

    // when
    Optional<BiddingStrategy> biddingStrategy = strategySelector.select(roundResults, initialQuantity, 0);

    // then
    assertThat(biddingStrategy).containsInstanceOf(ZeroBiddingStrategy.class);
    verify(statisticsService).calculateOpponentQuantity(roundResults);
    verify(statisticsService).calculateMyQuantity(roundResults);
    verifyNoMoreInteractions(statisticsService);
  }

  @Test
  @DisplayName("Should select MinimumBiddingStrategy during first round")
  void shouldSelectMinimumBiddingStrategyDuringFirstRound() {
    // given
    List<RoundResult> roundResults = Collections.emptyList();
    when(statisticsService.calculateMyQuantity(roundResults)).thenReturn(0);

    // when
    Optional<BiddingStrategy> biddingStrategy = strategySelector.select(roundResults, 12, 0);

    // then
    assertThat(biddingStrategy).containsInstanceOf(MinimumBidStrategy.class);
    verify(statisticsService).calculateOpponentQuantity(roundResults);
    verify(statisticsService).calculateMyQuantity(roundResults);
    verifyNoMoreInteractions(statisticsService);
  }

  @Test
  @DisplayName("Should not select MinimumBiddingStrategy during first round if initial QU less than defined amount")
  void shouldNotSelectMinimumBiddingStrategyDuringFirstRoundIfInitialQuantityAmountIsLessThanDefined() {
    // given
    List<RoundResult> roundResults = Collections.emptyList();
    when(statisticsService.calculateOpponentRemainingCash(roundResults, INITIAL_CASH)).thenReturn(INITIAL_CASH);
    when(statisticsService.calculateMyQuantity(roundResults)).thenReturn(0);
    int initialQuantity = 8;

    // when
    Optional<BiddingStrategy> biddingStrategy = strategySelector.select(roundResults, initialQuantity, INITIAL_CASH);

    // then
    assertThat(biddingStrategy).containsInstanceOf(AggressiveBiddingStrategy.class);
    verify(statisticsService, times(2)).calculateOpponentQuantity(roundResults);
    verify(statisticsService).calculateOpponentRemainingCash(roundResults, INITIAL_CASH);
    verify(statisticsService).calculateMyQuantity(roundResults);
    verify(statisticsService).calculateMyRemainingCash(roundResults, INITIAL_CASH);
    verifyNoMoreInteractions(statisticsService);
  }

  @Test
  @DisplayName("Should select MinimumBiddingStrategy, if yet not enough QU to win and opponent has no money")
  void shouldSelectMinimumBiddingStrategyIfNotEnoughToWin() {
    // given
    List<RoundResult> roundResults = List.of(RoundResult.builder().withOpponentRemainingCash(0).build());
    when(statisticsService.calculateMyQuantity(roundResults)).thenReturn(0);

    // when
    Optional<BiddingStrategy> biddingStrategy = strategySelector.select(roundResults, 0, 0);

    // then
    assertThat(biddingStrategy).containsInstanceOf(MinimumBidStrategy.class);
    verify(statisticsService).calculateOpponentQuantity(roundResults);
    verify(statisticsService).calculateMyQuantity(roundResults);
    verifyNoMoreInteractions(statisticsService);
  }

  @Test
  @DisplayName("Should not select MinimumBiddingStrategy, if yet not enough QU to win and opponent has money")
  void shouldNotSelectMinimumBiddingStrategyIfNotEnoughToWinAndOpponentHasMoney() {
    // given
    List<RoundResult> roundResults = List.of(RoundResult.builder().withOpponentRemainingCash(1).build());
    when(statisticsService.calculateMyQuantity(roundResults)).thenReturn(0);

    // when
    Optional<BiddingStrategy> biddingStrategy = strategySelector.select(roundResults, 0, INITIAL_CASH);

    // then
    assertThat(biddingStrategy).containsInstanceOf(AggressiveBiddingStrategy.class);
    verify(statisticsService, times(2)).calculateOpponentQuantity(roundResults);
    verify(statisticsService).calculateOpponentRemainingCash(roundResults, INITIAL_CASH);
    verify(statisticsService).calculateMyRemainingCash(roundResults, INITIAL_CASH);
    verify(statisticsService).calculateMyQuantity(roundResults);
    verifyNoMoreInteractions(statisticsService);
  }

  @ParameterizedTest(name = "Should select aggressiveStrategy, myWonQuantity={0}, opponentQuantity={1}, leftQuantity={2}")
  @CsvSource({"1, 3, 2", "10, 40, 40"})
  void shouldSelectAggressiveStrategy(int myWonQuantity, int opponentQuantity, int leftQuantity) {
    // given
    int initialQuantity = calculateInitialQuantity(myWonQuantity, leftQuantity, opponentQuantity);
    List<RoundResult> roundResults = List.of(
      RoundResult.builder()
        .withMyBid(5)
        .withMyWonQuantity(myWonQuantity)
        .withOpponentBid(0)
        .withOpponentRemainingCash(INITIAL_CASH)
        .withOpponentWonQuantity(opponentQuantity)
        .build());
    when(statisticsService.calculateMyQuantity(roundResults)).thenReturn(myWonQuantity);
    when(statisticsService.calculateOpponentRemainingCash(roundResults, INITIAL_CASH)).thenReturn(INITIAL_CASH);

    // when
    Optional<BiddingStrategy> biddingStrategy = strategySelector.select(roundResults, initialQuantity, INITIAL_CASH);

    // then
    assertThat(biddingStrategy).containsInstanceOf(AggressiveBiddingStrategy.class);
    verify(statisticsService, times(2)).calculateOpponentQuantity(roundResults);
    verify(statisticsService).calculateOpponentRemainingCash(roundResults, INITIAL_CASH);
    verify(statisticsService).calculateMyRemainingCash(roundResults, INITIAL_CASH);
    verify(statisticsService).calculateMyQuantity(roundResults);
    verifyNoMoreInteractions(statisticsService);
  }

  @Test
  @DisplayName("Should select DefaultBiddingStrategy")
  void shouldSelectDefaultBiddingStrategy() {
    // given
    int initialQuantity = 100;
    int myWonQuantity = 44;
    int opponentWonQuantity = 10;
    int opponentRemainingCash = 5;
    int myRemainingCash = 10;
    List<RoundResult> roundResults = List.of(RoundResult.builder()
      .withMyWonQuantity(myWonQuantity)
      .withOpponentRemainingCash(opponentRemainingCash)
      .withOpponentWonQuantity(opponentWonQuantity)
      .build());
    when(statisticsService.calculateMyQuantity(roundResults)).thenReturn(myWonQuantity);
    when(statisticsService.calculateOpponentQuantity(roundResults)).thenReturn(opponentWonQuantity);
    when(statisticsService.calculateOpponentRemainingCash(roundResults, INITIAL_CASH)).thenReturn(opponentWonQuantity);
    when(statisticsService.calculateMyRemainingCash(roundResults, INITIAL_CASH)).thenReturn(myRemainingCash);

    // when
    Optional<BiddingStrategy> biddingStrategy = strategySelector.select(roundResults, initialQuantity, INITIAL_CASH);

    // then
    assertThat(biddingStrategy).containsInstanceOf(DefaultBiddingStrategy.class);
    verify(statisticsService, times(2)).calculateOpponentQuantity(roundResults);
    verify(statisticsService).calculateOpponentRemainingCash(roundResults, INITIAL_CASH);
    verify(statisticsService).calculateMyRemainingCash(roundResults, INITIAL_CASH);
    verify(statisticsService).calculateMyQuantity(roundResults);
    verifyNoMoreInteractions(statisticsService);
  }

  private int calculateInitialQuantity(int myWonQuantity, int leftQuantity, int opponentQuantity) {
    return myWonQuantity + leftQuantity + opponentQuantity;
  }

  private List<RoundResult> givenRoundResults(int myWonQuantity) {
    return List.of(RoundResult.builder().withMyWonQuantity(myWonQuantity).build());
  }
}
