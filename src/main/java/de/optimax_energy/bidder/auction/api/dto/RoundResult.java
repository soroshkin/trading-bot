package de.optimax_energy.bidder.auction.api.dto;

public class RoundResult {

  private final int myBid;

  private final int withdrawCash;

  private final int myWonQuantity;

  private final int opponentBid;

  private final int opponentWonQuantity;

  public RoundResult(int myBid, int withdrawCash, int myWonQuantity, int opponentBid, int opponentWonQuantity) {
    this.myBid = myBid;
    this.withdrawCash = withdrawCash;
    this.myWonQuantity = myWonQuantity;
    this.opponentBid = opponentBid;
    this.opponentWonQuantity = opponentWonQuantity;
  }

  public int getMyBid() {
    return myBid;
  }

  public int getWithdrawCash() {
    return withdrawCash;
  }

  public int getOpponentBid() {
    return opponentBid;
  }

  public int getMyWonQuantity() {
    return myWonQuantity;
  }

  public int getOpponentWonQuantity() {
    return opponentWonQuantity;
  }

  @Override
  public String toString() {
    return "RoundResult{" +
      "myBid=" + myBid +
      ", myWonQuantity=" + myWonQuantity +
      ", opponentBid=" + opponentBid +
      ", opponentWonQuantity=" + opponentWonQuantity +
      '}';
  }
}
