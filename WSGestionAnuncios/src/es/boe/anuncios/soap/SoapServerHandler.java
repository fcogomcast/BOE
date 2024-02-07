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

import es.boe.anuncios.contexto.ContextoLlamada;
import es.boe.anuncios.preferencias.Preferencias;
import es.boe.anuncios.preferencias.PreferenciasException;
import es.boe.anuncios.preferencias.PreferenciasFactory;
import es.boe.anuncios.utils.Constantes;
import es.boe.anuncios.utils.Utils;
import es.tributasenasturias.utils.log.LogFactory;
import es.tributasenasturias.utils.log.Logger;




/**
 * Clase que intercepta los mensajes de entrada y los escribe en los log.
 * @author crubencvs
 *
 */
public class SoapServerHandler implements SOAPHandler<SOAPMessageContext> {


	private void log(SOAPMessageContext context) 
	{
		Logger log=null;
		try
		{
			Preferencias pref= PreferenciasFactory.newInstance();
			String idSesion="Sin número de llamada recuperado.";
			Boolean salida = (Boolean)context.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
			idSesion= ((ContextoLlamada)context.get(Constantes.CONTEXTO)).getIdLlamada();
			if (idSesion==null)
			{
				idSesion="Sin número de llamada recuperado.";
			}
			log=LogFactory.newLogger(pref.getModoLog(), pref.getFicheroLogSoapServer(), idSesion);
			String direccion=(salida)?"Envío":"Recepción";
			SOAPMessage msg = context.getMessage();
			ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
	        msg.writeTo(byteArray);
	        String soapMessage = new String(byteArray.toByteArray());
	        log.info(direccion+"::"+soapMessage);
		}
		catch (javax.xml.soap.SOAPException ex)
		{
			if (log!=null)
			{
				log.info("Error en la grabación de log de SOAP servidor:" + ex.getMessage());
			}
			Utils.generateSOAPErrMessage(context.getMessage(), "Error en la grabación de log de SOAP:"+ ex.getMessage(), Constantes.ERROR_LOG_NUM, Constantes.ERROR_LOG_MSG);
		}
		catch (java.io.IOException ex)
		{
			if (log!=null)
			{
				log.info("Error en la grabación de log de SOAP servidor por error de Entrada/Salida:" + ex.getMessage());
			}
			Utils.generateSOAPErrMessage(context.getMessage(), "Error en la grabación de log de SOAP:"+ ex.getMessage(), Constantes.ERROR_LOG_NUM, Constantes.ERROR_LOG_MSG);
		}
		catch (PreferenciasException ex)
		{
			//En este punto es seguro que no tenemos log. Grabamos en la consola de servidor.
			System.err.println ("Servicio de Notificaciones BOE: Error de preferencias en manejador SOAP ("+SoapServerHandler.class.getName()+":"+ex.getMessage());
			ex.printStackTrace();
		}
	}
	/**
	 * Primera y última entrada en el log de aplicación
	 * @param context Contexto del mensaje SOAP
	 */
	private void entradasLogApp(SOAPMessageContext context)
	{
		try{
		
			ContextoLlamada contexto = (ContextoLlamada) context.get(Constantes.CONTEXTO);
			Logger log = contexto.getLogger();
			Boolean salida = (Boolean)context.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
			if (!salida.booleanValue())
			{
				log.info("Inicio del servicio ded notificaciones de anuncios.");
			}
			else
			{
				log.info("Final del servicio de notificaciones de anuncios.");
			}
		}
		catch (Exception e)
		{
			//Lo ignoramos, si no se ha podido poco podemos hacer. Imprimimos en la consola
			System.err.println ("Error en entradasLogApp  :" + e.getMessage());
			e.printStackTrace();
		}
	}
	/**
	 * Crea el objeto de contexto de llamada y le asigna los valores de Preferencias, log e id de llamada
	 * que se utilizarán a lo largo de la llamada al servicio web.
	 * @param context Contexto SOAP
	 */
	private void creaContexto(SOAPMessageContext context)
	{
		Logger log=null;
		try
		{
			String idLlamada=Utils.getIdLlamada();
			Preferencias pref = PreferenciasFactory.newInstance();
			log=LogFactory.newLogger(pref.getModoLog(), pref.getFicheroLogApp(), idLlamada);
			//Objeto de contexto. Realmente se debería crear antes de este manejador, pero por aprovechar
			//mejor los recursos (parar un mensaje en un manejador no es grátis), se hace aquí.
			ContextoLlamada contextoLlamada = ContextoLlamada.newInstance(pref, log, idLlamada);
			context.put (Constantes.CONTEXTO, contextoLlamada);
			context.setScope(Constantes.CONTEXTO, MessageContext.Scope.APPLICATION);
		}
		catch (PreferenciasException ex)
		{
			//Si no hay preferencias, no debemos tener log.
			System.err.println ("Servicio de Notificaciones BOE:: error en preferencias ::"+ex.getMessage());
			ex.printStackTrace();
			Utils.generateSOAPErrMessage(context.getMessage(), "Error en las preferencias del servicio al hacer log:"+ ex.getMessage(), Constantes.ERROR_LOG_NUM, Constantes.ERROR_LOG_MSG);
		}
	}
	/**
	 * Destruye el objeto de contexto de llamada, que contiene datos relativos a cada llamada
	 * @param context Contexto SOAP donde se almacena el contexto de la llamada
	 */
	private void destruyeContexto(SOAPMessageContext context)
	{
		context.remove(Constantes.CONTEXTO);
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
		Boolean salida = (Boolean)context.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
		if (!salida.booleanValue()) //Sería extraño, nos entraría un SOAP Fault
		{
			creaContexto(context);
		}
		entradasLogApp(context);
		log(context);
		
		if (salida.booleanValue())
		{
			destruyeContexto(context);
		}
		return true;
	}

	@Override
	public boolean handleMessage(SOAPMessageContext context) {
		
		Boolean salida = (Boolean)context.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
		if (!salida.booleanValue())
		{
			creaContexto(context);
		}
		try
		{
			entradasLogApp(context);
			log(context);
			if (salida.booleanValue())
			{
				destruyeContexto(context);
			}
			return true;
		}
		catch (SOAPFaultException e)
		{
			if (salida.booleanValue())
			{
				destruyeContexto(context);
			}
			throw e;
		}
		
	}

	

}
