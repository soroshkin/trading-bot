package de.optimax_energy.bidder.auction;

import de.optimax_energy.bidder.auction.api.RoundResultStorageOperations;
import de.optimax_energy.bidder.auction.api.Bidder;
import de.optimax_energy.bidder.auction.infrastructure.strategy.StatisticsService;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import java.util.Random;

@TestConfiguration
public class BidderTestConfiguration {

  private static final int AMOUNT_OF_PRODUCTS_IN_ONE_ROUND = 2;

  @Bean
  public Bidder dummyBidder(RoundResultStorageOperations roundResultStorageOperations, StatisticsService statisticsService) {
    return new TradingBot(null, roundResultStorageOperations, statisticsService) {
      @Override
      public int placeBid() {
        int bid = getInitialCash() / getInitialQuantity();
        return Math.min(getRemainingCash(), bid);
      }
    };
  }

  @Bean
  public Bidder randomBidder(RoundResultStorageOperations roundResultStorageOperations, StatisticsService statisticsService) {
    return new TradingBot(null, roundResultStorageOperations, statisticsService) {

      @Override
      public int placeBid() {
        int halfOfBids = getInitialQuantity() / AMOUNT_OF_PRODUCTS_IN_ONE_ROUND;
        double botMaxBidMultiplier = 1.5;
        int bid = new Random().nextInt((int) (botMaxBidMultiplier * getInitialCash() / halfOfBids));
        return Math.min(getRemainingCash(), bid);
      }
    };
  }

  @Bean
  public Bidder aggressiveBidder(RoundResultStorageOperations roundResultStorageOperations, StatisticsService statisticsService) {
    return new TradingBot(null, roundResultStorageOperations, statisticsService) {
      @Override
      public int placeBid() {
        int bid = getInitialCash() / (getInitialQuantity() / AMOUNT_OF_PRODUCTS_IN_ONE_ROUND - 1);
        return Math.min(getRemainingCash(), bid);
      }
    };
  }
}