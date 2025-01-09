package monologue;

import static monologue.Monologue.GlobalLog;

import edu.wpi.first.hal.HAL;
import edu.wpi.first.hal.HALUtil;
import edu.wpi.first.hal.PowerJNI;
import edu.wpi.first.hal.can.CANJNI;
import edu.wpi.first.hal.can.CANStatus;
import edu.wpi.first.networktables.GenericPublisher;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.util.datalog.StringLogEntry;
import edu.wpi.first.wpilibj.DataLogManager;
import edu.wpi.first.wpilibj.PowerDistribution;
import edu.wpi.first.wpilibj.RobotController;
import edu.wpi.first.wpilibj.TimedRobot;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/** Logs "extra" information. */
public class ExtrasLogger {
  static final double RADIO_LOG_PERIOD_SECONDS = 5.81;

  private final CANStatus status = new CANStatus();
  PowerDistribution pdh;
  private final RadioLogUtil radioLogUtil = new RadioLogUtil();
  private final GenericPublisher ntRadioEntry =
      NetworkTableInstance.getDefault()
          .getTopic("GlobalLog/RadioStatus/StatusJSON")
          .genericPublish("json");
  private final StringLogEntry dataLogRadioEntry =
      new StringLogEntry(DataLogManager.getLog(), "GlobalLog/RadioStatus/StatusJSON", "", "json");

  static void start(TimedRobot robot, PowerDistribution pdh) {
    var logger = new ExtrasLogger();
    logger.pdh = pdh;
    robot.addPeriodic(
        () -> {
          logger.logSystem();
          logger.logCan();
          logger.logPdh();
        },
        0.02);
    robot.addPeriodic(logger::logRadio, RADIO_LOG_PERIOD_SECONDS);
  }

  private void logSystem() {
    GlobalLog.log("SystemStats/SerialNumber", HALUtil.getSerialNumber());
    GlobalLog.log("SystemStats/Comments", HALUtil.getComments());
    GlobalLog.log("SystemStats/SystemActive", HAL.getSystemActive());
    GlobalLog.log("SystemStats/BrownedOut", HAL.getBrownedOut());
    GlobalLog.log("SystemStats/RSLState", HAL.getRSLState());
    GlobalLog.log("SystemStats/SystemTimeValid", HAL.getSystemTimeValid());
    GlobalLog.log("SystemStats/BrownoutVoltage", PowerJNI.getBrownoutVoltage());
    GlobalLog.log("SystemStats/CPUTempCelcius", PowerJNI.getCPUTemp());

    GlobalLog.log("SystemStats/BatteryVoltage", PowerJNI.getVinVoltage());
    GlobalLog.log("SystemStats/BatteryCurrent", PowerJNI.getVinCurrent());

    GlobalLog.log("SystemStats/3v3Rail/Voltage", PowerJNI.getUserVoltage3V3());
    GlobalLog.log("SystemStats/3v3Rail/Current", PowerJNI.getUserCurrent3V3());
    GlobalLog.log("SystemStats/3v3Rail/Active", PowerJNI.getUserActive3V3());
    GlobalLog.log("SystemStats/3v3Rail/CurrentFaults", PowerJNI.getUserCurrentFaults3V3());

    GlobalLog.log("SystemStats/5vRail/Voltage", PowerJNI.getUserVoltage5V());
    GlobalLog.log("SystemStats/5vRail/Current", PowerJNI.getUserCurrent5V());
    GlobalLog.log("SystemStats/5vRail/Active", PowerJNI.getUserActive5V());
    GlobalLog.log("SystemStats/5vRail/CurrentFaults", PowerJNI.getUserCurrentFaults5V());

    GlobalLog.log("SystemStats/6vRail/Voltage", PowerJNI.getUserVoltage6V());
    GlobalLog.log("SystemStats/6vRail/Current", PowerJNI.getUserCurrent6V());
    GlobalLog.log("SystemStats/6vRail/Active", PowerJNI.getUserActive6V());
    GlobalLog.log("SystemStats/6vRail/CurrentFaults", PowerJNI.getUserCurrentFaults6V());
  }

  private void logCan() {
    CANJNI.getCANStatus(status);
    GlobalLog.log("SystemStats/CANBus/Utilization", status.percentBusUtilization);
    GlobalLog.log("SystemStats/CANBus/OffCount", status.busOffCount);
    GlobalLog.log("SystemStats/CANBus/TxFullCount", status.txFullCount);
    GlobalLog.log("SystemStats/CANBus/ReceiveErrorCount", status.receiveErrorCount);
    GlobalLog.log("SystemStats/CANBus/TransmitErrorCount", status.transmitErrorCount);
  }

  private void logPdh() {
    if (pdh == null) {
      return;
    }

    GlobalLog.log("SystemStats/PowerDistribution/Temperature", pdh.getTemperature());
    GlobalLog.log("SystemStats/PowerDistribution/Voltage", pdh.getVoltage());
    GlobalLog.log("SystemStats/PowerDistribution/ChannelCurrent", pdh.getAllCurrents());
    GlobalLog.log("SystemStats/PowerDistribution/TotalCurrent", pdh.getTotalCurrent());
    GlobalLog.log("SystemStats/PowerDistribution/TotalPower", pdh.getTotalPower());
    GlobalLog.log("SystemStats/PowerDistribution/TotalEnergy", pdh.getTotalEnergy());
    GlobalLog.log("SystemStats/PowerDistribution/ChannelCount", pdh.getNumChannels());
  }

  private void logRadio() {
    radioLogUtil.refresh();
    GlobalLog.log("RadioStatus/Connected", radioLogUtil.radioLogResult.isConnected);
    ntRadioEntry.setString(radioLogUtil.radioLogResult.statusJson);
    dataLogRadioEntry.append(radioLogUtil.radioLogResult.statusJson);
  }

  private static class RadioLogResult {
    public String statusJson = "";
    public boolean isConnected = false;
  }

  private static class RadioLogUtil {
    private static final Duration REQUEST_TIMEOUT_DURATION = Duration.ofSeconds(1);

    private static URI getRadioStatusEndpoint() {
      var teamNumber = RobotController.getTeamNumber();

      return URI.create("http://10." + teamNumber / 100 + "." + teamNumber % 100 + ".1/status");
    }

    public final RadioLogResult radioLogResult = new RadioLogResult();

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final HttpRequest request =
        HttpRequest.newBuilder()
            .uri(getRadioStatusEndpoint())
            .timeout(REQUEST_TIMEOUT_DURATION)
            .build();

    /** Get the latest radio status and update the {@link this#radioLogResult} object. */
    public void refresh() {
      try {
        var response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        radioLogResult.isConnected = true;
        radioLogResult.statusJson = response.body();
      } catch (Exception e) {
        radioLogResult.isConnected = false;
        radioLogResult.statusJson = "";
      }
    }
  }
}
