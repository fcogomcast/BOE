package es.boe.subastas.soap;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;

import javax.xml.soap.Detail;
import javax.xml.soap.DetailEntry;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFault;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.soap.SOAPFaultException;


import es.boe.subastas.xml.XMLDOMUtils;







public final class Utils {
	
	private Utils(){};
	/**
	 * Codifica un array en hexadecimal
	 * @param aInput Array de bytes
	 * @return Cadena correspondiente al Array pero expresada en hexadecimal.
	 */
	static private String hexEncode( byte[] aInput){
	    StringBuilder result = new StringBuilder();
	    char[] digits = {'0', '1', '2', '3', '4','5','6','7','8','9','a','b','c','d','e','f'};
	    for ( int idx = 0; idx < aInput.length; ++idx) {
	      byte b = aInput[idx];
	      result.append( digits[ (b&0xf0) >> 4 ] );
	      result.append( digits[ b&0x0f] );
	    }
	    return result.toString();
	  }

	/**
	 * Recupera un checkcum basado en un identificador único. Se asegura que el identificador es único.
	 */
	public static String getIdLlamada()
	{
		UUID id=UUID.randomUUID();
		String identificador=null;
		byte[] mensaje = id.toString().getBytes();
		try{
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(mensaje);
			identificador=hexEncode(md.digest());

		}catch(NoSuchAlgorithmException nsae){
			identificador="";
		}
		return identificador;
	}
	/**
	 * Genera un SOAP Fault. El fault contendrá una sección de detalle.
	 * @param msg Mensaje SOAP sobre el que se generará el error. Se utilizará para añadirle la sección SOAP Fault 
	 * @param reason Texto de razón de error (aparecerá en el faultString  )
	 * @param codigo Código de error que aparecerá en el detalle.
	 * @param mensaje Mensaje que aparecerá en el detalle.
	 */
	@SuppressWarnings("unchecked")
	public static void generateSOAPErrMessage(SOAPMessage msg, String reason, String codigo, String mensaje) {
	       try {
	    	  SOAPEnvelope soapEnvelope= msg.getSOAPPart().getEnvelope();
	          SOAPBody soapBody = msg.getSOAPPart().getEnvelope().getBody();
	          SOAPFault soapFault = soapBody.addFault();
	          soapFault.setFaultString(reason);
	          Detail det= soapFault.addDetail();
	          Name name = soapEnvelope.createName("id");
	          det.addDetailEntry(name);
	          
	          name = soapEnvelope.createName("mensaje");
	          det.addDetailEntry(name);
	          DetailEntry entry;
	          Iterator<DetailEntry> it=det.getDetailEntries();
	          while (it.hasNext())
	          {
	        	  entry=it.next();
	        	  if (entry.getLocalName().equals("id"))
	        	  {	
	        		XMLDOMUtils.setNodeText (entry.getOwnerDocument(),entry, codigo);  
	        	  }
	        	  if (entry.getLocalName().equals("mensaje"))
	        	  {	
	        		XMLDOMUtils.setNodeText (entry.getOwnerDocument(),entry, mensaje);  
	        	  }
	        		  
	          }
	          throw new SOAPFaultException(soapFault); 
	       }
	       catch(SOAPException e) { }
	}
	/**
	 * Sustituye un SOAP Fault por otro con más detalle. Se utiliza para
	 * sustituir el SOAP Fault que envía BOE (que no es estándar) con uno estándar.
	 * @param msg Mensaje SOAP en el que se sustituirá el SOAP Fault.  
	 * @param reason Texto de razón de error (aparecerá en el faultString  )
	 * @param codigo Código de error que aparecerá en el detalle.
	 * @param mensaje Mensaje que aparecerá en el detalle.
	 */
	@SuppressWarnings("unchecked")
	public static void sustituyeSOAPFault(SOAPMessage msg, String reason, String codigo, String mensaje) {
	       try {
	    	  SOAPEnvelope soapEnvelope= msg.getSOAPPart().getEnvelope();
	          SOAPBody soapBody = msg.getSOAPPart().getEnvelope().getBody();
	          soapBody.removeContents();
	          SOAPFault soapFault = soapBody.addFault();
	          soapFault.setFaultString(reason);
	          Detail det= soapFault.addDetail();
	          Name name = soapEnvelope.createName("id");
	          det.addDetailEntry(name);
	          
	          name = soapEnvelope.createName("mensaje");
	          det.addDetailEntry(name);
	          DetailEntry entry;
	          Iterator<DetailEntry> it=det.getDetailEntries();
	          while (it.hasNext())
	          {
	        	  entry=it.next();
	        	  if (entry.getLocalName().equals("id"))
	        	  {	
	        		XMLDOMUtils.setNodeText (entry.getOwnerDocument(),entry, codigo);  
	        	  }
	        	  if (entry.getLocalName().equals("mensaje"))
	        	  {	
	        		XMLDOMUtils.setNodeText (entry.getOwnerDocument(),entry, mensaje);  
	        	  }
	        		  
	          }
	          throw new SOAPFaultException(soapFault); 
	       }
	       catch(SOAPException e) { }
	}
	
	/*public static void firmaMensaje(SOAPMessageContext context) throws XMLDOMDocumentException, WSSecurityException, SOAPException
	{
		String mensajeSalida = XMLDOMUtils.getPrettyXMLText(context.getMessage().getSOAPPart());
		mensajeSalida=WSSecurityFactory.newConstructorResultado(ctx.getLogger(), pref,new SoapClientHandler(idLlamada)).firmaMensaje(mensajeSalida);
		context.getMessage().getSOAPPart().setContent(new StreamSource(new StringReader(mensajeSalida)));
	}
    */
	 //** Para convertir de lo que nos devuelve el lanzador a un xml con tags correctos.
		  private static HashMap<String,String> htmlEntities;
		  static {
		    htmlEntities = new HashMap<String,String>();
		    htmlEntities.put("&lt;","<")    ; htmlEntities.put("&gt;",">");
		    htmlEntities.put("&amp;","&")   ; htmlEntities.put("&quot;","\"");
		    htmlEntities.put("&agrave;","à"); htmlEntities.put("&Agrave;","À");
		    htmlEntities.put("&acirc;","â") ; htmlEntities.put("&auml;","ä");
		    htmlEntities.put("&Auml;","Ä")  ; htmlEntities.put("&Acirc;","Â");
		    htmlEntities.put("&aring;","å") ; htmlEntities.put("&Aring;","Å");
		    htmlEntities.put("&aelig;","æ") ; htmlEntities.put("&AElig;","Æ" );
		    htmlEntities.put("&ccedil;","ç"); htmlEntities.put("&Ccedil;","Ç");
		    htmlEntities.put("&eacute;","é"); htmlEntities.put("&Eacute;","É" );
		    htmlEntities.put("&egrave;","è"); htmlEntities.put("&Egrave;","È");
		    htmlEntities.put("&ecirc;","ê") ; htmlEntities.put("&Ecirc;","Ê");
		    htmlEntities.put("&euml;","ë")  ; htmlEntities.put("&Euml;","Ë");
		    htmlEntities.put("&iuml;","ï")  ; htmlEntities.put("&Iuml;","Ï");
		    htmlEntities.put("&ocirc;","ô") ; htmlEntities.put("&Ocirc;","Ô");
		    htmlEntities.put("&ouml;","ö")  ; htmlEntities.put("&Ouml;","Ö");
		    htmlEntities.put("&oslash;","ø") ; htmlEntities.put("&Oslash;","Ø");
		    htmlEntities.put("&szlig;","ß") ; htmlEntities.put("&ugrave;","ù");
		    htmlEntities.put("&Ugrave;","Ù"); htmlEntities.put("&ucirc;","û");
		    htmlEntities.put("&Ucirc;","Û") ; htmlEntities.put("&uuml;","ü");
		    htmlEntities.put("&Uuml;","Ü")  ; htmlEntities.put("&nbsp;"," ");
		    htmlEntities.put("&copy;","\u00a9");
		    htmlEntities.put("&reg;","\u00ae");
		    htmlEntities.put("&euro;","\u20a0");
		  }
		  /**
		   * Decodifica secuencias de escape en HTML.
		   * @param source Cadena que contiene texto con secuencias de escape.
		   * @return
		   */
		  public static final String unescapeHTML(String source) {
		      int i, j;

		      boolean continueLoop;
		      int skip = 0;
		      do {
		         continueLoop = false;
		         i = source.indexOf("&", skip);
		         if (i > -1) {
		           j = source.indexOf(";", i);
		           if (j > i) {
		             String entityToLookFor = source.substring(i, j + 1);
		             String value = (String) htmlEntities.get(entityToLookFor);
		             if (value != null) {
		               source = source.substring(0, i)
		                        + value + source.substring(j + 1);
		               continueLoop = true;
		             }
		             else if (value == null){
		                skip = i+1;
		                continueLoop = true;
		             }
		           }
		         }
		      } while (continueLoop);
		      return source;
		  }


}
