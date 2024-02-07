package es.boe.controlpublicacion;

import java.io.UnsupportedEncodingException;
import es.boe.controlpublicacion.bd.Datos;
import es.boe.controlpublicacion.bd.Datos.ResultadoControlPublicacion;
import es.boe.controlpublicacion.contexto.ContextoLlamada;
import es.boe.controlpublicacion.exceptions.ControlPublicacionException;
import es.boe.controlpublicacion.utils.Base64;
import es.boe.controlpublicacion.utils.Constantes;
import es.tributasenasturias.utils.log.Logger;

public class ControlPublicacionImpl {

	ContextoLlamada contexto;
	public ControlPublicacionImpl(ContextoLlamada contexto)
	{
		this.contexto = contexto;
	}
	
	
	public Respuesta controlPublicacion(String publicacion) throws ControlPublicacionException{
		Respuesta respuesta= new Respuesta();
		Logger log = this.contexto.getLogger();
		try {
			log.info("1. Se decodifica el mensaje Base64");
			String xmlPublicacion = new String(Base64.decode(publicacion.toCharArray()),"UTF-8");
			log.info("2. Mensaje Decodificado. Se envía la petición a base de datos.");
			Datos dat = new Datos(this.contexto);
			ResultadoControlPublicacion control= dat.controlPublicacion(xmlPublicacion);
			if (!control.isError())
			{
				log.info("2.1. Procesado correctamente");
				respuesta.setCodigo(Constantes.RESPUESTA_OK_CODE);
				respuesta.setDescripcion(Constantes.RESPUESTA_OK_MSG);
			}
			else
			{
				log.error("2.1. Error en el proceso en la base de datos:" + control.getCodigoResultado()+"-->"+ control.getDescripcionResultado());
				//Modificar por algo con mas sentido
				respuesta.setCodigo(Constantes.ERROR_TECNICO_CODE);
				respuesta.setDescripcion(Constantes.ERROR_TECNICO_MSG);
			}
		} catch (UnsupportedEncodingException e) {
			throw new ControlPublicacionException("Error al interpretar el XML de control de publicación:" + e.getMessage(),e);
		} 
		return respuesta;
	}
}
