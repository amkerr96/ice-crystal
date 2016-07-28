import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class GUIdriver extends JFrame{

    public static void main(String[] args) {
        new GUIdriver();
    }
    
    SimpleMapApp map;

    public GUIdriver(){
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(1200, 600);
        this.setLayout(new BorderLayout());
        this.setVisible(true);

        JPanel container = new JPanel();
        JPanel panelOne = new JPanel();
        JPanel panelTwo = new JPanel();
        JPanel panelTemp = new JPanel();
        
        JButton FileButton = new JButton("Select IB Report");
        panelTemp.add(FileButton);
		FileButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				CSVParser.chooseFile();
				map.draw2();
			}
		});
		
		// Panel 1
		map = new SimpleMapApp();
        panelOne.add(map);
        map.init();
        
        // Panel 2
        panelTwo.setLayout(new BoxLayout(panelTwo, BoxLayout.Y_AXIS));
        panelTwo.add(new JLabel("   Locations"));
        panelTwo.add(new JButton("Berlin"));
        panelTwo.add(new JButton("Venice"));
        panelTwo.add(new JButton("Lisbon"));       
        
        container.setLayout(new FlowLayout());
        container.add(panelTemp);
        container.add(panelOne);
        container.add(panelTwo);

        this.add(container);
        this.pack();
    }

 }