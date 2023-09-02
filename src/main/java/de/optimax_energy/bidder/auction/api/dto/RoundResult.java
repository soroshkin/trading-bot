package de.optimax_energy.bidder.auction.api.dto;

public class RoundResult {

  private final int myBid;

  private final int myWonQuantity;

  private final int opponentBid;

  private final int opponentRemainingCash;

  private final int opponentWonQuantity;

  private RoundResult(Builder builder) {
    this.myBid = builder.myBid;

    this.myWonQuantity = builder.myWonQuantity;
    this.opponentBid = builder.opponentBid;
    this.opponentRemainingCash = builder.opponentRemainingCash;
    this.opponentWonQuantity = builder.opponentWonQuantity;
  }

  public static Builder builder() {
    return new Builder();
  }

  public int getMyBid() {
    return myBid;
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

  public int getOpponentRemainingCash() {
    return opponentRemainingCash;
  }

  @Override
  public String toString() {
    return "RoundResult{" +
      "myBid=" + myBid +
      ", myWonQuantity=" + myWonQuantity +
      ", opponentBid=" + opponentBid +
      ", opponentRemainingCash=" + opponentRemainingCash +
      ", opponentWonQuantity=" + opponentWonQuantity +
      '}';
  }

  public static final class Builder {

    private int myBid;

    private int myWonQuantity;

    private int opponentBid;

    private int opponentRemainingCash;

    private int opponentWonQuantity;

    private Builder() {
    }

    public Builder withMyBid(int myBid) {
      this.myBid = myBid;
      return this;
    }

    public Builder withMyWonQuantity(int myWonQuantity) {
      this.myWonQuantity = myWonQuantity;
      return this;
    }

    public Builder withOpponentBid(int opponentBid) {
      this.opponentBid = opponentBid;
      return this;
    }

    public Builder withOpponentRemainingCash(int opponentRemainingCash) {
      this.opponentRemainingCash = opponentRemainingCash;
      return this;
    }

    public Builder withOpponentWonQuantity(int opponentWonQuantity) {
      this.opponentWonQuantity = opponentWonQuantity;
      return this;
    }

    public RoundResult build() {
      return new RoundResult(this);
    }
  }
}
