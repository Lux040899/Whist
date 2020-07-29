import java.util.ArrayList;

import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.Hand;

public class SmartNPC extends NPC
{
  private ArrayList<Card> playedCards;

  public SmartNPC(int id, Strategy strategy)
  {
    super(id, strategy);
    playedCards = new ArrayList<>();
  }

  // Observer to keep track of cards that have been played throughout each round.
  @Override
  public void updateEndOfTrick(Hand trick)
  {
    for(Card card : trick.getCardList())
    {
      playedCards.add(card);
    }
    if(playedCards.size() >= Whist.nbStartCards * Whist.nbPlayers)
    {
      playedCards.clear();
    }
  }

  public ArrayList<Card> getPlayedCards()
  {
    return this.playedCards;
  }
}