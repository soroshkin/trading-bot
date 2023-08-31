package de.optimax_energy.bidder.auction.infrastructure;

import de.optimax_energy.bidder.auction.api.dto.RoundResult;

import java.util.List;
import java.util.Optional;

public class StatisticsService {

  private final int initialCash;

  public StatisticsService(Integer initialCash) {
    this.initialCash = initialCash;
  }

  public int calculateOpponentQuantity(List<RoundResult> roundResults) {
    return roundResults.stream()
      .map(RoundResult::getOpponentWonQuantity)
      .reduce(0, Integer::sum);
  }

  public int calculateMyQuantity(List<RoundResult> roundResults) {
    return roundResults.stream()
      .map(RoundResult::getMyWonQuantity)
      .reduce(0, Integer::sum);
  }

  public int calculateMyRemainingCash(List<RoundResult> roundResults) {
    return initialCash - calculateMySpentCash(roundResults);
  }

  public Optional<RoundResult> getLastRoundResult(List<RoundResult> roundResults) {
    if (roundResults.isEmpty()) {
      return Optional.empty();
    }

    return Optional.of(roundResults.get(roundResults.size() - 1));
  }

  public int calculateOpponentRemainingCash(List<RoundResult> roundResults) {
    return roundResults.isEmpty() ? initialCash
      : roundResults.get(roundResults.size() - 1).getOpponentRemainingCash();
  }

  public int calculateOpponentAverageBid(List<RoundResult> roundResults) {
    int numberOfLastBids = 3;
    if (roundResults.size() >= numberOfLastBids) {
      return roundResults.subList(roundResults.size() - numberOfLastBids, roundResults.size()).stream()
        .map(RoundResult::getOpponentBid)
        .mapToInt(o -> o)
        .average()
        .stream()
        .mapToLong(Math::round)
        .mapToInt(i -> (int) i)
        .findFirst()
        .orElse(0);
    } else {
      return roundResults.stream()
        .map(RoundResult::getOpponentBid)
        .mapToInt(o -> o)
        .average()
        .stream()
        .mapToLong(Math::round)
        .mapToInt(i -> (int) i)
        .findFirst()
        .orElse(0);
    }
  }

  private int calculateMySpentCash(List<RoundResult> roundResults) {
    return roundResults.stream()
      .map(RoundResult::getMyBid)
      .reduce(0, Integer::sum);
  }
}
