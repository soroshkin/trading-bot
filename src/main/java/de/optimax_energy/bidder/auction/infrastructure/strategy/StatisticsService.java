package de.optimax_energy.bidder.auction.infrastructure.strategy;

import de.optimax_energy.bidder.auction.api.dto.RoundResult;

import java.util.List;

public class StatisticsService {

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

  public int calculateMyRemainingCash(List<RoundResult> roundResults, int initialCash) {
    return initialCash - calculateMySpentCash(roundResults);
  }

  public int calculateOpponentRemainingCash(List<RoundResult> roundResults, int initialCash) {
    return roundResults.isEmpty() ? initialCash
      : roundResults.get(roundResults.size() - 1).getOpponentRemainingCash();
  }

  public int calculateOpponentAverageBid(List<RoundResult> roundResults) {
    int numberOfLastBids = 3;
    List<RoundResult> roundResultsForAverageCalculation = (roundResults.size() >= numberOfLastBids)
      ? roundResults.subList(roundResults.size() - numberOfLastBids, roundResults.size())
      : roundResults;

    return roundResultsForAverageCalculation.stream()
      .map(RoundResult::getOpponentBid)
      .mapToInt(o -> o)
      .average()
      .stream()
      .mapToLong(Math::round)
      .mapToInt(i -> (int) i)
      .findFirst()
      .orElse(0);
  }

  private int calculateMySpentCash(List<RoundResult> roundResults) {
    return roundResults.stream()
      .map(RoundResult::getMyBid)
      .reduce(0, Integer::sum);
  }
}