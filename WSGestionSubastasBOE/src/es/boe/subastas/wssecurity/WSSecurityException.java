/**
 * 
 */
package es.boe.subastas.wssecurity;

/**
 * @author crubencvs
 *
 */
public class WSSecurityException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -880968733063316466L;
	/**
	 * 
	 */

	public WSSecurityException(String error, Throwable original){
		super(WSSecurityException.class.getName()+"::"+error,original);
	}
	public WSSecurityException (String error)
	{
		super(WSSecurityException.class.getName()+"::"+error);
	}
	

}
