package de.optimax_energy.bidder.auction.infrastructure;

import de.optimax_energy.bidder.auction.api.BiddingStrategy;
import de.optimax_energy.bidder.auction.api.dto.RoundResult;

import java.util.List;

public class DefaultBiddingStrategy implements BiddingStrategy {

  private final int initialQuantity;

  private final int initialCash;

  private final StatisticsService statisticsService;

  public DefaultBiddingStrategy(Integer initialQuantity, Integer initialCash, StatisticsService statisticsService) {
    this.initialQuantity = initialQuantity;
    this.initialCash = initialCash;
    this.statisticsService = statisticsService;
  }

  @Override
  public int placeBid(List<RoundResult> roundResults) {
    return calculateAveragePriceOfBidToWin(roundResults);
  }

  @Override
  public StrategyName getStrategyName() {
    return StrategyName.DEFAULT;
  }

  /**
   * According to article
   *
   * @return amount of each bid
   * @see <a href="https://cs.brown.edu/courses/csci1440/lectures/2022/first_price_auctions.pdf">Bayes-Nash Equilibrium in the First-Price Auction</a>
   * bidder maximizes his utility by bidding  z=(n−1)/n*v
   */
  private int calculateAveragePriceOfBidToWin(List<RoundResult> roundResults) {
    int numberOfBidsToWin = (initialQuantity / AMOUNT_OF_PRODUCTS_IN_ONE_ROUND / 2) + 1;
    int bid = initialCash / numberOfBidsToWin / 2;

    int opponentRemainingCash = statisticsService.calculateOpponentRemainingCash(roundResults);
    if (opponentRemainingCash < bid) {
      return opponentRemainingCash + 1;
    }

    return bid;
  }
}
