package de.optimax_energy.bidder.auction.infrastructure.strategy;

import de.optimax_energy.bidder.auction.api.BiddingStrategy;
import de.optimax_energy.bidder.auction.api.dto.RoundResult;
import de.optimax_energy.bidder.auction.api.dto.StrategyName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class StrategyFactory {

  private static final int AGGRESSIVE_STRATEGY_THRESHOLD = 20;

  private final Logger logger = LoggerFactory.getLogger(getClass());

  private final Map<StrategyName, BiddingStrategy> auctionStrategies;

  private final StatisticsService statisticsService;

  public StrategyFactory(Map<StrategyName, BiddingStrategy> auctionStrategies, StatisticsService statisticsService) {
    this.auctionStrategies = auctionStrategies;
    this.statisticsService = statisticsService;
  }

  public Optional<BiddingStrategy> buildStrategy(int myCurrentQuantity, List<RoundResult> roundResults, int initialQuantity) {
    int opponentQuantity = statisticsService.calculateOpponentQuantity(roundResults);
    int remainingQuantity = initialQuantity - myCurrentQuantity - opponentQuantity;

    if (isEnoughQuantityToWin(initialQuantity, myCurrentQuantity)
      || isAlreadyLost(initialQuantity, myCurrentQuantity + remainingQuantity)) {
      logger.info("Zero bid strategy has been chosen");
      return Optional.ofNullable(auctionStrategies.get(StrategyName.ZERO_BID));
    }

    if (roundResults.isEmpty()) {
      logger.info("First round bid strategy has been chosen");
      return Optional.ofNullable(auctionStrategies.get(StrategyName.MINIMUM_BID));
    }

    if (!isEnoughQuantityToWin(initialQuantity, myCurrentQuantity) && opponentHasNoMoney(roundResults)) {
      logger.info("Opponent has no money, I can spend minimal amount of money");
      return Optional.ofNullable(auctionStrategies.get(StrategyName.MINIMUM_BID));
    }

    if (shouldBidMoreAggressively(initialQuantity, myCurrentQuantity, roundResults)) {
      logger.info("Looks like I am loosing, choosing more aggressive bid strategy");
      return Optional.ofNullable(auctionStrategies.get(StrategyName.AGGRESSIVE));
    }

    BiddingStrategy strategy = auctionStrategies.get(StrategyName.DEFAULT);
    logger.info("Default strategy has been chosen");

    return Optional.ofNullable(strategy);
  }

  private boolean opponentHasNoMoney(List<RoundResult> roundResults) {
    return roundResults.get(roundResults.size() - 1).getOpponentRemainingCash() == 0;
  }

  private boolean isEnoughQuantityToWin(int initialQuantity, int myCurrentQuantity) {
    return myCurrentQuantity >= initialQuantity / 2 + 1;
  }

  private boolean isAlreadyLost(int initialQuantity, int myPotentialQuantity) {
    return myPotentialQuantity < initialQuantity / 2;
  }

  private boolean shouldBidMoreAggressively(int initialQuantity, int myCurrentQuantity, List<RoundResult> roundResults) {
    int opponentQuantity = statisticsService.calculateOpponentQuantity(roundResults);
    int remainingQuantity = initialQuantity - myCurrentQuantity - opponentQuantity;
    int requiredQuantityNotToLoose = initialQuantity / 2;
    int requiredQuantityLeftToWin = requiredQuantityNotToLoose + 1 - myCurrentQuantity;
    int opponentRemainingCash = statisticsService.calculateOpponentRemainingCash(roundResults);
    int myRemainingCash = statisticsService.calculateMyRemainingCash(roundResults);

    return opponentQuantity > myCurrentQuantity && opponentRemainingCash > myRemainingCash
      || requiredQuantityLeftToWin * 100.0 / remainingQuantity >= AGGRESSIVE_STRATEGY_THRESHOLD;
  }
}
