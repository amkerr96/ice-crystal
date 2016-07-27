import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class GUIdriver extends JFrame{

    public static void main(String[] args) {
        new GUIdriver();
    }

    public GUIdriver(){
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(1200, 600);
        this.setLayout(new BorderLayout());
        this.setVisible(true);

        JPanel container = new JPanel();
        JPanel panelOne = new JPanel();
        JPanel panelTwo = new JPanel();
        
        SimpleMapApp map = new SimpleMapApp();
        panelOne.add(map);
        map.init();

        //panelTwo.setLayout(new FlowLayout());
        panelTwo.setLayout(new BoxLayout(panelTwo, BoxLayout.Y_AXIS));
        panelTwo.add(new JLabel("   Locations"));
        panelTwo.add(new JButton("Berlin"));
        panelTwo.add(new JButton("Venice"));
        panelTwo.add(new JButton("Lisbon"));
        
        
        container.setLayout(new FlowLayout());
        container.add(panelOne);
        container.add(panelTwo);

        this.add(container);
        this.pack();
    }

 }