package cl.duoc.maquinaria.controller;

import cl.duoc.maquinaria.model.CargadorFrontal;
import cl.duoc.maquinaria.model.Excavadora;
import cl.duoc.maquinaria.model.Maquina;
import cl.duoc.maquinaria.repository.MaquinaRepository;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * CONTROLLER — Coordina la ventana: lee el formulario, valida, pide al modelo que
 * cree la máquina y al repository que la guarde, y muestra el resultado.
 * No calcula costos ni toca archivos.
 */
public class MainController {

    // [CLAVE] Cada atributo @FXML se llama IGUAL que su fx:id en main-view.fxml.
    // Si un nombre no coincide, el atributo queda en null y la app falla al abrir.

    // ---------- Formulario ----------
    @FXML private ComboBox<String> cbTipo;
    @FXML private TextField txtCodigo;
    @FXML private TextField txtHorasUso;
    @FXML private TextField txtPotencia;
    @FXML private VBox panelExcavadora;
    @FXML private TextField txtPeso;
    @FXML private CheckBox chkMantencion;
    @FXML private CheckBox chkCertificacion;
    @FXML private VBox panelCargador;
    @FXML private TextField txtCapacidadBalde;

    // ---------- Búsqueda y tabla ----------
    @FXML private TextField txtBuscar;
    @FXML private TableView<Maquina> tablaMaquinas;
    @FXML private TableColumn<Maquina, String> colTipo;
    @FXML private TableColumn<Maquina, String> colCodigo;
    @FXML private TableColumn<Maquina, Integer> colHorasUso;
    @FXML private TableColumn<Maquina, Double> colPotencia;
    @FXML private TableColumn<Maquina, String> colCosto;

    // ---------- Mensajes ----------
    @FXML private Label lblMensaje;

    private static final String EXCAVADORA = "Excavadora";
    private static final String CARGADOR = "Cargador Frontal";

    private final MaquinaRepository repository = new MaquinaRepository();
    // [CLAVE] ObservableList: la tabla la "escucha"; al hacer add/remove se actualiza sola.
    private final ObservableList<Maquina> datos = FXCollections.observableArrayList();
    // [CLAVE] FilteredList: vista filtrada encima de "datos". Filtrar NO borra nada de "datos".
    private FilteredList<Maquina> datosFiltrados;

    private final NumberFormat formatoPesos = NumberFormat.getCurrencyInstance(Locale.of("es", "CL"));

    /** JavaFX llama a este método solo, justo después de cargar el FXML. */
    @FXML
    public void initialize() {
        // 1) Opciones del selector; los paneles específicos parten ocultos
        cbTipo.getItems().addAll(EXCAVADORA, CARGADOR);
        mostrarPaneles(false, false);

        // 2) Qué muestra cada columna. [CLAVE] El costo lo calcula el MODELO, el controlador solo lo formatea.
        colTipo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTipo()));
        colCodigo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCodigo()));
        colHorasUso.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getHorasUso()));
        colPotencia.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getPotencia()));
        colCosto.setCellValueFactory(c ->
                new SimpleStringProperty(formatoPesos.format(c.getValue().calcularCostoServicio())));

        // 3) La tabla muestra la lista FILTRADA (no la original)
        datosFiltrados = new FilteredList<>(datos, m -> true);   // al inicio: todas pasan
        tablaMaquinas.setItems(datosFiltrados);

        // 4) [CLAVE] Listener: cada vez que cambia el texto de búsqueda se vuelve a filtrar.
        //    toLowerCase() en ambos lados = no distingue mayúsculas; contains() = coincidencia parcial.
        txtBuscar.textProperty().addListener((obs, textoAnterior, textoNuevo) -> {
            String filtro = textoNuevo.trim().toLowerCase();
            datosFiltrados.setPredicate(m ->
                    filtro.isEmpty() || m.getCodigo().toLowerCase().contains(filtro));
        });

        // 5) Cargar ambos archivos al iniciar (si no existen, la lista queda vacía sin error)
        try {
            repository.cargar();
            datos.setAll(repository.listar());
            mostrarMensaje(datos.size() + " máquina(s) cargada(s).", false);
        } catch (IOException e) {
            mostrarMensaje("Error al leer los archivos: " + e.getMessage(), true);
        }
    }

    /** Al cambiar el tipo, solo quedan visibles los datos de ese tipo. */
    @FXML
    private void onCambiarTipo() {
        String tipo = cbTipo.getValue();
        mostrarPaneles(EXCAVADORA.equals(tipo), CARGADOR.equals(tipo));
    }

    @FXML
    private void onAgregar() {
        Maquina nueva = crearMaquinaDesdeFormulario();
        if (nueva == null) {
            return;   // error de validación: ya se mostró el mensaje y los datos escritos se mantienen
        }
        try {
            // [CLAVE] Orden: 1º se guarda (archivo), 2º se muestra (tabla).
            // Si guardar falla, salta al catch y la tabla no cambia: tabla y archivo siempre coinciden.
            repository.agregar(nueva);
            datos.add(nueva);
            mostrarMensaje(nueva.getCodigo() + " (" + nueva.getTipo() + ") registrada correctamente.", false);
            limpiarFormulario();
        } catch (IOException e) {
            mostrarMensaje("Error al guardar los archivos: " + e.getMessage(), true);
        }
    }

    @FXML
    private void onLimpiar() {
        limpiarFormulario();
        mostrarMensaje("", false);
    }

    @FXML
    private void onEliminar() {
        // [CLAVE] getSelectedItem() devuelve null si no hay fila seleccionada
        Maquina seleccionada = tablaMaquinas.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            mostrarMensaje("Debe seleccionar una máquina de la tabla para eliminarla.", true);
            return;
        }
        try {
            repository.eliminar(seleccionada);
            datos.remove(seleccionada);
            mostrarMensaje(seleccionada.getCodigo() + " (" + seleccionada.getTipo() + ") eliminada correctamente.", false);
        } catch (IOException e) {
            mostrarMensaje("Error al guardar los archivos: " + e.getMessage(), true);
        }
    }

    /**
     * [CLAVE] Valida campo por campo. Si algo falla: muestra qué campo y por qué,
     * devuelve null y NO borra lo escrito. Si todo está bien, crea el objeto del modelo.
     */
    private Maquina crearMaquinaDesdeFormulario() {
        String tipo = cbTipo.getValue();
        if (tipo == null) {
            mostrarMensaje("Tipo: debe seleccionar Excavadora o Cargador Frontal.", true);
            return null;
        }

        String codigo = txtCodigo.getText().trim();   // trim(): "   " cuenta como vacío
        if (codigo.isEmpty()) {
            mostrarMensaje("Código: no puede estar vacío.", true);
            return null;
        }

        int horasUso = leerEntero(txtHorasUso);
        if (horasUso < 0 || horasUso > 20000) {
            mostrarMensaje("Horas de uso: debe ser un número entero entre 0 y 20000.", true);
            return null;
        }

        double potencia = leerDecimal(txtPotencia);
        if (!(potencia > 0)) {
            mostrarMensaje("Potencia: debe ser un número mayor que cero.", true);
            return null;
        }

        try {
            if (tipo.equals(EXCAVADORA)) {
                double peso = leerDecimal(txtPeso);
                if (!(peso > 0)) {
                    mostrarMensaje("Peso: debe ser un número mayor que cero.", true);
                    return null;
                }
                return new Excavadora(codigo, horasUso, potencia, peso,
                        chkMantencion.isSelected(), chkCertificacion.isSelected());
            } else {
                double capacidad = leerDecimal(txtCapacidadBalde);
                if (!(capacidad > 0)) {
                    mostrarMensaje("Capacidad del balde: debe ser un número mayor que cero.", true);
                    return null;
                }
                return new CargadorFrontal(codigo, horasUso, potencia, capacidad);
            }
        } catch (IllegalArgumentException e) {
            // Respaldo: si el modelo rechaza algo, se muestra su propio mensaje
            mostrarMensaje(e.getMessage(), true);
            return null;
        }
    }

    /** Texto -> entero. Si no es un número devuelve -1 (valor inválido para que falle la validación). */
    private int leerEntero(TextField campo) {
        try {
            return Integer.parseInt(campo.getText().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    /** Texto -> decimal. Acepta coma o punto. Si no es un número devuelve NaN (inválido). */
    private double leerDecimal(TextField campo) {
        try {
            return Double.parseDouble(campo.getText().trim().replace(',', '.'));
        } catch (NumberFormatException e) {
            return Double.NaN;
        }
    }

    private void limpiarFormulario() {
        cbTipo.setValue(null);
        txtCodigo.clear();
        txtHorasUso.clear();
        txtPotencia.clear();
        txtPeso.clear();
        chkMantencion.setSelected(false);
        chkCertificacion.setSelected(false);
        txtCapacidadBalde.clear();
        mostrarPaneles(false, false);
    }

    private void mostrarPaneles(boolean verExcavadora, boolean verCargador) {
        panelExcavadora.setVisible(verExcavadora);
        panelExcavadora.setManaged(verExcavadora);   // managed=false: el panel oculto no deja un hueco
        panelCargador.setVisible(verCargador);
        panelCargador.setManaged(verCargador);
    }

    /** Cada mensaje reemplaza al anterior; verde = éxito, rojo = error. Nada va a la consola. */
    private void mostrarMensaje(String texto, boolean esError) {
        lblMensaje.setText(texto);
        lblMensaje.setStyle(esError ? "-fx-text-fill: #c62828;" : "-fx-text-fill: #2e7d32;");
    }
}
