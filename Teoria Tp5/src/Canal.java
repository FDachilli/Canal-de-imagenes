import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import javax.imageio.ImageIO;

import Herramientas.Calculos;
import Herramientas.Color;


public class Canal {
	Calculos calculos;
	ArrayList <Color> colores;
	double [][] transCanal;
	double [][] matPerdida;
	double [][] matConjunta;
	double [] prob_original;
	double [] prob_posterior;
	final int MAX_ITERACIONES = 100000;
	final int MIN_ITERACIONES = 1000;
	int pasos_conv;
	
	public Canal(){
		this.calculos = new Calculos();
		this.colores = new ArrayList <>();
		this.transCanal = new double [8][8]; 
		this.matPerdida = new double [8][8];
		this.matConjunta = new double [8][8];
		this.prob_original = new double [8];
		this.prob_posterior = new double [8];
		
	}

	public void set_matriz_canal(BufferedImage original, BufferedImage post_canal) {
		calculos.calcular_matriz_conjunta(original, post_canal, colores, matConjunta);
		calculos.calcular_matriz_canal (matConjunta,prob_original, transCanal);
		calculos.probabilidad(colores, original);
	}
	
	public void set_matriz_perdida() {
		calculos.calcular_matriz_perdida (matConjunta, prob_posterior, matPerdida);
	}
	
	
	public void mirar(){
		for (int i=0; i<8;i++){
			double suma = 0;
			for (int j=0; j<8; j++)
			{
				suma = suma + transCanal [j][i];
				System.out.println("Probabilidad simbolos (" + j + "," + i + "): "+ transCanal [j][i]);
			}
			
			System.out.println("Suma probabilidades de " + i + ": "+ suma);
	}
	}
	
//La imagen se necesita para utilizar el metodo probabilidad();
	public void simular_por_cantidad(int cant_Max, BufferedImage imagen){
		double prob=0;
		double acum[]= new double [8];
		int indice_o=0;	//Indice del pixel que sale
		int indice_d=0;	//Indice del pixel que llega
		int R;
    	double [][] matAcumulada = calcular_matriz_acumulada (transCanal);
		
    	//Seteo en 0
		calculos.set_ceros(matPerdida);
		calculos.set_ceros(prob_posterior);
		calculos.set_ceros(matConjunta);
		
		//Setea los p(x), necesarios para la acumulada
		this.calculos.probabilidad(this.colores,imagen);
		
		//Creo la acumulada
		acum[0]=colores.get(0).getProbabilidad();
		for(int i=1; i<acum.length; i++){
			acum[i]=acum[i-1]+colores.get(i).getProbabilidad();
		}
		
		//Itero hasta cant_Max y creo la matriz conjunta
		for(int n=0;n<cant_Max;n++){
			indice_o=0;
			prob = Math.random();
			
			while(indice_o<colores.size()-1 && acum[indice_o]<prob){
				indice_o++;
			}	
			int i = obtener_mayor_prob(indice_o, matAcumulada);
			R=colores.get(i).getR();
			indice_d = calculos.verificar_indice(R,this.colores);
			matConjunta[indice_d][indice_o]++;	
		}
		
		for(int i=0; i<8; i++){
			for(int j=0; j<8; j++){
				matConjunta[j][i] /= cant_Max;
			}
		}
		
		//calculos.set_ceros(transCanal);
		
		//Creo la matriz condicional
		calculos.calcular_matriz_canal(matConjunta,new double[8],transCanal);
		calculos.calcular_matriz_perdida(matConjunta,prob_posterior,matPerdida);
	}
	
	public boolean converge(double prob, double prob_ant, double epsilon){
		
		if(Math.abs(prob-prob_ant)<epsilon)
			return true;
		else
			return false;
	}
	
	//Optimizado para utilizar con convergencia. Basicamente lo que hace es no vaciar la matriz conjunta, sino que seguir trabajando sobre esta. 
		public void simular_por_cantidad_opt(int cant_Max, BufferedImage imagen, double acum[]){
			double prob=0;
			int indice_o=0;	//Indice del pixel que sale
			int indice_d=0;	//Indice del pixel que llega
			int R;
	    	double [][] matAcumulada = calcular_matriz_acumulada (transCanal);
	    	
			prob = Math.random();
			
			while(indice_o<colores.size()-1 && acum[indice_o]<prob){
				indice_o++;
			}	
			
			int i = obtener_mayor_prob(indice_o, matAcumulada);
			
			R=colores.get(i).getR();
			
			indice_d = calculos.verificar_indice(R,this.colores);
			
			for(int i1=0; i1<8; i1++){
				for(int j=0; j<8; j++){
					matConjunta[j][i1] *= cant_Max-1;
				}
			}

			matConjunta[indice_d][indice_o]++;	

			for(int i1=0; i1<8; i1++){
				for(int j=0; j<8; j++){
					matConjunta[j][i1] /= cant_Max;
				}
			}			
			
			//Creo la matriz condicional
			calculos.calcular_matriz_canal(matConjunta,new double[8],transCanal);
			calculos.calcular_matriz_perdida(matConjunta,prob_posterior,matPerdida);
		}
	
	public void simular_por_convergencia(double epsilon, BufferedImage imagen){
		double prob_act_ruido=0;
		double prob_ant_ruido=-1;
		double prob_act_perdida=0;
		double prob_ant_perdida=-1;
		double prob_act_inf=0;
		double prob_ant_inf=-1;
		double [][] aux_trans = new double[transCanal.length][];
		double [][] aux_conj = new double[matConjunta.length][];
		int n = 0;

		double acum[]= new double [8];
		
		//Copio la matriz condicional
		for(int i = 0; i < transCanal.length; i++)
		    aux_trans[i] = transCanal[i].clone();

		//Copio la matriz conjunta ? Es necesario ? 
		for(int i = 0; i < matConjunta.length; i++)
		    aux_conj[i] = matConjunta[i].clone();
		
		//Setea los p(x), necesarios para la acumulada
			this.calculos.probabilidad(this.colores,imagen);
				
		//Creo la acumulada
		acum[0]=colores.get(0).getProbabilidad();
		for(int i=1; i<acum.length; i++){
			acum[i]=acum[i-1]+colores.get(i).getProbabilidad();
		}

    	//Seteo en 0
		calculos.set_ceros(matPerdida);
		calculos.set_ceros(matConjunta);
		
		while(n<MIN_ITERACIONES){
			calculos.set_ceros(prob_posterior);
			
			for(int i = 0; i < transCanal.length; i++)
				transCanal[i] = aux_trans[i].clone();
			
			n++;
			this.simular_por_cantidad_opt(n, imagen, acum);

			prob_ant_ruido = prob_act_ruido;
			prob_act_ruido = this.calcular_ruido();
			
			prob_ant_perdida = prob_act_perdida;
			prob_act_perdida = this.calcular_perdida();
			
			prob_ant_inf = prob_act_inf;
			prob_act_inf = this.informacion_mutua();
		}
		
		while(!(converge(prob_act_ruido,prob_ant_ruido,epsilon) && converge(prob_act_perdida,prob_ant_perdida,epsilon) && converge(prob_act_inf,prob_ant_inf,epsilon)) && n<MAX_ITERACIONES){


			calculos.set_ceros(prob_posterior);
			
			for(int i = 0; i < transCanal.length; i++)
				transCanal[i] = aux_trans[i].clone();
			
			n++;
			this.simular_por_cantidad_opt(n, imagen, acum);

			prob_ant_ruido = prob_act_ruido;
			prob_act_ruido = this.calcular_ruido();
			
			prob_ant_perdida = prob_act_perdida;
			prob_act_perdida = this.calcular_perdida();
			
			prob_ant_inf = prob_act_inf;
			prob_act_inf = this.informacion_mutua();
		}
		pasos_conv = n;
	}
	
	public int get_pasos(){
		return pasos_conv;
	}
	public int obtener_mayor_prob(int indice, double[][] matAcumulada){
		
		double r= Math.random();
		for (int i=0; i<8; i++)
			if (r < matAcumulada [i][indice])
				return i;
		return -1;
	}

	
	public double calcular_ruido (){
		double ruido=0;
		for(int i=0; i < 8; i++){
			double suma = 0;
			for(int j=0; j < 8; j++){
				if(transCanal[j][i] != 0)//Para evitar NaN
					suma += -transCanal[j][i]*(double)(Math.log10(transCanal[j][i])/Math.log10(2));
			}
			ruido += prob_original [i]*suma;
		}
		return ruido;
	}
	
	
	public double calcular_perdida (){
		
		/*for(int t=0; t<8;t++){
			double s=0;
			for (int r=0;r<8;r++){
				s=s+matPerdida [t][r];
				System.out.print (matPerdida [t][r]+ "---");
			}
			System.out.println ("");
			System.out.println ("------------------------------------");
			System.out.println (s);
			System.out.println ("------------------------------------");
		}*/
		
		double perdida=0;
		for(int i=0; i < 8; i++){
			double suma = 0;
			for(int j=0; j < 8; j++){
				if(matPerdida[i][j] != 0)//Para evitar NaN
					suma += -matPerdida[i][j]*(double)(Math.log10(matPerdida[i][j])/Math.log10(2));
			}
			
			perdida += prob_posterior[i]*suma;
		}
		return perdida;
	}
	
	
	public double informacion_mutua (){
		double i_m=0;
		for (int i=0; i<8; i++){
			for (int j=0; j<8;j++){
				if(matConjunta[i][j] != 0){
				double x= prob_original[j]*prob_posterior[i];
				i_m += matConjunta [i][j]*(double)(Math.log10(matConjunta[i][j]/x)/Math.log10(2));
			}
		  }
		}
		return i_m;
	}
	
	public double [][] calcular_matriz_acumulada (double [][] matriz){
		double [][] matAcumulada = matriz;
    	//Calculo la matriz acumulada
    	for (int j=0; j<8; j++)
    		for (int i=1; i<8; i++)
    			matriz [i][j] += matriz [i-1][j]; 
    	return matAcumulada;
	}
	
	
	public BufferedImage enviar_imagen(BufferedImage imagen) {
		int R;
		int indice;
    	int indice_aux=0;
    	double [][] matAcumulada = calcular_matriz_acumulada (transCanal);

		BufferedImage imagen_aux = new BufferedImage(imagen.getWidth(), imagen.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
		
		for(int i=0; i<imagen.getHeight(); i++){
			for(int j=0; j<imagen.getWidth(); j++){
				
				int rgbO = imagen.getRGB(j, i);
		 
				java.awt.Color color_auxO = new java.awt.Color(rgbO, true);
				R = color_auxO.getRed();
				indice = calculos.verificar_indice(R, this.colores);
				
				if(indice!=-1){
					indice_aux = obtener_mayor_prob(indice, matAcumulada);
					if (indice_aux!=-1)
						R = this.colores.get(indice_aux).getR();
				}
				
				java.awt.Color rgb = new java.awt.Color(R,R,R);
				imagen_aux.setRGB(j, i, rgb.getRGB());
			}
		}
		return imagen_aux;
	}
}
