package es.boe.controlpublicacion.soap;

import java.io.StringReader;
import java.util.HashSet;
import java.util.Set;


import javax.xml.namespace.QName;
import javax.xml.transform.stream.StreamSource;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import javax.xml.ws.soap.SOAPFaultException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import es.boe.controlpublicacion.contexto.ContextoLlamada;
import es.boe.controlpublicacion.preferencias.Preferencias;
import es.boe.controlpublicacion.seguridad.InfoCertificado;
import es.boe.controlpublicacion.seguridad.InfoPermisosCertificado;
import es.boe.controlpublicacion.seguridad.PropertyConfigurator;
import es.boe.controlpublicacion.seguridad.SeguridadException;
import es.boe.controlpublicacion.seguridad.SeguridadFactory;
import es.boe.controlpublicacion.seguridad.VerificadorCertificado;
import es.boe.controlpublicacion.seguridad.VerificadorPermisoServicio;
import es.boe.controlpublicacion.seguridad.InfoCertificado.Validez;
import es.boe.controlpublicacion.utils.Constantes;
import es.boe.controlpublicacion.utils.Utils;
import es.boe.controlpublicacion.utils.XMLDOMDocumentException;
import es.boe.controlpublicacion.utils.XMLDOMUtils;
import es.boe.controlpublicacion.wssecurity.WSSecurityFactory;
import es.tributasenasturias.utils.log.Logger;


public class Seguridad implements SOAPHandler<SOAPMessageContext> {

	
	/**
	 * Extrae el contenido del certificado de la firma de WSSecurity
	 * @param mensaje Mensaje firmado.
	 * @return Cadena que contiene el certificado de la firma.
	 * @throws XMLDOMDocumentException
	 */
	private String extraerCertificado(String mensaje)
			throws XMLDOMDocumentException {
		Document doc = XMLDOMUtils.parseXml(mensaje);
		Node certificado = XMLDOMUtils
				.selectSingleNode(
						doc,
						"/*[local-name()='Envelope']/*[local-name()='Header']/*[local-name()='Security']/*[local-name()='BinarySecurityToken']/text()");
		if (certificado==null)
		{
			return "";
		}
		return certificado.getNodeValue();

	}
	/**
	 * Comprueba si el certificado es válido y tiene permisos sobre el servicio.
	 * @param pref Preferencias
	 * @param context Contexto de mensaje SOAP
	 * @param contexto {@link ContextoLlamada} de la llamada
	 * @throws XMLDOMDocumentException En caso de no poder extraer el certificado del mensaje SOAP
	 * @throws SeguridadException En caso de no poder comprobar los permisos.
	 */
	private boolean comprobarCertificado(String xml,ContextoLlamada contexto)
			throws XMLDOMDocumentException, SeguridadException
	{
		// Extraemos el certificado.
		String certificado = extraerCertificado(xml);
		Preferencias pref = contexto.getPreferencias();
		VerificadorCertificado vc = SeguridadFactory.newVerificadorCertificado(pref.getEndPointAutenticacion() , new SoapClientHandler(contexto));
		InfoCertificado i = vc.login(certificado);
		if (i.getValidez().equals(Validez.VALIDO))
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	/**
	 * Comprueba si la identidad del certificado tiene permisos sobre el servicio.
	 * @param pref Preferencias
	 * @param context Contexto de mensaje SOAP
	 * @param log Logger
	 * @param idSesion Id de la sesión
	 * @throws XMLDOMDocumentException En caso de no poder extraer el certificado del mensaje SOAP
	 * @throws SeguridadException En caso de no poder comprobar los permisos.
	 */
	private boolean comprobarPermisos(String xml, ContextoLlamada contexto)
			throws XMLDOMDocumentException, SeguridadException
	{
		// Extraemos el certificado.
		String certificado = extraerCertificado(xml);
		Preferencias pref = contexto.getPreferencias();
			// Comprobamos permisos sobre el certificado
		VerificadorPermisoServicio per = SeguridadFactory
			.newVerificadorPermisoServicio(new PropertyConfigurator(
						pref.getEndPointAutenticacion(), pref.getEndPointLanzador(), 
						pref.getPAPermisoServicio(), pref.getEsquemaBaseDatos(),
						new SoapClientHandler(contexto)));
		InfoPermisosCertificado ipe= per.tienePermisosCertificado(certificado, pref.getAliasServicio());
    	return ipe.isCertificadoAutorizado();
	}
	/**
	 * Gestiona la seguridad del mensaje de entrada o salida
	 * Valida firma y permisos del firmante sobre el servicio.
	 * Firma mensaje a la salida
	 * @param context
	 */
	private boolean seguridadMensaje(SOAPMessageContext context) throws SOAPFaultException{
		Logger log = null;
		try {
			//Recuperamos de contexto los objetos que necesitamos.
			ContextoLlamada contexto = (ContextoLlamada) context.get(Constantes.CONTEXTO);
			Preferencias pref = contexto.getPreferencias(); 
			log = contexto.getLogger();
			if (pref==null || log == null)
			{
				throw new Exception ("Error. No se encuentran los objetos de preferencias o log en contexto de llamada SOAP.");
			}
			Boolean salida = (Boolean) context
					.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
			if (!salida
					&& ("S".equals(pref.getValidaPermisosServicio()) || 
						"S".equals (pref.getValidaCertificado()) ||
						"S".equals(pref.getValidaFirma()))) 
			{
				//Extraemos el texto del mensaje
				Node payload = context.getMessage().getSOAPPart().getDocumentElement();
				String xml = XMLDOMUtils.getXMLText(payload);
				if ("S".equalsIgnoreCase(pref.getValidaFirma()))
				{
					log.info("Se procede a validar la firma de mensaje");
					boolean firmaCorrecta = WSSecurityFactory.newConstructorResultado(log, pref,new SoapClientHandler(contexto)).validaFirmaSinCertificado(xml);
					if (firmaCorrecta)
					{
						log.info("Firma validada");
					}
					else
					{
						log.info("Firma no válida");
						Utils.generaRespuestaError (context,Constantes.ERROR_FIRMA_ENTRADA_CODE, Constantes.ERROR_FIRMA_ENTRADA_MSG);
						return false;
					}
				}

				if ("S".equalsIgnoreCase(pref.getValidaCertificado()))
				{
					log.info ("Se procede a comprobar si el certificado de la firma es válido.");
					boolean cerValido=comprobarCertificado(xml,contexto);
					if (cerValido)
					{
						log.info ("Certificado válido");
					}
					else
					{
						log.info ("Certificado no válido");
						Utils.generaRespuestaError (context,Constantes.ERROR_FIRMA_ENTRADA_CODE, Constantes.ERROR_FIRMA_ENTRADA_MSG);
						return false;
					}
				}
				if ("S".equalsIgnoreCase(pref.getValidaPermisosServicio()))
				{
					log.info ("Se procede a comprobar si la persona asociada con el certificado tiene permisos sobre el servicio.");
					boolean cerValido=comprobarPermisos(xml,contexto);
					if (cerValido)
					{
						log.info ("Existen permisos sobre el servicio.");
					}
					else
					{
						log.info ("No existen permisos sobre el servicio.");
						Utils.generaRespuestaError (context,Constantes.ERROR_FIRMA_ENTRADA_CODE, Constantes.ERROR_FIRMA_ENTRADA_MSG);
						return false;
					}
				}
			}
			else if (salida && "S".equalsIgnoreCase(pref.getFirmaSalida())){ 
				String mensajeSalida = XMLDOMUtils.getPrettyXMLText(context.getMessage().getSOAPPart());
				mensajeSalida=WSSecurityFactory.newConstructorResultado(log, pref,new SoapClientHandler(contexto)).firmaMensaje(mensajeSalida);
				context.getMessage().getSOAPPart().setContent(new StreamSource(new StringReader(mensajeSalida)));
			}
			return true; //Operaciones OK
		} catch (XMLDOMDocumentException ex) {
			Boolean salida = (Boolean) context
			.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
			if (!salida.booleanValue())
			{
				log.error("Error en las comprobaciones de seguridad del mensaje:"
						+ ex.getMessage(), ex);
				Utils.generaRespuestaError (context,Constantes.ERROR_FIRMA_ENTRADA_CODE, Constantes.ERROR_FIRMA_ENTRADA_MSG);
				return false;
			}
			else
			{
				//Este caso es complicado de manejar, ya que puede haberse actualizado la información
				//y fallar en su respuesta.
				log.error("Error en la generación de firma a la salida del mensaje:"
						+ ex.getMessage(), ex);
				Utils.generaRespuestaError (context,Constantes.ERROR_FIRMA_SALIDA_CODE, Constantes.ERROR_FIRMA_SALIDA_MSG);
				return false;
			}
		}
		catch (SeguridadException ex) {
			Boolean salida = (Boolean) context
			.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
			if (!salida.booleanValue())
			{
				log.error("Error en las comprobaciones de seguridad del mensaje:"
						+ ex.getMessage(), ex);
				Utils.generaRespuestaError (context,Constantes.ERROR_FIRMA_ENTRADA_CODE, Constantes.ERROR_FIRMA_ENTRADA_MSG);
				return false;
			}
			else
			{
				//Este caso es complicado de manejar, ya que puede haberse actualizado la información
				//y fallar en su respuesta.
				log.error("Error en la generación de firma a la salida del mensaje:"
						+ ex.getMessage(), ex);
				Utils.generaRespuestaError (context,Constantes.ERROR_FIRMA_SALIDA_CODE, Constantes.ERROR_FIRMA_SALIDA_MSG);
				return false;
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
					System.err.println (new java.util.Date().toString()+"::Control de publicación en BOE::error en las comprobaciones de seguridad del mensaje::"+ex.getMessage());
					ex.printStackTrace();
					
				}
				Utils.generaRespuestaError (context,Constantes.ERROR_FIRMA_ENTRADA_CODE, Constantes.ERROR_FIRMA_ENTRADA_MSG);
				return false;
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
		return seguridadMensaje(context);
	}

}
