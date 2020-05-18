package citizenDT.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import citizenDT.device.type.BluetoothDevice;
import citizenDT.device.type.Device;

import java.awt.*;
import java.awt.event.ActionListener;

class DevicePanel extends JPanel /*implements ActionListener*/ {

	private static final long serialVersionUID = 1L;
	
	private static final int BORDER_TOP = 0;
    private static final int BORDER_LEFT = 20;
    private static final int BORDER_RIGHT = 0;
    private static final int BORDER_BOTTOM = BORDER_TOP;
    private static final int BUTTON_WIDTH = 150;
    private static final int BUTTON_HEIGHT = 30;
    private static final int SEPARATOR_WIDTH = 0;
    private static final int SEPARATOR_HEIGHT = 10;
    private static final int YOUR_DEVICES_HEIGHT = 20;

    private static final String BTN_ADD_DEVICE_NAME = "AGGIUNGI";
    private static final String BTN_REMOVE_DEVICE_NAME = "RIMUOVI";
    private static final String YOUR_DEVICES_TEXT = "Dispositivi";
    private static final String DEVICE_TAG = "braccialetto_";
    private static final String DEVICE_MODEL = "Samsung";

    private final JButton addDeviceBtn;
    private final JButton removeDeviceBtn;
    private final JPanel devicesPanel;

    private final UserFrame controller;
    private int deviceCount = 0;

    DevicePanel(final UserFrame controller, final int width, final int height) {
        this.controller = controller;
        
    	setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        final Dimension dimension = new Dimension(width, height);
        setPreferredSize(dimension);
        setMaximumSize(dimension);
        setMinimumSize(dimension);
        setSize(dimension);
        setBorder(new EmptyBorder(BORDER_TOP, BORDER_LEFT, BORDER_BOTTOM, BORDER_RIGHT));

        final Dimension buttonSize = new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT);
        final Dimension separatorSize = new Dimension(SEPARATOR_WIDTH, SEPARATOR_HEIGHT);

        addDeviceBtn = createButton(BTN_ADD_DEVICE_NAME, buttonSize, true, ev -> addDevice());
        add(addDeviceBtn);
        add(Box.createRigidArea(separatorSize));

        removeDeviceBtn = createButton(BTN_REMOVE_DEVICE_NAME, buttonSize, false, ev -> removeDevice());
        add(removeDeviceBtn);
        add(Box.createRigidArea(separatorSize));

        final JLabel label = Views.createLabel(YOUR_DEVICES_TEXT, YOUR_DEVICES_HEIGHT);
        add(label);
        
        devicesPanel = new JPanel();
        devicesPanel.setLayout(new BoxLayout(devicesPanel, BoxLayout.Y_AXIS));
        add(devicesPanel);

    }

    void addDevice() {
    	final String deviceName = DEVICE_TAG + (deviceCount + 1);
    	final Device newDevice = new BluetoothDevice(deviceName);
    	controller.addDevice(newDevice, DEVICE_MODEL);
    }
    
    void deviceAdded() {
    	final String deviceName = DEVICE_TAG + (++deviceCount);
    	addNewDeviceLabel(deviceName);
		removeDeviceBtn.setEnabled(true);
		refresh();
    }
    
    void removeDevice() {
    	if(deviceCount > 0) {
    		final String deviceName = DEVICE_TAG + (deviceCount - 1);
        	final Device oldDevice = new BluetoothDevice(deviceName);
        	controller.removeDevice(oldDevice);
    	}
    }
    
    void deviceRemoved() {
		deviceCount--;
		devicesPanel.remove(deviceCount);
		if(deviceCount == 0) {
			removeDeviceBtn.setEnabled(false);
		}
		refresh();
    }
    
    private void refresh() {
    	devicesPanel.validate();
    	devicesPanel.repaint();
    	validate();
    	repaint();
    }
    
    private void addNewDeviceLabel(final String deviceName) {
    	final JPanel p = new JPanel();
    	p.add(Views.createLabel(deviceName, 15));
    	devicesPanel.add(p, deviceCount - 1);
    	devicesPanel.add(Box.createRigidArea(new Dimension(SEPARATOR_WIDTH, SEPARATOR_HEIGHT)));
    }

    private JButton createButton(final String text, final Dimension buttonSize, final boolean enabled, final ActionListener ev){
        JButton btn = new JButton(text);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMaximumSize(buttonSize);
        btn.setEnabled(enabled);
        btn.addActionListener(ev);
        return btn;
    }
}
