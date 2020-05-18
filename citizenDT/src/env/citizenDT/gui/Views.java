package citizenDT.gui;

import java.awt.Font;

import javax.swing.JLabel;

final class Views {

	static JLabel createLabel(final String text, final int size) {
    	final JLabel label = new JLabel(text);
    	label.setOpaque(true);
    	label.setFont(new Font(label.getFont().getName(), Font.PLAIN, size));
    	return label;
    }
    
    static JLabel createLabel(final int size) {
    	return createLabel("", size);
    }
	
}
