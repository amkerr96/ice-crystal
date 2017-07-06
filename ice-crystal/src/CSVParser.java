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
	static HashMap<String, Integer> locations = new HashMap<String, Integer>();
	static HashMap<String, String> states = new HashMap<String, String>();
	static HashMap<String, HashMap< String, Double>> archLocations = new HashMap<String, HashMap< String, Double>>();
	static HashMap<String, HashMap< String, Integer>> prodLocations = new HashMap<String, HashMap< String, Integer>>();
	static ArrayList<String> architectures = new ArrayList<String>();
	static ArrayList<String> productIDs = new ArrayList<String>();
	static HashMap<String, Double> ldosLocs = new HashMap<String, Double>();
	
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
        	String ldos = null; //17
        	String prodID = null; //22
        	String archGrp = null; //23
        	double spend = 0.0; //28   
        	int year = 0;
        	int ldosYear = 0;
        	
        	int counter = 0;
        	try {
	        	while ((line = br.readLine()) != null) {
	        		
	        		if (counter >= 2) {
		            	String [] fields = line.split("\t", -1);
		            	
		            	city = fields[18]; //5 -> 18
		            	state = fields[21]; //6 -> 21 (countries)
		            	date = fields[55]; //16 -> 55
		            	year = Integer.parseInt(date.split("/")[2]);
		            	//ldos = fields[17]; //17 will turn to binary switch later
//		            	if (!ldos.equals("<NULL>")) {
//		            		ldosYear = Integer.parseInt(ldos.split("/")[2]);
//		            	} else {
//		            		ldosYear = 99;
//		            	}
		            	prodID = fields[33]; //22 -> 33
		            	archGrp = fields[14]; //23 -> 14 (site name)

			        	
			        	if(!architectures.contains(archGrp)) {
			        		architectures.add(archGrp);
			        	}
			        	
//		            	if (!fields[28].equals("")) {
//		            		spend = round(Double.parseDouble(fields[28]), 2);
//		            	} else {
//		            		spend = 0.00;
//		            	}		   
	            	
		            	//System.out.printf("%s, %s, %s, %s, %s, %f\n", city, state, date, prodID, archGrp, spend);
//		            	if (year >= 10 && year < 90) {
//		            		System.out.println("Year: " + date.split("/")[1]);
//		            		System.out.println("LDoS: " + ldosYear);
		          	
			            	//Tier 1 <location, spend>
			            	if (locations.get(city) == null) {
			            		locations.put(city, 1);
			            		states.put(city, state);
			            	} else {
			            		locations.put(city, locations.get(city).intValue() + 1);
			            	}
			            	
			            	//Tier 2 <architecture, spend>
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
			            	
			            	//Tier 3 <productID, numberInstalled>
			            	HashMap<String, Integer> prods = new HashMap<String, Integer>();
			            	if(!productIDs.contains(prodID)) {
			            		productIDs.add(prodID);
			            	}
			            	if (prodLocations.get(city) == null) {
			            		prods.put(prodID, 1);
			            		prodLocations.put(city, prods);
			            	} else {
			            		HashMap<String, Integer> inner = prodLocations.get(city);
			            		if (inner.get(prodID) == null) {
			            			inner.put(prodID, 1);
			            		} else {
			            			inner.put(prodID, ((Integer)inner.get(prodID)).intValue() + 1);
			            		}		            	
			            		prodLocations.put(city, inner);
			            	}
			            	
//			            	//LDoS info - static, set to start at Aug 2016
//			            	if (ldosYear >= 16 && ldosYear < 19) {
//				            	if (ldosLocs.get(city) == null) {
//				            		ldosLocs.put(city, spend);
//				            	} else {
//				            		ldosLocs.put(city, ((Double)ldosLocs.get(city)).doubleValue() + spend);
//				            	}
//			            	}
//		            	}
	        		
	        		}
	        		counter++;
	        	}	
	        	
	        	//Test LDoS Refresh Ratios
//	            for (String locs : ldosLocs.keySet()) {
//	            	double ratio = (ldosLocs.get(locs) / locations.get(locs)) * 100;
//	        		System.out.println("City: "  + locs + ", Ratio: " + ratio + "%");
//	        	}
	        	
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

