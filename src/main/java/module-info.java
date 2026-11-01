module cl.duoc.maquinaria {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;

    // [CLAVE] FXMLLoader entra por reflexión a los atributos @FXML del controlador
    opens cl.duoc.maquinaria.controller to javafx.fxml;
    // [CLAVE] Gson entra por reflexión a los atributos privados del modelo
    opens cl.duoc.maquinaria.model to com.google.gson;

    exports cl.duoc.maquinaria;
}
