package de.optimax_energy.bidder.auction.infrastructure;

import de.optimax_energy.bidder.auction.api.AuctionResultStorageOperations;
import de.optimax_energy.bidder.auction.api.Bidder;
import de.optimax_energy.bidder.auction.api.BiddingStrategy;
import de.optimax_energy.bidder.auction.api.dto.RoundResult;
import de.optimax_energy.bidder.auction.api.dto.StrategyNotFoundException;
import de.optimax_energy.bidder.auction.infrastructure.strategy.StatisticsService;
import de.optimax_energy.bidder.auction.infrastructure.strategy.StrategyFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.UUID;

import static de.optimax_energy.bidder.auction.api.BiddingStrategy.AMOUNT_OF_PRODUCTS_IN_ONE_ROUND;

public class TradingBot implements Bidder {

  private final Logger logger = LoggerFactory.getLogger(getClass());

  private int initialQuantity;

  private int initialCash;

  private int quantity;

  private int remainingCash;

  private final StrategyFactory strategyFactory;

  private final AuctionResultStorageOperations auctionResultStorageOperations;

  private final StatisticsService statisticsService;

  private final String uuid;

  public TradingBot(StrategyFactory strategyFactory, AuctionResultStorageOperations auctionResultStorageOperations, StatisticsService statisticsService) {
    this.strategyFactory = strategyFactory;
    this.auctionResultStorageOperations = auctionResultStorageOperations;
    this.statisticsService = statisticsService;
    this.uuid = UUID.randomUUID().toString();
  }

  @Override
  public void init(int quantity, int cash) {
    this.initialQuantity = quantity;
    this.initialCash = cash;
    this.quantity = 0;
    this.remainingCash = cash;
  }

  @Override
  public int placeBid() {
    List<RoundResult> roundResults = auctionResultStorageOperations.getRoundResultsForBidder(uuid);
    BiddingStrategy selectedStrategy = strategyFactory.buildStrategy(quantity, roundResults, initialQuantity, initialCash)
      .orElseThrow(() -> new StrategyNotFoundException("Could not select strategy"));
    int bid = selectedStrategy.placeBid(roundResults, initialQuantity, initialCash);

    return Math.min(bid, remainingCash);
  }

  @Override
  public void bids(int myBid, int opponentBid) {
    logger.info("My bid is {}, competitor's bid is {}", myBid, opponentBid);
    auctionResultStorageOperations.addRoundResultForBidder(uuid, calculateRoundResult(myBid, opponentBid));
  }

  private RoundResult calculateRoundResult(int myBid, int opponentBid) {
    int opponentRemainingCashInLastRound = statisticsService.calculateOpponentRemainingCash(
      auctionResultStorageOperations.getRoundResultsForBidder(uuid), initialCash);
    int opponentRemainingCash = opponentRemainingCashInLastRound - opponentBid;
    remainingCash -= myBid;

    if (myBid > opponentBid) {
      quantity += AMOUNT_OF_PRODUCTS_IN_ONE_ROUND;

      return RoundResult.builder()
        .withMyBid(myBid)
        .withMyWonQuantity(AMOUNT_OF_PRODUCTS_IN_ONE_ROUND)
        .withOpponentBid(opponentBid)
        .withOpponentRemainingCash(opponentRemainingCash)
        .withOpponentWonQuantity(0)
        .build();
    }

    if (myBid < opponentBid) {
      return RoundResult.builder()
        .withMyBid(myBid)
        .withMyWonQuantity(0)
        .withOpponentBid(opponentBid)
        .withOpponentRemainingCash(opponentRemainingCash)
        .withOpponentWonQuantity(AMOUNT_OF_PRODUCTS_IN_ONE_ROUND)
        .build();
    } else {
      quantity += AMOUNT_OF_PRODUCTS_IN_ONE_ROUND / 2;

      return RoundResult.builder()
        .withMyBid(myBid)
        .withMyWonQuantity(AMOUNT_OF_PRODUCTS_IN_ONE_ROUND / 2)
        .withOpponentBid(opponentBid)
        .withOpponentRemainingCash(opponentRemainingCash)
        .withOpponentWonQuantity(AMOUNT_OF_PRODUCTS_IN_ONE_ROUND / 2)
        .build();
    }
  }

  public int getInitialQuantity() {
    return initialQuantity;
  }

  public int getInitialCash() {
    return initialCash;
  }

  public int getQuantity() {
    return quantity;
  }

  public int getRemainingCash() {
    return remainingCash;
  }

  public String getUuid() {
    return uuid;
  }

  @Override
  public String toString() {
    return "TradingBot{" +
      "initialQuantity=" + initialQuantity +
      ", initialCash=" + initialCash +
      ", quantity=" + quantity +
      ", remainingCash=" + remainingCash +
      '}';
  }
}