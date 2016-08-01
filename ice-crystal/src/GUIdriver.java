import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class GUIdriver extends JFrame implements PropertyChangeListener{

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

    public GUIdriver(){
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(1400, 800);
        this.setLayout(new BorderLayout());
        this.setResizable(false);
        
        settingsPanel = new JPanel();
        mapPanel = new JPanel();
        infoPanel = new JPanel();
        
        // For testing
        /*settingsPanel.setBackground(Color.GREEN);
        mapPanel.setBackground(Color.RED);
        infoPanel.setBackground(Color.BLUE);*/
        
        settingsPanel.setPreferredSize(new Dimension(200, 800));
        mapPanel.setPreferredSize(new Dimension(900, 800));
        //infoPanel.setPreferredSize(new Dimension(190, 590));

       // Panel settings   
       settingsPanel.setLayout(new GridLayout(3,1));
       JPanel panel_1 = new JPanel();
       filtersPanel = new JPanel();
       JPanel panel_3 = new JPanel();
       
       panel_1.setLayout(new GridLayout(3,1));
       
       filtersPanel.setLayout(new FlowLayout());
       stateScroll = new JScrollPane(filtersPanel);
       stateScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
       stateScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
       
       panel_3.setLayout(new GridLayout(3,1));
       
       settingsPanel.add(panel_1);
       settingsPanel.add(stateScroll);
       settingsPanel.add(panel_3);

       JButton fileButton = new JButton("Select IB Report");
       panel_1.add(new JPanel());
       panel_1.add(fileButton);
       JPanel filtersP = new JPanel();
       filtersP.setLayout(new BorderLayout());
       filtersP.add(new JLabel("Filters: "), BorderLayout.SOUTH);
       panel_1.add(filtersP);
       
       JButton filterAll = new JButton("All");
       JPanel filtersAllP = new JPanel();
       filtersAllP.setLayout(new BorderLayout());
       filtersAllP.add(filterAll, BorderLayout.NORTH);
       panel_3.add(filtersAllP);
       
       
       
       fileButton.addActionListener(new ActionListener() {    	   
    	   	public void actionPerformed(ActionEvent e) {
				String fileName = CSVParser.chooseFile();
				if(fileName != null) {
					CSVParser.parseFile(fileName);
					map.draw2();
					
				    ArrayList<String> states = CSVParser.getStates();
				    updateFilters(states);
				    //System.out.println(states);
				    //updateFilters(states);
				}
			}
       });
     
        // Panel info
       infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
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
    	for(ImageMarker im : selected) {
    		// TODO: Add functionality
    		infoPanel.add(new JButton(im.getName()));
    	}
    	infoPanel.revalidate();
    	infoPanel.repaint();
    	/*System.out.print("Selected: ");
    	for(ImageMarker i: selected) {
    		System.out.print(i.getName() + ", ");
    	}
    	System.out.println();*/
    }

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		updateInfoPanel(map.getSelected());
	}
	
	protected ImageIcon createImageIcon(String path,String description) {
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
		for(String s: states) {
			JButton b = new JButton(s);
			// Add action
			b.setPreferredSize(new Dimension(50, 25));
			b.addActionListener(new ActionListener() {    	   
	    	   	public void actionPerformed(ActionEvent e) {
	    	   		if(b.isSelected()) {
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
		if(state.equals("all")) {
			
		} else {
			if(on) {
				//add filter
				map.selectGroup(state, true);
			} else {
				//turn off filter
				map.selectGroup(state, false);
			}
		}
	}
	
	
 }