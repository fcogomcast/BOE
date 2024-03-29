package es.boe.controlpublicacion.preferencias;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.prefs.Preferences;

public class Preferencias 
{
	private Preferences m_preferencias;
	private final static String dirPref="proyectos/WSBOEControlPublicacion";
	private final static String FICHERO_PREFERENCIAS = "prefBOEControlPublicacion.xml";
	private final static String DIRECTORIO_PREFERENCIAS = dirPref;
 
	private HashMap<String, String> tablaPreferencias = new HashMap<String, String>();
	
	//nombres de las preferencias
	private final static String KEY_PREF_LOG = "ModoLog";
	//Firma
	private final static String KEY_PREF_ALIAS_CERTIFICADO_FIRMA = "AliasCertificadoFirma";
	private final static String KEY_PREF_ENDPOINT_SEGURIDAD = "EndPointSeguridadSW";
	//Autenticación de certificado con permiso de ejecución de servicio.
	private final static String KEY_PREF_ENDPOINT_AUTENTICACION_EPST = "EndPointAutenticacionEPST";
	private final static String KEY_PREF_VALIDA_PERMISOS = "ValidaPermisosServicio";
	private final static String KEY_PREF_PROCPERMISO_SERVICIO = "PAPermisoServicio";
	private final static String KEY_PREF_VALIDA_CERTIFICADO= "ValidaCertificado";
	private final static String KEY_PREF_ALIAS_SERVICIO = "AliasServicio";
	private final static String KEY_PREF_VALIDA_FIRMA = "ValidaFirma";
	private final static String KEY_PREF_FIRMA_SALIDA = "FirmaSalida";
	//Lanzador
	private final static String KEY_PREF_ENDPOINT_LANZADOR = "EndPointLanzador";
	private final static String KEY_PREF_ESQUEMA = "EsquemaBaseDatos";
	//Procedimientos de base de datos
	private final static String KEY_PREF_PA_CONTROL_PUBLICACION = "pAControlPublicacion";
	//Fichero de log
	private final static String KEY_PREF_APP_FICHERO_LOG="FicheroLogApp";
	private final static String KEY_PREF_SS_FICHERO_LOG="FicheroLogSoapServer";
	private final static String KEY_PREF_SC_FICHERO_LOG="FicheroLogSoapClient";
	protected Preferencias() throws PreferenciasException 
	{		
		cargarPreferencias();
	}
	protected synchronized final void cargarPreferencias() throws PreferenciasException
 {
		try
		{
			if(compruebaFicheroPreferencias())
			{		       
			
		        FileInputStream inputStream = new FileInputStream(DIRECTORIO_PREFERENCIAS + "/" + FICHERO_PREFERENCIAS);
		        Preferences.importPreferences(inputStream);
		        inputStream.close();
		
		        m_preferencias = Preferences.systemNodeForPackage(this.getClass());
		        
		        String[] keys = m_preferencias.keys();
		        String msgKeys ="Leyendo las siguientes claves -> ";
		        for(int i=0;i<keys.length;i++)
		        {
		        	msgKeys += "["+keys[i]+"] ";
		        }
		        
		        for(int i=0;i<keys.length;i++)
		        {
		        	String value = m_preferencias.get(keys[i], "");
		        	tablaPreferencias.put(keys[i], value);
		        }
			}
		}
		catch (Exception ex)
		{
			throw new PreferenciasException ("Error al cargar las preferencias: " +ex.getMessage(),ex);
		}
		
 }
	
	private synchronized void inicializaTablaPreferencias()
	{
		
		tablaPreferencias.clear();
		
		tablaPreferencias.put(KEY_PREF_LOG,									"INFO");
		tablaPreferencias.put(KEY_PREF_ALIAS_CERTIFICADO_FIRMA,				"Tributas");
		tablaPreferencias.put(KEY_PREF_ENDPOINT_AUTENTICACION_EPST,			"http://bus:7101/WSAutenticacionEPST/ProxyServices/PXAutenticacionEPST");
		tablaPreferencias.put(KEY_PREF_ENDPOINT_LANZADOR,					"http://bus:7101/WSInternos/ProxyServices/PXLanzador");  
		tablaPreferencias.put(KEY_PREF_ESQUEMA,								"EXPLOTACION");
		tablaPreferencias.put(KEY_PREF_PROCPERMISO_SERVICIO,				"INTERNET.permisoServicio");
		tablaPreferencias.put(KEY_PREF_VALIDA_PERMISOS,						"S");
		tablaPreferencias.put(KEY_PREF_VALIDA_CERTIFICADO,					"S");
		tablaPreferencias.put(KEY_PREF_APP_FICHERO_LOG,						"proyectos/WSBOEControlPublicacion/logs/Application.log");	
		tablaPreferencias.put(KEY_PREF_SS_FICHERO_LOG,						"proyectos/WSBOEControlPublicacion/logs/SOAP_SERVER.log");
		tablaPreferencias.put(KEY_PREF_SC_FICHERO_LOG,						"proyectos/WSBOEControlPublicacion/logs/SOAP_CLIENT.log");
		tablaPreferencias.put(KEY_PREF_ALIAS_SERVICIO,						"BOE_CTLPUB");
		tablaPreferencias.put(KEY_PREF_VALIDA_FIRMA,						"S");
		tablaPreferencias.put(KEY_PREF_FIRMA_SALIDA,						"S");
		tablaPreferencias.put(KEY_PREF_ENDPOINT_SEGURIDAD,					"http://bus:7101/WSInternos/ProxyServices/PXSeguridadWS");
		tablaPreferencias.put(KEY_PREF_PA_CONTROL_PUBLICACION,				"BOE.ControlPublicacion");
	}
	
	private synchronized boolean compruebaFicheroPreferencias() throws PreferenciasException
 {
		boolean existeFichero = false;
		
     File f = new File(DIRECTORIO_PREFERENCIAS + "/" + FICHERO_PREFERENCIAS);
     existeFichero = f.exists();
     if (existeFichero == false)
     {
         crearFicheroPreferencias();
     }
     
     return existeFichero;
 }
	
	 /***********************************************************
  * 
  * Creamos el fichero de preferencias con los valores por 
  * defecto
  * 
  ***************************************************************/
 private synchronized void crearFicheroPreferencias() throws PreferenciasException
 {
 	
     //preferencias por defecto
     m_preferencias = Preferences.systemNodeForPackage(this.getClass());
     
     inicializaTablaPreferencias();
     
     //recorremos la tabla cargada con las preferencias por defecto
     Iterator<Map.Entry<String, String>> itr = tablaPreferencias.entrySet().iterator();
     while(itr.hasNext())
     {
     	Map.Entry<String, String> e = (Map.Entry<String,String>)itr.next();
     	
     	m_preferencias.put(e.getKey(),e.getValue());
     }

     FileOutputStream outputStream = null;
     File fichero;
     try
     {
         fichero = new File(DIRECTORIO_PREFERENCIAS);
         if(fichero.exists() == false)
             if (fichero.mkdirs()==false)
             	{
             	 throw new java.io.IOException ("No se puede crear el directorio de las preferencias.");
             	}
         
         outputStream = new FileOutputStream(DIRECTORIO_PREFERENCIAS + "/" + FICHERO_PREFERENCIAS);
         m_preferencias.exportNode(outputStream);
     }
     catch (Exception e)
     {
    	throw new PreferenciasException("Error al crear el fichero de preferencias:" + e.getMessage(),e);
     }
     finally
     {
         try
         {
             if(outputStream != null)
                 outputStream.close();
         }
         catch(Exception e)
         {
        	 throw new PreferenciasException ("Error al cerrar el flujo del fichero de preferencias:" + e.getMessage(),e);
         }
     }
 }
 
 public void recargaPreferencias() throws PreferenciasException
 {
 	cargarPreferencias();
 }
 
 private String getValueFromTablaPreferencias(String key)
 {
 	String toReturn="";
 	
 	if(tablaPreferencias.containsKey(key))
 	{
 		toReturn = tablaPreferencias.get(key);
 	}
 	
 	//Logger.debug("Se ha pedido la preferencia '"+key+"' a lo que el sistema devuelve '"+toReturn+"'");
 	
 	return toReturn;
 }
 
 private void setValueIntoTablaPreferencias(String key, String value)
 {
 	//Logger.debug("Se actualizara el valor de la preferencia '"+key+"' a '"+value+"'");
 	tablaPreferencias.put(key, value);
 }

	public String getEsquemaBaseDatos() {
		return getValueFromTablaPreferencias(KEY_PREF_ESQUEMA);
	}
	public void setEsquemaBaseDatos(String esquema) {
		setValueIntoTablaPreferencias(KEY_PREF_ESQUEMA, esquema);
	}
    public String getEndPointLanzador() {
		return getValueFromTablaPreferencias(KEY_PREF_ENDPOINT_LANZADOR);
	}
	public void setEndPointLanzador(String endPointLanzador) {
		setValueIntoTablaPreferencias(KEY_PREF_ENDPOINT_LANZADOR, endPointLanzador);
	}
	public String getModoLog() {
		return getValueFromTablaPreferencias(KEY_PREF_LOG);
	}
	public void setModoLog(String modo) {
		setValueIntoTablaPreferencias(KEY_PREF_LOG, modo);
	}
	public String getAliasCertificadoFirma() {
		return getValueFromTablaPreferencias(KEY_PREF_ALIAS_CERTIFICADO_FIRMA);
	}
	public void setAliasCertificadoFirma(String certificadoFirma) {
		setValueIntoTablaPreferencias(KEY_PREF_ALIAS_CERTIFICADO_FIRMA, certificadoFirma);
	}
	public String getEndPointAutenticacion() {
		return getValueFromTablaPreferencias(KEY_PREF_ENDPOINT_AUTENTICACION_EPST);
	}
	public String getPAPermisoServicio() {
		return getValueFromTablaPreferencias(KEY_PREF_PROCPERMISO_SERVICIO);
	}
	public void setPAPermisoServicio(String pAPermisoServicio) {
		setValueIntoTablaPreferencias(KEY_PREF_PROCPERMISO_SERVICIO, pAPermisoServicio);
	}
	public String getValidaPermisosServicio() {
		return getValueFromTablaPreferencias(KEY_PREF_VALIDA_PERMISOS);
	}
	public void setValidaPermisosServicio(String validaPermisosServicio) {
		setValueIntoTablaPreferencias(KEY_PREF_VALIDA_PERMISOS, validaPermisosServicio);
	}
	public String getValidaCertificado() {
		return getValueFromTablaPreferencias(KEY_PREF_VALIDA_CERTIFICADO);
	}
	public void setValidaCertificado(String validaCertificado) {
		setValueIntoTablaPreferencias(KEY_PREF_VALIDA_CERTIFICADO, validaCertificado);
	}
	public String getValidaFirma() {
		return getValueFromTablaPreferencias(KEY_PREF_VALIDA_FIRMA);
	}
	public void setValidaFirma(String validaFirma) {
		setValueIntoTablaPreferencias(KEY_PREF_VALIDA_FIRMA, validaFirma);
	}
	public String getFicheroLogApp() {
		return getValueFromTablaPreferencias(KEY_PREF_APP_FICHERO_LOG);
	}
	public String getFicheroLogSoapServer() {
		return getValueFromTablaPreferencias(KEY_PREF_SS_FICHERO_LOG);
	}
	public String getFicheroLogSoapClient() {
		return getValueFromTablaPreferencias(KEY_PREF_SC_FICHERO_LOG);
	}
	public String getAliasServicio() {
		return getValueFromTablaPreferencias(KEY_PREF_ALIAS_SERVICIO);
	}
	public String getEndpointSeguridadSW() {
		return getValueFromTablaPreferencias(KEY_PREF_ENDPOINT_SEGURIDAD);
	}
	public String getFirmaSalida() {
		return getValueFromTablaPreferencias(KEY_PREF_FIRMA_SALIDA);
	}
	public String getpAControlPublicacion() {
		return getValueFromTablaPreferencias(KEY_PREF_PA_CONTROL_PUBLICACION);
	}
}
