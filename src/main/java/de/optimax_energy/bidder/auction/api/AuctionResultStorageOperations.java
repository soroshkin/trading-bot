package de.optimax_energy.bidder.auction.api;

import de.optimax_energy.bidder.auction.api.dto.RoundResult;

import java.util.List;

public interface AuctionResultStorageOperations {

  void addRoundResult(String bidderUuid, RoundResult roundResult);

  List<RoundResult> getRoundResultsForBidder(String bidderUuid);
}
