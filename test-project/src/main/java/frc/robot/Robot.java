package frc.robot;

import edu.wpi.first.epilogue.EpilogueConfiguration;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import monologue.LogLocal;
import monologue.Monologue;

public class Robot extends TimedRobot implements LogLocal {
	static class Hello extends SubsystemBase implements LogLocal {
		double x;
		String key;
		
		Hello(double x, String key) {
			this.x = x;
			this.key = key;
		}
		
		@Override
		public void periodic() {
			log(key, x);
			x++;
		}
		
		@Override
		public String toString() {
			return "Test Subsystem";
		}
	}
	
	private final Hello h = new Hello(2.0, "hi");
	private final Hello j = h;
	
	public Robot() {
		Monologue.setup(this, new EpilogueConfiguration());
	}
}
