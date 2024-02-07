package es.boe.subastas.soap;

import java.io.StringReader;
import java.util.HashSet;
import java.util.Set;


import javax.xml.namespace.QName;
import javax.xml.transform.stream.StreamSource;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import javax.xml.ws.soap.SOAPFaultException;


import es.boe.subastas.preferencias.Preferencias;
import es.boe.subastas.wssecurity.ComprobadorWS;
import es.boe.subastas.xml.XMLDOMDocumentException;
import es.boe.subastas.xml.XMLDOMUtils;
import es.tributasenasturias.seguridad.servicio.SeguridadException;
import es.tributasenasturias.utils.log.LogFactory;
import es.tributasenasturias.utils.log.Logger;


public class Seguridad implements SOAPHandler<SOAPMessageContext> {

	private Preferencias pref;
	private String idLlamada;
	public Seguridad(Preferencias pref, String idLlamada)
	{
		this.pref= pref;
		this.idLlamada= idLlamada;
	}
	/**
	 * Gestiona la seguridad del mensaje de salida
	 * Firma mensaje a la salida
	 * @param context {@link SOAPMessageContext} con el contexto del mensaje SOAP
	 */
	private void seguridadMensaje(SOAPMessageContext context) throws SOAPFaultException{
		Logger log = null;
		try {
			log=LogFactory.newLogger(pref.getModoLog(), pref.getFicheroLogSoapClient(), idLlamada);
			if (pref==null || log == null)
			{
				throw new Exception ("Error. No se encuentran los objetos de preferencias o log en contexto de llamada SOAP.");
			}
			Boolean salida = (Boolean) context
					.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
			if (salida && "S".equalsIgnoreCase(pref.getFirmaSalida())){ 
				String mensajeSalida = XMLDOMUtils.getPrettyXMLText(context.getMessage().getSOAPPart());
				mensajeSalida=ComprobadorWS.newInstance(log, pref,new SoapClientHandler(pref, idLlamada)).firmaMensaje(mensajeSalida);
				context.getMessage().getSOAPPart().setContent(new StreamSource(new StringReader(mensajeSalida)));
			}
		} catch (XMLDOMDocumentException ex) {
			Boolean salida = (Boolean) context
			.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
			if (!salida.booleanValue())
			{
				log.error("Error en las comprobaciones de seguridad del mensaje:"
						+ ex.getMessage(), ex);
				Utils.generateSOAPErrMessage(context.getMessage(), "Error en comunicación WSGestionSubastas <--> Servicio WSSecurity."	, "0002", "SOAP Handler");
			}
			else
			{
				log.error("Error en la generación de firma a la salida del mensaje:"
						+ ex.getMessage(), ex);
				Utils.generateSOAPErrMessage(context.getMessage(), "Error en comunicación WSGestionSubastas <--> Servicio WSSecurity."	, "0002", "SOAP Handler");
			}
		}
		catch (SeguridadException ex) {
			Boolean salida = (Boolean) context
			.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
			if (!salida.booleanValue())
			{
				log.error("Error en las comprobaciones de seguridad del mensaje:"
						+ ex.getMessage(), ex);
				Utils.generateSOAPErrMessage(context.getMessage(), "Error en comunicación WSGestionSubastas <--> Servicio WSSecurity."	, "0002", "SOAP Handler");
			}
			else
			{
				log.error("Error en la generación de firma a la salida del mensaje:"
						+ ex.getMessage(), ex);
				Utils.generateSOAPErrMessage(context.getMessage(), "Error en comunicación WSGestionSubastas <--> Servicio WSSecurity."	, "0002", "SOAP Handler");
			}
		}
		catch (Exception ex)
		{
			if (!(ex instanceof SOAPFaultException))
			{
				if (log!=null)
				{
					log.error("Error en las comprobaciones de seguridad del mensaje:"
							+ ex.getMessage(), ex);
				}
				else
				{
					System.err.println (new java.util.Date().toString()+"::Servicio de notificación de envío a BOE::error en las comprobaciones de seguridad del mensaje::"+ex.getMessage());
					ex.printStackTrace();
					
				}
				Utils.generateSOAPErrMessage(context.getMessage(), "Error en comunicación WSGestionSubastas <--> Servicio WSSecurity."	, "0002", "SOAP Handler");
			}
			else
			{
				throw (SOAPFaultException)ex;
			}
		}
	}

	@Override
	public Set<QName> getHeaders() {
		
		//Indicamos que entendemos la cabecera de seguridad de WS-Security.
		QName security= new QName("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd","Security");
		HashSet<QName> headersEntendidos= new HashSet<QName>();
		headersEntendidos.add(security);
		return headersEntendidos;
	}

	@Override
	public void close(MessageContext context) {
	}

	@Override
	public boolean handleFault(SOAPMessageContext context) {
		return true;
	}

	@Override
	public boolean handleMessage(SOAPMessageContext context) {
		seguridadMensaje(context);
		return true;
	}

}
