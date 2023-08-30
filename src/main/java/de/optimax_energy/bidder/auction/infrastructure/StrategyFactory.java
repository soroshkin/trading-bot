package de.optimax_energy.bidder.auction.infrastructure;

import de.optimax_energy.bidder.auction.api.BiddingStrategy;
import de.optimax_energy.bidder.auction.api.dto.RoundResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class StrategyFactory {

  private static final int AGGRESSIVE_STRATEGY_THRESHOLD = 20;

  private final Logger logger = LoggerFactory.getLogger(getClass());

  private final Map<StrategyName, BiddingStrategy> auctionStrategies;

  public StrategyFactory(Map<StrategyName, BiddingStrategy> auctionStrategies) {
    this.auctionStrategies = auctionStrategies;
  }

  public BiddingStrategy buildStrategy(int myCurrentQuantity, List<RoundResult> roundResults, int initialQuantity) {
    if (isEnoughQuantityToWin(initialQuantity, myCurrentQuantity)) {
      logger.info("Zero bid strategy has been chosen");
      return auctionStrategies.get(StrategyName.ZERO_BID);
    }

    if (roundResults.isEmpty()) {
      logger.info("First round bid strategy has been chosen");
      return auctionStrategies.get(StrategyName.FIRST_ROUND);
    }

    if (shouldBidMoreAggressively(initialQuantity, myCurrentQuantity, roundResults)) {
      logger.info("Looks like I am loosing, choosing more aggressive bid strategy");
      return auctionStrategies.get(StrategyName.AGGRESSIVE);
    }

    BiddingStrategy strategy = auctionStrategies.get(StrategyName.DEFAULT);
    logger.info("{} strategy has been chosen", strategy);

    return strategy;
  }

  private boolean isEnoughQuantityToWin(int initialQuantity, int myCurrentQuantity) {
    return myCurrentQuantity >= initialQuantity / 2 + 1;
  }

  private boolean isEnoughQuantityNotToLoose(int initialQuantity, int myPotentialQuantity) {
    return myPotentialQuantity >= initialQuantity / 2;
  }

  private boolean shouldBidMoreAggressively(int initialQuantity, int myCurrentQuantity, List<RoundResult> roundResults) {
    int opponentQuantity = calculateOpponentQuantity(roundResults);
    int leftQuantity = initialQuantity - myCurrentQuantity - opponentQuantity;
    if (!isEnoughQuantityNotToLoose(initialQuantity, myCurrentQuantity + leftQuantity)) {
      return false;
    }
    int requiredQuantityNotToLoose = initialQuantity / 2;
    int requiredQuantityLeftToWinNotToLoose = requiredQuantityNotToLoose - myCurrentQuantity;

    return (leftQuantity - requiredQuantityLeftToWinNotToLoose) * 100.0 / leftQuantity <= AGGRESSIVE_STRATEGY_THRESHOLD;
  }

  private int calculateOpponentQuantity(List<RoundResult> roundResults) {
    return roundResults.stream()
      .map(RoundResult::getOpponentWonQuantity)
      .reduce(0, Integer::sum);
  }
}
