package es.boe.subastas.wssecurity;

import java.util.ArrayList;
import java.util.List;

import javax.xml.ws.Binding;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Holder;
import javax.xml.ws.handler.Handler;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import es.boe.subastas.preferencias.Preferencias;
import es.tributasenasturias.seguridadws.Seguridad;
import es.tributasenasturias.seguridadws.Seguridad_Service;
import es.tributasenasturias.utils.log.Logger;

public class ComprobadorWS{

	
	private Seguridad_Service servicio;
	private Seguridad port;
	private Logger log;
	Preferencias pref;
	
	public static ComprobadorWS newInstance(Logger log, Preferencias pref, SOAPHandler<SOAPMessageContext> manejador){
		return new ComprobadorWS(log, pref, manejador);
	}
	@SuppressWarnings("unchecked")
	protected ComprobadorWS(Logger log, Preferencias pref,SOAPHandler<SOAPMessageContext> manejador) {
		this.log=log;
		this.pref=pref;
		String endPoint=pref.getEndpointSeguridadSW();
		servicio=new Seguridad_Service();
		port=servicio.getSeguridadSOAP();
		BindingProvider bpr= (BindingProvider)port;
		if (!endPoint.equals(""))
		{
			bpr.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endPoint);
		}
		if (manejador!=null)
		{
			Binding bi = bpr.getBinding();
			List<Handler> manejadores=(List<Handler>)bi.getHandlerChain();
			if (manejadores==null)
			{
				manejadores= new ArrayList<Handler>();
			}
			manejadores.add(manejador);
			bi.setHandlerChain(manejadores);
		}
	}
	/**
	 * Método que utiliza operaciones del servicio Web de seguridad según WS-Security
	 * para validar la firma de un mensaje sin validar que el certificado que ha firmado
	 * ese mensaje sea uno reconocido.
	 * @param soapMsg Mensaje SOAP con seguridad a validar.
	 * @return true si es válido, false si no.
	 */
	public boolean validaFirmaSinCertificado(String soapMsg)
	{
		boolean valido=false;
		try
		{
			Holder<Boolean> esError=new Holder<Boolean>();
			Holder<String>codigo = new Holder<String>();
			Holder<String>mensaje= new Holder<String>();
			port.validacionFirmaSinCertificado(soapMsg, esError, codigo, mensaje);
			log.debug("La validación de WS-Security del mensaje de como resultado:" + (esError.value?"no correcta":"correcta"));
			valido=!esError.value;
		}
		catch (Exception ex)
		{
			log.error("Error al validar la firma del mensaje con WS-Security:"+ex.getMessage(), ex);
			valido=false;
		}
		return valido;
	}
	/**
	 * Firma un mensaje SOAP con Signature WS-Security.
	 * @param soapMsg Mensaje SOAP a firmar
	 * @return Mensaje soap firmado o el original si no se ha podido firmar. Esto se hace así
	 * porque si el receptor del mensaje necesita firma, lo rechazará, y si no, procesará correctamente
	 * el mensaje.
	 */
	public String firmaMensaje (String soapMsg)
	{
		try
		{
			return port.firmarMensaje(soapMsg, pref.getAliasCertificadoFirma());
		}
		catch (Exception ex)
		{
			log.error("Error al firmar el mensaje:"+ex.getMessage(),ex);
			return soapMsg;
		}
		
	}
}
