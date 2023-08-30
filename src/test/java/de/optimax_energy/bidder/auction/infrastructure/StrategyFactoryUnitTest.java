package de.optimax_energy.bidder.auction.infrastructure;

import de.optimax_energy.bidder.UnitTest;
import de.optimax_energy.bidder.auction.api.AuctionResultStorageOperations;
import de.optimax_energy.bidder.auction.api.BiddingStrategy;
import de.optimax_energy.bidder.auction.api.dto.RoundResult;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;
import java.util.Map;

import static de.optimax_energy.bidder.auction.infrastructure.StrategyName.AGGRESSIVE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class StrategyFactoryUnitTest extends UnitTest {

  private static final int INITIAL_CASH = 100;

  @Mock
  private Map<StrategyName, BiddingStrategy> auctionStrategies;

  @Mock
  private AuctionResultStorageOperations auctionResultStorageOperations;

  @InjectMocks
  private StrategyFactory strategyFactory;

  @ParameterizedTest(name = "Should select aggressiveStrategy, myWonQuantity={0}, opponentQuantity={1}, leftQuantity={2}")
  @CsvSource({"1, 3, 2", "10, 40, 40"})
  void shouldSelectAggressiveStrategy(int myWonQuantity, int opponentQuantity, int leftQuantity) {
    // given
    int initialQuantity = calculateInitialQuantity(myWonQuantity, leftQuantity, opponentQuantity);
    BiddingStrategy givenStrategy = mock(BiddingStrategy.class);
    when(auctionStrategies.get(AGGRESSIVE)).thenReturn(givenStrategy);
    List<RoundResult> roundResults = List.of(new RoundResult(0, 0, myWonQuantity, 0, opponentQuantity));

    // when
    BiddingStrategy biddingStrategy = strategyFactory.buildStrategy(myWonQuantity, roundResults, initialQuantity);

    // then
    assertThat(biddingStrategy).isEqualTo(givenStrategy);
    verify(auctionStrategies).get(AGGRESSIVE);
    verifyNoInteractions(auctionResultStorageOperations);
  }

  private int calculateInitialQuantity(int myWonQuantity, int leftQuantity, int opponentQuantity) {
    return myWonQuantity + leftQuantity + opponentQuantity;
  }
}
