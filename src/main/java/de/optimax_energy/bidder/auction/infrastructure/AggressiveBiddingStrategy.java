package de.optimax_energy.bidder.auction.infrastructure;

import de.optimax_energy.bidder.auction.api.BiddingStrategy;
import de.optimax_energy.bidder.auction.api.dto.RoundResult;

import java.util.List;

public class AggressiveBiddingStrategy implements BiddingStrategy {

  private final int initialQuantity;

  private final int initialCash;

  private final StatisticsService statisticsService;

  public AggressiveBiddingStrategy(int initialQuantity, int initialCash, StatisticsService statisticsService) {
    this.initialQuantity = initialQuantity;
    this.initialCash = initialCash;
    this.statisticsService = statisticsService;
  }

  @Override
  public int placeBid(List<RoundResult> roundResults) {
    return calculateAveragePriceOfBidToWin(roundResults);
  }

  /**
   * According to implementation of
   *
   * @return amount of each bid
   * @see <a href="https://cs.brown.edu/courses/csci1440/lectures/2022/first_price_auctions.pdf">Bayes-Nash Equilibrium in the First-Price Auction</a>
   * bidder maximizes her utility by bidding  z=(n−1)/n*v
   */
  private int calculateAveragePriceOfBidToWin(List<RoundResult> roundResults) {
    int opponentQuantity = statisticsService.calculateOpponentQuantity(roundResults);
    int myCurrentQuantity = statisticsService.calculateMyQuantity(roundResults);
    int leftQuantity = initialQuantity - myCurrentQuantity - opponentQuantity;
    int leftCash = initialCash - statisticsService.calculateMyCash(roundResults);

    int requiredQuantityNotToLoose = initialQuantity / 2;
    int requiredQuantityLeftNotToLoose = requiredQuantityNotToLoose - myCurrentQuantity;

    int looseProbability = (requiredQuantityLeftNotToLoose + 1) * 100 / leftQuantity;

    int x2 = 50;
    int y2 = leftCash / (requiredQuantityLeftNotToLoose + 1);

    return y2 / x2 * looseProbability;
  }
}
