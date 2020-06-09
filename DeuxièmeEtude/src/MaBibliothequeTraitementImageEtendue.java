import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfInt4;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.features2d.DMatch;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.FeatureDetector;
import org.opencv.features2d.Features2d;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

import utilitaireAgreg.MaBibliothequeTraitementImage;

public class MaBibliothequeTraitementImageEtendue {
	//Contient toutes les méthodes necessaires à la transformation des images


	//Methode qui permet de transformer une matrice intialement au  format BGR au format HSV
	public static Mat transformeBGRversHSV(Mat matriceBGR){
		Mat matriceHSV=new Mat(matriceBGR.height(),matriceBGR.cols(),matriceBGR.type());
		Imgproc.cvtColor(matriceBGR,matriceHSV,Imgproc.COLOR_BGR2HSV);
		return matriceHSV;

	}

	//Methode qui convertit une matrice avec 3 canaux en un vecteur de 3 matrices monocanal (un canal par couleur)
	public static Vector<Mat> splitHSVChannels(Mat input) {
		Vector<Mat> channels = new Vector<Mat>(); 
		Core.split(input, channels);
		return channels;
	}

	//Methode qui permet d'afficher une image sur un panel
	public static JLabel afficheImage(String title, Mat img){
		MatOfByte matOfByte=new MatOfByte();
		Highgui.imencode(".png",img,matOfByte);
		byte[] byteArray=matOfByte.toArray();
		BufferedImage bufImage=null;
			InputStream in=new ByteArrayInputStream(byteArray);
			try {
				bufImage=ImageIO.read(in);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			JLabel pic = new JLabel(new ImageIcon(bufImage));
			return pic;


	}

	

	//Methode qui permet d'extraire les contours d'une image donnee
	public static List<MatOfPoint> ExtractContours(Mat input,boolean affichage) {
		// Detecter les contours des formes trouvées
		int thresh = 100;
		Mat canny_output = new Mat();
		List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
		MatOfInt4 hierarchy = new MatOfInt4();
		Imgproc.Canny( input, canny_output, thresh, thresh*2);


		/// Find extreme outer contours
		Imgproc.findContours( canny_output, contours, hierarchy,Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

		Mat drawing = Mat.zeros( canny_output.size(), CvType.CV_8UC3 );
		Random rand = new Random();
		for( int i = 0; i< contours.size(); i++ )
		{
			Scalar color = new Scalar( rand.nextInt(255 - 0 + 1) , rand.nextInt(255 - 0 + 1),rand.nextInt(255 - 0 + 1) );
			Imgproc.drawContours( drawing, contours, i, color, 1, 8, hierarchy, 0, new Point() );
		}
		
		JLabel pic = afficheImage("Detection contours",drawing);
		return contours;
	}
	
	public static JLabel getPicContours(Mat input,boolean affichage) {
		// Detecter les contours des formes trouvées
		int thresh = 100;
		Mat canny_output = new Mat();
		List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
		MatOfInt4 hierarchy = new MatOfInt4();
		Imgproc.Canny( input, canny_output, thresh, thresh*2);


		/// Find extreme outer contours
		Imgproc.findContours( canny_output, contours, hierarchy,Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

		Mat drawing = Mat.zeros( canny_output.size(), CvType.CV_8UC3 );
		Random rand = new Random();
		for( int i = 0; i< contours.size(); i++ )
		{
			Scalar color = new Scalar( rand.nextInt(255 - 0 + 1) , rand.nextInt(255 - 0 + 1),rand.nextInt(255 - 0 + 1) );
			Imgproc.drawContours( drawing, contours, i, color, 1, 8, hierarchy, 0, new Point() );
		}
		
		JLabel pic = afficheImage("Detection contours",drawing);
		return pic;
	}

	//Methode qui permet de decouper et identifier les contours carrés, triangulaires ou rectangulaires. 
	//Renvoie null si aucun contour rond n'a été trouvé.	
	//Renvoie une matrice carrée englobant un contour rond si un contour rond a été trouvé
	public static Mat DetectForm(Mat img,MatOfPoint contour) {
		MatOfPoint2f matOfPoint2f = new MatOfPoint2f();
		MatOfPoint2f approxCurve = new MatOfPoint2f();
		float[] radius = new float[1];
		Point center = new Point();
		Rect rect = Imgproc.boundingRect(contour);
		double contourArea = Imgproc.contourArea(contour);


		matOfPoint2f.fromList(contour.toList());
		// Cherche le plus petit cercle entourant le contour
		Imgproc.minEnclosingCircle(matOfPoint2f, center, radius);
		//System.out.println(contourArea+" "+Math.PI*radius[0]*radius[0]);
		//on dit que c'est un cercle si l'aire occupé par le contour est à supérieure à  80% de l'aire occupée par un cercle parfait
		if ((contourArea / (Math.PI*radius[0]*radius[0])) >=0.8) {
			//System.out.println("Cercle");
			Core.circle(img, center, (int)radius[0], new Scalar(255, 0, 0), 2);
			Core.rectangle(img, new Point(rect.x,rect.y), new Point(rect.x+rect.width,rect.y+rect.height), new Scalar (0, 255, 0), 2);
			Mat tmp = img.submat(rect.y,rect.y+rect.height,rect.x,rect.x+rect.width);
			Mat sign = Mat.zeros(tmp.size(),tmp.type());
			tmp.copyTo(sign);
			return sign;
		}else {

			Imgproc.approxPolyDP(matOfPoint2f, approxCurve, Imgproc.arcLength(matOfPoint2f, true) * 0.02, true);
			long total = approxCurve.total();
			if (total == 3 ) { // is triangle
				//System.out.println("Triangle");
				Point [] pt = approxCurve.toArray();
				Core.line(img, pt[0], pt[1], new Scalar(255,0,0),2);
				Core.line(img, pt[1], pt[2], new Scalar(255,0,0),2);
				Core.line(img, pt[2], pt[0], new Scalar(255,0,0),2);
				Core.rectangle(img, new Point(rect.x,rect.y), new Point(rect.x+rect.width,rect.y+rect.height), new Scalar (0, 255, 0), 2);
				Mat tmp = img.submat(rect.y,rect.y+rect.height,rect.x,rect.x+rect.width);
				Mat sign = Mat.zeros(tmp.size(),tmp.type());
				tmp.copyTo(sign);
				return sign;
			}
			if (total >= 4 && total <= 6) {
				List<Double> cos = new ArrayList<>();
				Point[] points = approxCurve.toArray();
				for (int j = 2; j < total + 1; j++) {
					cos.add(angle(points[(int) (j % total)], points[j - 2], points[j - 1]));
				}
				Collections.sort(cos);
				Double minCos = cos.get(0);
				Double maxCos = cos.get(cos.size() - 1);
				boolean isRect = total == 4 && minCos >= -0.1 && maxCos <= 0.3;
				boolean isPolygon = (total == 5 && minCos >= -0.34 && maxCos <= -0.27) || (total == 6 && minCos >= -0.55 && maxCos <= -0.45);
				if (isRect) {
					double ratio = Math.abs(1 - (double) rect.width / rect.height);
					//drawText(rect.tl(), ratio <= 0.02 ? "SQU" : "RECT");
					//System.out.println("Rectangle");
					Core.rectangle(img, new Point(rect.x,rect.y), new Point(rect.x+rect.width,rect.y+rect.height), new Scalar (0, 255, 0), 2);
					Mat tmp = img.submat(rect.y,rect.y+rect.height,rect.x,rect.x+rect.width);
					Mat sign = Mat.zeros(tmp.size(),tmp.type());
					tmp.copyTo(sign);
					return sign;
				}
				if (isPolygon) {
					//System.out.println("Polygon");
					//drawText(rect.tl(), "Polygon");
				}
			}			
		}
		return null;

	}


	public static double angle(Point a, Point b, Point c) {
		Point ab = new Point( b.x - a.x, b.y - a.y );
		Point cb = new Point( b.x - c.x, b.y - c.y );
		double dot = (ab.x * cb.x + ab.y * cb.y); // dot product
		double cross = (ab.x * cb.y - ab.y * cb.x); // cross product
		double alpha = Math.atan2(cross, dot);
		return Math.floor(alpha * 180. / Math.PI + 0.5);
	}

	
	//methode à completer
	public static double Similitude_Perso(Mat object,String signfile) {

		double sueil=180;
		// Conversion du signe de reference en niveaux de gris et normalisation
		Mat panneauref = Highgui.imread(signfile);
		Mat graySign = new Mat(panneauref.rows(), panneauref.cols(), panneauref.type());
		Imgproc.cvtColor(panneauref, graySign, Imgproc.COLOR_BGRA2GRAY);
		Core.normalize(graySign, graySign, 0, 255, Core.NORM_MINMAX);
		
		// transformation de l'image niveau de gris en image à palier noir et blanc
		Mat signeNoirEtBlanc=new Mat(graySign.size(),graySign.type());
		Imgproc.threshold(graySign, signeNoirEtBlanc, sueil, 255, Imgproc.THRESH_BINARY);
						
		//MaBibliothequeTraitementImageEtendue.afficheImage("image BetW",signeNoirEtBlanc);
		

		//Conversion du panneau extrait de l'image en gris et normalisation et redimensionnement à la taille du panneau de réference
		Mat grayObject = new Mat(panneauref.rows(), panneauref.cols(), panneauref.type());
		Imgproc.resize(object, object, graySign.size());
		//MaBibliothequeTraitementImageEtendue.afficheImage("Panneau extrait de l'image",object);
		Imgproc.cvtColor(object, grayObject, Imgproc.COLOR_BGRA2GRAY);
		Core.normalize(grayObject, grayObject, 0, 255, Core.NORM_MINMAX);
		//Imgproc.resize(grayObject, grayObject, graySign.size());	
		Mat objectNoirEtBlanc=new Mat(grayObject.size(),grayObject.type());
		Imgproc.threshold(grayObject, objectNoirEtBlanc, sueil, 255, Imgproc.THRESH_BINARY);
	
		Mat result=new Mat(grayObject.size(),grayObject.type());
		
		
		//MaBibliothequeTraitementImageEtendue.afficheImage("image BetW Sign",signeNoirEtBlanc);
		//MaBibliothequeTraitementImageEtendue.afficheImage("image BetW Objet",objectNoirEtBlanc);
		Core.compare(objectNoirEtBlanc,signeNoirEtBlanc,result,Core.CMP_EQ);
		//MaBibliothequeTraitementImageEtendue.afficheImage("Resultat", result);
		
		double ScoreTest=0;
		for(int k=0;k<result.size().height;k++) {
			for(int l=0;l<result.size().width;l++) {
				ScoreTest= ScoreTest + result.get(k, l)[0];
			}
		}
		ScoreTest=ScoreTest/(result.size().height*result.size().width);
		//System.out.println("Score : "+ScoreTest);
		
		
		
		
		return ScoreTest/255.0;
		

	}
	

	public static double Similitude_Extract(Mat object,String signfile) {
/**
		// Conversion du signe de reference en niveaux de gris et normalisation
		Mat panneauref = Highgui.imread(signfile);
		Mat graySign = new Mat(panneauref.rows(), panneauref.cols(), panneauref.type());
		Imgproc.cvtColor(panneauref, graySign, Imgproc.COLOR_BGRA2GRAY);
		Core.normalize(graySign, graySign, 0, 255, Core.NORM_MINMAX);
		Mat signeNoirEtBlanc=new Mat();**/
		

/***
		//Conversion du panneau extrait de l'image en gris et normalisation et redimensionnement à la taille du panneau de réference
		Mat grayObject = new Mat(panneauref.rows(), panneauref.cols(), panneauref.type());
		Imgproc.resize(object, object, graySign.size());
		//afficheImage("Panneau extrait de l'image",object);
		Imgproc.cvtColor(object, grayObject, Imgproc.COLOR_BGRA2GRAY);
		Core.normalize(grayObject, grayObject, 0, 255, Core.NORM_MINMAX);
		//Imgproc.resize(grayObject, grayObject, graySign.size());	***/
		
		
		// test extraction 
				
		//Mise à l'échelle
		Mat sroadSign = Highgui.imread(signfile);
		Mat sObject = new Mat();
		Imgproc.resize(object, sObject, sroadSign.size());
		Mat grayObject = new Mat(sObject.rows(), sObject.cols(),sObject.type());
		Imgproc.cvtColor(sObject, grayObject, Imgproc.COLOR_BGRA2GRAY);
		Core.normalize(grayObject, grayObject, 0, 255, Core.NORM_MINMAX);
		
		Mat graySign=new Mat(sroadSign.rows(),sroadSign.cols(),sroadSign.type());
		Imgproc.cvtColor(sroadSign, graySign, Imgproc.COLOR_BGRA2GRAY);
		Core.normalize(sroadSign, sroadSign, 0, 255, Core.NORM_MINMAX);
						
						
		//Extraction des caractéristiques et keypoints
		FeatureDetector orbDetector = FeatureDetector.create(FeatureDetector.ORB);
		DescriptorExtractor orbExtractor = DescriptorExtractor.create(DescriptorExtractor.ORB);
						
		MatOfKeyPoint objectKeypoints = new MatOfKeyPoint();
		orbDetector.detect(grayObject, objectKeypoints);
		
		MatOfKeyPoint signKeypoints = new MatOfKeyPoint();
		orbDetector.detect(graySign, signKeypoints);
		
		Mat objectDescriptor = new Mat(object.rows(), object.cols(),object.type());
		orbExtractor.compute(grayObject, objectKeypoints,objectDescriptor);
		
		Mat signDescriptor = new Mat(sroadSign.rows(), sroadSign.cols(),sroadSign.type());
		orbExtractor.compute(graySign, signKeypoints,signDescriptor);
		
		
		// Faire le matching
		MatOfDMatch matchs = new MatOfDMatch();
		DescriptorMatcher matcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE);
		matcher.match(objectDescriptor, signDescriptor, matchs);
		System.out.println(matchs.dump());
		Mat matchedImage =new Mat(sroadSign.rows(),sroadSign.cols()*2,sroadSign.type());
		Features2d.drawMatches(sObject, objectKeypoints, sroadSign, signKeypoints, matchs, matchedImage);
		
		
		
		return -1;
	}
	
	
	public static Mat seuillage(Mat input, int seuilRougeOrange, int seuilRougeViolet,int seuilSaturation){
		// Decomposition en 3 cannaux HSV
		Vector<Mat> channels = splitHSVChannels(input);
		
		
		// creation des differentes matrices seuil
		Mat imageSeuilSaturation=seuillage_exemple(channels.get(1), seuilSaturation);
		
		Mat imageSeuilRougeViolet=seuillage_exemple(channels.get(0), seuilRougeViolet);
		
		Mat imageSeuilRougeOrange=new Mat();
		Scalar niveau = new Scalar(seuilRougeOrange);
		Core.compare(channels.get(0), niveau, imageSeuilRougeOrange, Core.CMP_LT);
		
		
		// Union des matrices seuils
		Mat result1=new Mat();
		Core.bitwise_and(imageSeuilSaturation, imageSeuilRougeViolet, result1);
		
		Mat result2=new Mat();
		Core.bitwise_and(imageSeuilSaturation, imageSeuilRougeOrange, result2);
		
		Mat result=new Mat();
		Core.bitwise_or(result1, result2, result);
		
		return result;



	}
	
	//Methode d'exemple qui permet de saturer les couleurs rouges à partir d'un seul seuil 
		public static Mat seuillage_exemple(Mat input, int seuilRougeViolet){
			// Decomposition en 3 cannaux HSV
			Vector<Mat> channels = splitHSVChannels(input);
			//création d'un seuil 
			Scalar rougeviolet = new Scalar(seuilRougeViolet);
			//Création d'une matrice
			Mat rouges=new Mat();
			//Comparaison et saturation des pixels dont la composante rouge est plus grande que le seuil rougeViolet
			Core.compare(channels.get(0), rougeviolet, rouges, Core.CMP_GT);
			//image saturée à retourner
			return rouges;



		}
	
	
	// ----------------------------------------------------------------------------------------
	// Fonctions finales
	// ----------------------------------------------------------------------------------------	
		
	public static JLabel transformationEnHSV(Mat input) {
		Mat transformee=MaBibliothequeTraitementImageEtendue.transformeBGRversHSV(input);
		return MaBibliothequeTraitementImageEtendue.afficheImage("Composantes HSV", transformee);
		
	}
	
	
	public static JLabel detectionSeuillageRouge(Mat input) {
		Mat transformee=MaBibliothequeTraitementImageEtendue.transformeBGRversHSV(input);
		Mat saturee=MaBibliothequeTraitementImage.seuillage(transformee, 6, 170, 110);
		return MaBibliothequeTraitementImageEtendue.afficheImage("Detection et seuillage des rouges", saturee);
	}

	
	public static JLabel detectionContours(Mat input) {
		Mat transformee=MaBibliothequeTraitementImageEtendue.transformeBGRversHSV(input);
		Mat saturee=MaBibliothequeTraitementImage.seuillage(transformee, 6, 170, 110);
		//Création d'une liste des contours à partir de l'image saturée
		List<MatOfPoint> ListeContours= MaBibliothequeTraitementImageEtendue .ExtractContours(saturee,true);
		return MaBibliothequeTraitementImageEtendue .getPicContours(saturee,true);
	}
	
	
	
	public static rendu extractionPanneau(Mat input) {
		Mat transformee=MaBibliothequeTraitementImageEtendue.transformeBGRversHSV(input);
		Mat saturee=MaBibliothequeTraitementImage.seuillage(transformee, 6, 170, 110);
		//Création d'une liste des contours à partir de l'image saturée
		List<MatOfPoint> ListeContours= MaBibliothequeTraitementImageEtendue .ExtractContours(saturee,false);
		
		// Extraction panneaux
		Mat objetrond = new Mat();
		int i=0;
		double [] scores=new double [6];
		rendu retour = new rendu();
		
		//Pour tous les contours de la liste
		for (MatOfPoint contour: ListeContours  ){
			i++;
			objetrond=MaBibliothequeTraitementImage.DetectForm(input,contour);
			
			if (objetrond!=null){
				
				System.out.println(" ");
				MaBibliothequeTraitementImage.afficheImage("Objet rond detécté", objetrond);
				scores[0]=MaBibliothequeTraitementImageEtendue.Similitude_Perso(objetrond,"ref30.jpg");
				System.out.println("score 30 :"+scores[0]);
				scores[1]=MaBibliothequeTraitementImageEtendue.Similitude_Perso(objetrond,"ref50.jpg");
				System.out.println("score 50 :"+scores[1]);
				scores[2]=MaBibliothequeTraitementImageEtendue.Similitude_Perso(objetrond,"ref70.jpg");
				System.out.println("score 70 :"+scores[2]);
				scores[3]=MaBibliothequeTraitementImageEtendue.Similitude_Perso(objetrond,"ref90.jpg");
				System.out.println("score 90 :"+scores[3]);
				scores[4]=MaBibliothequeTraitementImageEtendue.Similitude_Perso(objetrond,"ref110.jpg");
				System.out.println("score 110 :"+scores[4]);
				scores[5]=MaBibliothequeTraitementImageEtendue.Similitude_Perso(objetrond,"refdouble.jpg");
				System.out.println("score double :"+scores[5]);


				//recherche de l'index du maximum et affichage du panneau detecté
				double scoremax=-1;
				int indexmax=0;
				for(int j=0;j<scores.length;j++){
					if (scores[j]>scoremax){scoremax=scores[j];indexmax=j;}}
				
				if(scoremax<0){retour.type="Aucun Panneau détécté";}
				else{switch(indexmax){
				case -1:;break;
				case 0:
					retour.type="Panneau 30 détécté";
					retour.pic=MaBibliothequeTraitementImageEtendue.afficheImage("Panneau 30 détécté", objetrond);
					break;
				case 1:
					retour.type="Panneau 50 détécté";
					retour.pic=MaBibliothequeTraitementImageEtendue.afficheImage("Panneau 50 détécté", objetrond);
					break;
				case 2:
					retour.type="Panneau 70 détécté";
					retour.pic=MaBibliothequeTraitementImageEtendue.afficheImage("Panneau 70 détécté", objetrond);
					break;
				case 3:
					retour.pic=MaBibliothequeTraitementImageEtendue.afficheImage("Panneau 90 détécté", objetrond);
					retour.type="Panneau 90 détécté";
					break;
				case 4:
					retour.type="Panneau 110 détécté";
					retour.pic=MaBibliothequeTraitementImageEtendue.afficheImage("Panneau 110 détécté", objetrond);
					break;
				case 5:
					retour.type="Panneau interdiction de dépasser détécté";
					retour.pic=MaBibliothequeTraitementImageEtendue.afficheImage("interdiction de dépasser", objetrond);
					break;
				}}

			}
		}
		return retour;
	}

	
	public static void extractionDescriptors(Mat input) {
		
		Mat transformee=MaBibliothequeTraitementImageEtendue.transformeBGRversHSV(input);
		Mat saturee=MaBibliothequeTraitementImage.seuillage(transformee, 6, 170, 110);
		//Création d'une liste des contours à partir de l'image saturée
		List<MatOfPoint> ListeContours= MaBibliothequeTraitementImageEtendue .ExtractContours(saturee,false);
		
		// Extraction des caractéritiques
		Mat objetrond = new Mat();
						
		int i=0;
		double [] scores=new double [6];
						
		//Pour tous les contours de la liste
		for (MatOfPoint contour: ListeContours  ){
			i++;
			objetrond=MaBibliothequeTraitementImage.DetectForm(input,contour);
							
					if (objetrond!=null){
							String[] NomFichier= {"ref30.jpg","ref50.jpg","ref70.jpg","ref90.jpg","ref110.jpg","refdouble.jpg"};
							double[] ResultatEstimation= {0,0,0,0,0,0};
							
							for(int n=0;n<6;n++) {
								// Extraction caracteristiques et matching
								String signfile=NomFichier[n];
								Mat object=objetrond;
								
								//Mise à l'échelle
								Mat sroadSign = Highgui.imread(signfile);
								
								Mat sObject = new Mat();
								Imgproc.resize(object, sObject, sroadSign.size());
								Mat grayObject = new Mat(sObject.rows(), sObject.cols(),sObject.type());
								Imgproc.cvtColor(sObject, grayObject, Imgproc.COLOR_BGRA2GRAY);
								Core.normalize(grayObject, grayObject, 0, 255, Core.NORM_MINMAX);
								
								Mat graySign=new Mat(sroadSign.rows(),sroadSign.cols(),sroadSign.type());
								Imgproc.cvtColor(sroadSign, graySign, Imgproc.COLOR_BGRA2GRAY);	
								Core.normalize(sroadSign, sroadSign, 0, 255, Core.NORM_MINMAX);			
												
								//Extraction des caractéristiques et keypoints
								FeatureDetector orbDetector = FeatureDetector.create(FeatureDetector.ORB);
								DescriptorExtractor orbExtractor = DescriptorExtractor.create(DescriptorExtractor.ORB);
												
								MatOfKeyPoint objectKeypoints = new MatOfKeyPoint();
								orbDetector.detect(grayObject, objectKeypoints);
							
								MatOfKeyPoint signKeypoints = new MatOfKeyPoint();								
								orbDetector.detect(graySign, signKeypoints);

								
								Mat objectDescriptor = new Mat(object.rows(), object.cols(),object.type());
								orbExtractor.compute(grayObject, objectKeypoints,objectDescriptor);
								
								Mat signDescriptor= new Mat(sroadSign.rows(), sroadSign.cols(),sroadSign.type());
	
								orbExtractor.compute(graySign, signKeypoints,signDescriptor);
								
								// Faire le matching
								MatOfDMatch matchs = new MatOfDMatch();
								DescriptorMatcher matcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE);
								matcher.match(objectDescriptor, signDescriptor, matchs);
								
								//System.out.println(matchs.dump());
								Mat matchedImage =new Mat(sroadSign.rows(),sroadSign.cols()*2,sroadSign.type());
								Features2d.drawMatches(sObject, objectKeypoints, sroadSign, signKeypoints, matchs, matchedImage);
								MaBibliothequeTraitementImageEtendue.afficheImage("descriptor", matchedImage);
								
								
								// Etude resultats
								double result=0;
								for(int k=0;k<matchs.size().height;k++) {
									result=result+matchs.get(k,0)[3];	
								}
								
								ResultatEstimation[n]=result/matchs.size().height;
								System.out.println("Score "+NomFichier[n]+" : "+ResultatEstimation[n]);
							}
							
							//recherche de l'index du maximum et affichage du panneau detecté
							double scoremax=1000000;
							int indexmax=0;
							for(int j=0;j<ResultatEstimation.length;j++){
								if (ResultatEstimation[j]<scoremax){
									scoremax=ResultatEstimation[j];
									indexmax=j;}}
							
							if(scoremax>10000){System.out.println("Aucun Panneau détécté");}
							else{switch(indexmax){
							case -1:;break;
							case 0:
								System.out.println("Panneau 30 détécté");
								MaBibliothequeTraitementImageEtendue.afficheImage("Panneau 30 détécté", objetrond);
								break;
							case 1:
								System.out.println("Panneau 50 détécté");
								MaBibliothequeTraitementImageEtendue.afficheImage("Panneau 50 détécté", objetrond);
								break;
							case 2:
								System.out.println("Panneau 70 détécté");
								MaBibliothequeTraitementImageEtendue.afficheImage("Panneau 70 détécté", objetrond);
								break;
							case 3:
								MaBibliothequeTraitementImageEtendue.afficheImage("Panneau 90 détécté", objetrond);
								System.out.println("Panneau 90 détécté");
								break;
							case 4:
								System.out.println("Panneau 110 détécté");
								MaBibliothequeTraitementImageEtendue.afficheImage("Panneau 110 détécté", objetrond);
								break;
							case 5:
								System.out.println("Panneau interdiction de dépasser détécté");
								MaBibliothequeTraitementImageEtendue.afficheImage("interdiction de dépasser", objetrond);
								break;
							}
							
				
						}
			}
		}
	}
	
}



