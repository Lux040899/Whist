public class Score 
{
  private int[] scores;

  public Score(int nbPlayers)
  {
    scores = new int[nbPlayers];
    for(int i = 0; i < nbPlayers; i++) scores[i] = 0;
  }

  public void updateScore(int player)
  {
    scores[player]++;
  }

  public int getPlayerScore(int player)
  {
    return scores[player];
  }

  public int[] getScores()
  {
    return scores;
  }
}
