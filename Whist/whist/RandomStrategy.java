import java.util.Random;

import ch.aplu.jcardgame.Hand;

public class RandomStrategy implements Strategy
{
  private Random random;

  public RandomStrategy(int seed)
  {
    random = new Random(seed);
  }

  public void pickCard(Player player, Hand trick, Suit trump)
  {
    // Make any random move.
    int x = random.nextInt(player.getHand().getNumberOfCards());
    player.setSelectedCard(player.getHand().get(x));
  }
}