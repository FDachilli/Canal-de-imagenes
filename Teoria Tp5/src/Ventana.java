import java.awt.Button;
import java.awt.Checkbox;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.Format;
import java.text.NumberFormat;

import javax.imageio.ImageIO;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.text.NumberFormatter;

public class Ventana extends Frame{

	Canal canal_stars;
	Canal canal_nebula;
	Canal canal_actual;

	BufferedImage original_stars = null;
	BufferedImage por_Canal_stars = null;
	BufferedImage original_nebula = null;
	BufferedImage por_Canal_nebula = null;
	BufferedImage imagen_actual = null;
	
	double ruido;
	double perdida;
	double i_m;
	
	JFormattedTextField jtf;
	
	public class Resultado_Stars implements ActionListener{
		Ventana v;
		public Resultado_Stars(Ventana v){
			super();
			this.v = v;
		}
		public void actionPerformed(ActionEvent ae){
			this.v.canal_stars_orig();
			JOptionPane.showMessageDialog(null, "Ruido: " +v.get_Ruido() +"\n Perdida: " +v.get_Perdida() +"\n Informacion Mutua: " +v.get_Informacion(), "Resultados", 1);
		}
	}
	
	public class Resultado_Nebula implements ActionListener{
		Ventana v;
			public Resultado_Nebula(Ventana v){
				super();
				this.v = v;
			}
		  public void actionPerformed(ActionEvent ae){
				this.v.canal_nebula_orig();
				JOptionPane.showMessageDialog(null, "Ruido: " +v.get_Ruido() +"\n Perdida: " +v.get_Perdida() +"\n Informacion Mutua: " +v.get_Informacion(), "Resultados", 1);
		  }
		}
	
	public class Resultado_Sim_Conv implements ActionListener{
		Ventana v;
		public Resultado_Sim_Conv(Ventana v){
			super();
			this.v = v;
		}
	  public void actionPerformed(ActionEvent ae){
		  	Integer n = 0;
		  	this.v.simular_convergencia(0.00001);
			JOptionPane.showMessageDialog(null, "Ruido: " +v.get_Ruido() +"\n Perdida: " +v.get_Perdida() +"\n Informacion Mutua: " +v.get_Informacion() +"\n Converge en la iteracion: " +canal_actual.get_pasos(), "Resultados", 1);
	  }
	}

	public class Resultado_Sim_N implements ActionListener{
		Ventana v;
		public Resultado_Sim_N(Ventana v){
			super();
			this.v = v;
		}
	  public void actionPerformed(ActionEvent ae){
		  
		  String valor = jtf.getText();
		  Integer  a = Integer.parseInt(valor);
		  System.out.println("Valor:" +valor);
		  	this.v.simular_cantidad(a);
			JOptionPane.showMessageDialog(null, "Ruido: " +v.get_Ruido() +"\n Perdida: " +v.get_Perdida() +"\n Informacion Mutua: " +v.get_Informacion(), "Resultados", 1);
	  }
	}
	
	public class Salir implements ActionListener{
		Ventana v;
		public Salir(Ventana v){
			super();
			this.v = v;
		}
	  public void actionPerformed(ActionEvent ae){
		  v.dispose();
	  }
	}
	
	public double get_Ruido(){
		return ruido;
	}
	
	public double get_Perdida(){
		return perdida;
	}
	
	public double get_Informacion(){
		return i_m;
	}
	
	public Ventana(){
		super("Teoria TP5. 2016 Colacci, Joaquin. D'Achilli, Franco");
		
		FlowLayout fl = new FlowLayout();
	
		this.setBackground(new Color(255,192,203));
		setLayout(fl);
		Button b1 = new Button("Calcular canal con stars_8");
		b1.addActionListener(new Resultado_Stars(this));
		Button b2 = new Button("Calcular utilizando convergencia");
		b2.addActionListener(new Resultado_Sim_Conv(this));
		Button b3 = new Button("Calcular utilizando N iteraciones");
		b3.addActionListener(new Resultado_Sim_N(this));
		Button b4 = new Button("Pasar imagen nebula por el canal");
		b4.addActionListener(new Resultado_Nebula(this));
		Button b5 = new Button("Calcular utilizando convergencia");
		b5.addActionListener(new Resultado_Sim_Conv(this));
		Button b6 = new Button("Calcular utilizando N iteraciones");
		
		Button b7 = new Button("Salir");
		b7.addActionListener(new Salir(this));
		
		jtf = new JFormattedTextField();
		jtf.setPreferredSize (new Dimension( 200, 24 ));
		jtf.setText("Iteraciones");

		b6.addActionListener(new Resultado_Sim_N(this));
		
		add(new Label("Stars_8"));
		add(b1);
		add(b2);
		add(b3);
		add(new Label("\nNebula"));
		add(b4);
		add(b5);
		add(b6);
		add(jtf);
		add(b7);
		setSize(700,500);
		setVisible(true);
		
	}
	
	public void canal_stars_orig(){
		//Canal canal_stars = new Canal();
		canal_stars = new Canal();
		try {
		original_stars = ImageIO.read(new File("stars_8.bmp"));
		por_Canal_stars =  ImageIO.read(new File("stars_8_noisy.bmp"));
	
		//Crea el canal a partir de dos imagenes
		canal_stars.set_matriz_canal(original_stars,por_Canal_stars);
		
		canal_stars.set_matriz_perdida();
		ruido = canal_stars.calcular_ruido();
		System.out.println("Ruido de canal de Stars_8: "+ ruido);
		perdida = canal_stars.calcular_perdida();
		System.out.println("Perdida de canal de Stars_8: "+ perdida);
		i_m = canal_stars.informacion_mutua();
		System.out.println("Informacion mutua de canal de Stars_8: "+ i_m);
		
		imagen_actual = original_stars;
		canal_actual = canal_stars;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	
	public void simular_cantidad(int n){
		canal_actual.simular_por_cantidad(n, imagen_actual);
		ruido = canal_actual.calcular_ruido();
		System.out.println("Ruido de canal por simulacion: "+ ruido);
		perdida = canal_actual.calcular_perdida();
		System.out.println("Perdida de canal por simulacion: "+ perdida);
		i_m = canal_actual.informacion_mutua();
		System.out.println("Informacion mutua de canal de la simulacion: "+ i_m);
	}
	
	public void canal_nebula_orig(){
		// IMAGEN NEBULA 
		// ----------------------------------------------------------------------
		
		try{
		original_nebula = ImageIO.read(new File("nebula1.bmp"));
		//La envio por el canal de stars, por eso afecta la simulacion por convergencia
		ImageIO.write(canal_stars.enviar_imagen(original_nebula), "bmp", new File("nebula_noisy.bmp"));
		
		//Con imagen "Nebula"
		por_Canal_nebula =  ImageIO.read(new File("nebula_noisy.bmp"));
		canal_nebula = new Canal ();
		
		//Crea el canal a partir de dos imagenes
		canal_nebula.set_matriz_canal(original_nebula,por_Canal_nebula);
		//canal_nebula.mirar();
		canal_nebula.set_matriz_perdida();
		ruido = canal_nebula.calcular_ruido();
		System.out.println("Ruido de canal de Nebula: "+ ruido);
		perdida = canal_nebula.calcular_perdida();
		System.out.println("Perdida de canal de Nebula: "+ perdida);
		i_m = canal_nebula.informacion_mutua();
		System.out.println("Informacion mutua de canal de Nebula: "+ i_m);
		
		imagen_actual = original_nebula;
		canal_actual = canal_nebula;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	
	public void simular_convergencia(double epsilon){
		canal_actual.simular_por_convergencia(epsilon,imagen_actual);
		ruido = canal_actual.calcular_ruido();
		System.out.println("Ruido de canal por simulacion: "+ ruido);
		perdida = canal_actual.calcular_perdida();
		System.out.println("Perdida de canal por simulacion: "+ perdida);
		i_m = canal_actual.informacion_mutua();
		System.out.println("Informacion mutua de canal de la simulacion: "+ i_m);
	}
}
