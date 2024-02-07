package es.boe.subastas;

import java.nio.charset.Charset;


import es.boe.subastas.codecs.Base64;
import es.boe.subastas.io.BOEClient;
import es.boe.subastas.preferencias.Preferencias;
import es.tributasenasturias.utils.log.Logger;

/**
 * Implementación de las operaciones
 * @author crubencvs
 *
 */
public class SubastasImpl {

	
	private Preferencias pref;
	private String idLlamada;
	private Logger log;
	
	public static SubastasImpl newInstance(Preferencias pref, Logger log, String idLlamada) {
		
		return new SubastasImpl(pref, log, idLlamada);
	}
	
	private SubastasImpl(Preferencias pref, Logger log, String idLlamada) {
		this.pref= pref;
		this.log= log;
		this.idLlamada= idLlamada;
		
	}
	/**
	 * Realiza el envío de una subasta
	 * @param envio XML de envío de la subasta
	 * @return Respuesta del envío
	 */
	public RespuestaEnvio envioSubasta(String envio) {
		log.debug("Se codifica el envío en Base 64");
		String envioBase64= String.valueOf(Base64.encode(envio.getBytes(Charset.forName("UTF-8"))));
		log.debug("Envío codificado, se comunica a BOE");
		BOEClient boe= BOEClient.newInstance(pref, idLlamada);
		return boe.envioSubasta(envioBase64);
	}
	
	/**
	 * Realiza el cambio de estado de una subasta
	 * @param estado Datos de estado
	 * @return Respuesta
	 */
	public RespuestaEstado cambioEstadoSubasta(Estado estado) {
		BOEClient boe= BOEClient.newInstance(pref, idLlamada);
		return boe.cambioEstadoSubasta(estado);
	}
	
	/**
	 * Realiza la consulta de una postura final
	 * @param postura Postura final que se quiere  consultar
	 * @return Datos de la postura
	 */
	public RespuestaPosturaFinal posturaFinal( PosturaFinal postura){
		BOEClient boe= BOEClient.newInstance(pref, idLlamada);
		return boe.posturaFinal(postura);
	}
}
