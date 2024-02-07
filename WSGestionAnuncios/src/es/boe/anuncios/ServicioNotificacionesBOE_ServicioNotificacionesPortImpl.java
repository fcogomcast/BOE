package es.boe.anuncios;

import javax.annotation.Resource;
import javax.jws.HandlerChain;
import javax.jws.WebService;
import javax.xml.ws.BindingType;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.WebServiceException;

import es.boe.anuncios.contexto.ContextoLlamada;
import es.boe.anuncios.exceptions.AnunciosException;
import es.boe.anuncios.utils.Constantes;

/**
 * This class was generated by the JAX-WS RI. Oracle JAX-WS 2.1.3-06/19/2008
 * 07:03 PM(bt) Generated source version: 2.1
 * 
 */
@WebService(portName = "ServicioNotificacionesPort", serviceName = "ServicioNotificacionesBOE", targetNamespace = "http://www.boe.es/ServicioNotificaciones/", wsdlLocation = "/wsdls/GestionAnuncios.wsdl", endpointInterface = "es.boe.anuncios.ServicioNotificaciones")
@BindingType("http://schemas.xmlsoap.org/wsdl/soap/http")
@HandlerChain (file="HandlerChain.xml")
public class ServicioNotificacionesBOE_ServicioNotificacionesPortImpl implements
		ServicioNotificaciones {

	@Resource
	 WebServiceContext wsc;
	public ServicioNotificacionesBOE_ServicioNotificacionesPortImpl() {
	}

	/**
	 * 
	 * @param envio
	 * @return returns es.boe.anuncios.Respuesta
	 */
	public Respuesta envioAnuncios(String envio) {
		try
		{
			ContextoLlamada contexto = (ContextoLlamada) wsc.getMessageContext().get(Constantes.CONTEXTO);
			if (contexto==null)
			{
				throw new AnunciosException("No se ha podido recuperar el contexto de llamada");
			}
			ServicioNotificacionesImpl imp = ServicioNotificacionesImpl.newInstance(contexto);
			return imp.envioAnuncios(envio);
		}
		catch (Exception e)
		{
			throw new WebServiceException("Error en "+this.getClass().getCanonicalName()+":"+e.getMessage(),e);
		}
	}

	/**
	 * 
	 * @param idEnvio
	 * @return returns es.boe.anuncios.Respuesta
	 */
	public Respuesta consultaEnvio(String idEnvio) {

		try
		{
			ContextoLlamada contexto = (ContextoLlamada) wsc.getMessageContext().get(Constantes.CONTEXTO);
			if (contexto==null)
			{
				throw new AnunciosException("No se ha podido recuperar el contexto de llamada");
			}
			ServicioNotificacionesImpl imp = ServicioNotificacionesImpl.newInstance(contexto);
			return imp.consultaEnvio(idEnvio);
		}
		catch (Exception e)
		{
			throw new WebServiceException ("Error en "+ this.getClass().getCanonicalName()+":"+e.getMessage(),e);
		}
	}

	/**
	 * 
	 * @param idAnuncio
	 * @return returns es.boe.anuncios.Respuesta
	 */
	public Respuesta consultaAnuncio(String idAnuncio) {
		try
		{
			ContextoLlamada contexto = (ContextoLlamada) wsc.getMessageContext().get(Constantes.CONTEXTO);
			if (contexto==null)
			{
				throw new AnunciosException("No se ha podido recuperar el contexto de llamada");
			}
			ServicioNotificacionesImpl imp = ServicioNotificacionesImpl.newInstance(contexto);
			return imp.consultaAnuncio(idAnuncio);
		}
		catch (Exception e)
		{
			throw new WebServiceException ("Error en "+ this.getClass().getCanonicalName()+":"+e.getMessage(),e);
		}
	}
	
	/**
	 * 
	 * @param idEnvio
	 * @return returns es.boe.anuncios.Respuesta
	 */
	public Respuesta anulacionEnvio(String idEnvio) {
		try
		{
			ContextoLlamada contexto = (ContextoLlamada) wsc.getMessageContext().get(Constantes.CONTEXTO);
			if (contexto==null)
			{
				throw new AnunciosException("No se ha podido recuperar el contexto de llamada");
			}
			ServicioNotificacionesImpl imp = ServicioNotificacionesImpl.newInstance(contexto);
			return imp.anulacionEnvio(idEnvio);
		}
		catch (Exception e)
		{
			throw new WebServiceException ("Error en "+ this.getClass().getCanonicalName()+":"+e.getMessage(),e);
		}
	}

	/**
	 * 
	 * @param idAnuncio
	 * @return returns es.boe.anuncios.Respuesta
	 */
	public Respuesta anulacionAnuncio(String idAnuncio) {
		try
		{
			ContextoLlamada contexto = (ContextoLlamada) wsc.getMessageContext().get(Constantes.CONTEXTO);
			if (contexto==null)
			{
				throw new AnunciosException("No se ha podido recuperar el contexto de llamada");
			}
			ServicioNotificacionesImpl imp = ServicioNotificacionesImpl.newInstance(contexto);
			return imp.anulacionAnuncio(idAnuncio);
		}
		catch (Exception e)
		{
			throw new WebServiceException ("Error en "+ this.getClass().getCanonicalName()+":"+e.getMessage(),e);
		}
	}

}
