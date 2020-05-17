package citizenDT.device.sensor;

import citizenDT.common.LeafCategory;

public interface Sensor<X> {
	X getData();
	LeafCategory getLeafCategory();
}
