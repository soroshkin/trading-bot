package de.optimax_energy.bidder.auction.infrastructure.strategy;

import de.optimax_energy.bidder.auction.api.BiddingStrategy;
import de.optimax_energy.bidder.auction.api.dto.RoundResult;
import de.optimax_energy.bidder.auction.api.dto.StrategyName;

import java.util.List;

class AggressiveBiddingStrategy implements BiddingStrategy {

  private final StatisticsService statisticsService;

  AggressiveBiddingStrategy(StatisticsService statisticsService) {
    this.statisticsService = statisticsService;
  }

  @Override
  public int placeBid(List<RoundResult> roundResults, int initialQuantity, int initialCash) {
    return calculateAveragePriceOfBidToWin(roundResults, initialQuantity, initialCash);
  }

  @Override
  public StrategyName getStrategyName() {
    return StrategyName.AGGRESSIVE;
  }

  private int calculateAveragePriceOfBidToWin(List<RoundResult> roundResults, int initialQuantity, int initialCash) {
    int opponentQuantity = statisticsService.calculateOpponentQuantity(roundResults);
    int myCurrentQuantity = statisticsService.calculateMyQuantity(roundResults);
    int remainingQuantity = initialQuantity - myCurrentQuantity - opponentQuantity;
    int myRemainingCash = statisticsService.calculateMyRemainingCash(roundResults, initialCash);
    int requiredQuantityNotToLoose = initialQuantity / 2;
    int opponentRemainingCash = statisticsService.calculateOpponentRemainingCash(roundResults, initialCash);

    if (doesOpponentNeedOneQUToWin(requiredQuantityNotToLoose, opponentQuantity)) {
      return Math.min(opponentRemainingCash + 1, myRemainingCash);
    }

    // the assumption is if the bid ~1.34 times higher than average, then bot's bid should be higher in ~84% of cases
    // due to normal distribution of the opponent's bids
    double oneSigmaMultiplier = 1.34;
    int maxBidMultiplier = 3;
    int maxBid = maxBidMultiplier * (myRemainingCash / (requiredQuantityNotToLoose / AMOUNT_OF_PRODUCTS_IN_ONE_ROUND));
    int opponentAverageBid = statisticsService.calculateOpponentAverageBid(roundResults);
    int bid = Math.min((int) Math.round(opponentAverageBid * oneSigmaMultiplier), maxBid);

    if (isThereChanceToWin(requiredQuantityNotToLoose, myCurrentQuantity, remainingQuantity)
      && opponentRemainingCash < bid) {
      return opponentRemainingCash + 1;
    }

    if (opponentAverageBid < MINIMUM_INITIAL_QUANTITY_TO_APPLY_MINIMUM_BID_STRATEGY) {
      bid = opponentAverageBid + 2;
    }

    return bid;
  }

  private boolean isThereChanceToWin(int requiredQuantityNotToLoose, int myCurrentQuantity, int remainingQuantity) {
    return requiredQuantityNotToLoose <= myCurrentQuantity + remainingQuantity;
  }

  private boolean doesOpponentNeedOneQUToWin(int requiredQuantityNotToLoose, int opponentQuantity) {
    return (requiredQuantityNotToLoose - opponentQuantity) == 0;
  }
}
