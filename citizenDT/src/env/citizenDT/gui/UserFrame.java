package citizenDT.gui;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import citizenDT.state.LeafCategory;

final class UserFrame extends JFrame {
	
	private static final long serialVersionUID = 1L;
	private static final int WIDTH = 650;
	private static final int HEIGHT = 400;
	private static final int MARGIN = 10;
	private static final String TITLE = "Citizen Digital Twin";
	
	//private final UserGUI artifact;
	private final InformationPanel informationPanel;

	UserFrame(final UserGUI artifact) {
		//this.artifact = artifact;
        
        final JPanel mainPanel = new JPanel();
        mainPanel.setBorder(new EmptyBorder(MARGIN, MARGIN, MARGIN, MARGIN));
        mainPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
        getContentPane().add(mainPanel);
        
        this.informationPanel = new InformationPanel(WIDTH - MARGIN, HEIGHT - MARGIN);
        mainPanel.add(informationPanel);
		
        this.setSize(new Dimension(WIDTH, HEIGHT));
        this.setTitle(TITLE);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLocationRelativeTo(null);
		this.pack();
		this.setResizable(false);
		this.setVisible(true);
	}
	
	void updateInfo(final Map<LeafCategory, String> info) {
		final Map<LeafCategory, String> infoCopy = new HashMap<>(info);
		final String name = infoCopy.getOrDefault(LeafCategory.NAME, "");
		final String surname = infoCopy.getOrDefault(LeafCategory.SURNAME, "");
		infoCopy.remove(LeafCategory.NAME);
		infoCopy.remove(LeafCategory.SURNAME);
		
		SwingUtilities.invokeLater(() -> {
			if(!name.isBlank() && !surname.isBlank()) {
				informationPanel.updateNameAndSurname(name, surname);
			}
			informationPanel.updateInformations(infoCopy);
		});
	}
	
	
}
