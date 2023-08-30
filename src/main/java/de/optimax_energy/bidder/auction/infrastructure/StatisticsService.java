package de.optimax_energy.bidder.auction.infrastructure;

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

  public int calculateMyCash(List<RoundResult> roundResults) {
    return roundResults.stream()
      .map(RoundResult::getWithdrawCash)
      .reduce(0, Integer::sum);
  }
}
