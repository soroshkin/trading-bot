package de.optimax_energy.bidder.auction.infrastructure.strategy;

import de.optimax_energy.bidder.UnitTest;
import de.optimax_energy.bidder.auction.api.dto.RoundResult;
import de.optimax_energy.bidder.auction.infrastructure.strategy.MinimumBidStrategy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class MinimumBidStrategyUnitTest extends UnitTest {

  private final MinimumBidStrategy minimumBidStrategy = new MinimumBidStrategy();

  @Test
  @DisplayName("Should return minimal bid")
  void shouldReturnMinimalBid() {
    // given
    List<RoundResult> roundResults = Collections.emptyList();

    // when
    int bid = minimumBidStrategy.placeBid(roundResults);

    // then
    assertThat(bid).isOne();
  }
}
