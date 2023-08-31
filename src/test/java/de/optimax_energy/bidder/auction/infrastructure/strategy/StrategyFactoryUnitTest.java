package de.optimax_energy.bidder.auction.infrastructure.strategy;

import de.optimax_energy.bidder.UnitTest;
import de.optimax_energy.bidder.auction.api.BiddingStrategy;
import de.optimax_energy.bidder.auction.api.dto.RoundResult;
import de.optimax_energy.bidder.auction.api.dto.StrategyName;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static de.optimax_energy.bidder.auction.api.dto.StrategyName.AGGRESSIVE;
import static de.optimax_energy.bidder.auction.api.dto.StrategyName.DEFAULT;
import static de.optimax_energy.bidder.auction.api.dto.StrategyName.MINIMUM_BID;
import static de.optimax_energy.bidder.auction.api.dto.StrategyName.ZERO_BID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

class StrategyFactoryUnitTest extends UnitTest {

  private static final int INITIAL_CASH = 100;

  @Mock
  private Map<StrategyName, BiddingStrategy> auctionStrategies;

  @Mock
  private StatisticsService statisticsService;

  @InjectMocks
  private StrategyFactory strategyFactory;

  @ParameterizedTest(name = "Should select ZeroBiddingStrategy, myWonQuantity={0}, opponentQuantity={1}, leftQuantity={2}")
  @CsvSource({"7, 3, 2", "1, 7, 2"})
  void shouldSelectZeroBiddingStrategy(int myWonQuantity, int opponentQuantity, int leftQuantity) {
    // given
    int initialQuantity = calculateInitialQuantity(myWonQuantity, leftQuantity, opponentQuantity);
    BiddingStrategy givenStrategy = mock(BiddingStrategy.class);
    when(auctionStrategies.get(ZERO_BID)).thenReturn(givenStrategy);
    List<RoundResult> roundResults = givenRoundResults();
    when(statisticsService.calculateOpponentQuantity(roundResults)).thenReturn(opponentQuantity);

    // when
    Optional<BiddingStrategy> biddingStrategy = strategyFactory.buildStrategy(myWonQuantity, roundResults, initialQuantity);

    // then
    assertThat(biddingStrategy).hasValue(givenStrategy);
    verify(auctionStrategies).get(ZERO_BID);
    verify(statisticsService).calculateOpponentQuantity(roundResults);
    verifyNoMoreInteractions(statisticsService, auctionStrategies);
  }

  @Test
  @DisplayName("Should select MinimumBiddingStrategy during first round")
  void shouldSelectMinimumBiddingStrategyDuringFirstRound() {
    // given
    BiddingStrategy givenStrategy = mock(BiddingStrategy.class);
    when(auctionStrategies.get(MINIMUM_BID)).thenReturn(givenStrategy);
    List<RoundResult> roundResults = Collections.emptyList();

    // when
    Optional<BiddingStrategy> biddingStrategy = strategyFactory.buildStrategy(0, roundResults, 10);

    // then
    assertThat(biddingStrategy).hasValue(givenStrategy);
    verify(auctionStrategies).get(MINIMUM_BID);
    verify(statisticsService).calculateOpponentQuantity(roundResults);
    verifyNoMoreInteractions(statisticsService, auctionStrategies);
  }

  @Test
  @DisplayName("Should select MinimumBiddingStrategy, if yet not enough QU to win and opponent has no money")
  void shouldSelectMinimumBiddingStrategyIfNotEnoughToWin() {
    // given
    BiddingStrategy givenStrategy = mock(BiddingStrategy.class);
    when(auctionStrategies.get(MINIMUM_BID)).thenReturn(givenStrategy);
    List<RoundResult> roundResults = List.of(RoundResult.builder().withOpponentRemainingCash(0).build());

    // when
    Optional<BiddingStrategy> biddingStrategy = strategyFactory.buildStrategy(0, roundResults, 0);

    // then
    assertThat(biddingStrategy).hasValue(givenStrategy);
    verify(auctionStrategies).get(MINIMUM_BID);
    verify(statisticsService).calculateOpponentQuantity(roundResults);
    verifyNoMoreInteractions(statisticsService, auctionStrategies);
  }

  @Test
  @DisplayName("Should not select MinimumBiddingStrategy, if yet not enough QU to win and opponent has money")
  void shouldNotSelectMinimumBiddingStrategyIfNotEnoughToWinAndOpponentHasMoney() {
    // given
    BiddingStrategy givenStrategy = mock(BiddingStrategy.class);
    when(auctionStrategies.get(AGGRESSIVE)).thenReturn(givenStrategy);
    List<RoundResult> roundResults = List.of(RoundResult.builder().withOpponentRemainingCash(1).build());

    // when
    Optional<BiddingStrategy> biddingStrategy = strategyFactory.buildStrategy(0, roundResults, 0);

    // then
    assertThat(biddingStrategy).hasValue(givenStrategy);
    verify(auctionStrategies).get(AGGRESSIVE);
    verify(statisticsService, times(2)).calculateOpponentQuantity(roundResults);
    verify(statisticsService).calculateOpponentRemainingCash(roundResults);
    verify(statisticsService).calculateMyRemainingCash(roundResults);
    verifyNoMoreInteractions(statisticsService, auctionStrategies);
  }

  @ParameterizedTest(name = "Should select aggressiveStrategy, myWonQuantity={0}, opponentQuantity={1}, leftQuantity={2}")
  @CsvSource({"1, 3, 2", "10, 40, 40"})
  void shouldSelectAggressiveStrategy(int myWonQuantity, int opponentQuantity, int leftQuantity) {
    // given
    int initialQuantity = calculateInitialQuantity(myWonQuantity, leftQuantity, opponentQuantity);
    BiddingStrategy givenStrategy = mock(BiddingStrategy.class);
    when(auctionStrategies.get(AGGRESSIVE)).thenReturn(givenStrategy);
    List<RoundResult> roundResults = List.of(
      RoundResult.builder()
        .withMyBid(5)
        .withMyWonQuantity(myWonQuantity)
        .withOpponentBid(0)
        .withOpponentRemainingCash(INITIAL_CASH)
        .withOpponentWonQuantity(opponentQuantity)
        .build());
    when(statisticsService.calculateOpponentRemainingCash(roundResults)).thenReturn(INITIAL_CASH);

    // when
    Optional<BiddingStrategy> biddingStrategy = strategyFactory.buildStrategy(myWonQuantity, roundResults, initialQuantity);

    // then
    assertThat(biddingStrategy).hasValue(givenStrategy);
    verify(auctionStrategies).get(AGGRESSIVE);
    verify(statisticsService, times(2)).calculateOpponentQuantity(roundResults);
    verify(statisticsService).calculateOpponentRemainingCash(roundResults);
    verify(statisticsService).calculateMyRemainingCash(roundResults);
    verifyNoMoreInteractions(statisticsService, auctionStrategies);
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
    BiddingStrategy givenStrategy = mock(BiddingStrategy.class);
    when(auctionStrategies.get(DEFAULT)).thenReturn(givenStrategy);
    List<RoundResult> roundResults = List.of(RoundResult.builder()
      .withMyWonQuantity(myWonQuantity)
      .withOpponentRemainingCash(opponentRemainingCash)
      .withOpponentWonQuantity(opponentWonQuantity)
      .build());
    when(statisticsService.calculateOpponentQuantity(roundResults)).thenReturn(opponentWonQuantity);
    when(statisticsService.calculateOpponentRemainingCash(roundResults)).thenReturn(opponentWonQuantity);
    when(statisticsService.calculateMyRemainingCash(roundResults)).thenReturn(myRemainingCash);

    // when
    Optional<BiddingStrategy> biddingStrategy = strategyFactory.buildStrategy(myWonQuantity, roundResults, initialQuantity);

    // then
    assertThat(biddingStrategy).hasValue(givenStrategy);
    verify(auctionStrategies).get(DEFAULT);
    verify(statisticsService, times(2)).calculateOpponentQuantity(roundResults);
    verify(statisticsService).calculateOpponentRemainingCash(roundResults);
    verify(statisticsService).calculateMyRemainingCash(roundResults);
    verifyNoMoreInteractions(statisticsService, auctionStrategies);
  }

  private int calculateInitialQuantity(int myWonQuantity, int leftQuantity, int opponentQuantity) {
    return myWonQuantity + leftQuantity + opponentQuantity;
  }

  private List<RoundResult> givenRoundResults() {
    return List.of(RoundResult.builder().build());
  }
}
