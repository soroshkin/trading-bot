# trading-bot

## Description
Implementation of trading bot, which takes a part in one-round sealed-bid auction.
The hexagonal architecture was applied for packages and class structure. It allows easily to change implementations of ports and adapters (e.g. in-memory storage can be switched with spring data implementation) without changing the core domain logic and entities. The goal is to have maintainable and extendable components.

## Logic explanation
Main implementation is TradingBot, which implements Bidder interface. It uses StrategySelector to select appropriate logic during runtime depending on current situation. Currently 5 strategies are implemented: AggressiveBiddingStrategy, DefaultBiddingStrategy, MaximumBidStrategy, MinimumBidStrategy, ZeroBiddingStrategy.
There are bunch of conditions when one of these strategies is chosen. 

The DefaultBiddingStrategy implementation is based on these articles: 
https://cs.brown.edu/courses/csci1440/lectures/2022/first_price_auctions.pdf
https://cs.brown.edu/courses/cs1951k/lectures/2020/first_price_auctions.pdf.
 
They claim that "For all vi ∈ (0, 1], the first and second order conditions are satisfied, so we conclude that bidding $`bi = (n−1)*Vi/n`$ is optimal"
and "Therefore, bidder i maximizes her utility by bidding $`bi = (n−1)*Vi/n`$", where $`n`$ - number of bidders, $`Vi`$ - value of product (which is unknown). The assumption is that opponent's evaluation ($`Vi`$) is equal to initial cash divided by half of initial QU plus one.

AggressiveBiddingStrategy behaves differently, it multiplies opponent's bid by 1.34, but not higher than (myRemainingCash / (requiredQuantityNotToLoose / AMOUNT_OF_PRODUCTS_IN_ONE_ROUND)) multiplied by 3.

## Further improvements
The algorithm is not optimal and can be improved:
- it's efficiency significantly varies depending on initial QU and MU amount
- default strategy doesn't perform as well as expected, needs investigation
- statistics service calculates opponent average bid on 2 last rounds, but in many cases getting just last bid is more effective, needs investigation
- review coefficients
- additional strategies can be added:
  in case when the opponent always bids with same amount
  save money strategy
  strategies for very high/low QU/MU
  etc.
