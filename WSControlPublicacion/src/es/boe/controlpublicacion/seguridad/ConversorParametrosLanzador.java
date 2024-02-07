package es.boe.controlpublicacion.seguridad;


import java.util.ArrayList;
import java.util.HashMap;

import org.w3c.dom.Node;




/**
 * Clase de utilidad para manejar parámetros a procedimientos de base de datos.
 * 
 *
 */
public class ConversorParametrosLanzador 
{
	private HashMap<TIPOS,String> m_tipos = new HashMap<TIPOS, String>();
	private ArrayList<String> m_valorParametros = new ArrayList<String>();
	private ArrayList<String> m_formatoParametros = new ArrayList<String>();
	private ArrayList<TIPOS> m_tipoParametros = new ArrayList<TIPOS>();
	private String m_procedimientoAlmacenado = "";
	private String m_resultado = "";
	private org.w3c.dom.Document m_resultadoXML = null;
	//private GenericAppLogger logger;
	// Objeto para manejar árbol DOM.
	javax.xml.parsers.DocumentBuilderFactory fact;
	javax.xml.parsers.DocumentBuilder db;
	/**
	 * Enumeración de tipos de parámetro.
	 * 
	 *
	 */
	public enum TIPOS {String, Integer, Date}
	/**
	 * Constructor público.
	 * @throws javax.xml.parsers.ParserConfigurationException
	 */
	public ConversorParametrosLanzador() throws javax.xml.parsers.ParserConfigurationException
	{
		//inicializamos la correspondencia de tipos
		m_tipos.put(TIPOS.String, 	"1");
		m_tipos.put(TIPOS.Integer, 	"2");
		m_tipos.put(TIPOS.Date, 	"3");
		// Inicializamos  los objetos que nos servirán para parsear los resultados.
		fact = javax.xml.parsers.DocumentBuilderFactory.newInstance();
		db= fact.newDocumentBuilder(); // Lanza javax.xml.parsers.ParserConfigurationException
		//logger=new SimpleFileLogger();
		//logger= new TributasLogger();
	}
	/**
	 * Método que devuelve el resultado de una llamda.
	 * @return Cadena de resultado de una llamada.
	 */
	public String getResultado()
	{
		return m_resultado;
	}
	
	/**
	 * Obtiene el primer valor asociado al nombre del nodo dado
	 * @param nombreNodo
	 * @return
	 */
	public String getNodoResultado(String nombreNodo)
	{
		String toReturn = "";
		
		int initialIndex = m_resultado.indexOf("<"+nombreNodo+"/>");
		if (initialIndex!=-1)
		{
			toReturn="";
		}
		else
		{
			initialIndex = m_resultado.indexOf("<"+nombreNodo);
			if ( initialIndex >= 0)
			{
				int inicioValor = m_resultado.indexOf('>', initialIndex) + 1;
				int finValor = m_resultado.indexOf('<',inicioValor);
				
				toReturn = m_resultado.substring(inicioValor,finValor);
			}
		}
		
		//logger.info("Devolviendo el valor '"+toReturn+"' del nodo '"+nombreNodo+"'");
		
		return toReturn;
	}
	private String getNodoValor (Node nodo)
	{
		String valor=null;
		Node hijo=null;
		if (nodo!=null)
		{
			if (nodo.hasChildNodes())
			{
				for (hijo=nodo.getFirstChild();hijo!=null;hijo=hijo.getNextSibling())
				{
					if (hijo.getNodeType()==Node.TEXT_NODE||hijo.getNodeType()==Node.CDATA_SECTION_NODE)
					{
						valor = hijo.getNodeValue();// El texto del nodo está en el nodo texto hijo.
						break;
					}
				}
			}
		}
		return valor;
	}
	/**
	 * Obtiene el primer valor asociado al nombre del nodo dado
	 * @param nombreNodo
	 * @return
	 */
	public String getNodoResultadoX(String nombreNodo)
	{
		if (this.m_resultadoXML==null)
		{
			return getNodoResultado(nombreNodo);
		}
		String toReturn = "";
		
		org.w3c.dom.NodeList nodos = this.m_resultadoXML.getElementsByTagName(nombreNodo);
		if (nodos.getLength()>0) 
		{
			toReturn = getNodoValor(nodos.item(0));
		}
		
		return toReturn;
	}
	/**
	 * Parsea el resultado como XML. En caso de no poder hacerlo, simplemente lo ignora.
	 * @param nombreNodo
	 * @return
	 */
	private void interpretaResultadoXML()
	{
		if (this.m_resultado !=null && !this.m_resultado.equalsIgnoreCase(""))
		{
			org.xml.sax.InputSource inStr = new org.xml.sax.InputSource (); 
			inStr.setCharacterStream(new java.io.StringReader(m_resultado.toString()));// Ya es un string, pero para prevenir futuros cambios.
			try
			{
				this.m_resultadoXML = db.parse(inStr);
			}
			catch (java.io.IOException ex)
			{
				this.m_resultadoXML =null;
			}
			catch (org.xml.sax.SAXException ex)
			{
				this.m_resultadoXML=null;
			}
		}
	}
	/**
	 * Método para pasar un parámetro a la lista de un procedimiento  almacenado que se va a ejecutar.
	 * @param valor Valor del parámetro. Los parámetros son posicionales.
	 * @param tipo Tipo del parámetro.
	 */
	public void setParametro(String valor, TIPOS tipo)
	{
		String miVal="";
		if (valor==null) // Si no se hace así, introduce en la base de datos "null", al incluirlo en el ArrayList.
		{
			miVal="";
		}
		else
		{
			miVal=valor;
		}
		m_valorParametros.add(miVal);
		m_tipoParametros.add(tipo);
		m_formatoParametros.add(null);
	}
	/**
	 * Método para pasar un parámetro con formato a la lista de un procedimiento almacenado que se va a ejecutar.
	 * @param valor Valor del parámetro. Los parámetros son posicionales.
	 * @param tipo Tipo del parámetro.
	 * @param formato Formato del parámetro.
	 */
	public void setParametro(String valor, TIPOS tipo, String formato)
	{
		String miVal="";
		if (valor==null)
		{
			miVal="";
		}
		else
		{
			miVal=valor;
		}
		m_valorParametros.add(miVal);
		m_tipoParametros.add(tipo);
		m_formatoParametros.add(formato);
	}
	/**
	 * Método que indica el procedimiento almacenado que se va a ejecutar.
	 * @param nombre Nombre del procedimiento almacenado.
	 */
	public void setProcedimientoAlmacenado(String nombre)
	{
		m_procedimientoAlmacenado = nombre;
		m_valorParametros.clear();
		m_tipoParametros.clear();
		m_formatoParametros.clear();
	}
	
	public String getProcedimientoAlmacenado()
	{
		return m_procedimientoAlmacenado;
	}
	
	public String codifica()
	{
		String textoProcedimiento = "<proc nombre=\"" + m_procedimientoAlmacenado + "\">";
		String textoParametros = "";
		
		for(int i=0;i<m_valorParametros.size();i++)
		{
			String formato = "";
			if(m_formatoParametros.get(i) != null && m_formatoParametros.get(i).length()>0)
			{
				formato = "<formato>"+m_formatoParametros.get(i)+"</formato>";
			}
			else
			{
				formato = "<formato/>";
			}
			
	       	textoParametros += "<param id=\""+(i+1)+"\"><valor>"+m_valorParametros.get(i) +
	       	"</valor><tipo>"+m_tipos.get(m_tipoParametros.get(i))+"</tipo>"+formato+"</param>";
		}
			
		return "<peti>"+textoProcedimiento+textoParametros+"</proc></peti>";
	}
	
	public ArrayList<HashMap<String,String>> decodifica(String mensaje)
	{
		ArrayList<HashMap<String,String>> listaFilas = new ArrayList<HashMap<String,String>>();
		
		int i=0;
		while(i<mensaje.length())
		{
			int initialIndex = mensaje.indexOf("<fila>",i);
			if(initialIndex > 0)
			{
				int inicioFila = mensaje.indexOf('>', initialIndex) + 1;
				int finFila = mensaje.indexOf("</fila>",inicioFila);
				
				String fila = mensaje.substring(inicioFila, finFila);
				
				int j=0;
				HashMap<String,String> columnasFila = new HashMap<String, String>();
				while(j<fila.length())
				{
					int inicioColumna = fila.indexOf('<',j)+1;
					if(inicioColumna > 0)
					{
						int finColumna = fila.indexOf('>',inicioColumna);
						String nombreColumna = fila.substring(inicioColumna,finColumna);
						
						int finValor = fila.indexOf("</",finColumna);
						String valorColumna = fila.substring(finColumna+1,finValor);
						
						j = fila.indexOf('>',finValor);
						columnasFila.put(nombreColumna, valorColumna);
					}
					else
					{
						break;
					}
				}
				
				i = mensaje.indexOf('>',finFila);
				listaFilas.add(columnasFila);
			}
			else
			{
				break;
			}
		}
		
		return listaFilas;
	}
	public void setResultado(String resultado) {
		this.m_resultado = resultado;
		interpretaResultadoXML();
	}
}