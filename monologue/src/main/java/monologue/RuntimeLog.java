package monologue;

import edu.wpi.first.hal.DriverStationJNI;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.networktables.StringPublisher;

class RuntimeLog {
  private static final StringPublisher entry;

  static {
    // we need to make sure we never log network tables through the implicit wpilib logger
    entry = NetworkTableInstance.getDefault().getStringTopic("/MonologueSetup").publish();
    info("Monologue Setup Logger initialized");
  }

  static void info(String message) {
    entry.set("[Monologue] " + message);
  }

  static void warn(String warning) {
    String message = "[Monologue] (WARNING) " + warning;
    entry.set(message);
    DriverStationJNI.sendError(false, 1, false, message, "", "", true);
  }
}
