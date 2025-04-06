package crawler;

import java.io.Serializable;
import java.util.Map;
import java.util.TreeMap;

/**
 * 
 * @author Alberto Manuel Campos Clemente
 * @author Carlos Cambero Rojas
 * @version v4
 */

public class Ocurrencia implements Serializable {
	private Integer frecuencia;
	private Map<String, Integer> dicc = new TreeMap<String, Integer>();

	public Ocurrencia(Ocurrencia diccActual, Ocurrencia diccGuardado) {
		this.frecuencia = diccActual.getFrecuencia() + diccGuardado.getFrecuencia();
		this.dicc.putAll(diccActual.getDicc());
		this.dicc.putAll(diccGuardado.getDicc());
	}

	public Ocurrencia(String ruta) {
		this.frecuencia = 1;
		this.dicc.put(ruta, 1);
	}

	/*
	 * Método que permite incrementar la frecuencia tanto global como local de un
	 * token.
	 */
	public void incrementarFrecuencia(String ruta) {
		this.frecuencia++;
		Object o = this.dicc.get(ruta);
		if (o == null) {
			this.dicc.put(ruta, 1);
		} else {
			Integer cont = (Integer) o;
			this.dicc.put(ruta, cont + 1);
		}

	}

	public Integer getFrecuencia() {
		return this.frecuencia;
	}

	public void setFrecuencia(Integer frecuencia) {
		this.frecuencia = frecuencia;
	}

	public Map<String, Integer> getDicc() {
		return this.dicc;
	}

	public void setDicc(Map<String, Integer> dicc) {
		this.dicc = dicc;
	}

	public Integer getFrecuenciaFichero(String fichero) {
		Object o = this.dicc.get(fichero);
		return (Integer) o;
	}

}
