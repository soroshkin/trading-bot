package de.optimax_energy.bidder.auction.infrastructure.strategy;

import de.optimax_energy.bidder.UnitTest;
import de.optimax_energy.bidder.auction.api.dto.RoundResult;
import de.optimax_energy.bidder.auction.infrastructure.strategy.AggressiveBiddingStrategy;
import de.optimax_energy.bidder.auction.infrastructure.strategy.StatisticsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AggressiveBiddingStrategyUnitTest extends UnitTest {

  private static final int INITIAL_CASH = 1000;

  private static final int INITIAL_QUANTITY = 10;

  @Mock
  private StatisticsService statisticsService;

  private AggressiveBiddingStrategy aggressiveBiddingStrategy;

  @BeforeEach
  void setUp() {
    aggressiveBiddingStrategy = new AggressiveBiddingStrategy(INITIAL_QUANTITY, statisticsService);
  }

  @Test
  @DisplayName("Should return bid")
  void shouldReturnBid() {
    // given
    List<RoundResult> roundResults = givenRoundResults();
    when(statisticsService.calculateOpponentRemainingCash(roundResults)).thenReturn(INITIAL_CASH);

    // when
    int bid = aggressiveBiddingStrategy.placeBid(roundResults);

    // then
    assertThat(bid).isEqualTo(199);
    verify(statisticsService).calculateOpponentRemainingCash(roundResults);
  }

  @Test
  @DisplayName("Should return bid, which is bigger by 1, than opponents' remaining cash amount")
  void shouldReturnBidBiggerThanOpponentRemainingCash() {
    // given
    List<RoundResult> roundResults = givenRoundResults();
    int givenOpponentRemainingCash = 2;
    when(statisticsService.calculateOpponentRemainingCash(roundResults)).thenReturn(givenOpponentRemainingCash);

    // when
    int bid = aggressiveBiddingStrategy.placeBid(roundResults);

    // then
    assertThat(bid).isEqualTo(givenOpponentRemainingCash + 1);
    verify(statisticsService).calculateOpponentRemainingCash(roundResults);
  }

  private List<RoundResult> givenRoundResults() {
    return List.of(RoundResult.builder().build());
  }
}
