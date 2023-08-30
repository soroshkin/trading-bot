package de.optimax_energy.bidder.auction.infrastructure;

import de.optimax_energy.bidder.auction.api.BiddingStrategy;
import de.optimax_energy.bidder.auction.api.dto.RoundResult;

import java.util.List;

public class DefaultBiddingStrategy implements BiddingStrategy {

  private final int initialQuantity;

  private final int initialCash;

  public DefaultBiddingStrategy(Integer initialQuantity, Integer initialCash) {
    this.initialQuantity = initialQuantity;
    this.initialCash = initialCash;
  }

  @Override
  public int placeBid(List<RoundResult> roundResults) {
    return calculateAveragePriceOfBidToWin();
  }

  /**
   * According to implementation of
   * @see <a href="https://cs.brown.edu/courses/csci1440/lectures/2022/first_price_auctions.pdf">Bayes-Nash Equilibrium in the First-Price Auction</a>
   * bidder maximizes her utility by bidding  z=(n−1)/n*v
   *
   * @return amount of each bid
   */
  private Integer calculateAveragePriceOfBidToWin() {
    int productAmountToWin = (initialQuantity / AMOUNT_OF_PRODUCTS_IN_ONE_ROUND) / 2 + 1;
    return initialCash / productAmountToWin / 2;
  }
}
