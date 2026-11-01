package cl.duoc.maquinaria.dao;

import java.io.IOException;
import java.util.List;

/**
 * DAO — Define QUÉ se puede hacer con un archivo: leer la lista y guardarla.
 * <T> = el tipo que se guarda (Excavadora o CargadorFrontal).
 */
public interface MaquinaDao<T> {

    List<T> leer() throws IOException;

    void guardar(List<T> lista) throws IOException;
}
