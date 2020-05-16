package citizenDT.gui;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.Map;
import java.util.Optional;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import citizenDT.common.Data;
import citizenDT.common.LeafCategory;
import citizenDT.state.State;

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
	
	void updateInfo(final State state) {
		final Optional<Data> name = state.getData(LeafCategory.NAME);
		final Optional<Data> surname = state.getData(LeafCategory.SURNAME);
		final Map<LeafCategory,Data> info = state.getState();
		info.remove(LeafCategory.NAME);
		info.remove(LeafCategory.SURNAME);
		
		SwingUtilities.invokeLater(() -> {
			if(name.isPresent() && surname.isPresent()) {
				informationPanel.updateNameAndSurname(name.get().getValue(), surname.get().getValue());
			}
			informationPanel.updateInformations(info);
		});
	}
	
	
}
