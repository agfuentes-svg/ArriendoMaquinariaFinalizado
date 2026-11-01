package cl.duoc.maquinaria.model;

/**
 * MODEL — Clase abstracta base de todas las máquinas (proyecto base).
 * Guarda los datos comunes y valida sus reglas. No sabe nada de JavaFX ni de archivos.
 */
public abstract class Maquina {

    private String codigo;
    private int horasUso;
    private double potencia;

    // [CLAVE] Constructor vacío: Gson lo necesita para reconstruir los objetos al leer el JSON.
    // Sin él, la app guarda bien pero falla al volver a abrirla ("Unable to create instance...").
    protected Maquina() {
    }

    public Maquina(String codigo, int horasUso, double potencia) {
        // [CLAVE] El constructor usa los setters: así las validaciones se aplican también al crear.
        setCodigo(codigo);
        setHorasUso(horasUso);
        setPotencia(potencia);
    }

    /** Cada tipo de máquina calcula su propio costo (polimorfismo). */
    public abstract double calcularCostoServicio();

    /** "Excavadora" o "Cargador Frontal". */
    public abstract String getTipo();

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        if (codigo == null || codigo.trim().isEmpty()) {
            throw new IllegalArgumentException("El código no puede estar vacío.");
        }
        this.codigo = codigo.trim();
    }

    public int getHorasUso() {
        return horasUso;
    }

    public void setHorasUso(int horasUso) {
        if (horasUso < 0 || horasUso > 20000) {
            throw new IllegalArgumentException("Las horas de uso deben estar entre 0 y 20000.");
        }
        this.horasUso = horasUso;
    }

    public double getPotencia() {
        return potencia;
    }

    public void setPotencia(double potencia) {
        // "!(potencia > 0)" también rechaza NaN, que no es mayor ni menor que nada
        if (!(potencia > 0)) {
            throw new IllegalArgumentException("La potencia debe ser mayor que cero.");
        }
        this.potencia = potencia;
    }

    @Override
    public String toString() {
        return getTipo() + " " + codigo;
    }
}
