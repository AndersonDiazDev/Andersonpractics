package pe.edu.upeu.sysventas.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Setter
@Getter
public class Perfil {
    Long idPerfil;    //Relacion de agragacion
    String nombre;
    String codigo;
    List<Acceso> accesos;
}
