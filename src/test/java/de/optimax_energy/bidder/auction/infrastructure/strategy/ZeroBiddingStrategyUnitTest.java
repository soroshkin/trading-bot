package de.optimax_energy.bidder.auction.infrastructure.strategy;

import de.optimax_energy.bidder.UnitTest;
import de.optimax_energy.bidder.auction.api.dto.RoundResult;
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
}
