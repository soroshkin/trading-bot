package de.optimax_energy.bidder;

import de.optimax_energy.bidder.auction.api.AuctionResultStorageOperations;
import de.optimax_energy.bidder.auction.api.Bidder;
import de.optimax_energy.bidder.auction.infrastructure.TradingBot;
import de.optimax_energy.bidder.auction.infrastructure.strategy.StatisticsService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import java.util.Random;

@TestConfiguration
public class BidderTestConfiguration {

  private static final int AMOUNT_OF_PRODUCTS_IN_ONE_ROUND = 2;

  private final Integer initialQuantity;

  private final Integer halfOfBidsToWin;

  private final Integer initialCash;

  public BidderTestConfiguration(@Value("${bidder.quantity}") Integer initialQuantity,
                                 @Value("${bidder.cash}") Integer initialCash) {
    this.initialQuantity = initialQuantity;
    this.initialCash = initialCash;
    this.halfOfBidsToWin = initialQuantity / AMOUNT_OF_PRODUCTS_IN_ONE_ROUND - 1;
  }

  @Bean
  public Bidder dummyBidder(AuctionResultStorageOperations auctionResultStorageOperations, StatisticsService statisticsService) {
    TradingBot testBidder = new TradingBot(null, auctionResultStorageOperations, statisticsService) {
      @Override
      public int placeBid() {
        int bid = initialCash / initialQuantity;
        return Math.min(getRemainingCash(), bid);
      }
    };
    testBidder.init(initialQuantity, initialCash);

    return testBidder;
  }

  @Bean
  public Bidder randomBidder(AuctionResultStorageOperations auctionResultStorageOperations, StatisticsService statisticsService) {
    TradingBot testBidder = new TradingBot(null, auctionResultStorageOperations, statisticsService) {
      @Override
      public int placeBid() {
        int bid = (int) new Random().nextDouble(1.5 * initialCash / halfOfBidsToWin);
        return Math.min(getRemainingCash(), bid);
      }
    };
    testBidder.init(initialQuantity, initialCash);

    return testBidder;
  }

  @Bean
  public Bidder aggressiveBidder(AuctionResultStorageOperations auctionResultStorageOperations, StatisticsService statisticsService) {
    TradingBot testBidder = new TradingBot(null, auctionResultStorageOperations, statisticsService) {
      @Override
      public int placeBid() {
        int bid = initialCash / (initialQuantity / 2 - 20);
        return Math.min(getRemainingCash(), bid);
      }
    };
    testBidder.init(initialQuantity, initialCash);

    return testBidder;
  }
}