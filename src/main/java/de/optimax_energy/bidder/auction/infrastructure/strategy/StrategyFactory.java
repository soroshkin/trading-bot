package de.optimax_energy.bidder.auction.infrastructure.strategy;

import de.optimax_energy.bidder.auction.api.BiddingStrategy;
import de.optimax_energy.bidder.auction.api.dto.RoundResult;
import de.optimax_energy.bidder.auction.api.dto.StrategyName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static de.optimax_energy.bidder.auction.api.BiddingStrategy.AMOUNT_OF_PRODUCTS_IN_ONE_ROUND;

public class StrategyFactory {

  private static final int AGGRESSIVE_STRATEGY_THRESHOLD = 20;

  private static final int MINIMUM_INITIAL_QUANTITY_TO_APPLY_MINIMUM_BID_STRATEGY = 10;

  private final Logger logger = LoggerFactory.getLogger(getClass());

  private final Map<StrategyName, BiddingStrategy> auctionStrategies;

  private final StatisticsService statisticsService;

  public StrategyFactory(Map<StrategyName, BiddingStrategy> auctionStrategies, StatisticsService statisticsService) {
    this.auctionStrategies = auctionStrategies;
    this.statisticsService = statisticsService;
  }

  public Optional<BiddingStrategy> buildStrategy(int myWonQuantity, List<RoundResult> roundResults, int initialQuantity, int initialCash) {
    int opponentQuantity = statisticsService.calculateOpponentQuantity(roundResults);
    int remainingQuantity = initialQuantity - myWonQuantity - opponentQuantity;

    if (initialQuantity == AMOUNT_OF_PRODUCTS_IN_ONE_ROUND || isLastRound(remainingQuantity)) {
      logger.info("Selected maximumBidStrategy");
      return Optional.ofNullable(auctionStrategies.get(StrategyName.MAXIMUM_BID));
    }

    if (roundResults.isEmpty() && initialQuantity > MINIMUM_INITIAL_QUANTITY_TO_APPLY_MINIMUM_BID_STRATEGY) {
      logger.info("First round bid strategy has been chosen");
      return Optional.ofNullable(auctionStrategies.get(StrategyName.MINIMUM_BID));
    }

    if (isEnoughQuantityToWin(initialQuantity, myWonQuantity)
      || isAlreadyLost(initialQuantity, myWonQuantity + remainingQuantity)) {
      logger.info("Zero bid strategy has been chosen");
      return Optional.ofNullable(auctionStrategies.get(StrategyName.ZERO_BID));
    }

    if (!isEnoughQuantityToWin(initialQuantity, myWonQuantity) && opponentHasNoMoney(roundResults)) {
      logger.info("Opponent has no money, I can spend minimal amount of money");
      return Optional.ofNullable(auctionStrategies.get(StrategyName.MINIMUM_BID));
    }

    if (shouldBidMoreAggressively(initialQuantity, initialCash, myWonQuantity, roundResults)) {
      logger.info("Looks like I am loosing, choosing more aggressive bid strategy");
      return Optional.ofNullable(auctionStrategies.get(StrategyName.AGGRESSIVE));
    }

    BiddingStrategy strategy = auctionStrategies.get(StrategyName.DEFAULT);
    logger.info("Default strategy has been chosen");

    return Optional.ofNullable(strategy);
  }

  private boolean isLastRound(int remainingQuantity) {
    return remainingQuantity == AMOUNT_OF_PRODUCTS_IN_ONE_ROUND;
  }

  private boolean opponentHasNoMoney(List<RoundResult> roundResults) {
    return !roundResults.isEmpty() && roundResults.get(roundResults.size() - 1).getOpponentRemainingCash() == 0;
  }

  private boolean isEnoughQuantityToWin(int initialQuantity, int myWonQuantity) {
    return myWonQuantity >= initialQuantity / 2 + 1;
  }

  private boolean isAlreadyLost(int initialQuantity, int myPotentialQuantity) {
    return myPotentialQuantity < initialQuantity / 2;
  }

  private boolean shouldBidMoreAggressively(int initialQuantity, int initialCash, int myCurrentQuantity, List<RoundResult> roundResults) {
    int opponentQuantity = statisticsService.calculateOpponentQuantity(roundResults);
    int remainingQuantity = initialQuantity - myCurrentQuantity - opponentQuantity;
    int requiredQuantityNotToLoose = initialQuantity / 2;
    int requiredQuantityLeftToWin = requiredQuantityNotToLoose + 1 - myCurrentQuantity;
    int opponentRemainingCash = statisticsService.calculateOpponentRemainingCash(roundResults, initialCash);
    int myRemainingCash = statisticsService.calculateMyRemainingCash(roundResults, initialCash);

    return opponentQuantity > myCurrentQuantity && opponentRemainingCash > myRemainingCash
      || requiredQuantityLeftToWin * 100.0 / remainingQuantity >= AGGRESSIVE_STRATEGY_THRESHOLD;
  }
}
