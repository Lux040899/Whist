import ch.aplu.jcardgame.*;

public abstract class Player 
{
  private Hand hand;
  private Card selectedCard;
  private int id;

  public Player(int id)
  {
    this.id = id;
  }

  public int getId()
  {
    return this.id;
  }

  // Picks a card and sets it as selected card.
  public abstract void pickCard(Hand trick, Suit trump);

  public void updateHand(Hand newHand)
  {
    this.hand = newHand;
  }

  public Hand getHand()
  {
    return this.hand;
  }

  public Card getSelectedCard()
  {
    return this.selectedCard;
  }

  public void setSelectedCard(Card card)
  {
    this.selectedCard = card;
  }
}