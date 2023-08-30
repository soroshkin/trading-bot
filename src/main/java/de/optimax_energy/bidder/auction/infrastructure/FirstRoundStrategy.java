package de.optimax_energy.bidder.auction.infrastructure;

import de.optimax_energy.bidder.auction.api.BiddingStrategy;
import de.optimax_energy.bidder.auction.api.dto.RoundResult;

import java.util.List;

public class FirstRoundStrategy implements BiddingStrategy {

  private final int initialQuantity;

  private final int initialCash;

  public FirstRoundStrategy(Integer initialQuantity, Integer initialCash) {
    this.initialQuantity = initialQuantity;
    this.initialCash = initialCash;
  }

  @Override
  public int placeBid(List<RoundResult> roundResults) {
    return calculateAveragePriceOfBidToWin();
  }

  /**
   * To win the game we need to get half of initial quantity of product plus one.
   *
   * @return amount of each bid
   */
  private Integer calculateAveragePriceOfBidToWin() {
    int productAmountToWin = (initialQuantity / AMOUNT_OF_PRODUCTS_IN_ONE_ROUND) / 2 + 1;
    return initialCash / productAmountToWin;
  }
}
