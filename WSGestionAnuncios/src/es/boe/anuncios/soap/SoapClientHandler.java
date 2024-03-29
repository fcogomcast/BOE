package es.boe.anuncios.soap;

import java.io.ByteArrayOutputStream;
import java.util.Collections;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import javax.xml.ws.soap.SOAPFaultException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import es.boe.anuncios.contexto.ContextoLlamada;
import es.boe.anuncios.preferencias.Preferencias;
import es.boe.anuncios.utils.Utils;
import es.boe.anuncios.utils.XMLDOMUtils;
import es.tributasenasturias.utils.log.LogFactory;
import es.tributasenasturias.utils.log.Logger;


public class SoapClientHandler implements SOAPHandler<SOAPMessageContext> {

	//Objeto de contexto. Realmente se deber�a crear antes de este manejador, pero por aprovechar
	//mejor los recursos (parar un mensaje en un manejador no es gr�tis), se hace aqu�.
	private ContextoLlamada contexto;
	//Comprueba si se est� enviando a BOE. Si lo est�, debemos tratar de manera diferente sus SOAP Fault,
	//porque son no est�ndar.
	private boolean esBOE;
	public SoapClientHandler(ContextoLlamada context)
	{
		this(context,false);
	}
	public SoapClientHandler(ContextoLlamada context, boolean esComunicacionBOE)
	{
		this.contexto  = context;
		this.esBOE = esComunicacionBOE;
	}
	private void log(SOAPMessageContext context)
	{
		Logger log=null;
		try
		{
			Preferencias pref= contexto.getPreferencias();
			log=LogFactory.newLogger(pref.getModoLog(), pref.getFicheroLogSoapClient(), contexto.getIdLlamada());
			Boolean salida = (Boolean)context.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
			String direccion=(salida)?"Env�o":"Recepci�n";
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
				log.error("Error en la grabaci�n de log de SOAP cliente:" + ex.getMessage());
			}
			Utils.generateSOAPErrMessage(context.getMessage(), "Error en comunicacion WSGestionAnuncios <--> Servicio Remoto."	, "0002", "SOAP Handler");
		}
		catch (java.io.IOException ex)
		{
			if (log!=null)
			{
				log.error("Error en la grabaci�n de log de SOAP cliente:" + ex.getMessage());
			}
			Utils.generateSOAPErrMessage(context.getMessage(), "Error en comunicaci�n WSGestionAnuncios <--> Servicio Remoto."	, "0002", "SOAP Handler");
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
				log.error ("Error Inesperado en la grabaci�n de SOAP cliente:"+ex.getMessage());
				Utils.generateSOAPErrMessage(context.getMessage(), "Error en comunicaci�n WSGestionAnuncios <--> Servicio Remoto."	, "0002", "SOAP Handler");
			}
		}
	}
	/**
	 * Necesario para corregir el mensaje SOAPFault que nos env�a el BOE, ya que utilizan
	 * un faultcode no est�ndar y eso impide que Weblogic procese correctamente el mensaje.
	 * Curiosamente el Bus s� que lo deja pasar.
	 * @param context Contexto SOAP
	 */
	private void corregirFaultBOE(SOAPMessageContext context)
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
		        Utils.sustituyeSOAPFault(context.getMessage(), "Excepci�n recibida en el env�o a BOE"	, faultcode, faultstring);
			}
		}
		catch (javax.xml.soap.SOAPException ex)
		{
			if (log!=null)
			{
				log.error("Error al corregir el soapFault de BOE :" + ex.getMessage());
			}
			Utils.generateSOAPErrMessage(context.getMessage(), "Error en comunicacion WSGestionAnuncios <--> Servicio Remoto."	, "0002", "SOAP Handler");
		}
		catch (java.io.IOException ex)
		{
			if (log!=null)
			{
				log.error("Error al corregir el soapFault de BOE:" + ex.getMessage());
			}
			Utils.generateSOAPErrMessage(context.getMessage(), "Error en comunicaci�n WSGestionAnuncios <--> Servicio Remoto."	, "0002", "SOAP Handler");
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
				Utils.generateSOAPErrMessage(context.getMessage(), "Error en comunicaci�n WSGestionAnuncios <--> Servicio Remoto."	, "0002", "SOAP Handler");
			}
		}
	}
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
		if (esBOE)
		{
			corregirFaultBOE(context);
		}
		return true;
	}

	@Override
	public boolean handleMessage(SOAPMessageContext context) {
		log(context);
		return true;
	}
	
}
