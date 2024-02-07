package es.boe.controlpublicacion.contexto;

import es.boe.controlpublicacion.preferencias.Preferencias;
import es.tributasenasturias.utils.log.Logger;

public class ContextoLlamada {

	private Preferencias preferencias;
	private Logger logger;
	private String idLlamada;
	/**
	 * Devuelve el objeto de preferencias para el contexto de llamada actual
	 * @return {@link Preferencias}
	 */
	public Preferencias getPreferencias() {
		return preferencias;
	}
	/**
	 * Devuelve el objeto de log  para el contexto de llamada actual
	 * @return {@link Logger}
	 */
	public Logger getLogger() {
		return logger;
	}
	/**
	 * Devuelve el identificador de llamada para el contexto de llamada actual
	 * @return
	 */
	public String getIdLlamada() {
		return idLlamada;
	}
	
	/**
	 * Constructor privado. No se pueden instanciar objetos directamente
	 */
	private ContextoLlamada()
	{
		
	}
	/** 
	 * Constructor privado. No se pueden instanciar objetos más que a través del método factory
	 * @param pref {@link Preferencias} a cargar
	 * @param log {@link Logger} a cargar
	 * @param idLlamada Identificador de llamada
	 */
	private ContextoLlamada(Preferencias pref, Logger log, String idLlamada)
	{
		this.preferencias = pref;
		this.logger = log;
		this.idLlamada= idLlamada;
	}
	/**
	 * Método factory para crear una nueva instancia de contexto. 
	 * @param pref {@link Preferencias} a cargar
	 * @param log {@link Logger} a cargar
	 * @param idLlamada Identificador de llamada
	 * @return nuevo objeto {@link ContextoLlamada} creado
	 */
	public static ContextoLlamada newInstance (Preferencias pref, Logger log, String idLlamada)
	{
		return new ContextoLlamada (pref, log, idLlamada);
	}
	
	
}
