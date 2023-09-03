package de.optimax_energy.bidder.auction.infrastructure.strategy;

import de.optimax_energy.bidder.UnitTest;
import de.optimax_energy.bidder.auction.api.dto.RoundResult;
import de.optimax_energy.bidder.auction.api.dto.StrategyName;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class MinimumBidStrategyUnitTest extends UnitTest {

  private final MinimumBidStrategy minimumBidStrategy = new MinimumBidStrategy();

  @Test
  @DisplayName("Should return minimum bid")
  void shouldReturnMinimumBid() {
    // given
    List<RoundResult> roundResults = Collections.emptyList();

    // when
    int bid = minimumBidStrategy.placeBid(roundResults, 0, 0);

    // then
    assertThat(bid).isOne();
  }

  @Test
  @DisplayName("Should return strategy name")
  void shouldReturnStrategyName() {
    // when
    StrategyName strategyName = minimumBidStrategy.getStrategyName();

    // then
    assertThat(strategyName).isEqualTo(StrategyName.MINIMUM_BID);
  }
}
