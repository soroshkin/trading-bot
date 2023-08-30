package de.optimax_energy.bidder.auction;

import de.optimax_energy.bidder.BidderTestConfiguration;
import de.optimax_energy.bidder.IntegrationTest;
import de.optimax_energy.bidder.auction.api.Bidder;
import de.optimax_energy.bidder.auction.infrastructure.TradingBot;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;

import static org.assertj.core.api.Assertions.assertThat;

@ContextConfiguration(classes = {BidderTestConfiguration.class, BidderAutoConfiguration.class})
class AuctionIntegrationTest extends IntegrationTest {

  private static final int AMOUNT_OF_PRODUCTS_IN_ONE_ROUND = 2;

  @Autowired
  @Qualifier("tradingBot")
  private TradingBot tradingBot;

  @Autowired
  @Qualifier("dummyBidder")
  private TradingBot dummyBidder;

  @Autowired
  @Qualifier("randomBidder")
  private TradingBot randomBidder;

  @Autowired
  @Qualifier("aggressiveBidder")
  private TradingBot aggressiveBidder;

  @BeforeEach
  void setUp() {
    tradingBot.init(initialQuantity, initialCash);
    dummyBidder.init(initialQuantity, initialCash);
    randomBidder.init(initialQuantity, initialCash);
  }

  @Test
  @DisplayName("should win dummy bidder")
  void shouldWinDummyBidder() {
    // given
    int quantityLeft = initialQuantity;

    // when
    Bidder bidder = startAuction(quantityLeft, tradingBot, dummyBidder);

    // then
    assertThat(bidder).isInstanceOf(TradingBot.class);
  }

  @Test
  @DisplayName("should win random bidder")
  void shouldWinRandomBidder() {
    // given
    int quantityLeft = initialQuantity;

    // when
    Bidder bidder = startAuction(quantityLeft, tradingBot, randomBidder);

    // then
    assertThat(bidder).isInstanceOf(TradingBot.class);
  }

  @Test
  @DisplayName("should win aggressive bidder")
  void shouldWinAggressiveBidder() {
    // given
    int quantityLeft = initialQuantity;

    // when
    Bidder bidder = startAuction(quantityLeft, tradingBot, aggressiveBidder);

    // then
    assertThat(bidder).isInstanceOf(TradingBot.class);
  }

  private Bidder startAuction(int quantityToPlay, TradingBot tradingBot, TradingBot dummyBidder) {
    while (quantityToPlay >= AMOUNT_OF_PRODUCTS_IN_ONE_ROUND
      && (tradingBot.getRemainingCash() > 0 || dummyBidder.getRemainingCash() > 0)) {
      int tradingBotBid = tradingBot.placeBid();
      int dummyBidderBid = dummyBidder.placeBid();

      tradingBot.bids(tradingBotBid, dummyBidderBid);
      dummyBidder.bids(dummyBidderBid, tradingBotBid);
      quantityToPlay -= AMOUNT_OF_PRODUCTS_IN_ONE_ROUND;
    }

    printBidders(tradingBot, dummyBidder);
    return chooseWinner(tradingBot, dummyBidder);
  }

  private Bidder chooseWinner(TradingBot firstBidder, TradingBot secondBidder) {
    if (firstBidder.getQuantity() > secondBidder.getQuantity()) {
      return firstBidder;
    }
    if (firstBidder.getQuantity() < secondBidder.getQuantity()) {
      return secondBidder;
    } else {
      return chooseWinnerBasedOnLeftAmountOfCash(firstBidder, secondBidder);
    }
  }

  private Bidder chooseWinnerBasedOnLeftAmountOfCash(TradingBot firstBidder, TradingBot secondBidder) {
    if (firstBidder.getRemainingCash() > secondBidder.getRemainingCash()) {
      return firstBidder;
    }
    if (firstBidder.getRemainingCash() < secondBidder.getRemainingCash()) {
      return secondBidder;
    } else {
      Assertions.fail("Both bidders have same amount of product and money");
      return null;
    }
  }

  private void printBidders(Bidder firstBidder, Bidder secondBidder) {
    System.out.println(firstBidder);
    System.out.println(secondBidder);
  }
}
