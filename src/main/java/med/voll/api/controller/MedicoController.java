package med.voll.api.controller;

import jakarta.validation.Valid;
import med.voll.api.dto.medico.DatosActualizarMedico;
import med.voll.api.dto.medico.DatosListadoMedico;
import med.voll.api.dto.medico.DatosRegistroMedico;
import med.voll.api.model.Medico;
import med.voll.api.repository.MedicoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/medicos")
public class MedicoController {

    //Por testing no es muy recomendable utilizar @AutoWired por complicaciones. Pref Utilizar otra forma.
    //por fines didacticos y simplificar proceso, utilizaremos ese de momento.
    @Autowired //Inyecta el objeto.
    private MedicoRepository medicoRepository;

    /***
     *
      * @param datosRegistroMedico Obtener JSON utilizando el DTO como Contenedor.
     */
    @PostMapping
    public void registrarMedico(@RequestBody @Valid DatosRegistroMedico datosRegistroMedico){

        // Utilizamos DatosRegistroMedico, para crear un Objeto de Tipo Medico
        // Y ese Objeto Tipo Medico, pasarlo al Repository
        // Ya que nuestro Generics del Repository, solo acepta tipo Medico.
        medicoRepository.save(new Medico(datosRegistroMedico));
    }

    // Ocuparemos el DTO DatosActualizarMedico para limitar y definir que campos si se pueden actualizar.
    @PutMapping
    @Transactional // Notacion para que guarde automaticamente en DB despues de completado el metodo siempre que no haya ningun error. sino hace rollback automatico.
    public void actualizarMedico(@RequestBody @Valid DatosActualizarMedico datosActualizarMedico){

        // primero obtenemos instancia de Medico
        Medico medico = medicoRepository.getReferenceById(datosActualizarMedico.id());

        // Actualizamos los Datos del objeto medico con los datos del DTO recibido.
        medico.actualizarDatos(datosActualizarMedico);

    }

    // Delete Logico
    //Metodo para Eliminacion Logica de la base (Setear el Registro como Inactivo, pero no eliminarlo.)
    @DeleteMapping("/{id}")
    @Transactional
    public void eliminarMedico(@PathVariable Long id){
        // primero obtenemos instancia de Medico
        Medico medico = medicoRepository.getReferenceById(id);

        // Desactivamos Medico
        medico.desactivarMedico();
    }

    /*
    //Metodo para Eliminar de la DB
    @DeleteMapping("/{id}")
    @Transactional
    public void eliminarMedico(@PathVariable Long id){
        // primero obtenemos instancia de Medico
        Medico medico = medicoRepository.getReferenceById(id);

        //Eliminamos Medico
        medicoRepository.delete(medico);
    }
     */


    // Siempre utilizamos el DTO para filtrar
    // Pero ahora agregamos Paginacion a los resultados.
    @GetMapping
    public Page<DatosListadoMedico> listadoMedicos(@PageableDefault(size = 2) Pageable paginacion){
        // Traemos el Listado de Medicos
        // Luego lo mapeamos utilizando el DTO DatosListadoMedico (Requiere constructor en el DTO para Mapearlo)
        // y lo hacemos una lista
        //return medicoRepository.findAll(paginacion).map(DatosListadoMedico::new);

        //Consulta Personalizada
        //findBy (Es la implementacion de Spring DATA)
        //ActivoTrue (Es el Where que yo quiero que JPA Realice.)
        //Unido ya queda findByActivoTrue();
        return medicoRepository.findByActivoTrue(paginacion).map(DatosListadoMedico::new);
    }

    //Utilizaremos el DTO de tipo Record, llamado DatosListadomedico
    //para filtrar que Informacion vamos a Retornar desde la DB a la API
    /*
    @GetMapping
    public List<DatosListadoMedico> listadoMedicos(){
        // Traemos el Listado de Medicos
        // Luego lo mapeamos utilizando el DTO DatosListadoMedico (Requiere constructor en el DTO para Mapearlo)
        // y lo hacemos una lista
        return medicoRepository.findAll().stream().map(DatosListadoMedico::new).toList();
    }
     */

    /*
    //Metodo para Listar toda la informacion de la Base
    @GetMapping
    public List<Medico> ListarTodoMedicos(){
        return medicoRepository.findAll();
    }
     */
}
