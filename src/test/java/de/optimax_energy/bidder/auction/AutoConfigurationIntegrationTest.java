package de.optimax_energy.bidder.auction;

import de.optimax_energy.bidder.IntegrationTest;
import de.optimax_energy.bidder.auction.api.AuctionResultStorageOperations;
import de.optimax_energy.bidder.auction.api.Bidder;
import de.optimax_energy.bidder.auction.infrastructure.strategy.StatisticsService;
import de.optimax_energy.bidder.auction.infrastructure.strategy.StrategySelector;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

class AutoConfigurationIntegrationTest extends IntegrationTest {

  @Autowired(required = false)
  private Bidder tradingBot;

  @Autowired(required = false)
  private StrategySelector strategySelector;

  @Autowired(required = false)
  private AuctionResultStorageOperations auctionResultStorageOperations;

  @Autowired(required = false)
  private StatisticsService statisticsService;

  @Test
  @DisplayName("should autowire beans")
  void shouldAutowireBeans() {
    assertThat(tradingBot).isNotNull();
    assertThat(strategySelector).isNotNull();
    assertThat(auctionResultStorageOperations).isNotNull();
    assertThat(statisticsService).isNotNull();
  }
}
