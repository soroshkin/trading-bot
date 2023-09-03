package de.optimax_energy.bidder.auction.infrastructure.strategy;

import de.optimax_energy.bidder.auction.api.BiddingStrategy;
import de.optimax_energy.bidder.auction.api.dto.RoundResult;
import de.optimax_energy.bidder.auction.api.dto.StrategyName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static de.optimax_energy.bidder.auction.api.BiddingStrategy.AMOUNT_OF_PRODUCTS_IN_ONE_ROUND;
import static de.optimax_energy.bidder.auction.api.BiddingStrategy.MINIMUM_INITIAL_QUANTITY_TO_APPLY_MINIMUM_BID_STRATEGY;

public class StrategySelector {

  private static final int AGGRESSIVE_STRATEGY_THRESHOLD = 20;

  private static final Map<StrategyName, BiddingStrategy> STRATEGY_MAP = new EnumMap<>(StrategyName.class);

  private final Logger logger = LoggerFactory.getLogger(getClass());

  private final StatisticsService statisticsService;

  public StrategySelector(StatisticsService statisticsService) {
    this.statisticsService = statisticsService;
    initStrategyMap(statisticsService);
  }

  public Optional<BiddingStrategy> select(List<RoundResult> roundResults, int initialQuantity, int initialCash) {
    int opponentQuantity = statisticsService.calculateOpponentQuantity(roundResults);
    int myWonQuantity = statisticsService.calculateMyQuantity(roundResults);
    int remainingQuantity = initialQuantity - myWonQuantity - opponentQuantity;

    if (initialQuantity == AMOUNT_OF_PRODUCTS_IN_ONE_ROUND || isLastRound(remainingQuantity)) {
      logger.info("Selected maximumBidStrategy");
      return Optional.ofNullable(STRATEGY_MAP.get(StrategyName.MAXIMUM_BID));
    }

    if (roundResults.isEmpty() && (initialQuantity > MINIMUM_INITIAL_QUANTITY_TO_APPLY_MINIMUM_BID_STRATEGY)) {
      logger.info("It's first round, minimum bid strategy has been chosen");
      return Optional.ofNullable(STRATEGY_MAP.get(StrategyName.MINIMUM_BID));
    }

    if (isEnoughQuantityToWin(initialQuantity, myWonQuantity)
      || isAlreadyLost(initialQuantity, myWonQuantity + remainingQuantity)) {
      logger.info("Zero bid strategy has been chosen");
      return Optional.ofNullable(STRATEGY_MAP.get(StrategyName.ZERO_BID));
    }

    if (!isEnoughQuantityToWin(initialQuantity, myWonQuantity) && opponentHasNoMoney(roundResults)) {
      logger.info("Opponent has no money, I can spend minimum amount of money");
      return Optional.ofNullable(STRATEGY_MAP.get(StrategyName.MINIMUM_BID));
    }

    if (shouldBidMoreAggressively(initialQuantity, initialCash, myWonQuantity, roundResults)) {
      logger.info("Looks like I am loosing, choosing more aggressive bid strategy");
      return Optional.ofNullable(STRATEGY_MAP.get(StrategyName.AGGRESSIVE));
    }

    BiddingStrategy strategy = STRATEGY_MAP.get(StrategyName.DEFAULT);
    logger.info("Default strategy has been chosen");

    return Optional.ofNullable(strategy);
  }

  private void initStrategyMap(StatisticsService statisticsService) {
    STRATEGY_MAP.put(StrategyName.ZERO_BID, new ZeroBiddingStrategy());
    STRATEGY_MAP.put(StrategyName.MINIMUM_BID, new MinimumBidStrategy());
    STRATEGY_MAP.put(StrategyName.MAXIMUM_BID, new MaximumBidStrategy());
    STRATEGY_MAP.put(StrategyName.AGGRESSIVE, new AggressiveBiddingStrategy(statisticsService));
    STRATEGY_MAP.put(StrategyName.DEFAULT, new DefaultBiddingStrategy(statisticsService));
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

  /**
   * This method calculates if more aggressive strategy should be chosen.
   * it returns true in one of these cases:
   * 1. if bot has less money and QU than opponent. It means opponent bids higher and wins.
   * 2. if the ratio of amount of QU left to win and remaining QU higher than given threshold
   *
   * @param initialQuantity amount of initial QU
   * @param initialCash     amount of initial MU
   * @param myWonQuantity   amount of QU won by this bot
   * @param roundResults    list of results of all rounds
   * @return whether more aggressive strategy should be chosen or not
   */
  private boolean shouldBidMoreAggressively(int initialQuantity, int initialCash, int myWonQuantity, List<RoundResult> roundResults) {
    int opponentQuantity = statisticsService.calculateOpponentQuantity(roundResults);
    int remainingQuantity = initialQuantity - myWonQuantity - opponentQuantity;
    int requiredQuantityNotToLoose = initialQuantity / 2;
    int requiredQuantityLeftToWin = requiredQuantityNotToLoose + 1 - myWonQuantity;
    int opponentRemainingCash = statisticsService.calculateOpponentRemainingCash(roundResults, initialCash);
    int myRemainingCash = statisticsService.calculateMyRemainingCash(roundResults, initialCash);

    return doesOpponentHaveMoreQuAndCash(opponentQuantity, myWonQuantity, opponentRemainingCash, myRemainingCash)
      || (requiredQuantityLeftToWin * 100.0 / remainingQuantity >= AGGRESSIVE_STRATEGY_THRESHOLD);
  }

  private boolean doesOpponentHaveMoreQuAndCash(int opponentQuantity, int myCurrentQuantity, int opponentRemainingCash, int myRemainingCash) {
    return opponentQuantity > myCurrentQuantity && opponentRemainingCash > myRemainingCash;
  }
}
