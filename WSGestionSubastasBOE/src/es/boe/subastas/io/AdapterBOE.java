package es.boe.subastas.io;

import es.boe.serviciosubastas.Estado;
import es.boe.serviciosubastas.PosturaFinal;
import es.boe.subastas.Postor;
import es.boe.subastas.Postura;
import es.boe.subastas.RespuestaEnvio;
import es.boe.subastas.RespuestaEstado;
import es.boe.subastas.RespuestaPosturaFinal;
import es.boe.subastas.Resultado;

/**
 * Conversor de mensajes de BOE a mensajes de servicio
 * @author crubencvs
 *
 */
public class AdapterBOE {

	private AdapterBOE()
	{
		
	}
	/**
	 * Convierte una respuesta de envío de subasta de BOE a objeto de servicio
	 * @param respuestaBOE Respuesta recibida del envío de subasta a BOE
	 * @return Respuesta de envío
	 */
	public static RespuestaEnvio convertirRespuestaEnvio (es.boe.serviciosubastas.RespuestaEnvio respuestaBOE) {
		RespuestaEnvio r= new RespuestaEnvio();
		r.setFecha(respuestaBOE.getFecha());
		Resultado res= new Resultado();
		res.setCodigo(respuestaBOE.getResultado().getCodigo());
		res.setDescripcion(respuestaBOE.getResultado().getDescripcion());
		r.setResultado(res);
		r.setIdSubasta(respuestaBOE.getIdSubasta());
		r.setAnuncioBoeLiquidacion(respuestaBOE.getAnuncioBoeLiquidacion());
		r.setAnuncioBoeBorradorTexto(respuestaBOE.getAnuncioBoeBorradorTexto());
		r.setAnuncioBoeId791PagoTasa(respuestaBOE.getAnuncioBoeId791PagoTasa());
		r.setAnuncioBoeImporteTasa(respuestaBOE.getAnuncioBoeImporteTasa());
		r.setEstado(respuestaBOE.getEstado());
		r.setUrlSubasta(respuestaBOE.getUrlSubasta());
		return r;
	}
	/**
	 * Convierte una petición de cambio de estado de subasta de la estructura STPA a la de BOE
	 * @param estadoSTPA Estructura STPA de petición de cambio de estado
	 * @return Petición de cambio de estado con estructura BOE
	 */
	public static Estado convertirPeticionCambioEstado(es.boe.subastas.Estado estadoSTPA) {
		Estado e= new Estado();
		e.setIdSubasta(estadoSTPA.getIdSubasta());
		e.setIdLote(estadoSTPA.getIdLote());
		e.setEstado(estadoSTPA.getEstado());
		e.setFechaReanudacion(estadoSTPA.getFechaReanudacion());
		e.setObservaciones(estadoSTPA.getObservaciones());
		return e;
	}
	/**
	 * Convierte una respuesta de cambio de estado de subasta de estructura BOE a estructura STPA
	 * @param respuestaBOE Respuesta de cambio de estado de subasta  con estructura BOE
	 * @return Respuesta de cambio de estado de BOE estuctura STPA
	 */
	public static RespuestaEstado convertirRespuestaCambioEstado(es.boe.serviciosubastas.RespuestaEstado respuestaBOE) {
		RespuestaEstado r= new RespuestaEstado();
		r.setFecha(respuestaBOE.getFecha());
		Resultado res= new Resultado();
		res.setCodigo(respuestaBOE.getResultado().getCodigo());
		res.setDescripcion(respuestaBOE.getResultado().getDescripcion());
		r.setResultado(res);
		r.setIdSubasta(respuestaBOE.getIdSubasta());
		r.setIdLote(respuestaBOE.getIdLote());
		r.setEstado(respuestaBOE.getEstado());
		return r;
	}
	/**
	 * Convierte una petición de postura final con estructura STPA a estructura BOE
	 * @param posturaFinalSTPA Petición de postura final con estructura STPA
	 * @return Petición de postura final con estructura BOE
	 */
	public static PosturaFinal convertirPeticionPosturaFinal(es.boe.subastas.PosturaFinal posturaFinalSTPA){
		PosturaFinal p= new PosturaFinal();
		p.setIdSubasta(posturaFinalSTPA.getIdSubasta());
		p.setIdLote(posturaFinalSTPA.getIdLote());
		p.setPosicion(posturaFinalSTPA.getPosicion());
		return p;
	}
	/**
	 * Convierte una respuesta de postura final con estructura BOE a estructura STPA
	 * @param respuestaBOE Respuesta de postura final con estructura BOE
	 * @return Respuesta de postura final con estructura STPA
	 */
	public static RespuestaPosturaFinal convertirRespuestaPosturaFinal(es.boe.serviciosubastas.RespuestaPosturaFinal respuestaBOE){
		RespuestaPosturaFinal r= new RespuestaPosturaFinal();
		r.setFecha(respuestaBOE.getFecha());
		Resultado res= new Resultado();
		res.setCodigo(respuestaBOE.getResultado().getCodigo());
		res.setDescripcion(respuestaBOE.getResultado().getDescripcion());
		r.setResultado(res);
		if (respuestaBOE.getPostura()!=null && 
				respuestaBOE.getPostura().getIdSubasta()!=null){
			Postura pos= new Postura();
			es.boe.serviciosubastas.Postura pboe= respuestaBOE.getPostura();
			pos.setIdSubasta(pboe.getIdSubasta());
			pos.setIdLote(pboe.getIdLote());
			pos.setPosicion(pboe.getPosicion());
			pos.setTotalPosturasFinales(pboe.getTotalPosturasFinales());
			pos.setConsignacion(pboe.getConsignacion());
			pos.setNrc(pboe.getNrc());
			pos.setIban(pboe.getIban());
			pos.setImporte(pboe.getImporte());
			pos.setReserva(pboe.getReserva());
			pos.setRol(pboe.getRol());
			pos.setCertificacion(pboe.getCertificacion());
			Postor p1= new Postor();
			es.boe.serviciosubastas.Postor postorBOE= pboe.getPostor();
			p1.setNif(postorBOE.getNif());
			p1.setNombre(postorBOE.getNombre());
			p1.setDireccion(postorBOE.getDireccion());
			p1.setCodpostal(postorBOE.getCodpostal());
			p1.setLocalidad(postorBOE.getLocalidad());
			p1.setProvincia(postorBOE.getProvincia());
			p1.setPais(postorBOE.getPais());
			p1.setTelefono(postorBOE.getTelefono());
			p1.setEmail(postorBOE.getEmail());
			pos.setPostor(p1);
			p1= new Postor();
			postorBOE=pboe.getPostor2();
			if (postorBOE!=null) {
				p1.setNif(postorBOE.getNif());
				p1.setNombre(postorBOE.getNombre());
				p1.setDireccion(postorBOE.getDireccion());
				p1.setCodpostal(postorBOE.getCodpostal());
				p1.setLocalidad(postorBOE.getLocalidad());
				p1.setProvincia(postorBOE.getProvincia());
				p1.setPais(postorBOE.getPais());
				p1.setTelefono(postorBOE.getTelefono());
				p1.setEmail(postorBOE.getEmail());
				pos.setPostor2(p1);
			}
			r.setPostura(pos);
		}
		return r;
	}
}
