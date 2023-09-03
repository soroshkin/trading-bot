package de.optimax_energy.bidder.auction.api;

import de.optimax_energy.bidder.auction.api.dto.RoundResult;

import java.util.List;

public interface RoundResultStorageOperations {

  void addRoundResultForBidder(String bidderUuid, RoundResult roundResult);

  List<RoundResult> getRoundResultsForBidder(String bidderUuid);
}
