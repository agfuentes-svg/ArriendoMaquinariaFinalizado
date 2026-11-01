package cl.duoc.maquinaria.repository;

import cl.duoc.maquinaria.dao.JsonMaquinaDao;
import cl.duoc.maquinaria.dao.MaquinaDao;
import cl.duoc.maquinaria.model.CargadorFrontal;
import cl.duoc.maquinaria.model.Excavadora;
import cl.duoc.maquinaria.model.Maquina;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * REPOSITORY — Maneja TODAS las máquinas como una sola colección en memoria
 * y, cada vez que cambia, le pide a cada DAO que guarde su archivo.
 * No usa nada de JavaFX.
 */
public class MaquinaRepository {

    // Un DAO por archivo, como pide el caso
    private final MaquinaDao<Excavadora> excavadoraDao =
            new JsonMaquinaDao<>("excavadoras.json", Excavadora.class);
    private final MaquinaDao<CargadorFrontal> cargadorDao =
            new JsonMaquinaDao<>("cargadores_frontales.json", CargadorFrontal.class);

    private final List<Maquina> maquinas = new ArrayList<>();

    /** Lee ambos archivos y los junta en una sola lista. */
    public void cargar() throws IOException {
        maquinas.clear();
        maquinas.addAll(excavadoraDao.leer());
        maquinas.addAll(cargadorDao.leer());
    }

    public List<Maquina> listar() {
        return maquinas;
    }

    public void agregar(Maquina maquina) throws IOException {
        maquinas.add(maquina);
        try {
            guardar();
        } catch (IOException e) {
            // [CLAVE] Rollback: si no se pudo escribir, se deshace el cambio
            // para que memoria y archivo sigan coincidiendo.
            maquinas.remove(maquina);
            throw e;
        }
    }

    public void eliminar(Maquina maquina) throws IOException {
        int posicion = maquinas.indexOf(maquina);
        maquinas.remove(maquina);
        try {
            guardar();
        } catch (IOException e) {
            maquinas.add(posicion, maquina);     // rollback: se repone en su misma posición
            throw e;
        }
    }

    /** [CLAVE] Separa la colección por tipo y guarda cada tipo en su archivo. */
    private void guardar() throws IOException {
        List<Excavadora> excavadoras = new ArrayList<>();
        List<CargadorFrontal> cargadores = new ArrayList<>();
        for (Maquina m : maquinas) {
            if (m instanceof Excavadora e) {               // instanceof con patrón (Java 16+)
                excavadoras.add(e);
            } else if (m instanceof CargadorFrontal c) {
                cargadores.add(c);
            }
        }
        excavadoraDao.guardar(excavadoras);
        cargadorDao.guardar(cargadores);
    }
}
