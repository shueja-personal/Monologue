package monologue;

import edu.wpi.first.epilogue.EpilogueConfiguration;
import edu.wpi.first.wpilibj.PowerDistribution;
import edu.wpi.first.wpilibj.TimedRobot;
import java.util.ArrayList;
import monologue.LoggingTree.StaticObjectNode;

public class Monologue {
  private static boolean HAS_SETUP_BEEN_CALLED = false;
  private static boolean EXTRAS_LOGGING_ENABLED = false;
  static boolean IS_DISABLED = false;
  static EpilogueConfiguration config = new EpilogueConfiguration();

  private static final ArrayList<Runnable> prematureCalls = new ArrayList<>();
  private static final ArrayList<StaticObjectNode> trees = new ArrayList<>();

  /**
   * Monologue's global logger; can be used as a proxy where {@link LogScope} cannot be used. Will
   * log under the "GlobalLog" namespace in datalog and networktables.
   */
  public static final GlobalLogger GlobalLog = new GlobalLogger();

  public static class GlobalLogger implements LogScope {
    GlobalLogger() {}

    public void setRootPath(String path) {
      var pathList = LogScope.registry.get(this);
      if (pathList != null) pathList.clear();
      LogScope.addNode(this, new StaticObjectNode(path, this));
    }
  }

  /** Sets up monologue as disabled. This will cause log calls to be ignored. */
  public static void setupDisabled() {
    if (HAS_SETUP_BEEN_CALLED) {
      RuntimeLog.warn("Monologue.setup() has already been called, further calls will do nothing");
      return;
    }
    HAS_SETUP_BEEN_CALLED = true;
    IS_DISABLED = true;
  }

  /**
   * Is the main entry point for the monologue library. It will interate over every member of the
   * provided Logged object and evaluated if it should be logged to the network tables or to a file.
   *
   * <p>Will also recursively check field values for classes that implement LogLocal and log those
   * as well.
   *
   * @param robot the root Logged object to log
   * @apiNote Should only be called once, if another {@link LogScope} tree needs to be created use
   *     {@link #logTree(Object, String)} for additional trees
   */
  public static <R extends TimedRobot & LogScope> void setup(
      R robot, EpilogueConfiguration epilogueConfig) {
    if (HAS_SETUP_BEEN_CALLED) {
      RuntimeLog.warn("Monologue.setup() has already been called, further calls will do nothing");
      return;
    }
    Monologue.config = epilogueConfig;
    HAS_SETUP_BEEN_CALLED = true;
    logTree(robot, config.root);
    GlobalLog.setRootPath(config.root);
    prematureCalls.forEach(Runnable::run);
    robot.addPeriodic(Monologue::updateAll, robot.getPeriod());
    System.gc();
    RuntimeLog.info("Monologue.setup() finished");
  }

  public static void startExtrasLogging(TimedRobot robot, PowerDistribution pdh) {
    if (EXTRAS_LOGGING_ENABLED) {
      RuntimeLog.warn("Extras logging has already been started; further calls will do nothing.");
      return;
    }
    EXTRAS_LOGGING_ENABLED = true;
    ExtrasLogger.start(robot, pdh);
  }

  /**
   * Creates a logging tree for the provided {@link LogScope} object. Will also recursively check
   * field values for classes that implement {@link LogScope} and log those as well.
   *
   * @param loggable the obj to scrape
   * @param path the path to log to
   * @throws IllegalStateException
   */
  static void logTree(Object loggable, String path) {
    if (!hasBeenSetup())
      throw new IllegalStateException(
          "Tried to use Monologue.logTree() before using a Monologue setup method");

    if (path == null || path.isEmpty()) {
      RuntimeLog.warn("Invalid path for Monologue.logTree(): " + path);
      return;
    } else if (path.equals("/")) {
      RuntimeLog.warn("Root path of / is not allowed for Monologue.logTree()");
      return;
    }
    RuntimeLog.info(
        "Monologue.logTree() called on " + loggable.getClass().getName() + " with path " + path);

    StaticObjectNode node = new StaticObjectNode(path, loggable);
    Eval.exploreNodes(Eval.getLoggedClasses(loggable.getClass()), node);
    LogScope.addNode(loggable, node);

    trees.add(node);

    updateAll();
  }

  /**
   * Updates all the loggers, ideally called every cycle.
   *
   * @apiNote Should only be called on the same thread monologue was setup on
   */
  static void updateAll() {
    if (!hasBeenSetup()) RuntimeLog.warn("Called Monologue.updateAll before Monologue was setup");
    for (StaticObjectNode tree : trees) {
      tree.log(null);
    }
  }

  static void prematureLog(Runnable runnable) {
    prematureCalls.add(runnable);
  }

  /**
   * Checks if the Monologue library has been setup.
   *
   * @return true if Monologue has been setup, false otherwise
   */
  static boolean hasBeenSetup() {
    return HAS_SETUP_BEEN_CALLED;
  }
}
