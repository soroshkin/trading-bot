package de.optimax_energy.bidder.auction.infrastructure.strategy;

import de.optimax_energy.bidder.UnitTest;
import de.optimax_energy.bidder.auction.api.BiddingStrategy;
import de.optimax_energy.bidder.auction.api.dto.RoundResult;
import de.optimax_energy.bidder.auction.api.dto.StrategyName;
import de.optimax_energy.bidder.auction.infrastructure.strategy.StatisticsService;
import de.optimax_energy.bidder.auction.infrastructure.strategy.StrategyFactory;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static de.optimax_energy.bidder.auction.api.dto.StrategyName.AGGRESSIVE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
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
//    when(statisticsService.calculateMySpentCash(roundResults)).thenReturn(1);

    // when
    Optional<BiddingStrategy> biddingStrategy = strategyFactory.buildStrategy(myWonQuantity, roundResults, initialQuantity);

    // then
    assertThat(biddingStrategy).isNotEmpty().isEqualTo(givenStrategy);
    verify(auctionStrategies).get(AGGRESSIVE);
    verify(statisticsService).calculateOpponentRemainingCash(roundResults);
    verifyNoMoreInteractions(statisticsService);
  }

  private int calculateInitialQuantity(int myWonQuantity, int leftQuantity, int opponentQuantity) {
    return myWonQuantity + leftQuantity + opponentQuantity;
  }
}
