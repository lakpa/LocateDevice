package support;

import java.io.Serializable;

/**
 * @author lakpa
 *
 * The Class BluetoothDeviceModel.
 */
public class BluetoothDeviceModel implements Serializable {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The signal strength. */
	private String signalStrength;
	
	/** The mac address. */
	private String macAddress;
	
	/**
	 * Instantiates a new bluetooth device model.
	 *
	 * @param signalStrength the signal strength
	 * @param macAddress the mac address
	 */
	public BluetoothDeviceModel(String signalStrength, String macAddress) {
		this.signalStrength = signalStrength;
		this.macAddress = macAddress;
	}

	/**
	 * Gets the signal strength.
	 *
	 * @return the signal strength
	 */
	public String getSignalStrength() {
		return signalStrength;
	}

	/**
	 * Sets the signal strength.
	 *
	 * @param signalStrength the new signal strength
	 */
	public void setSignalStrength(String signalStrength) {
		this.signalStrength = signalStrength;
	}

	/**
	 * Gets the mac address.
	 *
	 * @return the mac address
	 */
	public String getMacAddress() {
		return macAddress;
	}

	/**
	 * Sets the mac address.
	 *
	 * @param macAddress the new mac address
	 */
	public void setMacAddress(String macAddress) {
		this.macAddress = macAddress;
	}

}
