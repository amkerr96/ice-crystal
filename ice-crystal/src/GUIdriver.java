import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class GUIdriver extends JFrame implements PropertyChangeListener {

	public static void main(String[] args) {
		new GUIdriver();
	}

	SimpleMapApp map;
	JPanel container;
	JPanel mapPanel;
	JPanel infoPanel;
	JPanel settingsPanel;
	JPanel filtersPanel;
	JScrollPane stateScroll;

	public GUIdriver() {
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(1400, 800);
		this.setLayout(new BorderLayout());
		this.setResizable(false);

		settingsPanel = new JPanel();
		mapPanel = new JPanel();
		infoPanel = new JPanel();

		// For testing
		/*
		 * settingsPanel.setBackground(Color.GREEN);
		 * mapPanel.setBackground(Color.RED);
		 * infoPanel.setBackground(Color.BLUE);
		 */

		settingsPanel.setPreferredSize(new Dimension(200, 800));
		mapPanel.setPreferredSize(new Dimension(900, 800));
		// infoPanel.setPreferredSize(new Dimension(190, 590));

		// Panel settings
		settingsPanel.setLayout(new GridLayout(3, 1));
		JPanel panel_1 = new JPanel();
		filtersPanel = new JPanel();
		JPanel panel_3 = new JPanel();

		panel_1.setLayout(new GridLayout(3, 1));

		filtersPanel.setLayout(new FlowLayout());
		stateScroll = new JScrollPane(filtersPanel);
		stateScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		stateScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

		panel_3.setLayout(new GridLayout(3, 1));

		settingsPanel.add(panel_1);
		settingsPanel.add(stateScroll);
		settingsPanel.add(panel_3);

		// File button
		JButton fileButton = new JButton("Select IB Report");
		fileButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String fileName = CSVParser.chooseFile();
				if (fileName != null) {
					CSVParser.parseFile(fileName);
					map.draw2();

					ArrayList<String> states = CSVParser.getStates();
					updateFilters(states);
					// System.out.println(states);
					// updateFilters(states);
				}
			}
		});
		panel_1.add(new JPanel());
		panel_1.add(fileButton);
		JPanel filtersP = new JPanel();
		filtersP.setLayout(new BorderLayout());
		filtersP.add(new JLabel("Filters: "), BorderLayout.SOUTH);
		panel_1.add(filtersP);

		// Filter all button
		JButton filterAll = new JButton("All");
		filterAll.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (filterAll.isSelected()) {
					filterAll.setSelected(false);
					applyFilter("all", false);
					for (Component c : filtersPanel.getComponents()) {
						if (c instanceof JButton) {
							((JButton) c).setSelected(false);
						}
					}
				} else {
					filterAll.setSelected(true);
					applyFilter("all", true);
					for (Component c : filtersPanel.getComponents()) {
						if (c instanceof JButton) {
							((JButton) c).setSelected(true);
						}
					}
				}
			}
		});
		JPanel filtersAllP = new JPanel();
		filtersAllP.setLayout(new BorderLayout());
		filtersAllP.add(filterAll, BorderLayout.NORTH);
		panel_3.add(filtersAllP);

		// Panel info
		infoPanel.setLayout(new FlowLayout()/*BoxLayout(infoPanel, BoxLayout.Y_AXIS)*/);
		//infoPanel.setPreferredSize(new Dimension(300,1000));
		infoPanel.add(new JLabel("   Locations"));

		// Make info scrollable
		JScrollPane scrollPane = new JScrollPane(infoPanel);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setPreferredSize(new Dimension(300, 800));

		// Create
		this.add(settingsPanel, BorderLayout.WEST);
		this.add(mapPanel, BorderLayout.CENTER);
		this.add(scrollPane, BorderLayout.EAST);
		this.pack();
		this.setVisible(true);

		// Panel map
		map = new SimpleMapApp();
		map.addPropertyChangeListener(this);
		mapPanel.add(map);
		map.init();
	}

	public void updateInfoPanel(ArrayList<ImageMarker> selected) {
		infoPanel.removeAll();
		infoPanel.add(new JLabel("   Locations"));
		for (ImageMarker im : selected) {
			// TODO: Add functionality
			infoPanel.add(new JButton(im.getName()));
		}
		infoPanel.setPreferredSize(new Dimension(300, 20 * selected.size()));
		infoPanel.revalidate();
		infoPanel.repaint();
		/*
		 * System.out.print("Selected: "); for(ImageMarker i: selected) {
		 * System.out.print(i.getName() + ", "); } System.out.println();
		 */
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		ArrayList<ImageMarker> sel = map.getSelected();
		Collections.sort(sel);
		updateInfoPanel(sel);
	}

	protected ImageIcon createImageIcon(String path, String description) {
		java.net.URL imgURL = getClass().getResource(path);
		if (imgURL != null) {
			return new ImageIcon(imgURL, description);
		} else {
			System.err.println("Couldn't find file: " + path);
			return null;
		}
	}

	public void updateFilters(ArrayList<String> states) {
		int len = states.size();

		stateScroll.setPreferredSize(new Dimension(200, (len / 2) * 25));
		filtersPanel.setPreferredSize(new Dimension(200, (len / 2) * 25));

		java.util.Collections.sort(states);
		for (String s : states) {
			JButton b = new JButton(s);
			// Add action
			b.setPreferredSize(new Dimension(50, 25));
			b.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (b.isSelected()) {
						b.setSelected(false);
						applyFilter(s, false);
					} else {
						b.setSelected(true);
						applyFilter(s, true);
					}
				}
			});
			b.setSelected(false);
			filtersPanel.add(b);
		}

		stateScroll.revalidate();
		stateScroll.repaint();
	}

	public void applyFilter(String state, boolean on) {
		map.selectGroup(state, on);
	}
}