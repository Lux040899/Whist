import java.util.ArrayList;

import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.Hand;

public class SmartStrategy implements Strategy
{
  public void pickCard(Player player, Hand trick, Suit trump)
  {
    ArrayList<Card> trumpCards = player.getHand().getCardsWithSuit(trump);
    Card highestCard = null;
    boolean trumpInTrick = false;
    // Check if first player
    if(trick.getNumberOfCards() == 0)
    {
      // Play trump ace if player has it, otherwise play highest card in hand.
      for(Card card : player.getHand().getCardList())
      {
        if((Suit) card.getSuit() == trump && (Rank) card.getRank() == Rank.ACE)
        {
          player.setSelectedCard(card);
          return;
        }
        else if(highestCard == null 
                || (highestCard.getRankId() > card.getRankId()
                    && (Suit) card.getSuit() != trump))
        {
          highestCard = card;
        }
      }
      player.setSelectedCard(highestCard);
      return;
    }
    else
    {
      ArrayList<Card> trickSuitCards = player.getHand().getCardsWithSuit((Suit) trick.get(0).getSuit());
      if(trick.getCardsWithSuit(trump).size() > 0 && trick.get(0).getSuit() != trump)
      {
        trumpInTrick = true;
      }

      Card winningCard = currentTrickWinningCard(trick, trump);
      // If player has cards of the trick suit, then play something from there.
      if(trickSuitCards.size() > 0)
      {
        // If it is the last player, play a card just enough to win, if trump has not been played.
        if(trick.getNumberOfCards() == Whist.nbPlayers - 1)
        {
          if(!trumpInTrick)
          {
            for(Card card : trickSuitCards)
            {
              if(card.getRankId() < winningCard.getRankId())
              {
                player.setSelectedCard(card);
                return;
              }
            }
          }
        }
        // Play the highest card of the same suit as trick, if trump has not been played.
        else if(!trumpInTrick)
        {
          Card highestCardTrickSuit = trickSuitCards.get(0);
          for(Card card : trickSuitCards)
          {
            if(highestCardTrickSuit.getRankId() > card.getRankId())
            {
              highestCardTrickSuit = card;
            }
          }
          if(highestCardTrickSuit.getRankId() < winningCard.getRankId())
          {
            player.setSelectedCard(highestCardTrickSuit);
            return;
          }
        }
      }
      // Should try and play trump.
      else if(trumpCards.size() > 0)
      {
        if(trick.getNumberOfCards() == Whist.nbPlayers - 1)
        {
          // If last player and no trumps have been played, then play lowest trump
          if(!trumpInTrick)
          {
            Card lowestTrump = trumpCards.get(0);
            for(Card card : trumpCards)
            {
              if(lowestTrump.getRankId() < card.getRankId())
              {
                lowestTrump = card;
              }
            }
            player.setSelectedCard(lowestTrump);
            return;
          }
          else
          {
            // If trump has been played, then play the lowest trump card to beat that.
            for(Card card : trumpCards)
            {
              if(card.getRankId() < winningCard.getRankId())
              {
                player.setSelectedCard(card);
                return;
              }
            }
          }
        }
        // Play the highest trump card if winning card is not of trump suit.
        else if(!trumpInTrick)
        {
          Card highestTrump = trumpCards.get(0);
          for(Card card : trumpCards)
          {
            if(highestTrump.getRankId() > card.getRankId())
            {
              highestTrump = card;
            }
          }
          player.setSelectedCard(highestTrump);
          return;
        }
        // If the winning card is a trump card, then play just enough to beat it.
        else if(trumpInTrick)
        {
          for(Card card : trumpCards)
          {
            if(card.getRankId() < winningCard.getRankId())
            {
              player.setSelectedCard(card);
              return;
            }
          }
        }
      }
    }
    player.setSelectedCard(lowestLegalCard(player.getHand(), trick, trump));
  }

  private Card currentTrickWinningCard(Hand trick, Suit trump)
  {
    Card winningCard = trick.get(0);
    for(Card card : trick.getCardList())
    {
      if(card == null
         || (card.getRankId() < winningCard.getRankId() && card.getSuit() == winningCard.getSuit())
         || ((Suit) card.getSuit() == trump && winningCard.getSuit() != trump))
      {
        winningCard = card;
      }
    }
    return winningCard;
  }

  private Card lowestLegalCard(Hand hand, Hand trick, Suit trump)
  {
    System.out.println("checklegal");
    Suit trickSuit = (Suit) trick.get(0).getSuit();
    System.out.println(trick.get(0).toString());
    Card lowestCard;

    if(hand.getCardsWithSuit(trickSuit).size() != 0)
    {
      lowestCard = hand.getCardsWithSuit(trickSuit).get(0);
      // Lowest same suit as trick card.
      for(Card card : hand.getCardsWithSuit(trickSuit))
      {
        if(lowestCard.getRankId() < card.getRankId())
        {
          lowestCard = card;
        }
      }
      return lowestCard;
    }

    // Lowest any card.
    lowestCard = hand.get(0);
    for(Card card : hand.getCardList())
    {
      if(lowestCard.getRankId() < card.getRankId())
      {
        lowestCard = card;
      }
    }
    return lowestCard;
  }
}