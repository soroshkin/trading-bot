package de.optimax_energy.bidder.auction;

import de.optimax_energy.bidder.auction.api.AuctionResultStorageOperations;
import de.optimax_energy.bidder.auction.api.Bidder;
import de.optimax_energy.bidder.auction.api.BiddingStrategy;
import de.optimax_energy.bidder.auction.infrastructure.AggressiveBiddingStrategy;
import de.optimax_energy.bidder.auction.infrastructure.DefaultBiddingStrategy;
import de.optimax_energy.bidder.auction.infrastructure.MinimumBidStrategy;
import de.optimax_energy.bidder.auction.infrastructure.StatisticsService;
import de.optimax_energy.bidder.auction.infrastructure.StrategyFactory;
import de.optimax_energy.bidder.auction.infrastructure.StrategyName;
import de.optimax_energy.bidder.auction.infrastructure.TradingBot;
import de.optimax_energy.bidder.auction.infrastructure.ZeroBiddingStrategy;
import de.optimax_energy.bidder.auction.infrastructure.storage.AuctionResultInMemoryStorage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

import java.util.Map;

@AutoConfiguration
public class BidderAutoConfiguration {

  private final Integer initialQuantity;

  private final Integer initialCash;

  public BidderAutoConfiguration(@Value("${bidder.quantity}") Integer initialQuantity,
                                 @Value("${bidder.cash}") Integer initialCash) {
    this.initialQuantity = initialQuantity;
    this.initialCash = initialCash;
  }

  @Bean
  public Map<StrategyName, BiddingStrategy> auctionStrategies(BiddingStrategy zeroBiddingStrategy,
                                                              BiddingStrategy aggressiveBiddingStrategy,
                                                              BiddingStrategy minimumBidStrategy,
                                                              BiddingStrategy defaultBiddingStrategy) {
    return Map.of(zeroBiddingStrategy.getStrategyName(), zeroBiddingStrategy,
      aggressiveBiddingStrategy.getStrategyName(), aggressiveBiddingStrategy,
      minimumBidStrategy.getStrategyName(), minimumBidStrategy,
      defaultBiddingStrategy.getStrategyName(), defaultBiddingStrategy);
  }

  @Bean
  public Bidder tradingBot(StrategyFactory strategyFactory, AuctionResultStorageOperations auctionResultStorageOperations) {
    TradingBot tradingBot = new TradingBot(strategyFactory, auctionResultStorageOperations);
    tradingBot.init(initialQuantity, initialCash);
    return tradingBot;
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
    return new AggressiveBiddingStrategy(initialQuantity, statisticsService);
  }

  @Bean
  public BiddingStrategy minimumBidStrategy() {
    return new MinimumBidStrategy();
  }

  @Bean
  public BiddingStrategy defaultBiddingStrategy(StatisticsService statisticsService) {
    return new DefaultBiddingStrategy(initialQuantity, initialCash, statisticsService);
  }

  @Bean
  public AuctionResultStorageOperations auctionResultStorageOperations() {
    return new AuctionResultInMemoryStorage();
  }

  @Bean
  public StatisticsService statisticsService() {
    return new StatisticsService(initialCash);
  }
}
