package es.boe.controlpublicacion.soap;

import java.io.ByteArrayOutputStream;
import java.util.Collections;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import es.boe.controlpublicacion.contexto.ContextoLlamada;
import es.boe.controlpublicacion.preferencias.Preferencias;
import es.boe.controlpublicacion.utils.Utils;
import es.tributasenasturias.utils.log.LogFactory;
import es.tributasenasturias.utils.log.Logger;


public class SoapClientHandler implements SOAPHandler<SOAPMessageContext> {

	//Objeto de contexto. Realmente se debería crear antes de este manejador, pero por aprovechar
	//mejor los recursos (parar un mensaje en un manejador no es grátis), se hace aquí.
	ContextoLlamada contexto;
	public SoapClientHandler(ContextoLlamada context)
	{
		this.contexto = context;
	}
	private void log(SOAPMessageContext context)
	{
		Logger log=null;
		try
		{
			Preferencias pref= this.contexto.getPreferencias();
			log=LogFactory.newLogger(pref.getModoLog(), pref.getFicheroLogSoapClient(), contexto.getIdLlamada());
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
				log.info("Error en la grabación de log de SOAP cliente:" + ex.getMessage());
			}
			Utils.generateSOAPErrMessage(context.getMessage(), "Error en proceso de comunicación Control de publicación en BOE <--> Servicio Remoto."	, "0002", "SOAP Handler");
		}
		catch (java.io.IOException ex)
		{
			if (log!=null)
			{
				log.info("Error en la grabación de log de SOAP cliente:" + ex.getMessage());
			}
			Utils.generateSOAPErrMessage(context.getMessage(), "Error en proceso de comunicación Control de publicación en BOE <--> Servicio Remoto."	, "0002", "SOAP Handler");
		}
		catch (Exception ex)
		{
			//En este punto podemos no tener log. Grabamos en la consola de servidor.
			if (log==null)
			{
				System.err.println ("Control de publicación en BOE: Error inesperado ("+SoapClientHandler.class.getName()+":"+ex.getMessage());
				ex.printStackTrace();
			}
			else
			{
				log.info("Error en la grabación de log de SOAP cliente:" + ex.getMessage());
			}
			Utils.generateSOAPErrMessage(context.getMessage(), "Error en proceso de comunicación Control de publicación en BOE <--> Servicio Remoto."	, "0002", "SOAP Handler");
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
		return true;
	}

	@Override
	public boolean handleMessage(SOAPMessageContext context) {
		log(context);
		return true;
	}
	
}
