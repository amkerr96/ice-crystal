import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.EmptyBorder;

public class CSVParser extends JFrame {
	static int buttonHit = 0;
	static JFrame frame;
	static HashMap<String, Double> locations = new HashMap<String, Double>();
	static HashMap<String, String> states = new HashMap<String, String>();
	static HashMap<String, HashMap< String, Double>> archLocations = new HashMap<String, HashMap< String, Double>>();
	static HashMap<String, HashMap< String, Integer>> prodLocations = new HashMap<String, HashMap< String, Integer>>();
	static ArrayList<String> architectures = new ArrayList<String>();
	
    public static void main(String [] args) {
    	frame = new JFrame("Unit Tester");
    	JPanel panel = new JPanel();
        JPanel logoPanel = new JPanel();
    	JButton button1 = new JButton("IB Report");
		JButton button2 = new JButton("Geographical Setup");
		JButton button3 = new JButton("Competitor Comparison");

		button1.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				buttonHit = 1;
				chooseFile();
				frame.dispose();
			}
		});
		button2.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				buttonHit = 2;
				//action
				frame.dispose();
			}
		});
		button3.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				buttonHit = 3;
				//action
				frame.dispose();
			}
		});
	
//		String path = System.getProperty("user.dir") + "/Logo.png";
//		ImageIcon iconLogo = new ImageIcon(path);
//		JLabel picLabel = new JLabel();
//        picLabel.setIcon(iconLogo);		

        panel.setBorder(new EmptyBorder(new Insets(20, 30, 20, 30)));
		panel.add(button1);
		panel.add(Box.createRigidArea(new Dimension(3, 0)));
		panel.add(button2);
		panel.add(Box.createRigidArea(new Dimension(3, 0)));
		panel.add(button3);
//		logoPanel.add(picLabel);

        frame.setLayout(new BorderLayout());
		frame.getContentPane().add(panel, BorderLayout.NORTH);
        frame.getContentPane().add(logoPanel, BorderLayout.SOUTH);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);

    }

    public static String chooseFile() {
    	String fileName = null;

        JFileChooser fileChooser = new JFileChooser();
		fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
		int result = fileChooser.showOpenDialog(frame);
		if (result == JFileChooser.APPROVE_OPTION) {
		    File selectedFile = fileChooser.getSelectedFile();
		    fileName = selectedFile.getAbsolutePath();			
		}
		return fileName;
    }
    
    public static void parseFile(String fileName) {
        try {
        	//Reader
            FileReader fr = new FileReader(fileName);
            BufferedReader br = new BufferedReader(fr);

            //Raw info by line
        	String line = null; 
//        	String street = null; //4
        	String city = null; //5
        	String state = null; //6
//        	String zip = null; //7
        	String date = null; //16
//        	String ldos = null; //17
        	String prodID = null; //22
        	String archGrp = null; //23
        	double spend = 0.0; //28        	     	
        	
        	int counter = 0;
        	try {
	        	while ((line = br.readLine()) != null) {
	        	//  Limited Testing
	//        	while(counter < 13) {
	//            	counter++;
	//        		line=br.readLine();
	//        		System.out.println(line);
	        		
	        		if (counter >= 4) {
		            	String [] fields = line.split("\t", -1);
		            	
		            	city = fields[5];
		            	state = fields[6];
		            	date = fields[16];
		            	prodID = fields[22];
		            	archGrp = fields[23];
			        	
			        	if(!architectures.contains(archGrp)) {
			        		architectures.add(archGrp);
			        	}
			        	
		            	if (!fields[28].equals("")) {
		            		spend = round(Double.parseDouble(fields[28]), 2);
		            	} else {
		            		spend = 0.00;
		            	}		   
	            	
		            	//System.out.printf("%s, %s, %s, %s, %s, %f\n", city, state, date, prodID, archGrp, spend);
	        		
		            	//Tier 1
		            	if (locations.get(city) == null) {
		            		locations.put(city, spend);
		            		states.put(city, state);
		            	} else {
		            		locations.put(city, ((Double)locations.get(city)).doubleValue() + spend);
		            	}
		            	
		            	//Tier 2
		            	HashMap<String, Double> archs = new HashMap<String, Double>();
		            	if (archLocations.get(city) == null) {
		            		archs.put(archGrp, spend);
		            		archLocations.put(city, archs);
		            	} else {
		            		HashMap<String, Double> inner = archLocations.get(city);
		            		if (inner.get(archGrp) == null) {
		            			inner.put(archGrp, spend);
		            		} else {
		            			inner.put(archGrp, ((Double)inner.get(archGrp)).doubleValue() + spend);
		            		}		            	
		            		archLocations.put(city, inner);
		            	}
		            	
		            	//Tier 3 still in progress
		            	HashMap<String, Double> prods = new HashMap<String, Double>();
//		            	if (prodLocations.get(city) == null) {
//		            		prods.put(prodID, spend);
//		            		prodLocations.put(city, prods);
//		            	} else {
//		            		HashMap<String, Double> inner = prodLocations.get(city);
//		            		if (inner.get(prodID) == null) {
//		            			inner.put(prodID, spend);
//		            		} else {
//		            			inner.put(prodID, ((Double)inner.get(prodID)).doubleValue() + spend);
//		            		}		            	
//		            		prodLocations.put(city, inner);
//		            	}		            	
	        		
	        		
	        		}
	        		counter++;
	        	}	
//	        	System.out.println(locations.toString());
//	        	System.out.println(archLocations.toString());
	        	
    		} catch (ArrayIndexOutOfBoundsException e) {	

        	} catch(NullPointerException e) {

        	} 
//            System.out.println("STATES: " + states);
//            System.out.println("LOCATIONS: " + locations);
//            System.out.println("ARCH LOCATIONS: " + archLocations);
//            System.out.println("PROD LOCATIONS: " + prodLocations);


            if (buttonHit == 2 || buttonHit == 3) {
            	
        	}

            br.close();        
        }
        catch(FileNotFoundException ex) {
            System.out.println(
                "Unable to open file '" + 
                fileName + "'");                
        }
        catch(IOException ex) {
            System.out.println(
                "Error reading file '" 
                + fileName + "'");
        }
    }
    
    public static ArrayList<String> getStates() {
    	ArrayList<String> statesList = new ArrayList<String>();
    	for(String s: states.keySet()) {
    		String state = states.get(s);
    		if(!statesList.contains(state)) {
    			statesList.add(state);
    		}
    	}
    	return statesList;
    }
        
    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
    

}

