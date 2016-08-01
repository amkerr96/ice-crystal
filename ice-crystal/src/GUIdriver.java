import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.Icon;
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

    public GUIdriver(){
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(1200, 600);
        this.setLayout(new BorderLayout());
        this.setResizable(false);
        
        settingsPanel = new JPanel();
        mapPanel = new JPanel();
        infoPanel = new JPanel();
        
        // For testing
        /*settingsPanel.setBackground(Color.GREEN);
        mapPanel.setBackground(Color.RED);
        infoPanel.setBackground(Color.BLUE);*/
        
        settingsPanel.setPreferredSize(new Dimension(200, 600));
        mapPanel.setPreferredSize(new Dimension(800, 600));
        //infoPanel.setPreferredSize(new Dimension(190, 590));

       // Panel settings        
       settingsPanel.setLayout(new BoxLayout(settingsPanel, BoxLayout.Y_AXIS));
       JButton FileButton = new JButton("Select IB Report");
       settingsPanel.add(FileButton);
       
       FileButton.addActionListener(new ActionListener() {    	   
    	   	public void actionPerformed(ActionEvent e) {
    			/*ImageIcon icon = createImageIcon("ui/ajax-loader.gif", "Image");
    		    JLabel label = new JLabel(icon);
    	        settingsPanel.add(label);
    	        settingsPanel.revalidate();*/
				CSVParser.chooseFile();
				map.draw2();
				/*settingsPanel.remove(label);
    	        settingsPanel.revalidate();*/
			}
       });
     
        // Panel info
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.add(new JLabel("   Locations"));
        
        // Make info scrollable
        JScrollPane scrollPane = new JScrollPane(infoPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);        
        scrollPane.setPreferredSize(new Dimension(200, 600));
        
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
    		infoPanel.add(new JButton(im.getName()));
    	}
    	infoPanel.revalidate();
    	infoPanel.repaint();
    	System.out.print("Selected: ");
    	for(ImageMarker i: selected) {
    		System.out.print(i.getName() + ", ");
    	}
    	System.out.println();
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
	
 }