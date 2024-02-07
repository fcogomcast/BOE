package es.boe.controlpublicacion.bd;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.ws.Binding;
import javax.xml.ws.handler.Handler;

import es.boe.controlpublicacion.contexto.ContextoLlamada;
import es.boe.controlpublicacion.exceptions.ControlPublicacionException;
import es.boe.controlpublicacion.preferencias.Preferencias;
import es.boe.controlpublicacion.soap.SoapClientHandler;



/**
 * Clase de acceso a datos.
 */
public class Datos 
{
	
	private final static String ERRORNODE = "error";
	private final static String STRING1_CANU="STRING1_CANU";
	private final static String STRING2_CANU="STRING2_CANU";
	
	private stpa.services.LanzaPLService lanzaderaWS; // Servicio Web
	private stpa.services.LanzaPL lanzaderaPort; // Port (operaciones) a las que se llamas
	private ConversorParametrosLanzador conversor;
	private Preferencias preferencias;
	private String errorLlamada;
	//Constantes para utilizar fuera de la clase.
	public static final String DES_RESULTADO_LLAMADA_BD = "DesResultadoBD";
	public static final String COD_RESULTADO_LLAMADA_BD = "CodResultadoBD";
	public static final String C_JUSTIFICANTE_PETICION_PATE = "Justificante";
	public static final String C_ESTADO_PETICION_PATE = "Estado";
	public static final String C_NRC = "NRC";
	public static final String C_NUM_OPERACION = "Nume_Oper";
	public static final String C_NUM_UNICO = "Num_Unico";
	public static final String C_APLICACION = "Servicio";
	public static final String C_FECHA_PAGO = "Fecha_Pago";
	public static final String C_FECHA_OPERACION = "Fecha_Operacion";
	public static final String C_PASARELA_PAGO_BD = "Pasarela_Pago";
	public static final String C_NIF_CONTRIBUYENTE = "Nif_Contribuyente";
	public static final String C_FECHA_DEVENGO = "Fecha_Devengo";
	public static final String C_DATO_ESPECIFICO = "Dato_Especifico";
	public static final String C_EXPEDIENTE = "Expediente";
	public static final String C_NIF_OPERANTE = "Nif_Operante";
	public static final String C_IMPORTE = "Importe";
	public static String C_RES_LLAMADA_OK="0";
	
	public static class ResultadoControlPublicacion
	{
		private String codigoResultado;
		private String descripcionResultado;
		private boolean error;
		public boolean isError() {
			return error;
		}
		public void setError(boolean error) {
			this.error = error;
		}
		public String getCodigoResultado() {
			return codigoResultado;
		}
		public String getDescripcionResultado() {
			return descripcionResultado;
		}
		public void setCodigoResultado(String codigoResultado) {
			this.codigoResultado = codigoResultado;
		}
		public void setDescripcionResultado(String descripcionResultado) {
			this.descripcionResultado = descripcionResultado;
		}
		
		
	}
	/**
	 * Constructor
	 * @param contexto Contiene los datos del contexto de la llamada al servicio.
	 */
	@SuppressWarnings("unchecked")
	public Datos(ContextoLlamada contexto) throws ControlPublicacionException
	{
		preferencias = contexto.getPreferencias();
		String endPointLanzador=preferencias.getEndPointLanzador();
		lanzaderaWS = new stpa.services.LanzaPLService();
		lanzaderaPort = lanzaderaWS.getLanzaPLSoapPort();
		javax.xml.ws.BindingProvider bpr = (javax.xml.ws.BindingProvider) lanzaderaPort; // enlazador de protocolo para el servicio.
		bpr.getRequestContext().put (javax.xml.ws.BindingProvider.ENDPOINT_ADDRESS_PROPERTY,endPointLanzador); // Cambiamos el endpoint
		//Asociamos el log con este port.
		Binding bi = bpr.getBinding();
		List <Handler> handlerList = bi.getHandlerChain();
		if (handlerList == null)
		{
		   handlerList = new ArrayList<Handler>();
		}
		handlerList.add(new SoapClientHandler(contexto));
		bi.setHandlerChain(handlerList);
		
		try {
			conversor = new ConversorParametrosLanzador();
		} catch (ParserConfigurationException e) {
			throw new ControlPublicacionException("Error al crear el objeto de comunicación con base de datos:"+ e.getMessage(),e);
		}
		
	}
	

	public ResultadoControlPublicacion controlPublicacion(String xmlPublicacion) throws ControlPublicacionException
	{
		conversor.setProcedimientoAlmacenado(this.preferencias.getpAControlPublicacion());
		conversor.setParametro("<![CDATA["+xmlPublicacion+"]]>", ConversorParametrosLanzador.TIPOS.Clob);
		conversor.setParametro("P", ConversorParametrosLanzador.TIPOS.String);
		String resultadoEjecutarPL;
		try {
			resultadoEjecutarPL = Ejecuta();
		} catch (RemoteException e) {
			throw new ControlPublicacionException ("Error al acceder a " + this.preferencias.getpAControlPublicacion()+":"+e.getMessage(),e);
		}
		conversor.setResultado(resultadoEjecutarPL);
		this.setErrorLlamada(conversor.getNodoResultadoX(ERRORNODE));
		ResultadoControlPublicacion res = new ResultadoControlPublicacion();
		if ("".equals(this.errorLlamada))
		{
			String codResultado= conversor.getNodoResultadoX(STRING1_CANU);
			String desResultado= conversor.getNodoResultadoX(STRING2_CANU);
			res.setCodigoResultado(codResultado);
			res.setDescripcionResultado(desResultado);
			if ("OK".equalsIgnoreCase(codResultado))
			{
				res.setError(false);
			}
			else
			{
				res.setError(true);
			}
		}
		else
		{
			res.setError(true);
			res.setDescripcionResultado(this.getErrorLlamada());
		}
		return res;
	}
	
	/**
	 * Ejecuta un procedimiento almacenado en la base de datos mediante el servicio lanzador.
	 * @return String con los datos de XML de la respuesta del servicio.
	 * @throws RemoteException Si se produce un error de conexión.
	 */
	private String Ejecuta() throws RemoteException
	{
		return lanzaderaPort.executePL(
				preferencias.getEsquemaBaseDatos(),
				conversor.codifica(),
				"", "", "", "");
	}
	/**
	 * Método que devuelve el error en la ultima llamada a un procedimiento de esta clase.
	 * Los errores en llamada son devueltos en formato XML, este procedimiento devuelve el error
	 * extraído en formato de cadena.
	 * @return
	 */
	public String getErrorLlamada() {
		return errorLlamada;
	}

	private void  setErrorLlamada(String errorLlamada) {
		this.errorLlamada = errorLlamada;
	}
}
