package cl.duoc.maquinaria.model;

/**
 * MODEL — Interfaz para las máquinas que pueden certificar su seguridad.
 */
public interface Certificable {

    void certificarSeguridad(boolean certificada);

    boolean tieneCertificacionSeguridad();
}
