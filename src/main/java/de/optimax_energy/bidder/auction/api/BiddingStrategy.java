package de.optimax_energy.bidder.auction.api;

import de.optimax_energy.bidder.auction.api.dto.RoundResult;
import de.optimax_energy.bidder.auction.infrastructure.StrategyName;

import java.util.List;

public interface BiddingStrategy {

  int AMOUNT_OF_PRODUCTS_IN_ONE_ROUND = 2;

  int placeBid(List<RoundResult> roundResults);

  StrategyName getStrategyName();
}
