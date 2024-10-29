package es.liernisarraoa.gestionpersonasfiltro.Controladores;

import es.liernisarraoa.gestionpersonasfiltro.GestionPersonas;
import es.liernisarraoa.gestionpersonasfiltro.Modelo.Personas;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.ResourceBundle;

/**
 * Controlador principal de la aplicación de gestión de personas.
 * Maneja la interfaz principal y las operaciones sobre la tabla de personas.
 *
 * @author Lierni
 * @version 1.0
 */
public class ControladorPrincipal implements Initializable {
    private Scene sceneAniadir;
    private Stage modalAniadir;
    private Scene sceneModificar;
    private Stage modalModificar;
    private static Stage stagePrincipal;
    private Personas p;
    private ObservableList<Personas> items = FXCollections.observableArrayList();
    private FileChooser dialogoFicheroSave = new FileChooser();
    private int cont = 1;

    /** Tabla que muestra la lista de personas. */
    @FXML
    private TableView<Personas> tablaPersonas;

    /** Columna para mostrar el nombre de la persona. */
    @FXML
    private TableColumn<Personas, String> columnaNombre;

    /** Columna para mostrar el apellido de la persona. */
    @FXML
    private TableColumn<Personas, String> columnaApellido;

    /** Columna para mostrar la edad de la persona. */
    @FXML
    private TableColumn<Personas, Integer> columnaEdad;

    /** TexField para el nombre del filtrado */
    @FXML
    private TextField nombreFiltradoTextField;

    /**
     * Maneja el evento de agregar una nueva persona.
     * Abre una ventana modal para introducir los datos de la nueva persona.
     *
     * @param actionEvent El evento que desencadena la acción.
     * @throws Exception Si ocurre algún error al cargar la ventana modal.
     */
    public void agregarPersona(ActionEvent actionEvent) throws Exception {
        //Esto si el controlador necesita hacer algo en la ventana principal
        // Cargar el FXML de la ventana modal
        FXMLLoader loader = new FXMLLoader(GestionPersonas.class.getResource("aniadirPersona.fxml"));
        Parent root = loader.load();

        // Obtener el controlador de la ventana modal
        AniadirPersonaController modalControlador = loader.getController();

        // Pasar el TableView al controlador de la ventana modal
        modalControlador.setTablaPersonas(this.tablaPersonas);

        // Crear y mostrar la ventana modal
        modalAniadir = new Stage();
        sceneAniadir = new Scene(root);
        modalAniadir.setScene(sceneAniadir);
        modalAniadir.initModality(Modality.APPLICATION_MODAL);
        modalAniadir.setTitle("Agregar Persona");
        modalAniadir.getIcons().add(new Image(String.valueOf(GestionPersonas.class.getResource("/Imagenes/agenda.png"))));
        modalAniadir.showAndWait();
        items = tablaPersonas.getItems();
    }

    /**
     * Maneja el evento de modificar una persona existente.
     * Abre una ventana modal para editar los datos de la persona seleccionada.
     *
     * @param actionEvent El evento que desencadena la acción.
     * @throws IOException Si ocurre algún error al cargar la ventana modal.
     */
    public void modificarPersona(ActionEvent actionEvent) throws IOException {
        //Esto si el controlador necesita hacer algo en la ventana principal
        // Cargar el FXML de la ventana modal
        FXMLLoader loader = new FXMLLoader(GestionPersonas.class.getResource("modificarPersona.fxml"));
        Parent root = loader.load();

        p = tablaPersonas.getSelectionModel().getSelectedItem();
        // Obtener el controlador de la ventana modal
        ModificarPersonaController modalControlador = loader.getController();

        // Pasar el TableView al controlador de la ventana modal
        modalControlador.setP(p);
        modalControlador.setTabla(tablaPersonas);

        // Crear y mostrar la ventana modal
        modalModificar = new Stage();
        sceneModificar = new Scene(root);
        modalModificar.setScene(sceneModificar);
        modalModificar.initModality(Modality.APPLICATION_MODAL);
        modalModificar.setTitle("Modificar persona");
        modalModificar.getIcons().add(new Image(String.valueOf(GestionPersonas.class.getResource("/Imagenes/agenda.png"))));
        modalModificar.showAndWait();
        items = tablaPersonas.getItems();
    }

    /**
     * Maneja el evento de eliminar una persona de la tabla.
     * Elimina la persona seleccionada y muestra una alerta de confirmación.
     *
     * @param actionEvent El evento que desencadena la acción.
     */
    public void eliminarPersona(ActionEvent actionEvent) {
        Personas personaEliminar = tablaPersonas.getSelectionModel().getSelectedItem();
        tablaPersonas.getSelectionModel().clearSelection();
        tablaPersonas.getItems().remove(personaEliminar);
        items = tablaPersonas.getItems();
        alertaEliminar();
    }

    /**
     * Muestra una alerta informando que la persona ha sido eliminada.
     */
    private void alertaEliminar() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setTitle("Persona eliminada");
        alert.setContentText("La persona seleccionada se ha eliminado.");
        alert.showAndWait();
    }

    /**
     * Muestra un cuadro de dialogo para seleccionar ruta donde querer guardar archivo CSV
     */
    public void exportarCSV(ActionEvent actionEvent) {
        dialogoFicheroSave.setTitle("Guardar archivo");
        dialogoFicheroSave.setInitialDirectory(new File("C:\\Users\\Liliaam\\Documents"));
        dialogoFicheroSave.setInitialFileName("personas"+cont+".csv");
        cont++;
        File file = dialogoFicheroSave.showSaveDialog(stagePrincipal);
        if (file == null) {
            return;
        }
        try {
            // Write the HTML contents to the file. Overwrite the existing file.
            Files.write(file.toPath(), generarValorFichero().getBytes());
        }catch(IOException e) {
            e.printStackTrace();
        }
    }

    private String generarValorFichero() {
        String texto = "Nombre, Apellido, Edad\n";
        for(Personas p : items){
            texto += p.getNombre() + "," + p.getApellido() + "," + p.getEdad() + "\n";
        }
        return texto;
    }

    public void importarCSV(ActionEvent actionEvent) {
        dialogoFicheroSave.setTitle("Abrir archivo");
        File file = dialogoFicheroSave.showOpenDialog(stagePrincipal);
        dialogoFicheroSave.setInitialDirectory(new File("C:\\Users\\Liliaam\\Documents")); //Esto lo hago para ahorrarme tiempo en la spruebas, en realidad no hace falta
        if (file == null) {
            return;
        }
        try {
            // Read the file and populate the HTMLEditor
            byte[] resume = Files.readAllBytes(file.toPath());
            String[] personas = new String(resume).split("\n");
            generarPersonasEnTabla(personas);
        }catch(IOException e) {
            e.printStackTrace();
        }
    }

    private void generarPersonasEnTabla(String[] personas) {
        items.removeAll();
        for(int i = 1; i < personas.length; i++){
            String[] persona = personas[i].split(",");
            items.add(new Personas(persona[0], persona[1], Integer.parseInt(persona[2])));
        }
        tablaPersonas.getItems().removeAll();
        tablaPersonas.setItems(items);
    }

    /**
     * Maneja el evento de filtrar una persona de la tabla.
     * Filtra el nombre de la persona introducida.
     *
     * @param keyEvent El evento que desencadena la acción.
     */
    public void filtrarPorNombre(KeyEvent keyEvent) {
        if(keyEvent.getCode() == KeyCode.ENTER){
            String nombreFiltrar = nombreFiltradoTextField.getText().trim();
            ObservableList<Personas> itemsFilter = FXCollections.observableArrayList();
            if(!nombreFiltrar.isEmpty()){
                for(Personas p : items){
                    if(p.getNombre().equalsIgnoreCase(nombreFiltrar)){
                        itemsFilter.add(p);
                    }
                }
                //Agregamos el observable list y limpiamos la tabla
                tablaPersonas.getItems().removeAll();
                tablaPersonas.setItems(itemsFilter);
            } else {
                tablaPersonas.getItems().removeAll();
                tablaPersonas.setItems(items);
            }
        }
    }
    /**
     * Inicializa el controlador.
     * Configura la tabla y sus columnas para mostrar la información de las personas.
     *
     * @param url La ubicación utilizada para resolver rutas relativas para el objeto raíz.
     * @param resourceBundle Los recursos utilizados para localizar el objeto raíz.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        tablaPersonas.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        columnaEdad.setCellValueFactory(new PropertyValueFactory<>("edad"));
        columnaNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        columnaApellido.setCellValueFactory(new PropertyValueFactory<>("apellido"));
        columnaNombre.prefWidthProperty().bind(tablaPersonas.widthProperty().multiply(0.4));
        columnaApellido.prefWidthProperty().bind(tablaPersonas.widthProperty().multiply(0.4));
        columnaEdad.prefWidthProperty().bind(tablaPersonas.widthProperty().multiply(0.2));
    }

    public static void setStagePrincipal(Stage stage){
        stagePrincipal = stage;
    }
}