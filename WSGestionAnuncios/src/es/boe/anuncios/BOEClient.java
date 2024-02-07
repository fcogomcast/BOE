package es.boe.anuncios;

import java.util.ArrayList;
import java.util.List;

import javax.xml.ws.BindingProvider;
import javax.xml.ws.handler.Handler;


import es.boe.anuncios.contexto.ContextoLlamada;
import es.boe.anuncios.exceptions.AnunciosException;
import es.boe.anuncios.preferencias.Preferencias;
import es.boe.anuncios.preferencias.PreferenciasException;
import es.boe.anuncios.soap.Seguridad;
import es.boe.anuncios.soap.SoapClientHandler;

public class BOEClient {

	private es.boe.servicionotificaciones.ServicioNotificacionesBOE srv;
	private es.boe.servicionotificaciones.ServicioNotificaciones port;
	ContextoLlamada contexto;
	BOEClient(ContextoLlamada contexto)
	{
		this.srv=new es.boe.servicionotificaciones.ServicioNotificacionesBOE();
		this.port = srv.getServicioNotificacionesPort();
		this.contexto = contexto;
	}
	
	private Respuesta convertirRespuesta(es.boe.servicionotificaciones.Respuesta respuesta)
	{
		Respuesta resp = new Respuesta();
		resp.setIdEnvio(respuesta.getIdEnvio());
		resp.setFecha(respuesta.getFecha());
		Resultado resultado = new Resultado();
		resultado.setCodigo(respuesta.getResultado().getCodigo());
		resultado.setDescripcion(respuesta.getResultado().getDescripcion());
		resp.setResultado(resultado);
		ListaAnuncios listaAnuncios = new ListaAnuncios();
		Anuncio anuncio;
		if (respuesta.getAnuncios()!=null)
		{
			for (es.boe.servicionotificaciones.Anuncio an:respuesta.getAnuncios().getAnuncio())
			{
				anuncio = new Anuncio();
				anuncio.setId(an.getId());
				anuncio.setFechaPub(an.getFechaPub());
				anuncio.setIdBoe(an.getIdBoe());
				anuncio.setNbo(an.getNbo());
				anuncio.setCve(an.getCve());
				anuncio.setEstadoBoe(an.getEstadoBoe());
				anuncio.setUrl(an.getUrl());
				//Errores y avisos
				Error error;
				List<ListaErrores> listaErrores = anuncio.getErrores();
				if (an.getErrores().size()>0)
				{
					ListaErrores lError = new ListaErrores();
					for (int l=0;l<an.getErrores().size();l++)
					{
						//Lista de errores
						es.boe.servicionotificaciones.ListaErrores list=an.getErrores().get(l);
						//Ahora devolvemos el error concreto
						for (es.boe.servicionotificaciones.Error e:list.getError())
						{
							error = new Error();
							error.setCodigo(e.getCodigo());
							error.setDescripcion(e.getDescripcion());
							lError.getError().add(error);
						}
					}
					listaErrores.add(lError);
				}
				Aviso aviso;
				List<ListaAvisos> listaAvisos = anuncio.getAvisos();
				if (an.getAvisos().size()>0)
				{
					ListaAvisos lAviso = new ListaAvisos();
					for (int l=0;l<an.getAvisos().size();l++)
					{
						es.boe.servicionotificaciones.ListaAvisos list=an.getAvisos().get(l);
						for (es.boe.servicionotificaciones.Aviso a:list.getAviso())
						{
							aviso = new Aviso();
							aviso.setCodigo(a.getCodigo());
							aviso.setDescripcion(a.getDescripcion());
							lAviso.getAviso().add(aviso);
						}
					}
					listaAvisos.add(lAviso);
				}
				//Lista de causas de devolución (sólo estará para consulta)
				ListaCausas listaCausas = new ListaCausas();
				if (an.getCausasDevolucion()!=null)
				{
					if (an.getCausasDevolucion().getCausa().size()>0)
					{
						List<es.boe.servicionotificaciones.CausaDevolucion> listaCausasBOE= an.getCausasDevolucion().getCausa();
						for (int l=0;l<an.getCausasDevolucion().getCausa().size();l++)
						{
							es.boe.servicionotificaciones.CausaDevolucion c = listaCausasBOE.get(l);
							CausaDevolucion causa = new CausaDevolucion();
							causa.setDescripcion(c.getDescripcion());
							causa.setObservaciones(c.getObservaciones());
							listaCausas.getCausa().add(causa);
						}
						anuncio.setCausasDevolucion(listaCausas);
					}
				}
				listaAnuncios.getAnuncio().add(anuncio);
			}
			resp.setAnuncios(listaAnuncios);
		}
		return resp;
	}
	/**
	 * Asigna los manejadores de salida para el port 
	 * @param port ServicioNotificaciones mediante el que se pueden utilizar las operaciones del servicio remoto
	 * @param contexto {@link ContextoLlamada} con los datos comunes de esta llamada al servicio
	 */
	@SuppressWarnings("unchecked")
	private void asignaManejadores(es.boe.servicionotificaciones.ServicioNotificaciones port, ContextoLlamada contexto) throws PreferenciasException
	{
		Preferencias pref = contexto.getPreferencias();
		if (pref==null)
		{
			throw new PreferenciasException("Error, no se han encontrado las preferencias en el contexto de la llamada");
		}
		BindingProvider bpr = (BindingProvider)port;
		bpr.getRequestContext().put(
				BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
				pref.getEndpointBOE());
		List<Handler> handler=bpr.getBinding().getHandlerChain();
		if (handler==null)
		{
			handler= new ArrayList<Handler>();
		}
		handler.add(new Seguridad(contexto));
		handler.add(new SoapClientHandler(contexto,true));
		bpr.getBinding().setHandlerChain(handler);
	}
	/**
	 * Realiza el envío de una serie de anuncios.
	 * @param enviob64 Anuncios a enviar, en formato Base 64
	 * @return Respuesta del servicio
	 * @throws AnunciosException
	 */
	public Respuesta envioAnuncios (String enviob64) throws AnunciosException
	{
		try
		{
			this.asignaManejadores(this.port, contexto);
			return convertirRespuesta(port.envioAnuncios(enviob64));
		}
		catch (PreferenciasException e)
		{
			throw new AnunciosException("Error en el envío de anuncios:"+ e.getMessage(),e);
		}
	}
	/**
	 * Realiza la consulta de un envío
	 * @param idEnvio Identificador de envío
	 * @return Respuesta del servicio
	 * @throws AnunciosException
	 */
	public Respuesta consultaEnvio(String idEnvio) throws AnunciosException
	{
		try
		{
			this.asignaManejadores(this.port, contexto);
			return convertirRespuesta(port.consultaEnvio(idEnvio));
		}
		catch (PreferenciasException e)
		{
			throw new AnunciosException("Error en la consulta de envío:"+ e.getMessage(),e);
		}
	}
	/**
	 * Realiza la consulta de un anuncio
	 * @param idAnuncio Identificador del anuncio
	 * @return Datos del anuncio
	 * @throws AnunciosException
	 */
	public Respuesta consultaAnuncio (String idAnuncio) throws AnunciosException
	{
		try
		{
			this.asignaManejadores(this.port, contexto);
			return convertirRespuesta(port.consultaAnuncio(idAnuncio));
		}
		catch (PreferenciasException e)
		{
			throw new AnunciosException("Error en la consulta de anuncio:"+ e.getMessage(),e);
		}
	}
	/**
	 * Realiza la anulación de un envío
	 * @param idEnvio Identificador del envío en BOE
	 * @return Respuesta de la anulación
	 * @throws AnunciosException
	 */
	public Respuesta anulacionEnvio (String idEnvio) throws AnunciosException
	{
		try
		{
			this.asignaManejadores(this.port, contexto);
			return convertirRespuesta(port.anulacionEnvio(idEnvio));
		}
		catch (PreferenciasException e)
		{
			throw new AnunciosException("Error en la anulación del envío " + idEnvio + ":"+ e.getMessage(),e);
		}
	}
	/**
	 * Realiza la anulación de un anuncio
	 * @param idAnuncio Identificador del anuncio en BOE
	 * @return Respuesta de la anulación
	 * @throws AnunciosException
	 */
	public Respuesta anulacionAnuncio (String idAnuncio) throws AnunciosException
	{
		try
		{
			this.asignaManejadores(this.port, contexto);
			return convertirRespuesta(port.anulacionAnuncio(idAnuncio));
		}
		catch (PreferenciasException e)
		{
			throw new AnunciosException("Error en la anulación del anuncio " + idAnuncio + ":"+ e.getMessage(),e);
		}
	}
}
