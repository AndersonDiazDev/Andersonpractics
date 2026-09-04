package pe.edu.upeu.sysventas.model;


import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Emisor {
    Long idEmisor;
    String ruc;
    String nombrecomercial;
    String ubigeo;
    String domicilioFiscal;
    String urbanizacion;
    String deparamento;
    String provincia;
    String distrito;
}
