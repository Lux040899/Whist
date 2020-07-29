import ch.aplu.jcardgame.*;

public class InteractivePlayer extends Player
{
  public InteractivePlayer(int id)
  {
    super(id);
  }

  public void pickCard(Hand trick, Suit trump)
  {
    this.setSelectedCard(null);
    this.getHand().setTouchEnabled(true);
  }
}