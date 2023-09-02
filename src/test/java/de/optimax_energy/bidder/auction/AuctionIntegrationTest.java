package de.optimax_energy.bidder.auction;

import de.optimax_energy.bidder.BidderTestConfiguration;
import de.optimax_energy.bidder.IntegrationTest;
import de.optimax_energy.bidder.auction.api.AuctionResultStorageOperations;
import de.optimax_energy.bidder.auction.api.Bidder;
import de.optimax_energy.bidder.auction.infrastructure.TradingBot;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;

import static org.assertj.core.api.Assertions.assertThat;

@ContextConfiguration(classes = {BidderTestConfiguration.class, BidderAutoConfiguration.class})
class AuctionIntegrationTest extends IntegrationTest {

  private final Logger logger = LoggerFactory.getLogger(getClass());

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

  @Autowired
  private AuctionResultStorageOperations auctionResultStorageOperations;

  private int initialQuantity;

  void setUp(int initialQuantity, int initialCash) {
    this.initialQuantity = initialQuantity;
    tradingBot.init(initialQuantity, initialCash);
    dummyBidder.init(initialQuantity, initialCash);
    randomBidder.init(initialQuantity, initialCash);
    auctionResultStorageOperations.getRoundResultsForBidder(tradingBot.getUuid()).clear();
  }

  @ParameterizedTest(name = "Should win dummy bidder. initialQuantity={0}, initialCash={1}")
  @CsvSource({"50, 100", "100, 10000", "100, 1000", "10, 1000"})
  void shouldWinDummyBidder(int initialQuantity, int initialCash) {
    // given
    setUp(initialQuantity, initialCash);

    // when
    Bidder bidder = startAuction(tradingBot, dummyBidder);

    // then
    assertThat(bidder).isEqualTo(tradingBot);
  }

  @ParameterizedTest(name = "Should win aggressive random bidder bot more than in half of auctions. initialQuantity={0}, initialCash={1}")
  @CsvSource({"50, 100", "100, 10000", "100, 1000", "10, 1000"})
  void shouldWinRandomBidder(int initialQuantity, int initialCash) {
    // given
    // the number of auctions should be large enough to exclude randomness in results
    int numberOfTests = 100;

    // when
    int numberOfTradingBotWins = runAuctionMultipleTimesAndReturnNumberOfWinsOfFirstBidder(tradingBot, randomBidder, numberOfTests, initialQuantity, initialCash);

    // then
    assertThat(numberOfTradingBotWins * 100 / numberOfTests).isGreaterThan(50);
  }

  @Test
  @DisplayName("Should win aggressive bidder")
  void shouldWinAggressiveBidder() {
    // given
    setUp(100, 10000);

    // when
    Bidder bidder = startAuction(tradingBot, aggressiveBidder);

    // then
    assertThat(bidder).isEqualTo(tradingBot);
  }

  private Bidder startAuction(TradingBot tradingBot, TradingBot dummyBot) {
    int iterationIndex = 1;
    int quantityToPlay = initialQuantity;
    while (quantityToPlay >= AMOUNT_OF_PRODUCTS_IN_ONE_ROUND) {
      int tradingBotBid = tradingBot.placeBid();
      int dummyBidderBid = dummyBot.placeBid();

      tradingBot.bids(tradingBotBid, dummyBidderBid);
      dummyBot.bids(dummyBidderBid, tradingBotBid);
      quantityToPlay -= AMOUNT_OF_PRODUCTS_IN_ONE_ROUND;

      logger.info("Round {}. Trading bot remaining cash: {}, dummy bot remaining cash: {}", iterationIndex, tradingBot.getRemainingCash(), dummyBot.getRemainingCash());
      logger.info("Round {}. Trading bot quantity: {}, dummy bot quantity: {}", iterationIndex, tradingBot.getQuantity(), dummyBot.getQuantity());
      iterationIndex++;
    }

    return chooseWinner(tradingBot, dummyBot);
  }

  private int runAuctionMultipleTimesAndReturnNumberOfWinsOfFirstBidder(TradingBot tradingBot, TradingBot randomBidder, int numberOfTests, int initialQuantity, int initialCash) {
    int numberOfTradingBotWins = 0;
    for (int i = 0; i < numberOfTests; i++) {
      setUp(initialQuantity, initialCash);
      Bidder bidder = startAuction(tradingBot, randomBidder);
      if (bidder != null && bidder.equals(tradingBot)) {
        numberOfTradingBotWins++;
      }
      logger.info("number of wins {}", numberOfTradingBotWins);
    }

    return numberOfTradingBotWins;
  }

  private Bidder chooseWinner(TradingBot firstBidder, TradingBot secondBidder) {
    if (firstBidder.getQuantity() > secondBidder.getQuantity()) {
      return firstBidder;
    }

    if (firstBidder.getQuantity() < secondBidder.getQuantity()) {
      return secondBidder;
    }

    return chooseWinnerBasedOnLeftAmountOfCash(firstBidder, secondBidder);
  }

  private Bidder chooseWinnerBasedOnLeftAmountOfCash(TradingBot firstBidder, TradingBot secondBidder) {
    if (firstBidder.getRemainingCash() > secondBidder.getRemainingCash()) {
      return firstBidder;
    }

    if (firstBidder.getRemainingCash() < secondBidder.getRemainingCash()) {
      return secondBidder;
    }

    return null;
  }
}