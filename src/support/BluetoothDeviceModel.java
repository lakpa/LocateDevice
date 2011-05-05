package support;

import java.io.Serializable;

public class BluetoothDeviceModel implements Serializable {
	private String signalStrength;
	private String macAddress;
	
	public BluetoothDeviceModel(String signalStrength, String macAddress) {
		this.signalStrength = signalStrength;
		this.macAddress = macAddress;
	}

	public String getSignalStrength() {
		return signalStrength;
	}

	public void setSignalStrength(String signalStrength) {
		this.signalStrength = signalStrength;
	}

	public String getMacAddress() {
		return macAddress;
	}

	public void setMacAddress(String macAddress) {
		this.macAddress = macAddress;
	}

}
