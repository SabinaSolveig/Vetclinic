module org.example.vetclinic {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires java.sql;
    opens org.example.vetclinic to javafx.fxml;
    exports org.example.vetclinic;
    exports org.example.vetclinic.controller;
    exports org.example.vetclinic.model;
    opens org.example.vetclinic.controller to javafx.fxml;
    opens org.example.vetclinic.model to javafx.base, javafx.fxml;
}
