//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.3 in JDK 1.6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2015.05.19 at 01:53:28 PM CEST 
//


package es.boe.controlpublicacion.tipos;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * Lista con todos los anuncios publicados en el boletin correspodiente a la fecha
 * 
 * <p>Java class for ListaAnuncios complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ListaAnuncios">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="anuncio" type="{http://www.boe.es/ControlPublicacion}Anuncio" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ListaAnuncios", propOrder = {
    "anuncio"
})
public class ListaAnuncios {

    @XmlElement(required = true)
    protected List<Anuncio> anuncio;

    /**
     * Gets the value of the anuncio property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the anuncio property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAnuncio().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Anuncio }
     * 
     * 
     */
    public List<Anuncio> getAnuncio() {
        if (anuncio == null) {
            anuncio = new ArrayList<Anuncio>();
        }
        return this.anuncio;
    }

}
