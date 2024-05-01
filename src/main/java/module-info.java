module org.example.timereactionfx {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.almasb.fxgl.all;

    opens org.example.timereactionfx to javafx.fxml;
    exports org.example.timereactionfx;
}