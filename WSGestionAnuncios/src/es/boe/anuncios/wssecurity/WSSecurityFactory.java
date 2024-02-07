package es.boe.anuncios.wssecurity;


import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;


import es.boe.anuncios.preferencias.Preferencias;
import es.tributasenasturias.utils.log.Logger;
/**
 * Construye objetos para devolver respuestas del servicio.
 * @author crubencvs
 *
 */
public class WSSecurityFactory {
	
	private WSSecurityFactory(){};
	/**
	 * Devuelve una instancia  {@link ComprobadorWS} que permite construir manejar la seguridad WS Security
	 * @param context Objeto para realizar log
	 * @param preferencias Objeto de preferencias.
	 * @param manejador {@link SOAPHandler} que permitirá interceptar mensajes realizados a los servicios llamados desde este objeto.
	 * @return instancia ComprobadorWS
	 */
	public static ComprobadorWS newConstructorResultado(Logger log, Preferencias pref, SOAPHandler<SOAPMessageContext> manejador) throws WSSecurityException
	{
		return new ComprobadorWS(log, pref, manejador);
	}

}
