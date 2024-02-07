package es.boe.controlpublicacion.seguridad;


public class DatosPermisosServicio
{
	public static enum AutorizacionServicio {AUTORIZADO,NO_AUTORIZADO,ERROR};
	private AutorizacionServicio autorizacion;
	private String usuarioTributas;
	private String passwordTributas;
	protected void setAutorizacion(AutorizacionServicio autorizacion) {
		this.autorizacion = autorizacion;
	}
	protected void setUsuarioTributas(String usuarioTributas) {
		this.usuarioTributas = usuarioTributas;
	}
	protected void setPasswordTributas(String passwordTributas) {
		this.passwordTributas = passwordTributas;
	}
	public AutorizacionServicio getAutorizacion() {
		return autorizacion;
	}
	public String getUsuarioTributas() {
		return usuarioTributas;
	}
	public String getPasswordTributas() {
		return passwordTributas;
	}
	
}