package cl.duoc.maquinaria.model;

/**
 * MODEL — Excavadora: costo base $150.000; +25% si NO tiene la mantención al día.
 */
public class Excavadora extends Maquina implements Certificable {

    private static final double COSTO_BASE = 150000;
    private static final double RECARGO_SIN_MANTENCION = 0.25;

    private double pesoToneladas;
    private boolean mantencionAlDia;
    private boolean certificacionSeguridad;

    // Constructor vacío para Gson (private: solo Gson lo usa, nadie puede crear una excavadora sin datos)
    private Excavadora() {
    }

    public Excavadora(String codigo, int horasUso, double potencia,
                      double pesoToneladas, boolean mantencionAlDia, boolean certificacionSeguridad) {
        super(codigo, horasUso, potencia);
        setPesoToneladas(pesoToneladas);
        this.mantencionAlDia = mantencionAlDia;
        this.certificacionSeguridad = certificacionSeguridad;
    }

    @Override
    public double calcularCostoServicio() {
        if (mantencionAlDia) {
            return COSTO_BASE;
        }
        return COSTO_BASE * (1 + RECARGO_SIN_MANTENCION);   // 150.000 x 1,25 = 187.500
    }

    @Override
    public String getTipo() {
        return "Excavadora";
    }

    @Override
    public void certificarSeguridad(boolean certificada) {
        this.certificacionSeguridad = certificada;
    }

    @Override
    public boolean tieneCertificacionSeguridad() {
        return certificacionSeguridad;
    }

    public double getPesoToneladas() {
        return pesoToneladas;
    }

    public void setPesoToneladas(double pesoToneladas) {
        if (!(pesoToneladas > 0)) {
            throw new IllegalArgumentException("El peso debe ser mayor que cero.");
        }
        this.pesoToneladas = pesoToneladas;
    }

    public boolean isMantencionAlDia() {
        return mantencionAlDia;
    }
}
