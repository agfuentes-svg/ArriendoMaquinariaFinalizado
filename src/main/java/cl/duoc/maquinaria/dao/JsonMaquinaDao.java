package cl.duoc.maquinaria.dao;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO — Implementación con Gson. Es genérica: una instancia para excavadoras.json
 * y otra para cargadores_frontales.json.
 */
public class JsonMaquinaDao<T> implements MaquinaDao<T> {

    private final Path archivo;
    private final Type tipoLista;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public JsonMaquinaDao(String nombreArchivo, Class<T> clase) {
        this.archivo = Path.of(nombreArchivo);   // sin carpetas = carpeta raíz del proyecto
        // [CLAVE] TypeToken: le dice a Gson que el archivo es una List<clase> (ej. List<Excavadora>).
        // Sin esto, Gson no sabría de qué clase son los elementos de la lista.
        this.tipoLista = TypeToken.getParameterized(List.class, clase).getType();
    }

    @Override
    public List<T> leer() throws IOException {
        // [CLAVE] Primera ejecución: si el archivo no existe, se parte con lista vacía y SIN error.
        if (!Files.exists(archivo)) {
            return new ArrayList<>();
        }
        // try-with-resources: el archivo se cierra solo, aunque ocurra un error
        try (Reader reader = Files.newBufferedReader(archivo, StandardCharsets.UTF_8)) {
            List<T> lista = gson.fromJson(reader, tipoLista);    // JSON -> objetos (deserializar)
            return lista != null ? lista : new ArrayList<>();    // archivo vacío -> lista vacía
        } catch (RuntimeException e) {
            // JSON dañado: se transforma en IOException para que el controlador lo muestre como mensaje
            throw new IOException("el archivo " + archivo + " tiene un formato inválido.", e);
        }
    }

    @Override
    public void guardar(List<T> lista) throws IOException {
        try (Writer writer = Files.newBufferedWriter(archivo, StandardCharsets.UTF_8)) {
            gson.toJson(lista, tipoLista, writer);                // objetos -> JSON (serializar)
        }
    }
}
