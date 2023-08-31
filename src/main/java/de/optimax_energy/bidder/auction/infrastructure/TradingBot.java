package de.optimax_energy.bidder.auction.infrastructure;

import de.optimax_energy.bidder.auction.api.AuctionResultStorageOperations;
import de.optimax_energy.bidder.auction.api.Bidder;
import de.optimax_energy.bidder.auction.api.BiddingStrategy;
import de.optimax_energy.bidder.auction.api.dto.RoundResult;
import de.optimax_energy.bidder.auction.api.dto.StrategyNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class TradingBot implements Bidder {

  private static final int AMOUNT_OF_PRODUCTS_IN_ONE_ROUND = 2;

  private final Logger logger = LoggerFactory.getLogger(getClass());

  private Integer initialQuantity;

  private Integer initialCash;

  private Integer quantity;

  private Integer remainingCash;

  private final StrategyFactory strategyFactory;

  private final AuctionResultStorageOperations auctionResultStorageOperations;

  private final String uuid;

  private RoundResult lastRoundResult;

  public TradingBot(StrategyFactory strategyFactory, AuctionResultStorageOperations auctionResultStorageOperations) {
    this.strategyFactory = strategyFactory;
    this.auctionResultStorageOperations = auctionResultStorageOperations;
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
    BiddingStrategy selectedStrategy = strategyFactory.buildStrategy(quantity, roundResults, initialQuantity)
      .orElseThrow(() -> new StrategyNotFoundException("Could not select strategy"));
    int bid = selectedStrategy.placeBid(roundResults);

    if (bid > remainingCash) {
      logger.warn("Not enough money for a bid: required {}, remains {}, will spend everything", bid, remainingCash);
      bid = remainingCash;
    }

    lastRoundResult = RoundResult.builder()
      .withStrategyName(selectedStrategy.getStrategyName())
      .withMyBid(bid)
      .build();

    return bid;
  }

  @Override
  public void bids(int myBid, int opponentBid) {
    logger.info("My bid is {}, competitor's bid is {}", myBid, opponentBid);
    auctionResultStorageOperations.addRoundResult(uuid, calculateRoundResult(myBid, opponentBid));
  }

  private RoundResult calculateRoundResult(int myBid, int opponentBid) {
    int opponentRemainingCashInLastRound = getOpponentRemainingCash(auctionResultStorageOperations.getRoundResultsForBidder(uuid));
    StrategyName strategyName = getStrategyNameFromLastRoundResult(myBid);

    if (myBid > opponentBid) {
      quantity += AMOUNT_OF_PRODUCTS_IN_ONE_ROUND;
      remainingCash -= myBid;
      int opponentRemainingCash = opponentRemainingCashInLastRound - opponentBid;
      return RoundResult.builder()
        .withMyBid(myBid)
        .withMyWonQuantity(AMOUNT_OF_PRODUCTS_IN_ONE_ROUND)
        .withOpponentBid(opponentBid)
        .withOpponentRemainingCash(opponentRemainingCash)
        .withOpponentWonQuantity(0)
        .withStrategyName(strategyName)
        .build();
    }

    if (myBid == opponentBid) {
      quantity += AMOUNT_OF_PRODUCTS_IN_ONE_ROUND / 2;
      remainingCash -= myBid;
      int opponentRemainingCash = opponentRemainingCashInLastRound - opponentBid;
      return RoundResult.builder()
        .withMyBid(myBid)
        .withMyWonQuantity(AMOUNT_OF_PRODUCTS_IN_ONE_ROUND / 2)
        .withOpponentBid(opponentBid)
        .withOpponentRemainingCash(opponentRemainingCash)
        .withOpponentWonQuantity(AMOUNT_OF_PRODUCTS_IN_ONE_ROUND / 2)
        .withStrategyName(strategyName)
        .build();
    } else {
      remainingCash -= myBid;
      int opponentRemainingCash = opponentRemainingCashInLastRound - opponentBid;
      return RoundResult.builder()
        .withMyBid(myBid)
        .withMyWonQuantity(0)
        .withOpponentBid(opponentBid)
        .withOpponentRemainingCash(opponentRemainingCash)
        .withOpponentWonQuantity(AMOUNT_OF_PRODUCTS_IN_ONE_ROUND)
        .withStrategyName(strategyName)
        .build();
    }
  }

  private StrategyName getStrategyNameFromLastRoundResult(int myBid) {
    return (lastRoundResult != null
      && myBid == lastRoundResult.getMyBid()
      && lastRoundResult.getStrategyName() != null)
      ? lastRoundResult.getStrategyName()
      : StrategyName.UNKNOWN;
  }

  private int getOpponentRemainingCash(List<RoundResult> roundResults) {
    return roundResults.isEmpty() ? initialCash
      : Optional.ofNullable(roundResults.get(roundResults.size() - 1))
      .map(RoundResult::getOpponentRemainingCash)
      .orElseGet(() -> initialCash);
  }

  public Integer getQuantity() {
    return quantity;
  }

  public Integer getRemainingCash() {
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
