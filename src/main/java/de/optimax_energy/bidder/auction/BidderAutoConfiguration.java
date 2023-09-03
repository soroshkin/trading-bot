package de.optimax_energy.bidder.auction;

import de.optimax_energy.bidder.auction.api.AuctionResultStorageOperations;
import de.optimax_energy.bidder.auction.api.Bidder;
import de.optimax_energy.bidder.auction.infrastructure.storage.AuctionResultInMemoryStorageService;
import de.optimax_energy.bidder.auction.infrastructure.strategy.StatisticsService;
import de.optimax_energy.bidder.auction.infrastructure.strategy.StrategySelector;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
public class BidderAutoConfiguration {


  @Bean
  public Bidder tradingBot(StrategySelector strategySelector, AuctionResultStorageOperations auctionResultStorageOperations, StatisticsService statisticsService) {
    return new TradingBot(strategySelector, auctionResultStorageOperations, statisticsService);
  }

  @Bean
  public StrategySelector strategySelector(StatisticsService statisticsService) {
    return new StrategySelector( statisticsService);
  }

  @Bean
  public AuctionResultStorageOperations auctionResultStorageOperations() {
    return new AuctionResultInMemoryStorageService();
  }

  @Bean
  public StatisticsService statisticsService() {
    return new StatisticsService();
  }
}
