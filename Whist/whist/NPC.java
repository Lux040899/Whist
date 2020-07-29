import ch.aplu.jcardgame.Hand;

public class NPC extends Player
{
  private Strategy strategy;

  public NPC(int id, Strategy strategy)
  {
    super(id);
    this.strategy = strategy;
  }

  public void pickCard(Hand trick, Suit trump)
  {
    this.setSelectedCard(null);
    strategy.pickCard(this, trick, trump);
  }

  public void updateEndOfTrick(Hand trick)
  {
    return;
  }
}