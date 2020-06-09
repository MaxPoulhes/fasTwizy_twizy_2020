
 
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader; 
import java.io.IOException; 
import java.io.InputStreamReader; 

import javax.swing.*;

import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;

import utilitaireAgreg.MaBibliothequeTraitementImage;
import javax.swing.border.LineBorder;

public class SwingControlDemo {
   private JFrame mainFrame;
   private JLabel statusLabel;
   private JPanel controlPanel;
   private JPanel panel;
   protected static Mat m;

   public SwingControlDemo(){
      prepareGUI();
   }
   public static void main(String[] args){
	 //Enter data using BufferReader 
       System.out.println("Quel est le nom de votre fichier ?");
	   BufferedReader reader =  new BufferedReader(new InputStreamReader(System.in)); 
       String name = null;
       // Reading data using readLine 
       try {
		name = reader.readLine();
	} catch (IOException e) {
		e.printStackTrace();
	} 
       
        SwingControlDemo  swingControlDemo = new SwingControlDemo();    
        System.loadLibrary("opencv_java2413");
		swingControlDemo.m=Highgui.imread(name,Highgui.CV_LOAD_IMAGE_COLOR);
      	swingControlDemo.showButtonDemo();
   }
   private void prepareGUI(){
      mainFrame = new JFrame("Detection Panneaux");
      mainFrame.setSize(1280,720);
      
      mainFrame.addWindowListener(new WindowAdapter() {
         public void windowClosing(WindowEvent windowEvent){
            System.exit(0);
         }        
      });
      statusLabel = new JLabel("",JLabel.CENTER);    
      statusLabel.setLocation(161, 514);
      statusLabel.setSize(318,156);

      controlPanel = new JPanel();
      controlPanel.setBorder(new LineBorder(new Color(0, 0, 0)));
      controlPanel.setBounds(0, 0, 151, 681);
      mainFrame.getContentPane().setLayout(null);
      
      JLabel lblNewLabel = new JLabel("Panneau identifi\u00E9 :");
      lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
      lblNewLabel.setBounds(161, 11, 318, 156);
      mainFrame.getContentPane().add(lblNewLabel);
      mainFrame.getContentPane().add(controlPanel);
      mainFrame.getContentPane().add(statusLabel);
      
      JPanel panel_2 = new JPanel();
      panel_2.setBorder(new LineBorder(new Color(0, 0, 0)));
      panel_2.setBounds(151, 0, 336, 692);
      mainFrame.getContentPane().add(panel_2);
      

      
   }
   private static ImageIcon createImageIcon(String path, String description) {
      java.net.URL imgURL = SwingControlDemo.class.getResource(path);
      if (imgURL != null) {
         return new ImageIcon(imgURL, description);
      } else {            
         System.err.println("Couldn't find file: " + path);
         return null;
      }
   }   
   private void showButtonDemo(){

      //resources folder should be inside SWING folder.
      JButton loadButton = new JButton("Charger Image");        
      loadButton.setBounds(10, 49, 131, 23);
      JButton invButton = new JButton("Inverser couleur");
      invButton.setBounds(10, 83, 131, 23);
      JButton seuilButton = new JButton("Seuillage");
      seuilButton.setBounds(10, 117, 131, 23);
      JButton detecButton = new JButton("Detection contours");
      detecButton.setBounds(10, 151, 131, 23);
      JButton panButton = new JButton("Identifier Panneau");
      panButton.setBounds(10, 185, 131, 23);
      
      loadButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
        	 JLabel img = MaBibliothequeTraitementImageEtendue.afficheImage("Image testée", m);
        	 JPanel panel = new JPanel();
             panel.setBounds(489, 18, 765, 652);
             panel.add(img);
             mainFrame.getContentPane().add(panel);
             mainFrame.setVisible(true);  
         }          
      });
      invButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
        	 JLabel img = MaBibliothequeTraitementImageEtendue.transformationEnHSV(m);
        	 JPanel panel = new JPanel();
             panel.setBounds(489, 18, 765, 652);
             panel.add(img);
             mainFrame.getContentPane().add(panel);
             mainFrame.setVisible(true);
         }
      });
      seuilButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
        	 JLabel img = MaBibliothequeTraitementImageEtendue.detectionSeuillageRouge(m);
        	 JPanel panel = new JPanel();
             panel.setBounds(489, 18, 765, 652);
             panel.add(img);
             mainFrame.getContentPane().add(panel);
             mainFrame.setVisible(true);
         }
      });
      detecButton.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
        	  JLabel img = MaBibliothequeTraitementImageEtendue.detectionContours(m);
         	 JPanel panel = new JPanel();
              panel.setBounds(489, 18, 765, 652);
              panel.add(img);
              mainFrame.getContentPane().add(panel);
              mainFrame.setVisible(true);
          }
       });
      panButton.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
         	 rendu obtenu = MaBibliothequeTraitementImageEtendue.extractionPanneau(m);
         	 
         	 JLabel img = MaBibliothequeTraitementImageEtendue.afficheImage("Image testée", m);
         	 JPanel panel = new JPanel();
             panel.setBounds(489, 18, 765, 652);
             panel.add(img);
             
             JLabel img_1 = obtenu.pic;
             JPanel panel_1 = new JPanel();
             panel_1.setBounds(161, 185, 318, 318);
             panel_1.add(img_1);
             
             mainFrame.getContentPane().add(panel_1);
             mainFrame.getContentPane().add(panel);
             mainFrame.setVisible(true);
             statusLabel.setText(obtenu.type);
          }
       });
      controlPanel.setLayout(null);
      controlPanel.add(loadButton);
      controlPanel.add(invButton);
      controlPanel.add(seuilButton);       
      controlPanel.add(detecButton);
      controlPanel.add(panButton);
      mainFrame.setVisible(true);  
   }
}