import ch.aplu.jcardgame.*;
import ch.aplu.jgamegrid.*;
import java.awt.Font;
import java.util.ArrayList;
import java.util.Random;
import java.awt.Color;

@SuppressWarnings("serial")
public class WhistUI extends CardGame
{
  private static WhistUI instance;

  private static final int gameWidth = 700;
  private static final int gameHeight = 700;
  private static final int gameStatusHeight = 30;

  private final String version = "1.0";
  private final String initTitle = "Whist (V" + version + ") Constructed for UofM SWEN30006 with JGameGrid (www.aplu.ch)";
  private final String initStatus = "Initializing...";

  final String trumpImage[] = { "bigspade.gif", "bigheart.gif", "bigdiamond.gif", "bigclub.gif" };
  
  private final Location[] handLocations = {
  new Location(350, 625),
  new Location(75, 350),
  new Location(350, 75),
  new Location(625, 350)
  };

  private final Location[] scoreLocations = {
  new Location(575, 675),
  new Location(25, 575),
  new Location(575, 25),
  new Location(650, 575)
  };

  private Actor[] scoreActors = {null, null, null, null };
  private final Location trickLocation = new Location(350, 350);
  private final Location textLocation = new Location(350, 450);

  private final int handWidth = 400;
  private final int trickWidth = 40;

  private Location hideLocation = new Location(-500, -500);
  private Location trumpsActorLocation = new Location(50, 50);

  Font bigFont = new Font("Serif", Font.BOLD, 36);

  private Actor trumpsActor;

  private final Deck deck = new Deck(Suit.values(), Rank.values(), "cover");

  private final int npcThinkingTime = 2000;

  private Random random;

  public WhistUI()
  {
    super(gameWidth, gameHeight, gameStatusHeight);
    setTitle(initTitle);
    setStatusText(initStatus);
    random = new Random(Whist.seed);
  }

  public static synchronized WhistUI getInstance()
  {
    if(instance == null)
    {
      instance = new WhistUI();
    }
    return instance;
  }

  public void displayScores(int[] scores)
  {
    for(int i = 0; i < scores.length; i++)
    {
      scoreActors[i] = new TextActor(String.valueOf(scores[i]), Color.WHITE, bgColor, bigFont);
  		addActor(scoreActors[i], scoreLocations[i]);
    }
  }

  public void updateScoreOfPlayer(int playerIndex, int newScore)
  {
    removeActor(scoreActors[playerIndex]);
    scoreActors[playerIndex] = new TextActor(String.valueOf(newScore), Color.WHITE, bgColor, bigFont);
    addActor(scoreActors[playerIndex], scoreLocations[playerIndex]);
  }

  public void displayTrumpSuit(Suit trump)
  {
    trumpsActor = new Actor("sprites/" + trumpImage[trump.ordinal()]);
    addActor(trumpsActor, trumpsActorLocation);
  }

  public void renderPlayerHands(ArrayList<Player> players)
  {
    RowLayout[] layouts = new RowLayout[players.size()];
    for (int i = 0; i < players.size(); i++) 
    {
      layouts[i] = new RowLayout(handLocations[i], handWidth);
      layouts[i].setRotationAngle(90 * i);
      // layouts[i].setStepDelay(10);
      players.get(i).getHand().setView(this, layouts[i]);
      players.get(i).getHand().setTargetArea(new TargetArea(trickLocation));
      players.get(i).getHand().draw();
    }
    //	    for (int i = 0; i < players.size(); i++)  // This code can be used to visually hide the cards in a hand (make them face down)
    //        if(players.get(i) instanceof NPC)
    //	        players.get(i).getHand().setVerso(true);
  }

  public void removeTrumpSuit()
  {
    removeActor(trumpsActor);
  }

  public void renderTrick(Hand trick)
  {
    trick.setView(this, new RowLayout(trickLocation, (trick.getNumberOfCards()+2)*trickWidth));
	  trick.draw();
  }

  public void hideTrick(Hand trick)
  {
    trick.setView(this, new RowLayout(hideLocation, 0));
	  trick.draw();
  }

  public void setDelay(int time)
  {
    delay(time);
  }

  public void displayStatus(String string) 
  { 
    setStatusText(string); 
  }

  public void displayPlayerStatus(Player player)
  {
    if(player instanceof InteractivePlayer)
    {
      setStatusText("Player " + player.getId() + " double-click on card to lead.");
    }
    else if(player instanceof NPC)
    {
      setStatusText("Player " + player.getId() + " thinking...");
      delay(npcThinkingTime);
    }
  }

  public void initInteractivePlayers(ArrayList<Player> players)
  {
    for(Player player : players)
    {
      if(player instanceof InteractivePlayer)
      {
        CardListener cardListener = new CardAdapter()
        {
          public void leftDoubleClicked(Card card)
          {
            player.setSelectedCard(card);
            player.getHand().setTouchEnabled(false);
          }
        };
        player.getHand().addCardListener(cardListener);
      }
    }
  }

  public void showCardFace(Card card)
  {
    card.setVerso(false);
  }

  public void moveToTrick(Card card, Hand trick)
  {
    card.transfer(trick, true);
  }

  public void dealCardsToPlayers(ArrayList<Player> players)
  {
    Hand[] dealtHands = dealOutCards(players.size());
    for(int i = 0; i < dealtHands.length-1; i++)
    {
      dealtHands[i].sort(Hand.SortType.SUITPRIORITY, true);
      players.get(i).updateHand(dealtHands[i]);
    }
  }

  private Hand[] dealOutCards(int nbPlayers)
  {
    Hand[] hands = new Hand[nbPlayers + 1];
    
    ArrayList<Integer> cardIndices = new ArrayList<>();
    for(int i=0; i<deck.cards.length*deck.cards[0].length; i++)
    {
      cardIndices.add(i);
    }
    int numCards = cardIndices.size();

    int handIndex = 0;
    while(cardIndices.size() > numCards - (nbPlayers * Whist.nbStartCards))
    {
      int x = random.nextInt(cardIndices.size());
      if(hands[handIndex] == null)
      {
        hands[handIndex] = new Hand(deck);
      }

      if(hands[handIndex].getNumberOfCards() < Whist.nbStartCards)
      {
        hands[handIndex++].insert(deck.cards[cardIndices.get(x)%deck.cards.length][cardIndices.get(x)%deck.cards[0].length], false);
        cardIndices.remove(x);
      }
      if(handIndex == nbPlayers)
      {
        handIndex = 0;
      }
    }
    return hands;
  }

  public void showWinner(int winningPlayerIndex, Hand trick)
  {
    delay(600);
    hideTrick(trick);
    setStatusText("Player " + winningPlayerIndex + " wins trick.");
  }

  public Deck getDeck()
  {
    return deck;
  }

  public void gameOver(String text)
  {
    addActor(new Actor("sprites/gameover.gif"), textLocation);
    setStatusText(text);
    refresh();
  }
}