package es.boe.anuncios.exceptions;

public class AnunciosException extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 717641081003370899L;

	public AnunciosException(String message) {
		super(message);
	}

	public AnunciosException(Throwable cause) {
		super(cause);
	}

	public AnunciosException(String message, Throwable cause) {
		super(message, cause);
	}

}
