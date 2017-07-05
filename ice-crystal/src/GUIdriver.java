import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.knowm.xchart.PieChart;
import org.knowm.xchart.PieChartBuilder;
import org.knowm.xchart.XChartPanel;
import org.knowm.xchart.style.PieStyler.AnnotationType;
import org.knowm.xchart.style.Styler.ChartTheme;
import org.knowm.xchart.style.Styler.LegendPosition;

/**
 * @author aarkerr
 * @author rblowers
 */
public class GUIdriver extends JFrame implements PropertyChangeListener {

	/** Unfolding Maps App instance */
	private SimpleMapApp map;
	/** Panel to hold the map */
	private JPanel mapPanel;
	/** Panel on the left displaying filters and selectors */
	private JPanel settingsPanel;
	/** Top portion of the settings panel */
	private JPanel settingsPanelTop;
	/** Panel holding regional selectors */
	private JPanel regionalSelectorsPanel;
	/** Scroll pane for the regional selectors */
	private JScrollPane regionalSelectorScroll;
	/** Panel on the right displaying information about selected markers*/
	private JPanel infoPanel;
	/** Panel on the right displaying which markers are selected */
	private JPanel selectedPanel;
	/** Label displaying how much spend we're filtering by */
	private JLabel sliderLabel;
	/** Panel for displaying the pie chart */
	private JPanel pieChartPanel;
	/** Top of the top of the settings panel (sorry) */
	private JPanel panelTopUpper;
	/** Bottom of the settings panel */
	private JPanel settingsPanelBottom;
	/** Search box for filter by product IDs*/
	private JTextField searchBox;
	/** Panel for list of Product ID filters */
	private JPanel productIdPanel;
	/** List of location filters */
	private ArrayList<String> locationFilters;
	/** Has a file been selected? */
	public static boolean fileSelected;
	/** List of architectures */
	private ArrayList<String> architectures;
	/** List of product IDs */
	private ArrayList<String> productIds;
	/** List of product ID labels */
	private ArrayList<idLabel> idLabels;
	/** List of product IDs on which we're filtering */
	private ArrayList<String> productIdFilter;

	/**
	 * Lehgo
	 */
	public static void main(String[] args) {
		new GUIdriver();
	}

	/**
	 * Set everything up
	 */
	public GUIdriver() {
		// Instantiate ArrayLists
		locationFilters = new ArrayList<String>();
		idLabels = new ArrayList<idLabel>();
		productIdFilter = new ArrayList<String>();

		// Set up main frame
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(1400, 800);
		this.setLayout(new BorderLayout());
		this.setResizable(false);

		// Set up settings panel (left)
		settingsPanel = new JPanel();
		settingsPanel.setPreferredSize(new Dimension(200, 800));
		settingsPanel.setLayout(new GridLayout(3, 1));

		// Top of settings panel
		settingsPanelTop = new JPanel();
		settingsPanelTop.setLayout(new GridLayout(2, 1));
		panelTopUpper = new JPanel();
		panelTopUpper.setLayout(new GridLayout(3, 1));
		settingsPanelTop.add(panelTopUpper);
		productIdPanel = new JPanel();
		settingsPanel.add(settingsPanelTop);

		// Middle of settings panel
		regionalSelectorsPanel = new JPanel();
		JPanel filtersContainer = new JPanel();
		filtersContainer.setLayout(new BorderLayout());
		regionalSelectorsPanel.setLayout(new FlowLayout());
		regionalSelectorScroll = new JScrollPane(regionalSelectorsPanel);
		regionalSelectorScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		regionalSelectorScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		// Filter all button
		JButton filterAll = new JButton("All");
		filterAll.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (filterAll.isSelected()) {
					filterAll.setSelected(false);
					map.removeAll();
					for (Component c : regionalSelectorsPanel.getComponents()) {
						if (c instanceof JButton) {
							((JButton) c).setSelected(false);
						}
					}
				} else {
					filterAll.setSelected(true);
					map.addAll();
					for (Component c : regionalSelectorsPanel.getComponents()) {
						if (c instanceof JButton) {
							((JButton) c).setSelected(true);
						}
					}
				}
			}
		});
		filtersContainer.add(new JLabel("Select by location: "), BorderLayout.NORTH);
		filtersContainer.add(regionalSelectorScroll, BorderLayout.CENTER);
		filtersContainer.add(filterAll, BorderLayout.SOUTH);

		// Bottom of settings panel
		settingsPanelBottom = new JPanel();
		settingsPanelBottom.setLayout(new GridLayout(6, 1));

		// Set up map panel (middle)
		mapPanel = new JPanel();
		mapPanel.setPreferredSize(new Dimension(900, 800));

		// Set up info panel (right)
		infoPanel = new JPanel();
		infoPanel.setLayout(new GridLayout(2, 1));
		selectedPanel = new JPanel();
		selectedPanel.setLayout(new FlowLayout());
		selectedPanel.add(new JLabel("Locations:"));
		// Make info scrollable
		JScrollPane infoScrollPane = new JScrollPane(selectedPanel);
		infoScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		infoScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		infoScrollPane.setPreferredSize(new Dimension(300, 400));
		// Pie chart (bottom right)
		pieChartPanel = new JPanel();
		infoPanel.add(infoScrollPane);
		infoPanel.add(pieChartPanel);

		// File button
		JButton fileButton = new JButton("Select IB Report");
		fileButton.addActionListener(new ActionListener() {
			// Select a file
			public void actionPerformed(ActionEvent e) {
				fileSelected = true;
				String fileName = CSVParser.chooseFile();
				if (fileName != null) {
					// Parse file and draw map
					CSVParser.parseFile(fileName);
					architectures = CSVParser.architectures;
					map.drawMap();

					// Add filters based on map
					ArrayList<String> states = CSVParser.getStates();
					updateRegionalSelectors(states);

					// Add slider based on spend
					addSlider();

					// Add filtering by product IDs
					addProductIDFilter();

					// Add filtering by architecture
					addArchitectureFilter();

					// Add LDOS button
					addLDOSButton();

					settingsPanel.add(filtersContainer);
					settingsPanel.add(settingsPanelBottom);
				}
			}
		});
		panelTopUpper.add(fileButton);

		// Put it all together
		this.add(settingsPanel, BorderLayout.WEST);
		this.add(mapPanel, BorderLayout.CENTER);
		this.add(infoPanel, BorderLayout.EAST);
		this.pack();
		this.setVisible(true);

		// Initialize map
		map = new SimpleMapApp();
		map.addPropertyChangeListener(this);
		mapPanel.add(map);
		map.init();
	}

	/**
	 * Update the list of selected locations that appears on the right-hand
	 * panel.
	 * 
	 * @param selected
	 *            The list of selected locations
	 */
	public void updateSelectedPanel(ArrayList<LocationMarker> selected) {
		selectedPanel.removeAll();
		selectedPanel.add(new JLabel("   Locations"));
		for (LocationMarker im : selected) {
			JButton button = new JButton();
			button.addActionListener(new ActionListener() {
				// Display pie chart
				public void actionPerformed(ActionEvent e) {
					HashMap<String, Double> archs = CSVParser.archLocations.get(im.getName().toUpperCase());
					System.out.println("\n------" + im.getName() + "------");
					for (String arch : archs.keySet()) {
						System.out.println(arch + ": " + archs.get(arch));
					}
					drawPieChart(im);
				}
			});
			try {
				// Button formatting
				button.setLayout(new BorderLayout());
				JLabel label1 = new JLabel(im.getName());
				DecimalFormat formatter = new DecimalFormat("#,###.00");
				JLabel label2 = new JLabel("$" + String.valueOf(
						formatter.format(CSVParser.round(CSVParser.locations.get(im.getName().toUpperCase()), 2))));
				button.add(BorderLayout.NORTH, label1);
				button.add(BorderLayout.SOUTH, label2);
				selectedPanel.add(button);
			} catch (NullPointerException e) {
				selectedPanel.add(new JButton(im.getName()));
			}
		}
		selectedPanel.setPreferredSize(new Dimension(300, 20 * selected.size()));
		selectedPanel.revalidate();
		selectedPanel.repaint();
	}

	/**
	 * Fires upon change of selected markers within SimpleMapApp
	 * 
	 * @param evt
	 *            event
	 */
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		// Find out which markers are being displayed
		ArrayList<LocationMarker> sel = map.getDrawn();
		// Sort them and update selected panel
		Collections.sort(sel);
		updateSelectedPanel(sel);
	}

	/**
	 * Load an image icon
	 * 
	 * @param path
	 *            icon filepath
	 * @param description
	 *            description of icon
	 * @return
	 */
	protected ImageIcon createImageIcon(String path, String description) {
		java.net.URL imgURL = getClass().getResource(path);
		if (imgURL != null) {
			return new ImageIcon(imgURL, description);
		} else {
			System.err.println("Couldn't find file: " + path);
			return null;
		}
	}

	/**
	 * Update the "state" or regional selector panel.
	 * 
	 * @param states
	 *            The list of states present in the locations
	 */
	public void updateRegionalSelectors(ArrayList<String> states) {
		int len = states.size();

		regionalSelectorScroll.setPreferredSize(new Dimension(200, (len / 2) * 25));
		regionalSelectorsPanel.setPreferredSize(new Dimension(200, (len / 2) * 25));

		java.util.Collections.sort(states);
		for (String s : states) {
			JButton b = new JButton(s);
			b.setPreferredSize(new Dimension(50, 25));
			// Add or remove all based on states
			b.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (b.isSelected()) {
						locationFilters.remove(s);
						b.setSelected(false);
						map.removeByLocation(s);
					} else {
						locationFilters.add(s);
						b.setSelected(true);
						map.addByLocation(s);
					}
				}
			});
			b.setSelected(false);
			regionalSelectorsPanel.add(b);
		}

		regionalSelectorScroll.revalidate();
		regionalSelectorScroll.repaint();
	}

	/**
	 * Draw pie chart for a location.
	 * @param loc The selected location
	 */
	public void drawPieChart(LocationMarker loc) {
		try {
			pieChartPanel.remove(pieChartPanel.getComponent(0));

		} catch (ArrayIndexOutOfBoundsException e) {
		}

		// Create Chart
		PieChart chart = new PieChartBuilder().width(300).height(400).title("Architecture % in " + loc.getName())
				.theme(ChartTheme.GGPlot2).build();

		// Customize Chart
		chart.getStyler().setLegendVisible(true);
		chart.getStyler().setAnnotationType(AnnotationType.Percentage);
		chart.getStyler().setDrawAllAnnotations(true);
		// chart.getStyler().setAnnotationDistance(1.15);
		chart.getStyler().setPlotContentSize(.7);
		chart.getStyler().setStartAngleInDegrees(90);
		chart.getStyler().setLegendFont(new Font("TimesRoman", Font.PLAIN, 10));
		chart.getStyler().setLegendPadding(1);
		chart.getStyler().setLegendPosition(LegendPosition.InsideSE);

		HashMap<String, Double> archs = CSVParser.archLocations.get(loc.getName().toUpperCase());

		System.out.println("\n------" + loc.getName() + "------");
		if (archs.keySet().size() > 4) {
			chart.getStyler().setAnnotationDistance(1.15);
		}

		for (String arch : archs.keySet()) {
			System.out.println(arch + ": " + archs.get(arch));
			chart.addSeries(arch, archs.get(arch));
		}

		JPanel frame = new JPanel();
		frame.setLayout(new BorderLayout());

		// chart
		JPanel chartPanel = new XChartPanel<PieChart>(chart);
		frame.add(chartPanel, BorderLayout.CENTER);

		// label
		JLabel label = new JLabel("Pie Chart", SwingConstants.CENTER);
		frame.add(label, BorderLayout.SOUTH);

		// Display the window.
		pieChartPanel.add(frame);
		pieChartPanel.revalidate();
		pieChartPanel.repaint();
	}
	
	/**
	 * Update the list of product IDs based on the search bar
	 * @param s Text in the search bar
	 */
	public void updateProductIdPanel(String s) {
		int i = 0;
		// Remove all panels
		productIdPanel.removeAll();

		// Redraw panels that start with text written in search bar
		for (idLabel idl : idLabels) {
			String p = idl.getText();
			if (p.toLowerCase().startsWith(s.toLowerCase())) {
				productIdPanel.add(idl);
				i++;
			}
		}

		productIdPanel.setPreferredSize(new Dimension(100, 25 * i));
		productIdPanel.revalidate();
		productIdPanel.repaint();
	}

	/**
	 * Draw the slider to filter by spend.
	 */
	public void addSlider() {
		sliderLabel = new JLabel("Filter by spend:");
		// need to work out math behind this
		int min = (int) Math.floor(map.getMinSpend());
		int max = 40000;// (int) Math.ceil(map.getMaxSpend());
		
		JSlider spendSlider = new JSlider(0, max);
		spendSlider.setMajorTickSpacing(max / 3);
		spendSlider.setMinorTickSpacing(max / 9);
		spendSlider.setPaintTicks(true);
		spendSlider.setPaintLabels(true);
		spendSlider.setValue(0);
		spendSlider.addChangeListener(new SliderListener());

		settingsPanelBottom.add(sliderLabel);
		settingsPanelBottom.add(spendSlider);
	}

	/**
	 * Add product ID filter based on product IDs.
	 */
	public void addProductIDFilter() {
		// Find  and sort product IDs
		productIds = CSVParser.productIDs;
		Collections.sort(productIds);
		
		// Create search box
		searchBox = new JTextField();
		searchBox.addActionListener(new ActionListener() {
			// Update which IDs appear based on what's typed
			public void actionPerformed(ActionEvent e) {
				JTextField cb = (JTextField) e.getSource();
				String text = cb.getText();
				System.out.println("Typed " + text);
				updateProductIdPanel(text);
			}
		});
		JPanel searchPanel = new JPanel();
		searchPanel.setLayout(new GridLayout(2, 1));
		searchPanel.add(searchBox);
		
		// Add clear button to clear out selected IDs
		JButton clearIdFilter = new JButton("Clear ID Filter");
		clearIdFilter.addActionListener(new ActionListener() {
			@Override
			// Clear out selected IDs
			public void actionPerformed(ActionEvent e) {
				for (idLabel idl : idLabels) {
					// System.out.println(label.getText());
					if (idl.isSelected()) {
						idl.setSelected(false);
						idl.setBackground(null);
						productIdFilter.remove(idl.getText());
					}
				}
				map.updateProductIdFilter(productIdFilter);

				ArrayList<LocationMarker> sel = map.getDrawn();
				Collections.sort(sel);
				updateSelectedPanel(sel);
			}
		});
		searchPanel.add(clearIdFilter);
		
		panelTopUpper.add(new JLabel("Search by Product ID:"));
		panelTopUpper.add(searchPanel);

		// Update product ID list panel
		productIdPanel.setPreferredSize(new Dimension(100, 25 * productIds.size()));
		productIdPanel.setLayout(new FlowLayout());// GridLayout(productIds.size(),// 1));
		for (String p : productIds) {
			idLabel jl = new idLabel(p);
			idLabels.add(jl);
			updateProductIdPanel("");
		}
		// Make scrollable
		JScrollPane scrollPaneIds = new JScrollPane(productIdPanel);
		scrollPaneIds.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPaneIds.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPaneIds.setPreferredSize(productIdPanel.getSize());
		settingsPanelTop.add(scrollPaneIds);

		settingsPanelTop.revalidate();
		settingsPanelTop.repaint();
	}

	/**
	 * Add filter by architecture.
	 */
	public void addArchitectureFilter() {
		architectures.add(0, "All");
		JComboBox archList = new JComboBox(architectures.toArray());
		archList.setSelectedIndex(0);
		archList.addActionListener(new ActionListener() {
			// Filter on click
			public void actionPerformed(ActionEvent e) {
				JComboBox cb = (JComboBox) e.getSource();
				String archFilter = (String) cb.getSelectedItem();
				map.setArchFilter(archFilter);
				ArrayList<LocationMarker> sel = map.getDrawn();
				Collections.sort(sel);
				updateSelectedPanel(sel);
			}
		});

		JPanel archLabel = new JPanel();
		archLabel.setLayout(new BorderLayout());
		archLabel.add(new JLabel("Filter by Architecture"), BorderLayout.SOUTH);

		settingsPanelBottom.add(archLabel);
		settingsPanelBottom.add(archList);
	}

	/**
	 * Add button to filter by LDOS (not yet implemented)
	 */
	public void addLDOSButton() {
		JButton ldosButton = new JButton("LDOS Button");
		ldosButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JButton b = (JButton)e.getSource();
				if (b.isSelected()) {
					b.setSelected(false);
					map.removeByLdosLocation();
				} else {
					b.setSelected(true);
					map.addByLdosLocation();
				}
			}

		});
		settingsPanelBottom.add(new JLabel("Filter by LDOS"));
		settingsPanelBottom.add(ldosButton);
	}

	/**
	 * Custom ChangeListener Class for the slider
	 * 
	 * @author aarkerr
	 */
	class SliderListener implements ChangeListener {
		@Override
		public void stateChanged(ChangeEvent e) {
			JSlider source = (JSlider) e.getSource();
			if (!source.getValueIsAdjusting()) {
				NumberFormat formatter = NumberFormat.getCurrencyInstance();
				sliderLabel.setText("Fliter by spend: > " + formatter.format(source.getValue()));
				// Filter by spend
				map.setSpendFilter(source.getValue());
				ArrayList<LocationMarker> sel = map.getDrawn();
				Collections.sort(sel);
				updateSelectedPanel(sel);
			}
		}
	}

	/**
	 * Custom Label Class for the product Id labels
	 * 
	 * @author aarkerr
	 */
	class idLabel extends JLabel {
		/** Is this label selected? */
		protected boolean selected;

		/**
		 * Constructor for idLabel
		 * 
		 * @param s
		 *            Text of label
		 */
		public idLabel(String s) {
			// Call JLabel constructor
			super(s);
			this.setOpaque(true);
			// Default to not selected
			selected = false;
			// Add listener for clicking on label
			this.addMouseListener(new MouseListener() {
				public void mousePressed(MouseEvent e) {
					idLabel label = (idLabel) e.getSource();
					// System.out.println(label.getText());
					// Toggle selected and filter on click
					if (label.isSelected()) {
						label.setSelected(false);
						label.setBackground(null);
						productIdFilter.remove(label.getText());
					} else {
						label.setSelected(true);
						label.setBackground(new Color(13421772));
						productIdFilter.add(label.getText());
					}
					map.updateProductIdFilter(productIdFilter);

					ArrayList<LocationMarker> sel = map.getDrawn();
					Collections.sort(sel);
					updateSelectedPanel(sel);
				}

				// Nothing
				@Override
				public void mouseClicked(MouseEvent e) {
				}
				@Override
				public void mouseReleased(MouseEvent e) {
				}
				@Override
				public void mouseEntered(MouseEvent e) {
				}
				@Override
				public void mouseExited(MouseEvent e) {
				}
			});
		}

		/**
		 * Is this label selected?
		 * @return true if selected, false otherwise
		 */
		protected boolean isSelected() {
			return selected;
		}

		/**
		 * Set whether or not this label is selected
		 * @param s true for on, false for off
		 */
		protected void setSelected(boolean s) {
			selected = s;
		}
	}
}