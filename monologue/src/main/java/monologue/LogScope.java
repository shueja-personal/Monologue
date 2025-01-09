package monologue;

import edu.wpi.first.units.Measure;
import edu.wpi.first.units.Unit;
import edu.wpi.first.util.struct.Struct;
import edu.wpi.first.util.struct.StructFetcher;
import edu.wpi.first.util.struct.StructSerializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import monologue.LoggingTree.LoggingNode;

/** Interface that allows an object to be manually logged. */
@SuppressWarnings("unchecked")
public interface LogScope {
  Map<Object, ArrayList<LoggingNode>> registry = new HashMap<>();

  static void addNode(Object logged, LoggingNode node) {
    var lst = getNodes(logged);
    if (lst.size() > 100) {
      RuntimeLog.warn("Log namespace determination is likely recursive: " +
                          "An object instance is logged under >100 namespaces." +
                          "To prevent recursion, use the @NotLogged annotation from epilogue.");
    }
    if (!lst.contains(node)) {
      lst.add(node);
    }
  }

  static List<LoggingNode> getNodes(Object logged) {
    var nodes = registry.get(logged);
    if (nodes == null) {
      var newList = new ArrayList<LoggingNode>();
      registry.put(logged, newList);
      return newList;
    }
    return nodes;
  }

  /**
   * Logs a value to the respective key. If this method is called from a class implementing
   * LogLocal, it will be logged relative to the object's path.
   *
   * @param key The key to log the value under
   * @param value The value to log.
   */
  default <U extends Unit> Measure<U> log(String key, Measure<U> value) {
    if (Monologue.IS_DISABLED) return value;
    if (!Monologue.hasBeenSetup()) {
      Monologue.prematureLog(() -> log(key, value));
      return value;
    }
    String slashkey = "/" + key;
    for (LoggingNode node : getNodes(this)) {
      Monologue.config.backend.log(node.getPath() + slashkey, value);
    }
    return value;
  }

  /**
   * Logs a value to the respective key. If this method is called from a class implementing
   * LogLocal, it will be logged relative to the object's path.
   *
   * @param key The key to log the value under
   * @param value The value to log.
   */
  default String log(String key, String value) {
    if (Monologue.IS_DISABLED) return value;
    if (!Monologue.hasBeenSetup()) {
      Monologue.prematureLog(() -> log(key, value));
      return value;
    }
    String slashkey = "/" + key;
    for (LoggingNode node : getNodes(this)) {
      Monologue.config.backend.log(node.getPath() + slashkey, value);
    }
    return value;
  }

  /**
   * Logs a value to the respective key. If this method is called from a class implementing
   * LogLocal, it will be logged relative to the object's path.
   *
   * @param key The key to log the value under
   * @param value The value to log.
   */
  default boolean log(String key, boolean value) {
    if (Monologue.IS_DISABLED) return value;
    if (!Monologue.hasBeenSetup()) {
      Monologue.prematureLog(() -> log(key, value));
      return value;
    }
    String slashkey = "/" + key;
    for (LoggingNode node : getNodes(this)) {
      Monologue.config.backend.log(node.getPath() + slashkey, value);
    }
    return value;
  }

  /**
   * Logs a value to the respective key. If this method is called from a class implementing
   * LogLocal, it will be logged relative to the object's path.
   *
   * @param key The key to log the value under
   * @param value The value to log.
   */
  default int log(String key, int value) {
    if (Monologue.IS_DISABLED) return value;
    if (!Monologue.hasBeenSetup()) {
      Monologue.prematureLog(() -> log(key, value));
      return value;
    }
    String slashkey = "/" + key;
    for (LoggingNode node : getNodes(this)) {
      Monologue.config.backend.log(node.getPath() + slashkey, value);
    }
    return value;
  }

  /**
   * Logs a value to the respective key. If this method is called from a class implementing
   * LogLocal, it will be logged relative to the object's path.
   *
   * @param key The key to log the value under
   * @param value The value to log.
   */
  default long log(String key, long value) {
    if (Monologue.IS_DISABLED) return value;
    if (!Monologue.hasBeenSetup()) {
      Monologue.prematureLog(() -> log(key, value));
      return value;
    }
    String slashkey = "/" + key;
    for (LoggingNode node : getNodes(this)) {
      Monologue.config.backend.log(node.getPath() + slashkey, value);
    }
    return value;
  }

  /**
   * Logs a value to the respective key. If this method is called from a class implementing
   * LogLocal, it will be logged relative to the object's path.
   *
   * @param key The key to log the value under
   * @param value The value to log.
   */
  default float log(String key, float value) {
    if (Monologue.IS_DISABLED) return value;
    if (!Monologue.hasBeenSetup()) {
      Monologue.prematureLog(() -> log(key, value));
      return value;
    }
    String slashkey = "/" + key;
    for (LoggingNode node : getNodes(this)) {
      Monologue.config.backend.log(node.getPath() + slashkey, value);
    }
    return value;
  }

  /**
   * Logs a value to the respective key. If this method is called from a class implementing
   * LogLocal, it will be logged relative to the object's path.
   *
   * @param key The key to log the value under
   * @param value The value to log.
   */
  default double log(String key, double value) {
    if (Monologue.IS_DISABLED) return value;
    if (!Monologue.hasBeenSetup()) {
      Monologue.prematureLog(() -> log(key, value));
      return value;
    }
    String slashkey = "/" + key;
    for (LoggingNode node : getNodes(this)) {
      Monologue.config.backend.log(node.getPath() + slashkey, value);
    }
    return value;
  }

  /**
   * Logs a value to the respective key. If this method is called from a class implementing
   * LogLocal, it will be logged relative to the object's path.
   *
   * @param key The key to log the value under
   * @param value The value to log.
   */
  default boolean[] log(String key, boolean[] value) {
    if (Monologue.IS_DISABLED) return value;
    if (!Monologue.hasBeenSetup()) {
      Monologue.prematureLog(() -> log(key, value));
      return value;
    }
    String slashkey = "/" + key;
    for (LoggingNode node : getNodes(this)) {
      Monologue.config.backend.log(node.getPath() + slashkey, value);
    }
    return value;
  }

  /**
   * Logs a value to the respective key. If this method is called from a class implementing
   * LogLocal, it will be logged relative to the object's path.
   *
   * @param key The key to log the value under
   * @param value The value to log.
   */
  default int[] log(String key, int[] value) {
    if (Monologue.IS_DISABLED) return value;
    if (!Monologue.hasBeenSetup()) {
      Monologue.prematureLog(() -> log(key, value));
      return value;
    }
    String slashkey = "/" + key;
    for (LoggingNode node : getNodes(this)) {
      Monologue.config.backend.log(node.getPath() + slashkey, value);
    }
    return value;
  }

  /**
   * Logs a value to the respective key. If this method is called from a class implementing
   * LogLocal, it will be logged relative to the object's path.
   *
   * @param key The key to log the value under
   * @param value The value to log.
   */
  default long[] log(String key, long[] value) {
    if (Monologue.IS_DISABLED) return value;
    if (!Monologue.hasBeenSetup()) {
      Monologue.prematureLog(() -> log(key, value));
      return value;
    }
    String slashkey = "/" + key;
    for (LoggingNode node : getNodes(this)) {
      Monologue.config.backend.log(node.getPath() + slashkey, value);
    }
    return value;
  }

  /**
   * Logs a value to the respective key. If this method is called from a class implementing
   * LogLocal, it will be logged relative to the object's path.
   *
   * @param key The key to log the value under
   * @param value The value to log.
   */
  default float[] log(String key, float[] value) {
    if (Monologue.IS_DISABLED) return value;
    if (!Monologue.hasBeenSetup()) {
      Monologue.prematureLog(() -> log(key, value));
      return value;
    }
    String slashkey = "/" + key;
    for (LoggingNode node : getNodes(this)) {
      Monologue.config.backend.log(node.getPath() + slashkey, value);
    }
    return value;
  }

  /**
   * Logs a value to the respective key. If this method is called from a class implementing
   * LogLocal, it will be logged relative to the object's path.
   *
   * @param key The key to log the value under
   * @param value The value to log.
   */
  default double[] log(String key, double[] value) {
    if (Monologue.IS_DISABLED) return value;
    if (!Monologue.hasBeenSetup()) {
      Monologue.prematureLog(() -> log(key, value));
      return value;
    }
    String slashkey = "/" + key;
    for (LoggingNode node : getNodes(this)) {
      Monologue.config.backend.log(node.getPath() + slashkey, value);
    }
    return value;
  }

  /**
   * Logs a value to the respective key. If this method is called from a class implementing
   * LogLocal, it will be logged relative to the object's path.
   *
   * @param key The key to log the value under
   * @param value The value to log.
   */
  default String[] log(String key, String[] value) {
    if (Monologue.IS_DISABLED) return value;
    if (!Monologue.hasBeenSetup()) {
      Monologue.prematureLog(() -> log(key, value));
      return value;
    }
    String slashkey = "/" + key;
    for (LoggingNode node : getNodes(this)) {
      Monologue.config.backend.log(node.getPath() + slashkey, value);
    }
    return value;
  }

  /**
   * Logs a value to the respective key. If this method is called from a class implementing
   * LogLocal, it will be logged relative to the object's path.
   *
   * @param key The key to log the value under
   * @param value The value to log.
   */
  default <R extends StructSerializable> R log(String key, R value) {
    if (Monologue.IS_DISABLED) return value;
    if (!Monologue.hasBeenSetup()) {
      Monologue.prematureLog(() -> log(key, value));
      return value;
    }
    String slashkey = "/" + key;
    var clazz = (Class<R>) value.getClass();
    for (LoggingNode node : getNodes(this)) {
      Monologue.config.backend.log(
          node.getPath() + slashkey, value, StructFetcher.fetchStruct(clazz).orElseThrow());
    }
    return value;
  }

  /**
   * Logs a value to the respective key. If this method is called from a class implementing
   * LogLocal, it will be logged relative to the object's path.
   *
   * @param key The key to log the value under
   * @param value The value to log.
   */
  default <R extends StructSerializable> R[] log(String key, R[] value) {
    if (Monologue.IS_DISABLED) return value;
    if (!Monologue.hasBeenSetup()) {
      Monologue.prematureLog(() -> log(key, value));
      return value;
    }
    String slashkey = "/" + key;
    var clazz = (Class<R>) value.getClass().getComponentType();
    for (LoggingNode node : getNodes(this)) {
      Monologue.config.backend.log(
          node.getPath() + slashkey, value, StructFetcher.fetchStruct(clazz).orElseThrow());
    }
    return value;
  }

  /**
   * Logs a value to the respective key. If this method is called from a class implementing
   * LogLocal, it will be logged relative to the object's path.
   *
   * @param key The key to log the value under
   * @param value The value to log.
   */
  default <R> R log(String key, R value, Struct<R> struct) {
    if (Monologue.IS_DISABLED) return value;
    if (!Monologue.hasBeenSetup()) {
      Monologue.prematureLog(() -> log(key, value, struct));
      return value;
    }
    String slashkey = "/" + key;
    for (LoggingNode node : getNodes(this)) {
      Monologue.config.backend.log(node.getPath() + slashkey, value, struct);
    }
    return value;
  }

  /**
   * Logs a value to the respective key. If this method is called from a class implementing
   * LogLocal, it will be logged relative to the object's path.
   *
   * @param key The key to log the value under
   * @param value The value to log.
   */
  default <R> R[] log(String key, R[] value, Struct<R> struct) {
    if (Monologue.IS_DISABLED) return value;
    if (!Monologue.hasBeenSetup()) {
      Monologue.prematureLog(() -> log(key, value, struct));
      return value;
    }
    String slashkey = "/" + key;
    for (LoggingNode node : getNodes(this)) {
      Monologue.config.backend.log(node.getPath() + slashkey, value, struct);
    }
    return value;
  }
}
