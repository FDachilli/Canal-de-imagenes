package Herramientas;
import java.util.ArrayList;

public class ArbolHuffman {

		class  Nodo{
			Color color;
			Nodo izq,der;
		}
		Nodo raiz;
		
		public ArbolHuffman(){
			raiz = new Nodo();
		};
		
		//insertar() es el encargado de ordenar el vector y crear el arbol de Huffman.
		public void insertar (ArrayList<Color> vect){
			
			//Paso el vector de colores a la estructura del tipo del arbol (Nodo).
			
			ArrayList<Nodo> vect_nodo = new ArrayList<>();
			
			for(int i=0; i<vect.size(); i++){
				Nodo nuevo_nodo = new Nodo();
				nuevo_nodo.color = vect.get(i);
				nuevo_nodo.izq = null;
				nuevo_nodo.der = null;
				vect_nodo.add(nuevo_nodo);
			}
	
			insertar_(vect_nodo);
		}

		private void insertar_ (ArrayList<Nodo> vect){
			double primer_min=2;
			double segundo_min=2;
			int primer_pos=0;
			int segundo_pos=0;
			Nodo padre = new Nodo();
			padre.color = new Color();
			padre.der = new Nodo();
			padre.izq = new Nodo();
			

			//Busco el color con minimas probabilidades.
			for(int i=0; i<vect.size(); i++){
				if(primer_min>=vect.get(i).color.getProbabilidad()){
					primer_min = vect.get(i).color.getProbabilidad();
					primer_pos = i;
				}
			}	

			//Agrego el primer minimo y remuevo al nodo que ya no me interesan saber sus probabilidades. 
			padre.izq = vect.get(primer_pos);
			vect.remove(primer_pos);
			
			//Busco el segundo color con minimas probabilidades.
			for(int i=0; i<vect.size(); i++){
				if(segundo_min>=vect.get(i).color.getProbabilidad()){
					segundo_min = vect.get(i).color.getProbabilidad();
					segundo_pos = i;
				}
			}

			padre.der = vect.get(segundo_pos);
			vect.remove(segundo_pos);
				
			//El primer minimo esta a la derecha del segundo minimo
			if(primer_pos>segundo_pos){
				Nodo aux = new Nodo();
				aux= padre.der;
				padre.der=padre.izq;
				padre.izq=aux;
			}
				
			
			//Preciso guardar la suma de la probabilidad de los hijos, para eso uso la clase Color. (Solo preciso la probabilidad para orden y armar el arbol)
			padre.color.setProbabilidad(primer_min+segundo_min);
			
			//Al estar ordenados dada la posicion en la lista se les asigna si son hijos a la izq. o a la der. del padre.

			vect.add(padre);
			if(vect.size()==1){	//Llegue al nodo final (La raiz).
				this.raiz=padre;
			}
			else{
				insertar_(vect);
			}
		}
		
		public void set_codigos(){
			set_codigos(this.raiz,"");
		}
		
		private void set_codigos(Nodo nodo, String codigo){

			//Si llego a una hoja agrego el codigo.
			if(nodo.izq==null && nodo.der==null){
				nodo.color.setCodigo(codigo);
			}
			else{	//Rama izq es un 0, rama der. un 1.
					set_codigos(nodo.izq,codigo+'0');
					set_codigos(nodo.der,codigo+'1');
				}
		}

		//Bastante parecido a get_codigo(). 

		public void set_longitudes(){
			set_longitud(this.raiz,0);
		}
		
		public void set_longitud(Nodo nodo, int longitud){
			if(nodo.izq==null && nodo.der==null){
				nodo.color.setLongitud(longitud);
			}else{
				set_longitud(nodo.izq,longitud+1);
				set_longitud(nodo.der,longitud+1);
			}	
		}
		
}
