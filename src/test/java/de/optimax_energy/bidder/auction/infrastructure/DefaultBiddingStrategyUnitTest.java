package de.optimax_energy.bidder.auction.infrastructure;

import de.optimax_energy.bidder.UnitTest;
import de.optimax_energy.bidder.auction.api.dto.RoundResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DefaultBiddingStrategyUnitTest extends UnitTest {

  private static final int INITIAL_CASH = 1000;

  private static final int INITIAL_QUANTITY = 10;

  @Mock
  private StatisticsService statisticsService;

  private DefaultBiddingStrategy defaultBiddingStrategy;

  @BeforeEach
  void setUp() {
    defaultBiddingStrategy = new DefaultBiddingStrategy(INITIAL_QUANTITY, INITIAL_CASH, statisticsService);
  }

  @Test
  @DisplayName("Should return bid")
  void shouldReturnBid() {
    // given
    List<RoundResult> roundResults = givenRoundResults();
    when(statisticsService.calculateOpponentRemainingCash(roundResults)).thenReturn(INITIAL_CASH);

    // when
    int bid = defaultBiddingStrategy.placeBid(roundResults);

    // then
    assertThat(bid).isEqualTo(166);
    verify(statisticsService).calculateOpponentRemainingCash(roundResults);
  }

  @Test
  @DisplayName("Should return bid, which is bigger by 1, than opponents' remaining cash amount")
  void shouldReturnBidBiggerThanOpponentRemainingCash() {
    // given
    List<RoundResult> roundResults = givenRoundResults();
    int givenOpponentRemainingCash = 1;
    when(statisticsService.calculateOpponentRemainingCash(roundResults)).thenReturn(givenOpponentRemainingCash);

    // when
    int bid = defaultBiddingStrategy.placeBid(roundResults);

    // then
    assertThat(bid).isEqualTo(givenOpponentRemainingCash + 1);
    verify(statisticsService).calculateOpponentRemainingCash(roundResults);
  }

  private List<RoundResult> givenRoundResults() {
    return List.of(RoundResult.builder().build());
  }
}
