// Whist.java

import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.Hand;
import java.util.*;

import java.util.Properties;
import java.io.FileReader;
import java.io.IOException;

public class Whist 
{
  public static int seed;

  private static Random random;

  // return random Enum value
  public static <T extends Enum<?>> T randomEnum(Class<T> clazz) 
  {
    int x = random.nextInt(clazz.getEnumConstants().length);
    return clazz.getEnumConstants()[x];
  }

  public boolean rankGreater(Card card1, Card card2) 
  {
    return card1.getRankId() < card2.getRankId(); // Warning: Reverse rank order of cards (see comment on enum)
  }
  
  public static final int nbPlayers = 4;
  public static int nbStartCards = 13;
  public static int winningScore = 11;
  
  private static boolean enforceRules;

  private ArrayList<Player> players = new ArrayList<>();

  private Score playerScores;

  private Card cardSelected;

  private void initRound() 
  {
    WhistUI.getInstance().dealCardsToPlayers(players);

    WhistUI.getInstance().initInteractivePlayers(players);

    WhistUI.getInstance().renderPlayerHands(players);
  }

  // Returns winner, if any
  private Optional<Integer> playRound() 
  {
    // Select and display trump suit
    final Suit trumps = randomEnum(Suit.class);
    WhistUI.getInstance().displayTrumpSuit(trumps);

    Hand trick;
    int winner;
    Card winningCard;
    Suit lead;
    int nextPlayer = random.nextInt(nbPlayers); // randomly select player to lead for this round
    for (int i = 0; i < nbStartCards; i++) 
    {
      trick = new Hand(WhistUI.getInstance().getDeck());
      cardSelected = null;

      // Lead with selected card
      WhistUI.getInstance().displayPlayerStatus(players.get(nextPlayer));
      
      players.get(nextPlayer).pickCard(trick, trumps);
      while(players.get(nextPlayer).getSelectedCard() == null)
      {
        WhistUI.getInstance().setDelay(100);
      }
      cardSelected = players.get(nextPlayer).getSelectedCard();

      WhistUI.getInstance().renderTrick(trick);

      WhistUI.getInstance().showCardFace(cardSelected);

      // No restrictions on the card being lead
      lead = (Suit) cardSelected.getSuit();
      WhistUI.getInstance().moveToTrick(cardSelected, trick); // transfer to trick (includes graphic effect)
      winner = nextPlayer;
      winningCard = cardSelected;
      // End Lead

      for (int j = 1; j < nbPlayers; j++) 
      {
        if (++nextPlayer >= nbPlayers)
          nextPlayer = 0; // From last back to first
        cardSelected = null;
        
        // Follow with selected card
        WhistUI.getInstance().displayPlayerStatus(players.get(nextPlayer));
        players.get(nextPlayer).pickCard(trick, trumps);
        while(players.get(nextPlayer).getSelectedCard() == null)
        {
          WhistUI.getInstance().setDelay(100);
        }
        cardSelected = players.get(nextPlayer).getSelectedCard();

        WhistUI.getInstance().renderTrick(trick);
        WhistUI.getInstance().showCardFace(cardSelected); // In case it is upside down
        // Check: Following card must follow suit if possible
        if (cardSelected.getSuit() != lead
            && players.get(nextPlayer).getHand().getNumberOfCardsWithSuit(lead) > 0) 
        {
          // Rule violation
          String violation = "Follow rule broken by player " + nextPlayer + " attempting to play "
              + cardSelected;
          System.out.println(violation);
          if (enforceRules) 
          {
            try 
            {
              throw (new BrokeRuleException(violation));
            } 
            catch (BrokeRuleException e) 
            {
              e.printStackTrace();
              System.out.println("A cheating player spoiled the game!");
              System.exit(0);
            }
          }
        }
        // End Check
        WhistUI.getInstance().moveToTrick(cardSelected, trick); // transfer to trick (includes graphic effect)
        System.out.println("winning: suit = " + winningCard.getSuit() + ", rank = " + winningCard.getRankId());
        System.out.println(" played: suit = " + cardSelected.getSuit() + ", rank = " + cardSelected.getRankId());
        if ( // beat current winner with higher card
        (cardSelected.getSuit() == winningCard.getSuit()
            && rankGreater(cardSelected, winningCard))
            // trumped when non-trump was winning
            || (cardSelected.getSuit() == trumps && winningCard.getSuit() != trumps)) 
        {
          System.out.println("NEW WINNER");
          winner = nextPlayer;
          winningCard = cardSelected;
        }
        // End Follow
      }
      nextPlayer = winner;
      // Keeps original behaviour but isn't able to show status... left it in anyway.
      WhistUI.getInstance().showWinner(nextPlayer, trick);
      for(Player player : players)
      {
        if(player instanceof NPC)
        {
          ((NPC)player).updateEndOfTrick(trick);
        }
      }
      playerScores.updateScore(nextPlayer);
      WhistUI.getInstance().updateScoreOfPlayer(nextPlayer, playerScores.getPlayerScore(nextPlayer));
      if (winningScore == playerScores.getPlayerScore(nextPlayer))
        return Optional.of(nextPlayer);
    }
    return Optional.empty();
  }

  public Whist(int humanPlayers, int randomNPC, int legalNPC, int smartNPC) 
  {
    // Initialise score value for player.
    playerScores = new Score(nbPlayers);

    for(int i = 0; i < nbPlayers; i++)
    {
      if(humanPlayers > 0)
      {
        players.add(PlayerFactory.getInstance().getPlayer("INTERACTIVE", i, seed));
        humanPlayers--;
        continue;
      }
      if(smartNPC > 0)
      {
        players.add(PlayerFactory.getInstance().getPlayer("SMART", i, seed));
        smartNPC--;
        continue;
      }
      if(legalNPC > 0)
      {
        players.add(PlayerFactory.getInstance().getPlayer("LEGAL", i, seed));
        legalNPC--;
        continue;
      }
      if(randomNPC > 0)
      {
        players.add(PlayerFactory.getInstance().getPlayer("RANDOM", i, seed));
        randomNPC--;
        continue;
      }
    }

    WhistUI.getInstance().displayScores(playerScores.getScores());

    Optional<Integer> winner;
    do 
    {
      initRound();
      winner = playRound();
    } while (!winner.isPresent());

    WhistUI.getInstance().gameOver("Game over. Winner is player: " + winner.get());
  }

  public static void main(String[] args) throws IOException
  {
	  // System.out.println("Working Directory = " + System.getProperty("user.dir"));
    Properties gameProperties = new Properties();
    
    // Set default properties.
    gameProperties.setProperty("Starting_Cards", "13");
    gameProperties.setProperty("Winning_Score", "11");
    gameProperties.setProperty("Interactive_Players", "1");
    gameProperties.setProperty("Random_NPCs", "3");
    gameProperties.setProperty("Smart_NPCs", "0");
    gameProperties.setProperty("Enforce_Rules", "false");

    // Read in properties from file.
    FileReader inStream = null;
    try
    {
      inStream = new FileReader("original.properties");
      gameProperties.load(inStream);
    }
    finally
    {
      if(inStream != null)
      {
        inStream.close();
      }
    }
    
    // Seed
    try
    {
      seed = Integer.parseInt(gameProperties.getProperty("Seed"));
      random = new Random(seed);
    }
    // If no seed has been provided then every run will be completely random.
    catch(NumberFormatException e)
    {
      random = new Random();
      seed = random.nextInt();
    }
    // Starting cards
    nbStartCards = Integer.parseInt(gameProperties.getProperty("Starting_Cards"));
    // Winning score
    winningScore = Integer.parseInt(gameProperties.getProperty("Winning_Score"));
    // Smart NPCs
    int smartNPC = Integer.parseInt(gameProperties.getProperty("Smart_NPCs"));
    // Legal NPCs
    int legalNPC = Integer.parseInt(gameProperties.getProperty("Legal_NPCs"));
    // Random NPCs
    int randomNPC = Integer.parseInt(gameProperties.getProperty("Random_NPCs"));
    // Interactive players
    int interactivePlayers = Integer.parseInt(gameProperties.getProperty("Interactive_Players"));
    //Enforce rules of the game
    enforceRules = Boolean.parseBoolean(gameProperties.getProperty("Enforce_Rules"));

    new Whist(interactivePlayers, randomNPC, legalNPC, smartNPC);
  }
}