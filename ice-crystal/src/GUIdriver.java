import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.knowm.xchart.PieChart;
import org.knowm.xchart.PieChartBuilder;
import org.knowm.xchart.XChartPanel;
import org.knowm.xchart.style.PieStyler.AnnotationType;
import org.knowm.xchart.style.Styler.ChartTheme;
import org.knowm.xchart.style.Styler.LegendPosition;

public class GUIdriver extends JFrame implements PropertyChangeListener {

	public static void main(String[] args) {
		new GUIdriver();
	}

	SimpleMapApp map;
	JPanel container;
	JPanel mapPanel;
	JPanel selectedPanel;
	JPanel settingsPanel;
	JPanel filtersPanel;
	JScrollPane stateScroll;
	JPanel infoPanel;
	JLabel sliderLabel;
	JPanel pieChartPanel;
	private ArrayList<String> locationFilters;
	static boolean fileSelected;
	private ArrayList<String> architectures;
	private String archFilter;

	public GUIdriver() {
		locationFilters = new ArrayList<String>();

		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(1400, 800);
		this.setLayout(new BorderLayout());
		this.setResizable(false);

		settingsPanel = new JPanel();
		mapPanel = new JPanel();
		infoPanel = new JPanel();
		selectedPanel = new JPanel();

		// For testing
		/*
		 * settingsPanel.setBackground(Color.GREEN);
		 * mapPanel.setBackground(Color.RED);
		 * selectedPanel.setBackground(Color.BLUE);
		 */

		settingsPanel.setPreferredSize(new Dimension(200, 800));
		mapPanel.setPreferredSize(new Dimension(900, 800));
		// selectedPanel.setPreferredSize(new Dimension(190, 590));

		// Panel settings
		settingsPanel.setLayout(new GridLayout(3, 1));
		JPanel panel_1 = new JPanel();
		filtersPanel = new JPanel();
		JPanel panel_3 = new JPanel();

		panel_1.setLayout(new GridLayout(3, 1));

		JPanel filtersContainer = new JPanel();
		filtersContainer.setLayout(new BorderLayout());
		filtersPanel.setLayout(new FlowLayout());
		stateScroll = new JScrollPane(filtersPanel);
		stateScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		stateScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

		// Filter all button
		JButton filterAll = new JButton("All");
		filterAll.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (filterAll.isSelected()) {
					filterAll.setSelected(false);
					map.removeAll();
					for (Component c : filtersPanel.getComponents()) {
						if (c instanceof JButton) {
							((JButton) c).setSelected(false);
						}
					}
				} else {
					filterAll.setSelected(true);
					map.addAll();
					for (Component c : filtersPanel.getComponents()) {
						if (c instanceof JButton) {
							((JButton) c).setSelected(true);
						}
					}
				}
			}
		});

		filtersContainer.add(new JLabel("Select by location: "), BorderLayout.NORTH);
		filtersContainer.add(stateScroll, BorderLayout.CENTER);
		filtersContainer.add(filterAll, BorderLayout.SOUTH);

		panel_3.setLayout(new GridLayout(3,1));

		settingsPanel.add(panel_1);

		// File button
		JButton fileButton = new JButton("Select IB Report");
		fileButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fileSelected = true;
				String fileName = CSVParser.chooseFile();
				if (fileName != null) {
					CSVParser.parseFile(fileName);
					architectures = CSVParser.architectures;
					map.draw2();

					ArrayList<String> states = CSVParser.getStates();
					updateFilters(states);
					// System.out.println(states);
					// updateFilters(states);

					sliderLabel = new JLabel("Filter by spend:");

					int min = (int) Math.floor(map.getMinSpend());
					int max = (int) Math.ceil(map.getMaxSpend()) / 2;

					JSlider spendSlider = new JSlider(0, max);

					//System.out.println("Spend: " + min + " - " + max);
					spendSlider.setMajorTickSpacing(max / 3);
					spendSlider.setMinorTickSpacing(max / 9);
					spendSlider.setPaintTicks(true);
					spendSlider.setPaintLabels(true);
					spendSlider.setValue(0);
					spendSlider.addChangeListener(new SliderListener());

					panel_1.add(sliderLabel, BorderLayout.CENTER);
					panel_1.add(spendSlider, BorderLayout.SOUTH);
					panel_1.revalidate();
					panel_1.repaint();
					
					architectures.add(0, "All");
					JComboBox archList = new JComboBox(architectures.toArray());
					archList.setSelectedIndex(0);
					archList.addActionListener(new ActionListener() {

						public void actionPerformed(ActionEvent e) {
					        JComboBox cb = (JComboBox)e.getSource();
					        String archFilter = (String)cb.getSelectedItem();
							map.setArchFilter(archFilter);
							ArrayList<LocationMarker> sel = map.getDrawn();
							Collections.sort(sel);
							updateSelectedPanel(sel);
						}
						
					});
					
					JPanel archLabel = new JPanel();
					archLabel.setLayout(new BorderLayout());
					archLabel.add(new JLabel("Filter by Architecture"), BorderLayout.SOUTH);
					panel_3.add(archLabel);
					panel_3.add(archList);
					
					JButton ldosButton = new JButton("LDOS Button");
					ldosButton.addActionListener(new ActionListener() {

						@Override
						public void actionPerformed(ActionEvent e) {
							// TODO Rylan add stuff here!
							
						}
					
					});
					panel_3.add(ldosButton);
					
					settingsPanel.add(filtersContainer);
					settingsPanel.add(panel_3);
					
					//System.out.println("Arch: " + CSVParser.archLocations);
				}
			}
		});
		panel_1.add(fileButton);

		infoPanel.setLayout(new GridLayout(2, 1));
		selectedPanel.setLayout(new FlowLayout());
		selectedPanel.add(new JLabel("Locations:"));

		// Make info scrollable
		JScrollPane scrollPane = new JScrollPane(selectedPanel);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setPreferredSize(new Dimension(300, 400));

		pieChartPanel = new JPanel();

		// Create
		this.add(settingsPanel, BorderLayout.WEST);
		this.add(mapPanel, BorderLayout.CENTER);
		infoPanel.add(scrollPane);
		infoPanel.add(pieChartPanel);
		this.add(infoPanel, BorderLayout.EAST);
		this.pack();
		this.setVisible(true);

		// Panel map
		map = new SimpleMapApp();
		map.addPropertyChangeListener(this);
		mapPanel.add(map);
		map.init();
	}

	public void updateSelectedPanel(ArrayList<LocationMarker> selected) {
		selectedPanel.removeAll();
		selectedPanel.add(new JLabel("   Locations"));
		for (LocationMarker im : selected) {
			// TODO: Add functionality

			JButton button = new JButton();
			button.addActionListener(new ActionListener() {
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
		/*
		 * System.out.print("Selected: "); for(ImageMarker i: selected) {
		 * System.out.print(i.getName() + ", "); } System.out.println();
		 */
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		ArrayList<LocationMarker> sel = map.getDrawn();
		Collections.sort(sel);
		updateSelectedPanel(sel);
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
			filtersPanel.add(b);
		}

		stateScroll.revalidate();
		stateScroll.repaint();
	}

	public void drawPieChart(LocationMarker im) {
		try {
			pieChartPanel.remove(pieChartPanel.getComponent(0));

		} catch (ArrayIndexOutOfBoundsException e) {
			//
		}
		// Create Chart
		PieChart chart = new PieChartBuilder().width(300).height(400).title("Architecture % in " + im.getName()).theme(ChartTheme.GGPlot2).build();
		
		// Customize Chart
		chart.getStyler().setLegendVisible(true);
		chart.getStyler().setAnnotationType(AnnotationType.Percentage);
		chart.getStyler().setDrawAllAnnotations(true);
		//chart.getStyler().setAnnotationDistance(1.15);
		chart.getStyler().setPlotContentSize(.7);
		chart.getStyler().setStartAngleInDegrees(90);
		chart.getStyler().setLegendFont(new Font("TimesRoman", Font.PLAIN, 10));
		chart.getStyler().setLegendPadding(1);
		chart.getStyler().setLegendPosition(LegendPosition.InsideSE);

		HashMap<String, Double> archs = CSVParser.archLocations.get(im.getName().toUpperCase());

		System.out.println("\n------" + im.getName() + "------");
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

	class SliderListener implements ChangeListener {
		@Override
		public void stateChanged(ChangeEvent e) {
			JSlider source = (JSlider) e.getSource();
			if (!source.getValueIsAdjusting()) {
				NumberFormat formatter = NumberFormat.getCurrencyInstance();
				sliderLabel.setText("Fliter by spend: > " + formatter.format(source.getValue()));
				map.setSpendFilter(source.getValue());
				ArrayList<LocationMarker> sel = map.getDrawn();
				Collections.sort(sel);
				updateSelectedPanel(sel);
			}
		}
	}
	
}