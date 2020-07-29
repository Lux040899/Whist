import ch.aplu.jcardgame.Hand;

public interface Strategy 
{
  public void pickCard(Player player, Hand trick, Suit trump);
}