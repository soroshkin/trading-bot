package de.optimax_energy.bidder.auction;

import de.optimax_energy.bidder.auction.api.AuctionResultStorageOperations;
import de.optimax_energy.bidder.auction.api.Bidder;
import de.optimax_energy.bidder.auction.api.BiddingStrategy;
import de.optimax_energy.bidder.auction.api.dto.StrategyName;
import de.optimax_energy.bidder.auction.infrastructure.TradingBot;
import de.optimax_energy.bidder.auction.infrastructure.storage.AuctionResultInMemoryStorageService;
import de.optimax_energy.bidder.auction.infrastructure.strategy.AggressiveBiddingStrategy;
import de.optimax_energy.bidder.auction.infrastructure.strategy.DefaultBiddingStrategy;
import de.optimax_energy.bidder.auction.infrastructure.strategy.MaximumBidStrategy;
import de.optimax_energy.bidder.auction.infrastructure.strategy.MinimumBidStrategy;
import de.optimax_energy.bidder.auction.infrastructure.strategy.StatisticsService;
import de.optimax_energy.bidder.auction.infrastructure.strategy.StrategyFactory;
import de.optimax_energy.bidder.auction.infrastructure.strategy.ZeroBiddingStrategy;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

import java.util.Map;

@AutoConfiguration
public class BidderAutoConfiguration {

  @Bean
  public Map<StrategyName, BiddingStrategy> auctionStrategies(BiddingStrategy zeroBiddingStrategy,
                                                              BiddingStrategy aggressiveBiddingStrategy,
                                                              BiddingStrategy minimumBidStrategy,
                                                              BiddingStrategy maximumBidStrategy,
                                                              BiddingStrategy defaultBiddingStrategy) {
    return Map.of(zeroBiddingStrategy.getStrategyName(), zeroBiddingStrategy,
      aggressiveBiddingStrategy.getStrategyName(), aggressiveBiddingStrategy,
      minimumBidStrategy.getStrategyName(), minimumBidStrategy,
      maximumBidStrategy.getStrategyName(), maximumBidStrategy,
      defaultBiddingStrategy.getStrategyName(), defaultBiddingStrategy);
  }

  @Bean
  public Bidder tradingBot(StrategyFactory strategyFactory, AuctionResultStorageOperations auctionResultStorageOperations, StatisticsService statisticsService) {
    return new TradingBot(strategyFactory, auctionResultStorageOperations, statisticsService);
  }

  @Bean
  public StrategyFactory strategyFactory(Map<StrategyName, BiddingStrategy> auctionStrategies, StatisticsService statisticsService) {
    return new StrategyFactory(auctionStrategies, statisticsService);
  }

  @Bean
  public BiddingStrategy zeroBiddingStrategy() {
    return new ZeroBiddingStrategy();
  }

  @Bean
  public BiddingStrategy aggressiveBiddingStrategy(StatisticsService statisticsService) {
    return new AggressiveBiddingStrategy(statisticsService);
  }

  @Bean
  public BiddingStrategy minimumBidStrategy() {
    return new MinimumBidStrategy();
  }

  @Bean
  public BiddingStrategy maximumBidStrategy() {
    return new MaximumBidStrategy();
  }

  @Bean
  public BiddingStrategy defaultBiddingStrategy(StatisticsService statisticsService) {
    return new DefaultBiddingStrategy(statisticsService);
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
