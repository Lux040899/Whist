public class PlayerFactory 
{
  private static PlayerFactory instance;

  public static synchronized PlayerFactory getInstance()
  {
    if(instance == null)
    {
      instance = new PlayerFactory();
    }
    return instance;
  }

  public Player getPlayer(String playerType, int id, int seed)
  {
    if(playerType == null)
    {
      return null;
    }
    if(playerType.equalsIgnoreCase("INTERACTIVE"))
    {
      return new InteractivePlayer(id);
    }
    else if(playerType.equalsIgnoreCase("SMART"))
    {
      return new SmartNPC(id, new SmartStrategy());
    }
    else if(playerType.equalsIgnoreCase("LEGAL"))
    {
      return new NPC(id, new LegalStrategy(seed));
    }
    else if(playerType.equalsIgnoreCase("RANDOM"))
    {
      return new NPC(id, new RandomStrategy(seed));
    }
    return null;
  }
}