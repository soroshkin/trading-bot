package de.optimax_energy.bidder;

import de.optimax_energy.bidder.auction.api.AuctionResultStorageOperations;
import de.optimax_energy.bidder.auction.api.Bidder;
import de.optimax_energy.bidder.auction.infrastructure.TradingBot;
import de.optimax_energy.bidder.auction.infrastructure.strategy.StatisticsService;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import java.util.Random;

@TestConfiguration
public class BidderTestConfiguration {

  private static final int AMOUNT_OF_PRODUCTS_IN_ONE_ROUND = 2;

  @Bean
  public Bidder dummyBidder(AuctionResultStorageOperations auctionResultStorageOperations, StatisticsService statisticsService) {
    return new TradingBot(null, auctionResultStorageOperations, statisticsService) {
      @Override
      public int placeBid() {
        int bid = getInitialCash() / getInitialQuantity();
        return Math.min(getRemainingCash(), bid);
      }
    };
  }

  @Bean
  public Bidder randomBidder(AuctionResultStorageOperations auctionResultStorageOperations, StatisticsService statisticsService) {
    return new TradingBot(null, auctionResultStorageOperations, statisticsService) {

      @Override
      public int placeBid() {
        int halfOfBids = getInitialQuantity() / AMOUNT_OF_PRODUCTS_IN_ONE_ROUND;
        int bid = (int) new Random().nextDouble(1.5 * getInitialCash() / halfOfBids);
        return Math.min(getRemainingCash(), bid);
      }
    };
  }

  @Bean
  public Bidder aggressiveBidder(AuctionResultStorageOperations auctionResultStorageOperations, StatisticsService statisticsService) {
    return new TradingBot(null, auctionResultStorageOperations, statisticsService) {
      @Override
      public int placeBid() {
        int bid = getInitialCash() / (getInitialQuantity() / 2 - 1);
        return Math.min(getRemainingCash(), bid);
      }
    };
  }
}