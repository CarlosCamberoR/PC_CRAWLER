package crawler;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Hashtable;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import crawler.Ocurrencia;

/**
 * 
 * @author Alberto Manuel Campos Clemente
 * @author Carlos Cambero Rojas
 * @version v4
 */
public class Objeto {
	/*
	 * Método que permite guardar la estructura de un diccionario en un fichero.
	 */
	public void salvarDiccionario(Map<String, Ocurrencia> diccionario, String ruta) {
		try {
			FileOutputStream fos = new FileOutputStream(ruta);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(diccionario);
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	/*
	 * Método que permite recuperar la información de la estructura de un
	 * diccionario que haya sido almacenado en un fichero.
	 */
	public Map<String, Ocurrencia> cargarDiccionario(String ruta) {
		try {
			FileInputStream fis = new FileInputStream(ruta);
			ObjectInputStream ois = new ObjectInputStream(fis);

			Map<String, Ocurrencia> diccionario = (TreeMap<String, Ocurrencia>) ois.readObject();

			System.out.println(diccionario.toString());
			return diccionario;
		} catch (Exception e) {
			System.out.println(e);
		}
		return null;

	}

	public void salvarThesauro(Map<String, TreeSet<String>> thesauro, String ruta) {
		try {
			FileOutputStream fos = new FileOutputStream(ruta);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(thesauro);
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	public Map<String, TreeSet<String>> cargarThesauro(String ruta) {
		try {
			FileInputStream fis = new FileInputStream(ruta);
			ObjectInputStream ois = new ObjectInputStream(fis);

			Map<String, TreeSet<String>> thesauro = (Map<String, TreeSet<String>>) ois.readObject();

			System.out.println(thesauro.toString());
			return thesauro;
		} catch (Exception e) {
			System.out.println(e);
		}
		return null;

	}

	public void salvarThesauroInverso(TreeSet<String> thesauro, String ruta) {
		try {
			FileOutputStream fos = new FileOutputStream(ruta);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(thesauro);
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	public TreeSet<String> cargarThesauroInverso(String ruta) {
		try {
			FileInputStream fis = new FileInputStream(ruta);
			ObjectInputStream ois = new ObjectInputStream(fis);

			TreeSet<String> thesauro = (TreeSet<String>) ois.readObject();

			System.out.println(thesauro.toString());
			return thesauro;
		} catch (Exception e) {
			System.out.println(e);
		}
		return null;

	}
}
