import java.io.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.EmptyBorder;

public class CSVParser extends JFrame {
	static int buttonHit = 0;
	static JFrame frame;

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

    public static void chooseFile() {
       String fileName = null;

        JFileChooser fileChooser = new JFileChooser();
		fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
		int result = fileChooser.showOpenDialog(frame);
		if (result == JFileChooser.APPROVE_OPTION) {
		    File selectedFile = fileChooser.getSelectedFile();
		    fileName = selectedFile.getAbsolutePath();			
		}

        try {
        	//Reader
            FileReader fr = new FileReader(fileName);
            BufferedReader br = new BufferedReader(fr);

        	String field = br.readLine();

            while (field != null) {
             field = br.readLine();
            	try {
            		
        		} catch (ArrayIndexOutOfBoundsException e) {	

            	} catch(NullPointerException e) {

            	}
            }

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
}
