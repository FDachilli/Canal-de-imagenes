package Herramientas;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

public class Calculos {

	
	
//Se encarga de verificar el color de cada pixel, y guardar la cantidad total de repiticiones.
public static void verificar_color(int R, ArrayList<Color> vect){

int i = 0;
boolean find = false;
    
   while(i<=vect.size() && !find){
       if(i==vect.size()){
           Color nuevo_color = new Color();
           nuevo_color.setR(R);
           nuevo_color.addCantidad();
           vect.add(nuevo_color);
           find = true;
       }else
           if(vect.get(i).getR()==R){
               find = true;
               vect.get(i).addCantidad();
           }else
               i++;
   }
}

public static int verificar_color_indice (int R, ArrayList <Color> colores){
	
	int i = 0;
	boolean find = false;
	    
	   while(i<=colores.size() && !find){
	       if(i==colores.size()){
	           Color nuevo_color = new Color();
	           nuevo_color.setR(R);
	           colores.add(nuevo_color);
	           return i;
	       }else
	           if(colores.get(i).getR()==R){
	               return i;
	           }else
	               i++;
	   }
	   return -1;
}

public static int verificar_indice (int R, ArrayList<Color> arreglo){
int i = 0;
    
   while(i<arreglo.size()){
           if(arreglo.get(i).getR()==R)
               return i;
           else
               i++;
   }
return -1;
}
                


public static void calcular_probabilidad (BufferedImage buffer_img, ArrayList<Color> arreglo){
	
int R=0; 

/*Recorro la imagen y voy mirando cada pixel*/
   for(int i=0;i<buffer_img.getWidth();i++){ 
       for(int j=0;j<buffer_img.getHeight();j++){
    	   int rgb = buffer_img.getRGB(i, j);
    	   java.awt.Color color_aux = new java.awt.Color(rgb, true);
    	   
           R = color_aux.getRed(); 
           verificar_color(R,arreglo); 
           // Resultados de la forma[(R,G,B),(R,G,B),(155,155,2)]
       }//Fin del for para recorrer pixeles 
   }//Fin del for para recorrer pixeles
    
for(int i=0; i<8; i++){ 
   arreglo.get(i).setProbabilidad((double) arreglo.get(i).getCantidad() / (buffer_img.getWidth()*buffer_img.getHeight()));
}
}


//Calcula la probabilidad condicional entre un pixel de una determinada posicion en una imagen y con el pixel de la misma posicion de otra.

public static void calcular_matriz_conjunta (BufferedImage original, BufferedImage post,ArrayList <Color> colores, double [][] matConjunta){
	int R1=0;
	int R2=0;
	/*Recorro la imagen y voy mirando cada pixel*/ 
		for(int i=0;i<original.getWidth();i++){ 
			for(int j=0;j<original.getHeight();j++){
		    	int rgbO = original.getRGB(i, j);
		    	java.awt.Color color_auxO = new java.awt.Color(rgbO, true);
				R1 = color_auxO.getRed(); 
				int rgbC = post.getRGB(i, j);
		    	java.awt.Color color_auxC = new java.awt.Color(rgbC, true);
		    	R2 = color_auxC.getRed(); 		
				int indice1= verificar_color_indice(R1, colores);
				int indice2= verificar_color_indice(R2, colores);
				//busco indices para incrementar la matriz
				matConjunta[indice2][indice1]=matConjunta[indice2][indice1]+ 1;
				//incremento las transiciones del simbolo
				colores.get(indice1).addCantidad();
			}//Fin del for para recorrer pixeles 
		}//Fin del for para recorrer pixeles
	
		for (int k=0; k<8;k++)
			for(int h=0; h<8; h++){		
 				matConjunta [h][k]= matConjunta [h][k]/(original.getHeight()*original.getWidth());
				
			}
}

public void set_ceros(double[][] mat){
	for (int i=0; i<8; i++){
		for (int j=0; j<8; j++){
			mat[i][j] = 0;
		}
	}
}

public void set_ceros(double[] vec){
	for (int i=0; i<8; i++){
			vec[i] = 0;
	}
}

public void calcular_matriz_canal (double [][] matConjunta, double [] arreglo, double[][]transCanal){
	
	for (int i=0; i<8; i++){//calculo probabilidades de x
		for (int j=0; j<8; j++){
			arreglo [i] += matConjunta [j][i];
		}
	}
	
	for (int i=0; i<8;i++){
		for (int j=0; j<8;j++){
			if(arreglo[i]!=0)
				transCanal [j][i]= (matConjunta [j][i]/arreglo [i]);
			else
				transCanal[j][i] = 0;
		}
	}
}

public void calcular_matriz_perdida (double [][] matConjunta, double [] prob_posterior, double[][]matPerdida){
	
	for (int i=0; i<8; i++){//calculo probabilidades de y
		for (int j=0; j<8; j++){
			prob_posterior [i] += matConjunta [i][j];
		}
	}
	for (int i=0; i<8;i++){
		for (int j=0; j<8;j++){
			if (prob_posterior[i]!=0)
				matPerdida [i][j]= (matConjunta [i][j]/prob_posterior [i]);
			else
				matPerdida [i][j]=0;
		}
	}	
}


public void probabilidad (ArrayList <Color> arreglo, BufferedImage imagen){
	double n= imagen.getHeight()*imagen.getWidth();
	for (int i=0; i<8; i++){
		double x= arreglo.get(i).getCantidad()/n;
		arreglo.get(i).setProbabilidad(x);
	}
}


//Calcula la probabilidad condicional de un pixel con el siguiente. 
public static void calcular_prob_condicional (BufferedImage buffer_img, ArrayList<Color> arreglo, double matriz [][]){
int R1=0;
int R2=0; 
int [] transiciones = new int [8]; //guardo la cantidad de transiciones para calcular la probabilidad

/*Recorro la imagen y voy mirando cada pixel*/ 
	for(int i=0;i<buffer_img.getWidth();i++){ 
		for(int j=0;j<buffer_img.getHeight();j+=2){
	    	int rgb = buffer_img.getRGB(i, j);
	    	java.awt.Color color_aux = new java.awt.Color(rgb, true);
			R1 = color_aux.getRed(); 
			if ((j==buffer_img.getHeight()-1) && (i!=buffer_img.getWidth())){
				//Convencion (si los pixeles son impar considero el ultimo como dos iguales)
				R2 = R1; 
			} 
			else{
				buffer_img.getRGB(i, j+1);
		    	color_aux = new java.awt.Color(rgb, true);
				R2 = color_aux.getRed(); 
			}
					
			int indice1= verificar_indice(R1,arreglo);
			int indice2= verificar_indice(R2,arreglo);
			//busco indices para incrementar la matriz
			matriz[indice2][indice1]=matriz[indice2][indice1]+1;
			//incremento las transiciones del simbolo
			transiciones [indice1]= transiciones [indice1]+1;			
			
			// Resultados de la forma[(R,G,B),(R,G,B),(155,155,2)]
		}//Fin del for para recorrer pixeles 
	}//Fin del for para recorrer pixeles
	for (int k=0; k<8;k++)
		for(int h=0; h<8; h++){
			matriz [k][h]=matriz[k][h]/transiciones[k];
		}
}

public static void probabilidad_pares (ArrayList<Color> arreglo, double[][] matriz, ArrayList <Color> pares ){
for (int i=0; i<8; i++)
	for (int j=0; j<8; j++){
		Color aux= new Color();
		aux.setR(i);
		
		Color c= new Color();
		c.setR(j);
		aux.addColor(c);
		
		aux.setProbabilidad(arreglo.get(i).getProbabilidad() * matriz [i][j]);
		pares.add(aux);
	}

double suma=0;

for (int h=0; h<pares.size(); h++){
	suma=suma+pares.get(h).getProbabilidad();
}
}

public static double longitud_media (ArrayList <Color> colores){
double l=0;
for (int i=0; i< colores.size();i++){
	l=l+(colores.get(i).getProbabilidad()*colores.get(i).getLongitud());
}

if (colores.size()==8)
	return l;
else
	return l/2;
}

public static double entropia_sin_memoria (ArrayList <Color> colores){
double l=0;


for (int i=0; i< colores.size();i++){
	l=l+(colores.get(i).getProbabilidad()*(Math.log10(colores.get(i).getProbabilidad())/Math.log10(2)));
}

return -l;
}

public static double entropia_con_memoria (double [][] matriz, ArrayList <Color> colores){
double h = 0;
for (int i=0; i<8; i++)
	for (int j=0; j<8;j++){
		h=h+(colores.get(i).getProbabilidad()*(-matriz[i][j]*(Math.log10(matriz[i][j])/Math.log10(2))));
	}
return h;
}
}
