package de.optimax_energy.bidder.auction.infrastructure;

import de.optimax_energy.bidder.auction.api.AuctionResultStorageOperations;
import de.optimax_energy.bidder.auction.api.Bidder;
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
    int bid = Optional.ofNullable(strategyFactory.buildStrategy(quantity, roundResults, initialQuantity))
      .map(biddingStrategy -> biddingStrategy.placeBid(roundResults))
      .orElseThrow(() -> new StrategyNotFoundException("Could not select strategy"));

    if (bid > remainingCash) {
      logger.warn("Not enough money for a bid: required {}, remains {}, will spend everything", bid, remainingCash);
      bid = remainingCash;
    }

    return bid;
  }

  @Override
  public void bids(int myBid, int opponentBid) {
    logger.info("My bid is {}, competitor's bid is {}", myBid, opponentBid);
    auctionResultStorageOperations.addRoundResult(uuid, calculateRoundResult(myBid, opponentBid));
  }

  private RoundResult calculateRoundResult(int myBid, int opponentBid) {
    if (myBid > opponentBid) {
      quantity += AMOUNT_OF_PRODUCTS_IN_ONE_ROUND;
      remainingCash -= myBid;
      return new RoundResult(myBid, myBid, AMOUNT_OF_PRODUCTS_IN_ONE_ROUND, opponentBid, 0);
    }

    if (myBid == opponentBid) {
      quantity += AMOUNT_OF_PRODUCTS_IN_ONE_ROUND / 2;
      remainingCash -= myBid / 2;
      return new RoundResult(myBid, myBid / 2, AMOUNT_OF_PRODUCTS_IN_ONE_ROUND / 2, opponentBid, AMOUNT_OF_PRODUCTS_IN_ONE_ROUND / 2);
    } else {
      remainingCash -= myBid;
      return new RoundResult(myBid, myBid, 0, opponentBid, AMOUNT_OF_PRODUCTS_IN_ONE_ROUND);
    }
  }

  public Integer getInitialQuantity() {
    return initialQuantity;
  }

  public Integer getInitialCash() {
    return initialCash;
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
