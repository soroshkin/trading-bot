package de.optimax_energy.bidder.auction.infrastructure.storage;

import de.optimax_energy.bidder.auction.api.AuctionResultStorageOperations;
import de.optimax_energy.bidder.auction.api.dto.RoundResult;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class AuctionResultInMemoryStorage implements AuctionResultStorageOperations {

  private final Map<String, List<RoundResult>> roundResults = new HashMap<>();

  @Override
  public void addRoundResult(String bidderUuid, RoundResult roundResult) {
    List<RoundResult> roundResultsOfBidder = roundResults.getOrDefault(bidderUuid, new LinkedList<>());
    roundResultsOfBidder.add(roundResult);
  }

  @Override
  public List<RoundResult> getRoundResultsForBidder(String bidderUuid) {
    return Optional.ofNullable(roundResults.get(bidderUuid)).orElseGet(Collections::emptyList);
  }
}
