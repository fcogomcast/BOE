package es.boe.controlpublicacion.seguridad;

/**
 * Clase que contiene información sobre permisos del certificado en nuestros sistemas.
 * Si el certificado es válido.
 * Si el certificado está autorizado para la operación en nuestros sistemas
 * El usuario de tributas asociado con el certificado
 * La clave del usuario de tributas asociado con el certificado.
 * @author crubencvs
 *
 */
public class InfoPermisosCertificado {
	private boolean certificadoValido;
	private boolean certificadoAutorizado;
	private String usuarioTributas;
	private String passwordTributas;
	/**
	 * Recupera el usuario asociado al certificado en TRIBUTAS.
	 * @return
	 */
	public String getUsuarioTributas() {
		return usuarioTributas;
	}
	protected void setUsuarioTributas(String usuarioTributas) {
		this.usuarioTributas = usuarioTributas;
	}
	/**
	 * Recupera la clave del usuario asociado al certificado en TRIBUTAS
	 * @return
	 */
	public String getPasswordTributas() {
		return passwordTributas;
	}
	protected void setPasswordTributas(String passwordTributas) {
		this.passwordTributas = passwordTributas;
	}
	/**
	 * Indica si el certificado es válido (pero no indica que tenga permisos)
	 * @return
	 */
	public boolean isCertificadoValido() {
		return certificadoValido;
	}
	protected void setCertificadoValido(boolean certificadoValido) {
		this.certificadoValido = certificadoValido;
	}
	/**
	 * Indica que el certificado es válido Y tiene permisos de ejecución del servicio sobre
	 * el que se consultó en nuestro sistema.
	 * @return
	 */
	public boolean isCertificadoAutorizado() {
		return certificadoAutorizado;
	}
	protected void setCertificadoAutorizado(boolean certificadoAutorizado) {
		this.certificadoAutorizado = certificadoAutorizado;
	}
}
