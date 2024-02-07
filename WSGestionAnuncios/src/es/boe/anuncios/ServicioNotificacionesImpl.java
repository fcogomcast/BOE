package es.boe.anuncios;

import java.io.File;
import java.io.UnsupportedEncodingException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import es.boe.anuncios.contexto.ContextoLlamada;
import es.boe.anuncios.exceptions.AnunciosException;
import es.boe.anuncios.preferencias.Preferencias;
import es.boe.anuncios.utils.Base64;
import es.boe.anuncios.utils.XMLDOMDocumentException;
import es.boe.anuncios.utils.XMLDOMUtils;
import es.boe.dir3.Dir3;
import es.boe.dir3.Dir3.Nivel;
import es.tributasenasturias.utils.log.Logger;

public class ServicioNotificacionesImpl {
	ContextoLlamada contexto;
	Preferencias pref;
	/**
	 * Constructor privado
	 * @param contexto {@link ContextoLlamada} con datos del contexto para esta llamada.
	 */
	private ServicioNotificacionesImpl(ContextoLlamada contexto)
	{
		this.contexto= contexto;
		this.pref=contexto.getPreferencias();
	}
	/**
	 * Realiza el env�o de N anuncios. 
	 * Se recibe el XML, se insertan constantes, se convierte a base64, se firma mediante WSSecurity y se env�a al BOE.
	 * La firma y log de env�o se realizar� mediante un handler.
	 * La respuesta no se procesa, se env�a tal cual al llamante.
	 * @param envio XML de env�o de anuncios
	 * @param contexto Contexto de la llamada
	 * @return Respuesta
	 * @throws AnunciosException
	 */
	public Respuesta envioAnuncios(String envio) throws AnunciosException{
		try {
			Logger log = contexto.getLogger();
			log.debug("Se insertan las constantes en XML");
			String envioModificado=insertaConstantes(envio);
			log.debug("Se codifica el XML");
			String enviob64 = new String(Base64.encode(envioModificado.getBytes("UTF-8")));
			log.debug ("XML codificado, se env�a a BOE");
			BOEClient boe = new BOEClient(contexto);
			log.debug("Anuncios enviados, respuesta recibida");
			return boe.envioAnuncios(enviob64);
			
		} catch (UnsupportedEncodingException e) {
			throw new AnunciosException("Error al codificar el XML como UTF-8:"+ e.getMessage(),e);
		}
	}
	/**
	 * Realiza la consulta de un env�o
	 * @param idEnvio Identificador del env�o tal como lo han enviado en la respuesta del servicio de 
	 * 		  env�o de anuncios.
	 * @return Respuesta
	 * @throws AnunciosException
	 */
	public Respuesta consultaEnvio (String idEnvio) throws AnunciosException{
		Logger log = contexto.getLogger();
		log.debug ("Consulta de env�o en BOE");
		BOEClient boe = new BOEClient(contexto);
		log.debug ("Consulta realizada, respuesta recibida");
		return boe.consultaEnvio(idEnvio);
	}
	/**
	 * Realiza la consulta de un anuncio en BOE. El anuncio ha de haber sido enviado por STPA
	 * y haber sido correctamente recibido
	 * @param idAnuncio Identificador del anuncio cuyos datos se quieren consultar
	 * @return Datos del anuncio
	 * @throws AnunciosException
	 */
	public Respuesta consultaAnuncio(String idAnuncio) throws AnunciosException{
		Logger log = contexto.getLogger();
		log.debug("Consulta de anuncio en BOE");
		BOEClient boe = new BOEClient (contexto);
		log.debug("Consulta realizada, respuesta recibida");
		return boe.consultaAnuncio(idAnuncio);
	}
	/**
	 * Realiza la anulaci�n de un env�o completo en BOE. 
	 * @param idEnvio Identificador el env�o en BOE que se quiere anular.
	 * @return Respuesta de la anulaci�n
	 * @throws AnunciosException
	 */
	public Respuesta anulacionEnvio(String idEnvio) throws AnunciosException{
		Logger log = contexto.getLogger();
		log.debug("Anulaci�n de env�o en BOE");
		BOEClient boe = new BOEClient (contexto);
		log.debug("Anulaci�n realizada, respuesta recibida");
		return boe.anulacionEnvio(idEnvio);
	}
	/**
	 * Realiza la anulaci�n de un anuncio concreto. 
	 * @param idAnuncio Identificador del anuncio en BOE
	 * @return Respuesta de la anulaci�n
	 * @throws AnunciosException
	 */
	public Respuesta anulacionAnuncio(String idAnuncio) throws AnunciosException{
		Logger log = contexto.getLogger();
		log.debug("Anulaci�n de anuncio en BOE");
		BOEClient boe = new BOEClient (contexto);
		log.debug("Anulaci�n realizada, respuesta recibida");
		return boe.anulacionAnuncio(idAnuncio);
	}
	/**
	 * Devuelve una nueva instancia de la implementaci�n de operaciones del servicio
	 * @param contexto {@link ContextoLlamada}
	 * @return nueva instancia de la implementaci�n de operaciones de servicio
	 */
	public static ServicioNotificacionesImpl newInstance(ContextoLlamada contexto)
	{
		return new ServicioNotificacionesImpl(contexto);
	}
	/**
	 * Inserta las constantes necesarias para el env�o, como el email y la url de respuesta
	 * @param envio Cadena que contiene el xml de env�o
	 * @return Cadena con el xml de env�o modificada para incluirle las constantes
	 */
	private String insertaConstantes(String envio) throws AnunciosException
	{
		try {
			Document doc = XMLDOMUtils.parseXml(envio);
			Node email = XMLDOMUtils.selectSingleNode(doc, "//infPub/email");
			Node url = XMLDOMUtils.selectSingleNode(doc, "//infPub/urlSW");
			XMLDOMUtils.setNodeText(doc, email, pref.getEmailEnvio());
			XMLDOMUtils.setNodeText(doc, url, pref.getUrlRespuesta());
			//Construimos el dir3
			contruirDir3(doc);
			return XMLDOMUtils.getXMLText(doc);
		} catch (XMLDOMDocumentException e) {
			throw new AnunciosException("Error al insertar las constantes:" + e.getMessage(),e);
		}
	}
	
	private void contruirDir3(Document doc) throws AnunciosException
	{
		try {
			JAXBContext ctx = JAXBContext.newInstance(Dir3.class.getPackage().getName());
			Unmarshaller um = ctx.createUnmarshaller();
			Dir3 datos=  (Dir3)um.unmarshal(new File(pref.getFicheroDir3()));
			//Primero el remitente
			Node remitente = XMLDOMUtils.selectSingleNode(doc, "//anuncios/remitente");
			for (Nivel n: datos.getNivel())
			{
				Node nremitente = doc.createElement("nodoRemitente");
				XMLDOMUtils.setNodeText(doc, nremitente, n.getDescripcion());
				((Element)nremitente).setAttribute("nivel", n.getOrden());
				((Element)nremitente).setAttribute("idDir3", n.getId());
				remitente.appendChild(nremitente);
			}
			//Ahora, se a�aden los datos a cada uno de los anuncios
			NodeList emisores = XMLDOMUtils.getAllNodesCondicion(doc, "//anuncios/anuncio/emisor");
			for (int i=0;i<emisores.getLength();i++)
			{
				Node emisor = emisores.item(i);
				for (Nivel n:datos.getNivel())
				{
					Node nemisor = doc.createElement("nodoEmisor");
					XMLDOMUtils.setNodeText(doc, nemisor, n.getDescripcion());
					((Element)nemisor).setAttribute("nivel", n.getOrden());
					((Element)nemisor).setAttribute("idDir3", n.getId());
					emisor.appendChild(nemisor);
				}
			}
		} catch (JAXBException e) {
			throw new AnunciosException ("Fallo al procesar el fichero de DIR3: "+ e.getMessage(),e);
		}
		
	}
	
}
