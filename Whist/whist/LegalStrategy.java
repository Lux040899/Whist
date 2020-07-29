import java.util.ArrayList;
import java.util.Random;

import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.Hand;

public class LegalStrategy implements Strategy
{
  private Random random;

  public LegalStrategy(int seed)
  {
    random = new Random(seed);
  }
  
  public void pickCard(Player player, Hand trick, Suit trump)
  {
    int cardIndex;
    // Trick is non-empty, follow rules to play a card.
    if(trick.getNumberOfCards() != 0)
    {
      Suit suitToPlay = (Suit) trick.get(0).getSuit();

      ArrayList<Card> legalMoves = player.getHand().getCardsWithSuit(suitToPlay);
      // Pick a random move from available legal moves, if any.
      if(legalMoves.size() != 0)
      {
        cardIndex = random.nextInt(legalMoves.size());
        player.setSelectedCard(legalMoves.get(cardIndex));
        return;
      }
    }

    // Otherwise, make any random move.
    cardIndex = random.nextInt(player.getHand().getNumberOfCards());
    player.setSelectedCard(player.getHand().get(cardIndex));
  }
}