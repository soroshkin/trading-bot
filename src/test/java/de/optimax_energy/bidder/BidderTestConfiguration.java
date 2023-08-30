package de.optimax_energy.bidder;

import de.optimax_energy.bidder.auction.api.AuctionResultStorageOperations;
import de.optimax_energy.bidder.auction.api.Bidder;
import de.optimax_energy.bidder.auction.infrastructure.TradingBot;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import java.util.Random;

@TestConfiguration
public class BidderTestConfiguration {

  private final Integer initialQuantity;

  private final Integer initialCash;

  public BidderTestConfiguration(@Value("${bidder.quantity}") Integer initialQuantity,
                                 @Value("${bidder.cash}") Integer initialCash) {
    this.initialQuantity = initialQuantity;
    this.initialCash = initialCash;
  }

  @Bean
  public Bidder dummyBidder(AuctionResultStorageOperations auctionResultStorageOperations) {
    TradingBot tradingBot = new TradingBot(null, auctionResultStorageOperations) {
      @Override
      public int placeBid() {
        int bid = initialCash / initialQuantity;
        return this.getRemainingCash() < bid ? this.getRemainingCash() : bid;
      }
    };
    tradingBot.init(initialQuantity, initialCash);
    return tradingBot;
  }

  @Bean
  public Bidder randomBidder(AuctionResultStorageOperations auctionResultStorageOperations) {
    TradingBot tradingBot = new TradingBot(null, auctionResultStorageOperations) {
      @Override
      public int placeBid() {
        int bid = new Random().nextInt(initialCash / initialQuantity * 2);
        return this.getRemainingCash() < bid ? this.getRemainingCash() : bid;
      }
    };
    tradingBot.init(initialQuantity, initialCash);
    return tradingBot;
  }

  @Bean
  public Bidder aggressiveBidder(AuctionResultStorageOperations auctionResultStorageOperations) {
    TradingBot tradingBot = new TradingBot(null, auctionResultStorageOperations) {
      @Override
      public int placeBid() {
        int bid = initialCash / (initialQuantity / 2 - 1);
        return this.getRemainingCash() < bid ? this.getRemainingCash() : bid;
      }
    };
    tradingBot.init(initialQuantity, initialCash);
    return tradingBot;
  }
}
