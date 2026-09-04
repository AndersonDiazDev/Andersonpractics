package pe.edu.upeu.sysventas.model;

import javafx.scene.control.Menu;
import lombok.Getter;
import lombok.Setter;
import pe.edu.upeu.sysventas.enums.TipoTab;

import java.lang.reflect.Member;
@Getter
@Setter
public class Acceso {
    String idAcceso;
    String urlAcceso;
    String menuItemNombre;
    Menu menuNombre;
    TipoTab nombreTab;
}
