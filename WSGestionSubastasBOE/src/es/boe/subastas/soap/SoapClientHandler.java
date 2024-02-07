package es.boe.subastas.soap;

import java.io.ByteArrayOutputStream;
import java.util.Collections;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import es.boe.subastas.preferencias.Preferencias;
import es.tributasenasturias.utils.log.LogFactory;
import es.tributasenasturias.utils.log.Logger;


public class SoapClientHandler implements SOAPHandler<SOAPMessageContext> {

	private Preferencias pref;
	private String idLlamada;
	public SoapClientHandler(Preferencias pref, String idLlamada)
	{
		this.idLlamada= idLlamada;
		this.pref= pref;
	}
	private void log(SOAPMessageContext context)
	{
		Logger log=null;
		try
		{
			log=LogFactory.newLogger(pref.getModoLog(), pref.getFicheroLogSoapClient(), idLlamada);
			Boolean salida = (Boolean)context.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
			String direccion=(salida)?"Envío":"Recepción";
			SOAPMessage msg = context.getMessage();
			ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
	        msg.writeTo(byteArray);
	        String soapMessage = new String(byteArray.toByteArray());
	        log.info(direccion+":"+soapMessage);
		}
		catch (javax.xml.soap.SOAPException ex)
		{
			if (log!=null)
			{
				log.error("Error en la grabación de log de SOAP cliente:" + ex.getMessage());
			}
			Utils.generateSOAPErrMessage(context.getMessage(), "Error en comunicacion WSGestionSubastas <--> Servicio Remoto."	, "0002", "SOAP Handler");
		}
		catch (java.io.IOException ex)
		{
			if (log!=null)
			{
				log.error("Error en la grabación de log de SOAP cliente:" + ex.getMessage());
			}
			Utils.generateSOAPErrMessage(context.getMessage(), "Error en comunicación WSGestionSubastas <--> Servicio Remoto."	, "0002", "SOAP Handler");
		}
		catch (Exception ex)
		{
			//En este punto podemos no tener log. Grabamos en la consola de servidor.
			if (log==null)
			{
				System.err.println ("Servicio de Notificaciones BOE: Error inesperado en manejador SOAP ("+SoapServerHandler.class.getName()+":"+ex.getMessage());
				ex.printStackTrace();
			}
			else
			{
				log.error ("Error Inesperado en la grabación de SOAP cliente:"+ex.getMessage());
				Utils.generateSOAPErrMessage(context.getMessage(), "Error en comunicación WSGestionSubastas <--> Servicio Remoto."	, "0002", "SOAP Handler");
			}
		}
	}
	/**
	 * Necesario para corregir el mensaje SOAPFault que nos envía el BOE, ya que utilizan
	 * un faultcode no estándar y eso impide que Weblogic procese correctamente el mensaje.
	 * Curiosamente el Bus sí que lo deja pasar.
	 * @param context Contexto SOAP
	 */
	/*private void corregirFaultBOE(SOAPMessageContext context)
	{
		Logger log=null;
		try
		{
			Preferencias pref= contexto.getPreferencias();
			log=LogFactory.newLogger(pref.getModoLog(), pref.getFicheroLogSoapClient(), contexto.getIdLlamada());
			Boolean salida = (Boolean)context.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
			if (!salida) //Recibimos mensaje
			{
				SOAPMessage msg = context.getMessage();
				ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
		        msg.writeTo(byteArray);
		        String soapMessage = new String(byteArray.toByteArray());
		        Document d =  XMLDOMUtils.parseXml(soapMessage);
		        Node nfaultcode = XMLDOMUtils.selectSingleNode(d, "//*[local-name()='faultcode']");
		        Node nfaultstring = XMLDOMUtils.selectSingleNode(d, "//*[local-name()='faultstring']");
		        String faultcode=XMLDOMUtils.getNodeText(nfaultcode);
		        String faultstring=XMLDOMUtils.getNodeText(nfaultstring);
		        Utils.sustituyeSOAPFault(context.getMessage(), "Excepción recibida en el envío a BOE"	, faultcode, faultstring);
			}
		}
		catch (javax.xml.soap.SOAPException ex)
		{
			if (log!=null)
			{
				log.error("Error al corregir el soapFault de BOE :" + ex.getMessage());
			}
			Utils.generateSOAPErrMessage(context.getMessage(), "Error en comunicacion WSGestionSubastas <--> Servicio Remoto."	, "0002", "SOAP Handler");
		}
		catch (java.io.IOException ex)
		{
			if (log!=null)
			{
				log.error("Error al corregir el soapFault de BOE:" + ex.getMessage());
			}
			Utils.generateSOAPErrMessage(context.getMessage(), "Error en comunicación WSGestionSubastas <--> Servicio Remoto."	, "0002", "SOAP Handler");
		}
		catch (Exception ex)
		{
			if (ex instanceof SOAPFaultException)
			{
				throw (SOAPFaultException)ex;
			}
			//En este punto podemos no tener log. Grabamos en la consola de servidor.
			if (log==null)
			{
				System.err.println ("Servicio de Notificaciones BOE: Error inesperado en manejador SOAP ("+SoapClientHandler.class.getName()+":"+ex.getMessage());
				ex.printStackTrace();
			}
			else
			{
				log.error ("Error al corregir el soapFault de BOE:"+ex.getMessage());
				Utils.generateSOAPErrMessage(context.getMessage(), "Error en comunicación WSGestionSubastas <--> Servicio Remoto."	, "0002", "SOAP Handler");
			}
		}
	}*/
	@Override
	public Set<QName> getHeaders() {
		return Collections.emptySet();
	}

	@Override
	public void close(MessageContext context) {
	}

	@Override
	public boolean handleFault(SOAPMessageContext context) {
		log(context);
		/*if (esBOE)
		{
			corregirFaultBOE(context);
		}*/
		return true;
	}

	@Override
	public boolean handleMessage(SOAPMessageContext context) {
		log(context);
		return true;
	}
	
}
