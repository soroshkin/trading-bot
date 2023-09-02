package de.optimax_energy.bidder.auction.infrastructure.strategy;

import de.optimax_energy.bidder.UnitTest;
import de.optimax_energy.bidder.auction.api.dto.RoundResult;
import de.optimax_energy.bidder.auction.api.dto.StrategyName;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class MaximumBidStrategyUnitTest extends UnitTest {

  private final MaximumBidStrategy maximumBidStrategy = new MaximumBidStrategy();

  @Test
  @DisplayName("Should return maximum bid")
  void shouldReturnMinimalBid() {
    // given
    List<RoundResult> roundResults = Collections.emptyList();
    int initialCash = 999;

    // when
    int bid = maximumBidStrategy.placeBid(roundResults, 2, initialCash);

    // then
    assertThat(bid).isEqualTo(initialCash);
  }

  @Test
  @DisplayName("Should return strategy name")
  void shouldReturnStrategyName() {
    // when
    StrategyName strategyName = maximumBidStrategy.getStrategyName();

    // then
    assertThat(strategyName).isEqualTo(StrategyName.MAXIMUM_BID);
  }
}
