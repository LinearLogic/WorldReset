package ss.linearlogic.worldreset;

import java.util.logging.Logger;

public class WRLogger
{
  private static final Logger log = Logger.getLogger("Minecraft");

  public static void logInfo(String message)
  {
    log.info("[WorldReset] " + message);
  }
  
  public static void logWarning(String message) {
    log.warning("[WorldReset] " + message);
  }

  public static void logSevere(String message) {
    log.severe("[WorldReset] " + message);
  }
}