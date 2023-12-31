package de.optimax_energy.bidder.auction.infrastructure.strategy;

import de.optimax_energy.bidder.UnitTest;
import de.optimax_energy.bidder.auction.api.dto.RoundResult;
import de.optimax_energy.bidder.auction.api.dto.StrategyName;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ZeroBiddingStrategyUnitTest extends UnitTest {

  private final ZeroBiddingStrategy zeroBiddingStrategy = new ZeroBiddingStrategy();

  @Test
  @DisplayName("Should return 0 bid")
  void shouldReturnZeroBid() {
    // given
    List<RoundResult> roundResults = Collections.emptyList();

    // when
    int bid = zeroBiddingStrategy.placeBid(roundResults, 0, 0);

    // then
    assertThat(bid).isZero();
  }

  @Test
  @DisplayName("Should return strategy name")
  void shouldReturnStrategyName() {
    // when
    StrategyName strategyName = zeroBiddingStrategy.getStrategyName();

    // then
    assertThat(strategyName).isEqualTo(StrategyName.ZERO_BID);
  }
}
