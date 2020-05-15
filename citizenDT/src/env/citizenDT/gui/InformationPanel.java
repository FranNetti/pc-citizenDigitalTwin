package citizenDT.gui;

import javax.swing.*;

import citizenDT.state.LeafCategory;

import java.awt.*;
import java.util.Map;
import java.util.Objects;

class InformationPanel extends JPanel {

	private static final long serialVersionUID = 1L;
    private static final int SIZE = 20;

    private final JLabel userNameLabel = createLabel();
    private final JPanel informationArea;
    private final int length;
    private final int height;

    InformationPanel(final int length, final int height) {
    	this.length = length;
    	this.height = height;
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(length, height));
        
        add(createNameArea(), BorderLayout.NORTH);

        informationArea = createInfoArea();
        add(informationArea, BorderLayout.CENTER);
    }

    void updateInformations(final Map<LeafCategory, String> info) {
        Objects.requireNonNull(info);
    	informationArea.removeAll();
    	final Dimension d = new Dimension(length, 50);
    	info.entrySet()
    		.stream()
    		.map(entry -> new SingleInfoShower(entry.getKey(), entry.getValue(), d))
    		.forEach(informationArea::add);
    }
    
    void updateNameAndSurname(final String name, final String surname) {
    	Objects.requireNonNull(name);
    	Objects.requireNonNull(surname);
		userNameLabel.setText(name + " " + surname);
    }
    
    private JPanel createNameArea() {
    	JPanel nameArea = new JPanel();
        nameArea.setLayout(new BoxLayout(nameArea, BoxLayout.LINE_AXIS));
        userNameLabel.setOpaque(true);
        userNameLabel.setFont(new Font(userNameLabel.getFont().getName(), Font.PLAIN, SIZE));
        nameArea.add(Box.createHorizontalGlue());
        nameArea.add(userNameLabel);
        nameArea.add(Box.createHorizontalGlue());
        return nameArea;
    }

    private JPanel createInfoArea() {
        final JPanel infoArea = new JPanel();
        infoArea.setLayout(new BoxLayout(infoArea, BoxLayout.PAGE_AXIS));
        infoArea.add(Box.createVerticalGlue());
        return infoArea;
    }
    
    private final class SingleInfoShower extends JPanel {
    	
		private static final long serialVersionUID = 1L;
		private static final int ID_LENGTH = 150;

		SingleInfoShower(final LeafCategory id, final String info, final Dimension rowDimension) {
			
    		setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
    		setPreferredSize(rowDimension);
    		setSize(rowDimension);
    		setMinimumSize(rowDimension);
    		setMaximumSize(rowDimension);
			
			add(createIdArea(id.getDisplayName(), rowDimension.height));
			
			final JSeparator separator = new JSeparator(SwingConstants.VERTICAL);
			separator.setBackground(Color.getHSBColor(0,0,62));
			add(separator);
			
			final JLabel infoLabel = createLabel(info);
			add(infoLabel);
			add(Box.createHorizontalGlue());
			//add(createSingleInfoArea(info));
		}
    	
    	private JPanel createIdArea(final String id, final int rowHeight) {
    		JPanel idArea = new JPanel();
            idArea.setLayout(new BoxLayout(idArea, BoxLayout.LINE_AXIS));
            final Dimension d = new Dimension(ID_LENGTH, rowHeight);
            idArea.setSize(d);
            idArea.setPreferredSize(d);
            idArea.setMaximumSize(d);
            idArea.setMinimumSize(d);
            final JLabel idLabel = createLabel(id);
            idArea.add(idLabel);
            return idArea;
    	}
    	
    	private JPanel createSingleInfoArea(final String info) {
    		JPanel infoArea = new JPanel();
            final JLabel infoLabel = createLabel(info);
            infoArea.add(infoLabel);
            return infoArea;
    	}
    	
    }
    
    private static JLabel createLabel(final String text) {
    	final JLabel label = new JLabel(text);
    	label.setOpaque(true);
    	label.setFont(new Font(label.getFont().getName(), Font.PLAIN, SIZE));
    	return label;
    }
    
    private static JLabel createLabel() {
    	return createLabel("");
    }

}

