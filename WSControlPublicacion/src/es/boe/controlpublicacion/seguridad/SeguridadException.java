package es.boe.controlpublicacion.seguridad;


public class SeguridadException extends Exception {



	/**
	 * 
	 */
	private static final long serialVersionUID = -3836802667520948506L;

	public SeguridadException(String error, Throwable original) {
		super(SeguridadException.class.getName()+"::" +  error, original);
	}

	public SeguridadException(String error) {
		super(SeguridadException.class.getName()+"::"+ error);
	}

}
