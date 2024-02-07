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
import es.boe.controlpublicacion.preferencias.PreferenciasException;
import es.boe.controlpublicacion.preferencias.PreferenciasFactory;
import es.boe.controlpublicacion.utils.Constantes;
import es.boe.controlpublicacion.utils.Utils;
import es.tributasenasturias.utils.log.LogFactory;
import es.tributasenasturias.utils.log.Logger;




/**
 * Clase que intercepta los mensajes de entrada y los escribe en los log.
 * @author crubencvs
 *
 */
public class SoapServerHandler implements SOAPHandler<SOAPMessageContext> {


	private boolean log(SOAPMessageContext context) 
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
	        return true;
		}
		catch (javax.xml.soap.SOAPException ex)
		{
			if (log!=null)
			{
				log.info("Error en la grabación de log de SOAP servidor:" + ex.getMessage());
			}
			Utils.generaRespuestaError (context,Constantes.ERROR_TECNICO_CODE, Constantes.ERROR_TECNICO_MSG);
			return false;
		}
		catch (java.io.IOException ex)
		{
			if (log!=null)
			{
				log.info("Error en la grabación de log de SOAP servidor por error de Entrada/Salida:" + ex.getMessage());
			}
			Utils.generaRespuestaError (context,Constantes.ERROR_TECNICO_CODE, Constantes.ERROR_TECNICO_MSG);
			return false;
		}
		catch (PreferenciasException ex)
		{
			//En este punto es seguro que no tenemos log. Grabamos en la consola de servidor.
			System.err.println ("Control de publicación en BOE: Error de preferencias en manejador SOAP ("+SoapServerHandler.class.getName()+":"+ex.getMessage());
			ex.printStackTrace();
			return false;
		}
	}
	/**
	 * Crea el objeto de contexto de llamada y le asigna los valores de Preferencias, log e id de llamada
	 * que se utilizarán a lo largo de la llamada al servicio web.
	 * @param context Contexto SOAP
	 */
	private ContextoLlamada creaContexto(SOAPMessageContext context)
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
			return contextoLlamada;
		}
		catch (PreferenciasException ex)
		{
			//Si no hay preferencias, no debemos tener log.
			System.err.println ("Control de publicación en BOE:: error en preferencias ::"+ex.getMessage());
			ex.printStackTrace();
			Utils.generaRespuestaError (context,Constantes.ERROR_TECNICO_CODE, Constantes.ERROR_TECNICO_MSG);
			return null;
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
				log.info("Inicio de proceso de control de publicación");
			}
			else
			{
				log.info("Fin de proceso de control de publicación");
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
		if (!salida.booleanValue()) //Sería extraño, nos entraría un SOAP Fault.
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
		
		ContextoLlamada ctx=null;
		Boolean salida = (Boolean)context.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
		if (!salida.booleanValue())
		{
			ctx=creaContexto(context);
		}
		else
		{
			ctx = (ContextoLlamada)context.get(Constantes.CONTEXTO);
		}
		entradasLogApp(context);
		boolean resOK = log(context);
		if (salida.booleanValue())
		{
			destruyeContexto(context);
		}
		//Si hay un error, y es de salida, intentamos firmar el mensaje. Si no se puede, no se puede.
		if (!resOK)
		{
			try
			{
				Utils.firmaMensaje(context, ctx);
			}
			catch (Exception e)
			{
				//Ignoramos, sólo stacktrace
				System.err.println ("Control de publicación en BOE: Error al firmar un mensaje fallido:" + e.getMessage());
				e.printStackTrace();
			}
		}
		return resOK; //Si el log termina bien, continúa la cadena. Si no, corta. Internamente, si falla habrá cambiado el mensaje de respuesta
		
	}

	

}
