package de.optimax_energy.bidder.auction.infrastructure.strategy;

import de.optimax_energy.bidder.auction.api.BiddingStrategy;
import de.optimax_energy.bidder.auction.api.dto.RoundResult;
import de.optimax_energy.bidder.auction.api.dto.StrategyName;

import java.util.List;

class MinimumBidStrategy implements BiddingStrategy {

  @Override
  public int placeBid(List<RoundResult> roundResults, int initialQuantity, int initialCash) {
    return 1;
  }

  @Override
  public StrategyName getStrategyName() {
    return StrategyName.MINIMUM_BID;
  }
}
