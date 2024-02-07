package es.boe.controlpublicacion.seguridad;


import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.ws.Binding;
import javax.xml.ws.Holder;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.handler.Handler;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import es.tributasenasturias.servicios.clientes.autenticacionEPST.AutenticacionEPST;
import es.tributasenasturias.servicios.clientes.autenticacionEPST.AutenticacionEPST_Service;






/**
 * 
 * @author crubencvs
 * Clase que encapsulará la llamada al servicio de autenticación del Principado de Asturias.
 */
public class VerificadorCertificado{

	private String endpointAutenticacionEPST;
	AutenticacionEPST_Service serv;
	AutenticacionEPST port;
	//Manejador para los mensajes de comunicación.
	//No es necesario que exista, pero si se quieren registrar o manipular los mensajes, es 
	//necesario indicarlo.
	private SOAPHandler<SOAPMessageContext> manejadorMensajes=null;
	/**
	 * Constructor por defecto, no se podrá instanciar.
	 */
	@SuppressWarnings("unused")
	private VerificadorCertificado()
	{
		
	}
	
	private AutenticacionEPST_Service instanciarAutenticacionEPST_Service()
	{
		AutenticacionEPST_Service srv;
		//Se modifica de forma dinámica la localización del WSDL, que debe estar en el
		//jar donde se define AutenticacionEPST_Service, y debe estar situado en 
		//META-INF/wsdls/PXAutenticacionEPST.
		//Esto no es peor que lo que genera el cliente por defecto, que también lo mete,
		//pero al menos esto funciona bien.
		WebServiceClient ann = AutenticacionEPST_Service.class.getAnnotation(WebServiceClient.class);
		URL urlWsdl = AutenticacionEPST_Service.class.getResource("/META-INF/wsdls/PXAutenticacionEPST.wsdl");
		srv=new AutenticacionEPST_Service (urlWsdl,new QName(ann.targetNamespace(), ann.name()));
		return srv;
	}
	/**
	 * Construye un comprobador de certificado cuyas llamadas se dirigirán a un endpoint concreto.
	 * No se incluye información sobre manejadores de mensajes, con lo cual no se manejarán.
	 * @param endpointAutenticacionEPST endpoint de autenticación de EPST
	 */
	protected VerificadorCertificado(String endpointAutenticacionEPST) {		
		this.endpointAutenticacionEPST=endpointAutenticacionEPST;
		serv = instanciarAutenticacionEPST_Service();
		port = serv.getAutenticacionEPSTSOAP();
		javax.xml.ws.BindingProvider bpr = (javax.xml.ws.BindingProvider) port; 
		bpr.getRequestContext().put(javax.xml.ws.BindingProvider.ENDPOINT_ADDRESS_PROPERTY , endpointAutenticacionEPST);
	}
	/**
	 * Construye un comprobador de certificado cuyas llamadas se dirigirán a un endpoint concreto,
	 * y cuyos mensajes los interceptará un manejador que se indica.
	 */
	protected VerificadorCertificado(String endpointAutenticacionEPST, SOAPHandler<SOAPMessageContext> manejador) {		
		this(endpointAutenticacionEPST);
		this.manejadorMensajes=manejador;
	}
	
	/**
	 * Realiza la llamada a la función "login" del servicio de autenticación.
	 * @param certificado Certificado a validar
	 * @return identificador recuperado, si el resultado es Valido.
	 */
	@SuppressWarnings("unchecked")
	public InfoCertificado login (String certificado) throws SeguridadException
	{
		InfoCertificado info = SeguridadFactory.newInfoCertificado();
		//Para recuperar datos de la consulta de certificado.
        Holder<Boolean>esError = new Holder<Boolean>();
        Holder<String>idError = new Holder<String>();
        Holder<String>mensajeError = new Holder<String>();
        Holder<String>cif = new Holder<String>();
        Holder<String>nifNIE= new Holder<String>();
        Holder<String>razonSocial = new Holder<String>();
        Holder<String>apellido1 = new Holder<String>();
        Holder<String>apellido2 = new Holder<String>();
        Holder<String>nombre = new Holder<String>();
        Holder<String>tipo = new Holder<String>();
        //Incluímos el manejador de mensajes.
		if (manejadorMensajes!=null)
		{
			javax.xml.ws.BindingProvider bpr = (javax.xml.ws.BindingProvider) port;
			Binding bi = bpr.getBinding();
			List<Handler> handlerList = bi.getHandlerChain();
			if (handlerList == null)
			{
			   handlerList = new ArrayList<Handler>();
			}
			handlerList.add(manejadorMensajes);
			bi.setHandlerChain(handlerList);
		}
		try
		{
			//Se realiza la operación
			port.login(certificado, esError, idError, mensajeError, cif, nifNIE, razonSocial, apellido1, apellido2,nombre, tipo);
			if (!esError.value.booleanValue())
			{
				//Devolvemos CIF o NIF
				if (cif.value!=null && !"".equals(cif.value))
				{
					info.setCif(cif.value);
				}
				else if (nifNIE.value!=null && !"".equals(nifNIE.value))
				{
					info.setNifNie(nifNIE.value);
				}
				info.setApellido1(apellido1.value);
				info.setApellido2(apellido2.value);
				info.setRazonSocial(razonSocial.value);
				info.setNombre(nombre.value);
				info.setTipo(tipo.value);
				info.setValidez(InfoCertificado.Validez.VALIDO);
			}
			else
			{
				info.setValidez(InfoCertificado.Validez.INVALIDO);
			}
		}
		catch (Exception ex)
		{
			//Error producido, seguramente por comunicaciones o SOAP Fault.
			throw new SeguridadException ("Error producido en la comprobación de validez del certificado:"+ ex.getMessage(),ex);
		}
		return info;
	}



	public SOAPHandler<SOAPMessageContext> getManejadorMensajes() {
		return manejadorMensajes;
	}

	public void setManejadorMensajes(
			SOAPHandler<SOAPMessageContext> manejadorMensajes) {
		this.manejadorMensajes = manejadorMensajes;
	}
	/**
	 * @return recupera el endpoint de autenticación
	 */
	public String getEndpointAutenticacionEPST() {
		return endpointAutenticacionEPST;
	}
	/**
	 * @param endpointAutenticacionEPST el endpoint de autenticación a utilizar
	 */
	public void setEndpointAutenticacionEPST(String endpointAutenticacionEPST) {
		this.endpointAutenticacionEPST = endpointAutenticacionEPST;
	}
}
