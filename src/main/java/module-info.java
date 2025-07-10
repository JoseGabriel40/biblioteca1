module jala.university.libraryfx {
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

    // Pacotes principais
    exports com.example.library;

    // Pacotes de utilitários
    exports com.example.library.util;
    opens com.example.library.util to javafx.fxml;

    exports com.example.library.controle;
    opens com.example.library.controle to javafx.fxml;

    exports com.example.library.dao;
    opens com.example.library.dao to javafx.fxml;

    exports com.example.library.model;
    opens com.example.library.model to javafx.fxml;
}
