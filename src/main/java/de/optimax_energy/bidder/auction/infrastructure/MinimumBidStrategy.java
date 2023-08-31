package de.optimax_energy.bidder.auction.infrastructure;

import de.optimax_energy.bidder.auction.api.BiddingStrategy;
import de.optimax_energy.bidder.auction.api.dto.RoundResult;

import java.util.List;

public class MinimumBidStrategy implements BiddingStrategy {

  @Override
  public int placeBid(List<RoundResult> roundResults) {
    return 1;
  }

  @Override
  public StrategyName getStrategyName() {
    return StrategyName.MINIMUM_BID;
  }
}
