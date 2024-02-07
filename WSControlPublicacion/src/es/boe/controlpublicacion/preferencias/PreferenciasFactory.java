/**
 * 
 */
package es.boe.controlpublicacion.preferencias;

import es.boe.controlpublicacion.contexto.ContextoLlamada;



/** Implementa una factoría de preferencias. Se podrá crear una nueva instancia o bien recuperar
 *  la que exista en un contexto de llamada.
 * @author crubencvs
 *
 */
public class PreferenciasFactory {
	private PreferenciasFactory(){};
	public static Preferencias newInstance() throws PreferenciasException
	{
		return new Preferencias();
	}
	/**
	 * Recupera las preferencias de contexto
	 * @param context Contexto de llamada del que recuperar las preferencias
	 * @return Objeto que contiene las preferencias {@link Preferencias}
	 * @throws PreferenciasException En caso de no poder recuperar las preferencias del contexto que se pasa.
	 */
	public static Preferencias getPreferenciasContexto(ContextoLlamada context) throws PreferenciasException
	{
		if (context==null)
		{
			throw new PreferenciasException ("Imposible recuperar preferencias del contexto de la llamada. El contexto es nulo");
		}
		Preferencias pref=context.getPreferencias();
		if (pref==null)
		{
			throw new PreferenciasException ("Imposible recuperar preferencias del contexto de la llamada."); 
		}
		return pref;
		
	}
}
