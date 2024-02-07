package es.boe.subastas.exceptions;

public class SubastaException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8227747714728771819L;

	public SubastaException() {
		super();
	}

	public SubastaException(String message, Throwable cause) {
		super(message, cause);
	}

	public SubastaException(String message) {
		super(message);
	}

}
