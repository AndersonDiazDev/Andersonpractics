module pe.edu.upeu.sysventas {

    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires static lombok;

    requires java.logging;
    requires jakarta.validation;
    requires javafx.graphics;

    opens pe.edu.upeu.sysventas.controller;
    opens pe.edu.upeu.sysventas.model;
    opens pe.edu.upeu.sysventas;

    exports pe.edu.upeu.sysventas; }