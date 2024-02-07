/**
 * 
 */
package es.boe.controlpublicacion.seguridad;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.PropertyResourceBundle;

import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

/** Clase que se utiliza para configurar los componentes que permiten comprobar los permisos sobre un
 *  servicio.
 * @author crubencvs
 *
 */
public class PropertyConfigurator {
	
	private String endpointLanzadorBD;
	private String procBDPermisoServicio;
	private String esquemaBD;
	private String endpointServicioAutenticacion;
	private SOAPHandler<SOAPMessageContext> soapHandler;
	private PropertyResourceBundle prop;
	@SuppressWarnings("unused")
	private PropertyConfigurator() {}
	/**
	 * Constructor, permite indicar un fichero de propiedades desde el que se cargarán las
	 * necesarias para el funcionamiento de la funcionalidad de comprobación de permisos
	 * de ejecución del servicio.
	 * El fichero puede estar codificado en cualquier juego de caracteres.
	 * @param propertyFile Fichero de propiedades a utilizar como configuración.
	 * @throws IOException si no pudiera abrir el fichero.
	 */
	public PropertyConfigurator (File propertyFile) throws IOException
	{
		prop = new PropertyResourceBundle (new BufferedReader(new FileReader(propertyFile)));
		endpointServicioAutenticacion = prop.getString("sec.permisos.endpoint.endpointServicioAutenticacion");
		endpointLanzadorBD=prop.getString("sec.permisos.endpoint.endpointLanzador");
		procBDPermisoServicio = prop.getString("sec.permisos.bd.proc.procBDPermisoServicio");
		esquemaBD=prop.getString("sec.permisos.bd.esquemaBD");
	}
	/**
	 * Constructor, permite indicar un fichero de propiedades desde el que se cargarán las
	 * necesarias para el funcionamiento de la funcionalidad de comprobación de permisos
	 * de ejecución del servicio, así como una instancia de un manejador SOAP
	 * que permita realizar operaciones sobre los mensajes SOAP
	 * que se produzcan durante la operación de la comprobación de permisos.
	 * El fichero puede estar codificado en cualquier juego de caracteres.
	 * @param propertyFile Fichero de propiedades a utilizar como configuración.
	 * @param soapHandler Instancia de objeto que implementa {@link SOAPHandler} y que permite 
	 *          realizar operaciones sobre los mensajes que se produzcan.
	 * @throws IOException si no pudiera abrir el fichero.
	 */
	public PropertyConfigurator (File propertyFile, SOAPHandler<SOAPMessageContext> soapHandler) throws IOException
	{
		this(propertyFile);
		this.setSoapHandler(soapHandler);
	}
	
	/**
	 * Constructor, permite indicar las propiedades sin necesidad de fichero, una a una.
	 * @param endpointServicioAutenticacion Endpoint del servicio de autenticación de EPST
	 * @param endpointLanzador Endpoint del lanzador de peticiones a BD.
	 * @param procBDPermisoServicio Nombre del procedimiento de base de datos que comprueba permisos del servicio
	 * @param esquemaBD Nombre del esquema en el que se ejecutará el procedimiento anterior.
	 */
	public PropertyConfigurator (String endpointServicioAutenticacion, 
								String endpointLanzador, 
								String procBDPermisoServicio, String esquemaBD)
	{
		this.endpointServicioAutenticacion=endpointServicioAutenticacion;
		this.endpointLanzadorBD = endpointLanzador;
		this.procBDPermisoServicio = procBDPermisoServicio;
		this.esquemaBD=esquemaBD;
	}
	
	/**
	 * Constructor, permite indicar las propiedades sin necesidad de fichero, una a una.
	 * @param endpointServicioAutenticacion Endpoint del servicio de autenticación de EPST
	 * @param endpointLanzador Endpoint del lanzador de peticiones a BD.
	 * También permite añadir una referencia al manejador de mensajes SOAP que se usará internamente.
	 * @param procBDPermisoServicio Nombre del procedimiento de base de datos que comprueba permisos del servicio
	 * @param esquemaBD Nombre del esquema en el que se ejecutará el procedimiento anterior.
	 */
	public PropertyConfigurator (String endpointServicioAutenticacion, 
								String endpointLanzador, 
								String procBDPermisoServicio, 
								String esquemaBD,
								SOAPHandler<SOAPMessageContext> soapHandler)
	{
		this (endpointServicioAutenticacion, endpointLanzador, procBDPermisoServicio, esquemaBD);
		this.setSoapHandler(soapHandler);
	}
	/**
	 * @return the endpointLanzadorBD
	 */
	public String getEndpointLanzadorBD() {
		return endpointLanzadorBD;
	}
	/**
	 * @param endpointLanzadorBD the endpointLanzadorBD to set
	 */
	public void setEndpointLanzadorBD(String endpointLanzadorBD) {
		this.endpointLanzadorBD = endpointLanzadorBD;
	}
	/**
	 * @return the procBDPermisoServicio
	 */
	public String getProcBDPermisoServicio() {
		return procBDPermisoServicio;
	}
	/**
	 * @param procBDPermisoServicio the procBDPermisoServicio to set
	 */
	public void setProcBDPermisoServicio(String procBDPermisoServicio) {
		this.procBDPermisoServicio = procBDPermisoServicio;
	}
	/**
	 * @return the esquemaBD
	 */
	public String getEsquemaBD() {
		return esquemaBD;
	}
	/**
	 * @param esquemaBD the esquemaBD to set
	 */
	public void setEsquemaBD(String esquemaBD) {
		this.esquemaBD = esquemaBD;
	}
	/**
	 * @return el endpoint del servicio de autenticación que se usa
	 */
	public String getEndpointServicioAutenticacion() {
		return endpointServicioAutenticacion;
	}
	/**
	 * @param endpointServicioAutenticacion el endpoint del servicio de autenticación
	 */
	public void setEndpointServicioAutenticacion(
			String endpointServicioAutenticacion) {
		this.endpointServicioAutenticacion = endpointServicioAutenticacion;
	}
	/**
	 * @return the soapHandler
	 */
	public SOAPHandler<SOAPMessageContext> getSoapHandler() {
		return soapHandler;
	}
	/**
	 * @param soapHandler the soapHandler to set
	 */
	public void setSoapHandler(SOAPHandler<SOAPMessageContext> soapHandler) {
		this.soapHandler = soapHandler;
	}
}
