package es.boe.subastas.io;

import java.util.ArrayList;
import java.util.List;

import javax.xml.ws.BindingProvider;
import javax.xml.ws.handler.Handler;

import es.boe.serviciosubastas.ServicioSubastas;
import es.boe.serviciosubastas.ServicioSubastasBOE;
import es.boe.subastas.Estado;
import es.boe.subastas.PosturaFinal;
import es.boe.subastas.RespuestaEnvio;
import es.boe.subastas.RespuestaEstado;
import es.boe.subastas.RespuestaPosturaFinal;
import es.boe.subastas.preferencias.Preferencias;
import es.boe.subastas.soap.Seguridad;
import es.boe.subastas.soap.SoapClientHandler;

/**
 * Cliente de comunicación con el BOE. Permite invocar a sus operaciones.
 * @author crubencvs
 *
 */

public class BOEClient {
	
	private ServicioSubastasBOE srv;
	private ServicioSubastas port;
	private Preferencias pref;
	private String idLlamada;
	
	public static BOEClient newInstance(Preferencias pref, String idLlamada){
		if (pref==null || idLlamada==null){
			throw new IllegalArgumentException("No se puede inicializar "+ BOEClient.class.getCanonicalName()+ " por no tener los argumentos adecuados");
		}
		return new BOEClient(pref,idLlamada);
	}
	private BOEClient(Preferencias pref, String idLlamada) {
		this.pref= pref;
		this.idLlamada= idLlamada;
		this.srv= new ServicioSubastasBOE();
		this.port= srv.getServicioSubastasPort();
	}
	
	/**
	 * Asigna manejadores para los intercambios de mensajes
	 * @param port
	 */
	@SuppressWarnings("unchecked")
	private void asignaManejadores()
	{
		BindingProvider bpr = (BindingProvider)this.port;
		bpr.getRequestContext().put(
				BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
				pref.getEndpointBOE());
		List<Handler> handler=bpr.getBinding().getHandlerChain();
		if (handler==null)
		{
			handler= new ArrayList<Handler>();
		}
		handler.add(new Seguridad(pref, idLlamada));
		handler.add(new SoapClientHandler(pref, idLlamada));
		bpr.getBinding().setHandlerChain(handler);
	}
	
	/**
	 * Realiza el envío a BOE y devuelve una respuesta con el formato de servicio
	 * @param envio Envío en Base64
	 * @return Respuesta del envío
	 */
	public RespuestaEnvio envioSubasta(String envio){
		asignaManejadores();
		return AdapterBOE.convertirRespuestaEnvio(port.envioSubasta(envio));
	}
	/**
	 * Realiza la petición de cambio de estado a BOE y devuelve una respuesta
	 * @param estado Datos de cambio de estado
	 * @return Respuesta del cambio de estado
	 */
	public RespuestaEstado cambioEstadoSubasta(Estado estado){
		asignaManejadores();
		return AdapterBOE.convertirRespuestaCambioEstado(
				port.cambioEstadoSubasta(AdapterBOE.convertirPeticionCambioEstado(estado))
			);
	}
	
	/**
	 * Realiza la petición de datos de una postura final
	 * @param postura Identificación de la postura que se  quiere recuperar
	 * @return Datos de la postura
	 */
	public RespuestaPosturaFinal posturaFinal(PosturaFinal postura) {
		asignaManejadores();
		return AdapterBOE.convertirRespuestaPosturaFinal(
				  port.posturaFinal(AdapterBOE.convertirPeticionPosturaFinal(postura))
		   );
	}
	
}
