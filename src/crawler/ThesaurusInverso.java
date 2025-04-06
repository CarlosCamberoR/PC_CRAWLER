package crawler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * 
 * @author Alberto Manuel Campos Clemente
 * @author Carlos Cambero Rojas
 * @version v4
 */
public class ThesaurusInverso {
	private TreeSet<String> thesauro = new TreeSet<String>();

	public TreeSet<String> getThesauro() {
		return thesauro;
	}

	public void setThesauro(TreeSet<String> thesauro) {
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
		if (thesauro.contains(token)) {
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
				if (listFiles[i].getName().equals("ThesauroInverso.ser")) {
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
		this.thesauro = o.cargarThesauroInverso(file.getAbsolutePath() + "/ThesauroInverso.ser");
	}

	// Procesa el fichero del thesauro para introducirlo en la estructura
	public void procesarThesauro() throws IOException {
		BufferedReader br = new BufferedReader(new FileReader("./resources/ThesaurusInverso.thesauro"));
		String linea;

		while ((linea = br.readLine()) != null) {

			StringTokenizer st = new StringTokenizer(eliminarTildes(linea.toLowerCase()), " ");

			while (st.hasMoreElements()) {
				String token = st.nextToken();

				thesauro.add(token);

			}

		}
		br.close();

		escribirFicheroVista();

	}

	// Permite escribir un fichero para visualizar el contenido de la estructura
	public void escribirFicheroVista() throws IOException {
		PrintWriter pr = new PrintWriter(new FileWriter("./ThesauroInverso.vista"));

		Iterator<String> j = thesauro.iterator();
		while (j.hasNext()) {
			Object q = j.next();
			pr.println(q);

		}
		pr.close();
	}

}
