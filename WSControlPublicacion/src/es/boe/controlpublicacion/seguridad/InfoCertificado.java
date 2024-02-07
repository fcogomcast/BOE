package es.boe.controlpublicacion.seguridad;

/**
 * Contiene información sobre un certificado X509.v3
 * @author crubencvs
 *
 */
public class InfoCertificado {
//Estados del certificado.
//VALIDO: El certificado es válido según la entidad
//INVALIDO: El certificado no es válido según la entidad
	
 public static enum Validez {VALIDO,INVALIDO}
 
 private String cif;
 private String nifNie;
 private String razonSocial;
 private String apellido1;
 private String apellido2;
 private String nombre;
 private String tipo;
 private Validez validez;
 
public Validez getValidez() {
	return validez;
}
protected void setValidez(Validez validez) {
	this.validez = validez;
}
public String getCif() {
	return cif;
}
public String getNifNie() {
	return nifNie;
}
public String getRazonSocial() {
	return razonSocial;
}
public String getApellido1() {
	return apellido1;
}
public String getApellido2() {
	return apellido2;
}
public String getNombre() {
	return nombre;
}
public String getTipo() {
	return tipo;
}
protected void setCif(String cif) {
	this.cif = cif;
}
protected void setNifNie(String nifNie) {
	this.nifNie = nifNie;
}
protected void setRazonSocial(String razonSocial) {
	this.razonSocial = razonSocial;
}
protected void setApellido1(String apellido1) {
	this.apellido1 = apellido1;
}
protected void setApellido2(String apellido2) {
	this.apellido2 = apellido2;
}
protected void setNombre(String nombre) {
	this.nombre = nombre;
}
protected void setTipo(String tipo) {
	this.tipo = tipo;
}
 
}
