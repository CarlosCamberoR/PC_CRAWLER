package crawler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.swing.JOptionPane;

/**
 * 
 * @author Alberto Manuel Campos Clemente
 * @author Carlos Cambero Rojas
 * @version v4
 */
public class Thesaurus {
	private Map<String, TreeSet<String>> thesauro = new TreeMap<String, TreeSet<String>>();

	public Map<String, TreeSet<String>> getThesauro() {
		return thesauro;
	}

	public void setThesauro(Map<String, TreeSet<String>> thesauro) {
		this.thesauro = thesauro;
	}

	public String eliminarTildes(String cadena) {
		String cadenaSinTildes = cadena.replaceAll("[áÁ]", "a").replaceAll("[éÉ]", "e").replaceAll("[íÍ]", "i")
				.replaceAll("[óÓ]", "o").replaceAll("[úÚ]", "u");
		return cadenaSinTildes;
	}

	// Método que permite comprobar si un token existe o no dentro de la estructura.
	public boolean existe(String token) {
		boolean existe = false;
		if (thesauro.get(token) != null) {
			existe = true;
		}
		return existe;
	}

	// Método que permite comporbar si en el fichero dado existe el thesauro
	// guardado
	public boolean existeThesauro(File file) {
		boolean existe = false;

		if (file.isDirectory()) {

			File[] listFiles = file.listFiles();
			int i = 0;

			while (i < listFiles.length) {
				if (listFiles[i].getName().equals("Thesauro.ser")) {
					existe = true;
				}
				i++;
			}

		}
		return existe;
	}

	// Permite cargar el thesauro del fichero a la estructura de datos
	public void cargar(File file) {
		Objeto o = new Objeto();
		this.thesauro = o.cargarThesauro(file.getAbsolutePath() + "/Thesauro.ser");
	}

	// Procesa el fichero del thesauro para introducirlo en la estructura
	public void procesarThesauro() throws IOException {
		BufferedReader br = new BufferedReader(new FileReader("./resources/Thesaurus.thesauro"));
		String linea;

		while ((linea = br.readLine()) != null) {

			if (!linea.contains("#")) {

				StringTokenizer st = new StringTokenizer(eliminarTildes(linea.toLowerCase()), ";()");
				//
				List<String> sinonimos = new ArrayList<>();

				while (st.hasMoreElements()) {
					String token = st.nextToken();

					if (!token.equals("fig.") && !token.equals("norae")) {

						sinonimos.add(token);
					}

				}

				for (int i = 0; i < sinonimos.size(); i++) {
					for (int j = 0; j < sinonimos.size(); j++) {
						Object o = thesauro.get(sinonimos.get(i));
						if (i != j) {
							TreeSet<String> coleccionSinonimos;

							if (o == null) {
								coleccionSinonimos = new TreeSet<String>();

							} else {
								coleccionSinonimos = (TreeSet<String>) o;

							}
							coleccionSinonimos.add(sinonimos.get(j));
							thesauro.put(sinonimos.get(i), coleccionSinonimos);
						}

					}

				}

			}

		}
		br.close();

		escribirFicheroVista();

	}

	// Permite escribir un fichero para visualizar el contenido de la estructura
	public void escribirFicheroVista() throws IOException {
		PrintWriter pr = new PrintWriter(new FileWriter("./Thesauro.vista"));
		List<String> ficheros = new ArrayList<String>(thesauro.keySet());
		Collections.sort(ficheros);

		Iterator<String> j = ficheros.iterator();
		while (j.hasNext()) {
			Object q = j.next();
			Iterator<String> x = thesauro.get((String) q).iterator();

			pr.print(q + " : ");
			while (x.hasNext()) {

				Object p = x.next();
				pr.print(" " + p + ";");
			}
			pr.println();

		}
		pr.close();
	}

	// Método que permite visualizar los sinónimos de un token dado
	public void sinonimos(String token) {
		int i = 0;
		String mensaje = "";
		if (thesauro.get(token) != null) {
			Iterator<String> iterator = thesauro.get(token).iterator();
			while (iterator.hasNext()) {
				Object sinonimo = iterator.next();
				mensaje = mensaje + (String) sinonimo + ", ";
				i++;

				if (i > 6) {
					i = 0;
					mensaje = mensaje + "\n";

				}

			}
		} else {
			mensaje = mensaje + "La palabra que ha buscado no existe en el Thesauro.";
		}

		JOptionPane.showMessageDialog(null, mensaje);

	}
	
	public TreeSet<String> getSinonimos(String token) {
		return thesauro.get(token);
	}
}
