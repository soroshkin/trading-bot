package de.optimax_energy.bidder.auction;

import de.optimax_energy.bidder.auction.api.RoundResultStorageOperations;
import de.optimax_energy.bidder.auction.api.Bidder;
import de.optimax_energy.bidder.auction.infrastructure.storage.RoundResultInMemoryStorageService;
import de.optimax_energy.bidder.auction.infrastructure.strategy.StatisticsService;
import de.optimax_energy.bidder.auction.infrastructure.strategy.StrategySelector;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
public class BidderAutoConfiguration {


  @Bean
  public Bidder tradingBot(StrategySelector strategySelector, RoundResultStorageOperations roundResultStorageOperations, StatisticsService statisticsService) {
    return new TradingBot(strategySelector, roundResultStorageOperations, statisticsService);
  }

  @Bean
  public StrategySelector strategySelector(StatisticsService statisticsService) {
    return new StrategySelector( statisticsService);
  }

  @Bean
  public RoundResultStorageOperations auctionResultStorageOperations() {
    return new RoundResultInMemoryStorageService();
  }

  @Bean
  public StatisticsService statisticsService() {
    return new StatisticsService();
  }
}
