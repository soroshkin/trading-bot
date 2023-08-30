package de.optimax_energy.bidder.auction.api.dto;

public class StrategyNotFoundException extends RuntimeException {

  public StrategyNotFoundException(String message) {
    super(message);
  }
}
