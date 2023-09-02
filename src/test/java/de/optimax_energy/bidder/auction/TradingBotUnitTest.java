package de.optimax_energy.bidder.auction;

import de.optimax_energy.bidder.UnitTest;
import de.optimax_energy.bidder.auction.api.BiddingStrategy;
import de.optimax_energy.bidder.auction.api.dto.RoundResult;
import de.optimax_energy.bidder.auction.api.dto.StrategyNotFoundException;
import de.optimax_energy.bidder.auction.infrastructure.TradingBot;
import de.optimax_energy.bidder.auction.infrastructure.storage.AuctionResultInMemoryStorageService;
import de.optimax_energy.bidder.auction.infrastructure.strategy.StatisticsService;
import de.optimax_energy.bidder.auction.infrastructure.strategy.StrategyFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.refEq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

class TradingBotUnitTest extends UnitTest {

  private static final int INITIAL_CASH = 100;

  private static final int INITIAL_QUANTITY = 10;

  @Mock
  private StatisticsService statisticsService;

  @Mock
  private StrategyFactory strategyFactory;

  @Mock
  private AuctionResultInMemoryStorageService auctionResultInMemoryStorageService;

  @InjectMocks
  private TradingBot tradingBot;

  @Captor
  private ArgumentCaptor<RoundResult> roundResultArgumentCaptor;

  @BeforeEach
  void setUp() {
    tradingBot.init(INITIAL_QUANTITY, INITIAL_CASH);
  }

  @Test
  @DisplayName("Should place bid")
  void shouldPlaceBid() {
    // given
    List<RoundResult> roundResults = givenRoundResults();
    when(auctionResultInMemoryStorageService.getRoundResultsForBidder(anyString())).thenReturn(roundResults);
    BiddingStrategy givenStrategy = mock(BiddingStrategy.class);
    when(givenStrategy.placeBid(roundResults, INITIAL_QUANTITY, INITIAL_CASH)).thenReturn(0);
    when(strategyFactory.buildStrategy(anyInt(), refEq(roundResults), anyInt(), anyInt())).thenReturn(Optional.of(givenStrategy));

    // when
    int bid = tradingBot.placeBid();

    // then
    assertThat(bid).isZero();
    verify(auctionResultInMemoryStorageService).getRoundResultsForBidder(anyString());
    verify(strategyFactory).buildStrategy(anyInt(), refEq(roundResults), anyInt(), anyInt());
    verify(givenStrategy).placeBid(roundResults, INITIAL_QUANTITY, INITIAL_CASH);
    verifyNoMoreInteractions(statisticsService, auctionResultInMemoryStorageService, statisticsService);
  }

  @Test
  @DisplayName("Should return remaining cash, when bid is higher")
  void shouldReturnRemainingCashIfBidIsHigher() {
    // given
    List<RoundResult> roundResults = givenRoundResults();
    when(auctionResultInMemoryStorageService.getRoundResultsForBidder(anyString())).thenReturn(roundResults);
    BiddingStrategy givenStrategy = mock(BiddingStrategy.class);
    when(givenStrategy.placeBid(roundResults, INITIAL_QUANTITY, INITIAL_CASH)).thenReturn(0);
    when(strategyFactory.buildStrategy(anyInt(), refEq(roundResults), anyInt(), anyInt())).thenReturn(Optional.of(givenStrategy));

    // when
    int bid = tradingBot.placeBid();

    // then
    assertThat(bid).isZero();
    verify(auctionResultInMemoryStorageService).getRoundResultsForBidder(anyString());
    verify(strategyFactory).buildStrategy(anyInt(), refEq(roundResults), anyInt(), anyInt());
    verify(givenStrategy).placeBid(roundResults, INITIAL_QUANTITY, INITIAL_CASH);
    verifyNoMoreInteractions(statisticsService, auctionResultInMemoryStorageService, statisticsService);
  }

  @Test
  @DisplayName("Should throw exception when strategy is not found")
  void shouldThrowExceptionWhenStrategyIsNotFound() {
    // given
    List<RoundResult> roundResults = givenRoundResults();
    when(auctionResultInMemoryStorageService.getRoundResultsForBidder(anyString())).thenReturn(roundResults);
    when(strategyFactory.buildStrategy(anyInt(), refEq(roundResults), anyInt(), anyInt())).thenReturn(Optional.empty());

    // when - then
    assertThatThrownBy(() -> tradingBot.placeBid())
      .isExactlyInstanceOf(StrategyNotFoundException.class)
      .hasMessage("Could not select strategy");

    verify(auctionResultInMemoryStorageService).getRoundResultsForBidder(anyString());
    verify(strategyFactory).buildStrategy(anyInt(), refEq(roundResults), anyInt(), anyInt());
    verifyNoMoreInteractions(statisticsService, auctionResultInMemoryStorageService, statisticsService);
  }

  @ParameterizedTest(name = "Should save RoundResult. myBid={0}, myExpectedRemainingCash={1}, opponentBid={2}")
  @CsvSource({"1, 99, 0, 2, 100, 0",
    "0, 100, 1, 0, 99, 2",
    "1, 99, 1, 1, 99, 1"})
  void shouldSaveRoundResult(int myBid,
                             int myExpectedRemainingCash,
                             int opponentBid,
                             int expectedMyWonQuantity,
                             int expectedOpponentRemainingCash,
                             int expectedOpponentWonQuantity) {
    // given
    List<RoundResult> givenRoundResults = givenRoundResults();
    RoundResult expectedRoundResult = RoundResult.builder()
      .withMyBid(myBid)
      .withMyWonQuantity(expectedMyWonQuantity)
      .withOpponentBid(opponentBid)
      .withOpponentRemainingCash(expectedOpponentRemainingCash)
      .withOpponentWonQuantity(expectedOpponentWonQuantity)
      .build();
    when(auctionResultInMemoryStorageService.getRoundResultsForBidder(anyString())).thenReturn(givenRoundResults);
    when(statisticsService.calculateOpponentRemainingCash(givenRoundResults, INITIAL_CASH)).thenReturn(INITIAL_CASH);

    // when
    tradingBot.bids(myBid, opponentBid);

    // then
    verify(auctionResultInMemoryStorageService).addRoundResultForBidder(anyString(), roundResultArgumentCaptor.capture());
    RoundResult roundResult = roundResultArgumentCaptor.getValue();
    assertThat(roundResult).usingRecursiveComparison().isEqualTo(expectedRoundResult);
    assertThat(tradingBot.getRemainingCash()).isEqualTo(myExpectedRemainingCash);
    verify(auctionResultInMemoryStorageService).getRoundResultsForBidder(anyString());
    verify(statisticsService).calculateOpponentRemainingCash(givenRoundResults, INITIAL_CASH);
    verifyNoMoreInteractions(statisticsService, auctionResultInMemoryStorageService, statisticsService);
  }

  private List<RoundResult> givenRoundResults() {
    return List.of(RoundResult.builder().build());
  }
}
