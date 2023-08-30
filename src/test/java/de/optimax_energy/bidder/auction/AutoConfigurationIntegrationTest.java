package de.optimax_energy.bidder.auction;

import de.optimax_energy.bidder.IntegrationTest;
import de.optimax_energy.bidder.auction.api.Bidder;
import de.optimax_energy.bidder.auction.infrastructure.StrategyFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

class AutoConfigurationIntegrationTest extends IntegrationTest {

  @Autowired(required = false)
  private Bidder bidder;

  @Autowired(required = false)
  private StrategyFactory strategyFactory;

  @Test
  @DisplayName("should autowire beans")
  void shouldAutowireBeans() {
    assertThat(bidder).isNotNull();
    assertThat(strategyFactory).isNotNull();
  }
}
