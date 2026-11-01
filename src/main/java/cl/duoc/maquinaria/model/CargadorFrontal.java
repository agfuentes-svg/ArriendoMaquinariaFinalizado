package cl.duoc.maquinaria.model;

/**
 * MODEL — Cargador frontal: costo base $120.000; +20% si el balde supera 3 m³.
 */
public class CargadorFrontal extends Maquina {

    private static final double COSTO_BASE = 120000;
    private static final double RECARGO_BALDE_GRANDE = 0.20;
    private static final double LIMITE_BALDE_M3 = 3.0;

    private double capacidadBalde;

    // Constructor vacío para Gson
    private CargadorFrontal() {
    }

    public CargadorFrontal(String codigo, int horasUso, double potencia, double capacidadBalde) {
        super(codigo, horasUso, potencia);
        setCapacidadBalde(capacidadBalde);
    }

    @Override
    public double calcularCostoServicio() {
        if (capacidadBalde > LIMITE_BALDE_M3) {             // "supera": 3,0 exacto NO tiene recargo
            return COSTO_BASE * (1 + RECARGO_BALDE_GRANDE);  // 120.000 x 1,20 = 144.000
        }
        return COSTO_BASE;
    }

    @Override
    public String getTipo() {
        return "Cargador Frontal";
    }

    public double getCapacidadBalde() {
        return capacidadBalde;
    }

    public void setCapacidadBalde(double capacidadBalde) {
        if (!(capacidadBalde > 0)) {
            throw new IllegalArgumentException("La capacidad del balde debe ser mayor que cero.");
        }
        this.capacidadBalde = capacidadBalde;
    }
}
