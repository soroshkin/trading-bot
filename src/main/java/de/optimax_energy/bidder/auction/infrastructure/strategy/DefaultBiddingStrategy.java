package de.optimax_energy.bidder.auction.infrastructure.strategy;

import de.optimax_energy.bidder.auction.api.BiddingStrategy;
import de.optimax_energy.bidder.auction.api.dto.RoundResult;
import de.optimax_energy.bidder.auction.api.dto.StrategyName;

import java.util.List;

class DefaultBiddingStrategy implements BiddingStrategy {

  private final StatisticsService statisticsService;

  DefaultBiddingStrategy(StatisticsService statisticsService) {
    this.statisticsService = statisticsService;
  }

  @Override
  public int placeBid(List<RoundResult> roundResults, int initialQuantity, int initialCash) {
    return calculateAveragePriceOfBidToWin(roundResults, initialQuantity, initialCash);
  }

  @Override
  public StrategyName getStrategyName() {
    return StrategyName.DEFAULT;
  }

  /**
   * According to article
   * <a href="https://cs.brown.edu/courses/csci1440/lectures/2022/first_price_auctions.pdf">Bayes-Nash Equilibrium in the First-Price Auction</a>
   * bidder maximizes his utility by bidding z=(n−1)/n*v, where n - number of bidders, v - estimation of product.
   * We don't know the value of product, the assumption is that v = initialCash / numberOfBidsToWin and then
   * average bid to win will be equal to v / 2
   *
   * @return bid amount
   */
  private int calculateAveragePriceOfBidToWin(List<RoundResult> roundResults, int initialQuantity, int initialCash) {
    int numberOfBidsToWin = (initialQuantity / AMOUNT_OF_PRODUCTS_IN_ONE_ROUND / 2) + 1;
    int bid = initialCash / numberOfBidsToWin / 2;

    int opponentRemainingCash = statisticsService.calculateOpponentRemainingCash(roundResults, initialCash);
    if (opponentRemainingCash < bid) {
      return opponentRemainingCash + 1;
    }

    return bid;
  }
}
