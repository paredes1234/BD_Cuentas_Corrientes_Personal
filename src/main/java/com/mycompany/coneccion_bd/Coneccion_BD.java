package com.mycompany.coneccion_bd;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;
import javafx.application.Application;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Coneccion_BD extends Application {

    private Connection con;
    private BorderPane root;
    private Label lblTitulo;
    private Label lblDescripcion;
    private GridPane panelCampos;
    private TableView<ObservableList<String>> tabla;
    private ObservableList<ObservableList<String>> datos;

    private Map<String, TextField> campos;
    private Map<String, TablaConfig> configuraciones;

    private TablaConfig configActual;

    private int flaAct = 0;
    private String operacion = "";

    @Override
    public void start(Stage stage) {
        conectar();
        prepararEstadosRegistro();
        crearConfiguraciones();

        root = new BorderPane();
        
        root.setLeft(crearMenuLateral());
        root.setCenter(crearZonaCentral());

        Scene scene = new Scene(root, 1280, 720);

        stage.setTitle("Sistema de Cuentas Corrientes de Personal");
        stage.setScene(scene);
        stage.setOnCloseRequest(e -> cerrarConexion());
        stage.show();

        cambiarModulo("R1Z_TIPO_TRABAJADOR");
    }
    
    private void conectar() {
        try {
            con = ConexionBD.conectar();
        } catch (Exception e) {
            mensaje("Error de conexion: " + e.getMessage());
            System.exit(0);
        }
    }

    private void prepararEstadosRegistro() {
        try {
            PreparedStatement ps = con.prepareStatement(
                "INSERT IGNORE INTO GZZ_ESTADO_REGISTRO " +
                "(EstRegCod, EstRegNom, EstRegEstReg) VALUES " +
                "('A', 'Activo', 'A'), " +
                "('I', 'Inactivo', 'A'), " +
                "('*', 'Eliminado', 'A')"
            );

            ps.executeUpdate();
            ps.close();

        } catch (SQLException e) {
            mensaje("Error al preparar estados de registro: " + e.getMessage());
        }
    }

    private void crearConfiguraciones() {
        configuraciones = new LinkedHashMap<>();

        configuraciones.put(
            "GZZ_ESTADO_REGISTRO",
            new TablaConfig(
                "GZZ_ESTADO_REGISTRO",
                new String[]{"EstRegCod", "EstRegNom", "EstRegEstReg"},
                "EstRegCod",
                "EstRegEstReg"
            )
        );

        configuraciones.put(
            "GZZ_COMPANIA",
            new TablaConfig(
                "GZZ_COMPANIA",
                new String[]{"ComCod", "ComNom", "ComRuc", "ComDir", "ComEstReg"},
                "ComCod",
                "ComEstReg"
            )
        );
        
            configuraciones.put(
            "R1Z_TIPO_TRABAJADOR",
            new TablaConfig(
                "R1Z_TIPO_TRABAJADOR",
                new String[]{"TipTraCod", "TipTraNom", "TipTraEstReg"},
                "TipTraCod",
                "TipTraEstReg"
            )
        );
        
        configuraciones.put(
            "R1Z_CENTRO_COSTO",
            new TablaConfig(
                "R1Z_CENTRO_COSTO",
                new String[]{"CenCosCod", "CenCosNom", "CenCosEstReg"},
                "CenCosCod",
                "CenCosEstReg"
            )
        );

        configuraciones.put(
            "R1Z_ESTADO_TRABAJADOR",
            new TablaConfig(
                "R1Z_ESTADO_TRABAJADOR",
                new String[]{"EstTraCod", "EstTraNom", "EstTraEstReg"},
                "EstTraCod",
                "EstTraEstReg"
            )
        );

        configuraciones.put(
            "R1Z_TIPO_PRESTAMO",
            new TablaConfig(
                "R1Z_TIPO_PRESTAMO",
                new String[]{"TipPreCod", "TipPreNom", "TipPreEstReg"},
                "TipPreCod",
                "TipPreEstReg"
            )
        );

        configuraciones.put(
            "R1Z_TIPO_INTERES",
            new TablaConfig(
                "R1Z_TIPO_INTERES",
                new String[]{"TipIntCod", "TipIntNom", "TipIntDesc", "TipIntEstReg"},
                "TipIntCod",
                "TipIntEstReg"
            )
        );

        configuraciones.put(
            "R1Z_TIPO_MOVIMIENTO",
            new TablaConfig(
                "R1Z_TIPO_MOVIMIENTO",
                new String[]{"TipMovCod", "TipMovNom", "TipMovEstReg"},
                "TipMovCod",
                "TipMovEstReg"
            )
        );
    }

    private HBox crearPanelBotones1() {
        Button btnAdicionar = new Button("Adicionar");
        Button btnModificar = new Button("Modificar");
        Button btnEliminar = new Button("Eliminar");
        Button btnCancelar = new Button("Cancelar");

        btnAdicionar.setOnAction(e -> adicionar());
        btnModificar.setOnAction(e -> modificar());
        btnEliminar.setOnAction(e -> eliminar());
        btnCancelar.setOnAction(e -> cancelar());

        HBox hbox = new HBox(10);
        hbox.getChildren().addAll(btnAdicionar, btnModificar, btnEliminar, btnCancelar);

        return hbox;
    }

    private HBox crearPanelBotones2() {
        Button btnInactivar = new Button("Inactivar");
        Button btnReactivar = new Button("Reactivar");
        Button btnActualizar = new Button("Actualizar");
        Button btnSalir = new Button("Salir");

        btnInactivar.setOnAction(e -> inactivar());
        btnReactivar.setOnAction(e -> reactivar());
        btnActualizar.setOnAction(e -> actualizar());
        btnSalir.setOnAction(e -> salir());

        HBox hbox = new HBox(10);
        hbox.getChildren().addAll(btnInactivar, btnReactivar, btnActualizar, btnSalir);

        return hbox;
    }

    private VBox crearMenuLateral() {

        VBox menu = new VBox(10);
        menu.setPadding(new Insets(18));
        menu.setPrefWidth(290);

        menu.setStyle("-fx-background-color: #111827;");

        Label titulo = new Label("CUENTAS CORRIENTES");

        titulo.setStyle(
            "-fx-text-fill: white;" +
            "-fx-font-size: 20px;" +
            "-fx-font-weight: bold;"
        );

        Label subtitulo = new Label("Menu de mantenimiento");

        subtitulo.setStyle(
            "-fx-text-fill: #d1d5db;" +
            "-fx-font-size: 13px;"
        );

        menu.getChildren().addAll(
            titulo,
            subtitulo,
            separador(),
            seccion("Tablas Globales")
        );

        menu.getChildren().addAll(
            botonMenu("Estado Registro", "GZZ_ESTADO_REGISTRO"),
            botonMenu("Companias", "GZZ_COMPANIA"),

            separador(),

            seccion("Tablas Referenciales"),

            botonMenu("Tipo Trabajador", "R1Z_TIPO_TRABAJADOR"),
            botonMenu("Centro Costo", "R1Z_CENTRO_COSTO"),
            botonMenu("Estado Trabajador", "R1Z_ESTADO_TRABAJADOR"),
            botonMenu("Tipo Prestamo", "R1Z_TIPO_PRESTAMO"),
            botonMenu("Tipo Interes", "R1Z_TIPO_INTERES"),
            botonMenu("Tipo Movimiento", "R1Z_TIPO_MOVIMIENTO")
        );

        ScrollPane scroll = new ScrollPane(menu);

        scroll.setFitToWidth(true);

        scroll.setStyle(
            "-fx-background: #111827;" +
            "-fx-background-color: #111827;"
        );

        scroll.setPrefWidth(300);

        VBox contenedor = new VBox(scroll);

        contenedor.setPrefWidth(300);

        return contenedor;
    }    

    private VBox crearZonaCentral() {

        VBox zona = new VBox(12);

        zona.setPadding(new Insets(20));
        zona.setStyle("-fx-background-color: #f3f4f6;");

        lblTitulo = new Label();

        lblTitulo.setStyle(
            "-fx-font-size: 24px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: #111827;"
        );

        lblDescripcion = new Label();
        
        lblDescripcion.setStyle(
            "-fx-font-size: 14px;" +
            "-fx-text-fill: #4b5563;"
        );
        
        panelCampos = new GridPane();
        panelCampos.setPadding(new Insets(15));
        panelCampos.setHgap(12);
        panelCampos.setVgap(10);

        ScrollPane scrollCampos = new ScrollPane(panelCampos);
        scrollCampos.setFitToWidth(true);
        scrollCampos.setPrefHeight(260);

        TitledPane areaRegistro = new TitledPane("Area de Registro", scrollCampos);

        areaRegistro.setCollapsible(false);

        HBox panelBotones1 = crearPanelBotones1();
        HBox panelBotones2 = crearPanelBotones2();

        datos = FXCollections.observableArrayList();

        tabla = new TableView<>();
        tabla.setItems(datos);
        tabla.setPrefHeight(310);
        
        TitledPane areaGrilla = new TitledPane("Grilla de Datos", tabla);
        
        areaGrilla.setCollapsible(false);
        
        VBox.setVgrow(areaGrilla, Priority.ALWAYS);
        
        zona.getChildren().addAll(
                lblTitulo,
                lblDescripcion,
                areaRegistro,
                panelBotones1,
                panelBotones2,
                areaGrilla
        );

        return zona;
    }    
    
    private Label seccion(String texto) {
    Label lbl = new Label(texto);
    lbl.setStyle(
        "-fx-text-fill: #93c5fd;" +
        "-fx-font-size: 13px;" +
        "-fx-font-weight: bold;"
    );
    return lbl;
    }  
    
    private Separator separador() {
        Separator sp = new Separator();
        sp.setStyle("-fx-background-color: #374151;");
        return sp;
    } 
    
    private Button botonMenu(String texto, String tablaNombre) {

        Button btn = new Button(texto);

        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setPrefHeight(38);

        btn.setStyle(
            "-fx-background-color: #1f2937;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 13px;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 8;"
        );

        btn.setOnMouseEntered(e ->
            btn.setStyle(
                "-fx-background-color: #2563eb;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 13px;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 8;"
            )
        );

        btn.setOnMouseExited(e ->
            btn.setStyle(
                "-fx-background-color: #1f2937;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 13px;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 8;"
            )
        );

        btn.setOnAction(e -> cambiarModulo(tablaNombre));

        return btn;
    }
    
    private void cambiarModulo(String nombreTabla) {
        configActual = configuraciones.get(nombreTabla);
        lblTitulo.setText(nombreTabla);
        lblDescripcion.setText("Mantenimiento de " + nombreTabla);
        
        crearCampos();
        crearColumnas();
        cargarTabla();
        cancelar();
    }

    private void crearCampos() {
        panelCampos.getChildren().clear();
        campos = new LinkedHashMap<>();

        for (int i = 0; i < configActual.columnas.length; i++) {
            String columna = configActual.columnas[i];

            Label lbl = new Label(columna + ":");
            TextField txt = new TextField();
            txt.setPrefWidth(320);

            if (columna.equals(configActual.columnaEstado)) {
                txt.setEditable(false);
            }

            campos.put(columna, txt);

            panelCampos.add(lbl, 0, i);
            panelCampos.add(txt, 1, i);
        }
    }

    private void crearColumnas() {
        tabla.getColumns().clear();

        for (int i = 0; i < configActual.columnas.length; i++) {
            final int indice = i;
            String columnaNombre = configActual.columnas[i];

            TableColumn<ObservableList<String>, String> columna = new TableColumn<>(columnaNombre);
            columna.setCellValueFactory(data -> {
                ObservableList<String> fila = data.getValue();

                if (indice < fila.size()) {
                    return new ReadOnlyStringWrapper(fila.get(indice));
                }

                return new ReadOnlyStringWrapper("");
            });

            columna.setPrefWidth(180);
            tabla.getColumns().add(columna);
        }

        tabla.getSelectionModel().selectedItemProperty().addListener((obs, anterior, seleccionado) -> {
            if (seleccionado != null) {
                cargarRegistroSeleccionado(seleccionado);
                habilitarCampos(false, false);
            }
        });
    }

    private void adicionar() {
        limpiarCampos();
        tabla.getSelectionModel().clearSelection();

        habilitarCampos(true, true);
        campos.get(configActual.columnaEstado).setText("A");

        flaAct = 1;
        operacion = "ADICIONAR";

        mensaje("Ingrese los datos y presione Actualizar");
    }

    private void modificar() {
        ObservableList<String> fila = tabla.getSelectionModel().getSelectedItem();

        if (fila == null) {
            mensaje("Seleccione un registro para modificar");
            return;
        }

        cargarRegistroSeleccionado(fila);
        habilitarCampos(false, true);

        flaAct = 1;
        operacion = "MODIFICAR";

        mensaje("Modifique los datos y presione Actualizar");
    }

    private void eliminar() {
        ObservableList<String> fila = tabla.getSelectionModel().getSelectedItem();

        if (fila == null) {
            mensaje("Seleccione un registro para eliminar");
            return;
        }

        cargarRegistroSeleccionado(fila);
        campos.get(configActual.columnaEstado).setText("*");

        habilitarCampos(false, false);

        flaAct = 1;
        operacion = "ELIMINAR";

        mensaje("Registro preparado para eliminacion logica. Presione Actualizar");
    }

    private void inactivar() {
        ObservableList<String> fila = tabla.getSelectionModel().getSelectedItem();

        if (fila == null) {
            mensaje("Seleccione un registro para inactivar");
            return;
        }

        cargarRegistroSeleccionado(fila);
        campos.get(configActual.columnaEstado).setText("I");

        habilitarCampos(false, false);

        flaAct = 1;
        operacion = "INACTIVAR";

        mensaje("Registro preparado para inactivar. Presione Actualizar");
    }

    private void reactivar() {
        ObservableList<String> fila = tabla.getSelectionModel().getSelectedItem();

        if (fila == null) {
            mensaje("Seleccione un registro para reactivar");
            return;
        }

        cargarRegistroSeleccionado(fila);
        campos.get(configActual.columnaEstado).setText("A");

        habilitarCampos(false, false);

        flaAct = 1;
        operacion = "REACTIVAR";

        mensaje("Registro preparado para reactivar. Presione Actualizar");
    }

    private void actualizar() {
        if (flaAct == 0) {
            mensaje("No se selecciono ninguna operacion");
            return;
        }

        boolean correcto = false;

        if (operacion.equals("ADICIONAR")) {
            correcto = insertarRegistro();
        } else if (operacion.equals("MODIFICAR")) {
            correcto = modificarRegistro();
        } else if (operacion.equals("ELIMINAR") || operacion.equals("INACTIVAR") || operacion.equals("REACTIVAR")) {
            correcto = actualizarEstado();
        }

        if (correcto) {
            cargarTabla();
            limpiarCampos();
            habilitarCampos(false, false);
            tabla.getSelectionModel().clearSelection();

            flaAct = 0;
            operacion = "";
        }
    }

    private boolean insertarRegistro() {
        try {
            if (!validarCamposObligatorios()) {
                mensaje("Complete todos los campos obligatorios");
                return false;
            }

            StringBuilder sql = new StringBuilder();
            sql.append("INSERT INTO ");
            sql.append(configActual.nombreTabla);
            sql.append(" (");

            for (int i = 0; i < configActual.columnas.length; i++) {
                sql.append(configActual.columnas[i]);

                if (i < configActual.columnas.length - 1) {
                    sql.append(", ");
                }
            }

            sql.append(") VALUES (");

            for (int i = 0; i < configActual.columnas.length; i++) {
                sql.append("?");

                if (i < configActual.columnas.length - 1) {
                    sql.append(", ");
                }
            }

            sql.append(")");

            PreparedStatement ps = con.prepareStatement(sql.toString());

            for (int i = 0; i < configActual.columnas.length; i++) {
                String columna = configActual.columnas[i];
                String valor = campos.get(columna).getText().trim();

                if (columna.equals(configActual.columnaEstado)) {
                    valor = "A";
                }

                ps.setString(i + 1, valor);
            }

            int filas = ps.executeUpdate();
            ps.close();

            mensaje("Registro adicionado correctamente. Filas afectadas: " + filas);
            return true;

        } catch (SQLException e) {
            mensaje("Error al adicionar: " + e.getMessage());
            return false;
        }
    }

    private boolean modificarRegistro() {
        try {
            if (!validarCamposObligatorios()) {
                mensaje("Complete todos los campos obligatorios");
                return false;
            }

            StringBuilder sql = new StringBuilder();
            sql.append("UPDATE ");
            sql.append(configActual.nombreTabla);
            sql.append(" SET ");

            boolean primero = true;

            for (String columna : configActual.columnas) {
                if (!columna.equals(configActual.columnaPK) && !columna.equals(configActual.columnaEstado)) {
                    if (!primero) {
                        sql.append(", ");
                    }

                    sql.append(columna).append(" = ?");
                    primero = false;
                }
            }

            sql.append(" WHERE ");
            sql.append(configActual.columnaPK);
            sql.append(" = ?");

            PreparedStatement ps = con.prepareStatement(sql.toString());

            int indice = 1;

            for (String columna : configActual.columnas) {
                if (!columna.equals(configActual.columnaPK) && !columna.equals(configActual.columnaEstado)) {
                    ps.setString(indice, campos.get(columna).getText().trim());
                    indice++;
                }
            }

            ps.setString(indice, campos.get(configActual.columnaPK).getText().trim());

            int filas = ps.executeUpdate();
            ps.close();

            mensaje("Registro modificado correctamente. Filas afectadas: " + filas);
            return true;

        } catch (SQLException e) {
            mensaje("Error al modificar: " + e.getMessage());
            return false;
        }
    }

    private boolean actualizarEstado() {
        try {
            String sql = "UPDATE " + configActual.nombreTabla +
                         " SET " + configActual.columnaEstado + " = ?" +
                         " WHERE " + configActual.columnaPK + " = ?";

            PreparedStatement ps = con.prepareStatement(sql);

            ps.setString(1, campos.get(configActual.columnaEstado).getText().trim());
            ps.setString(2, campos.get(configActual.columnaPK).getText().trim());

            int filas = ps.executeUpdate();
            ps.close();

            if (operacion.equals("ELIMINAR")) {
                mensaje("Registro eliminado logicamente. Filas afectadas: " + filas);
            } else if (operacion.equals("INACTIVAR")) {
                mensaje("Registro inactivado correctamente. Filas afectadas: " + filas);
            } else if (operacion.equals("REACTIVAR")) {
                mensaje("Registro reactivado correctamente. Filas afectadas: " + filas);
            }

            return true;

        } catch (SQLException e) {
            mensaje("Error al actualizar estado: " + e.getMessage());
            return false;
        }
    }

    private void cargarTabla() {
        try {
            datos.clear();

            StringBuilder sql = new StringBuilder();
            sql.append("SELECT ");

            for (int i = 0; i < configActual.columnas.length; i++) {
                sql.append(configActual.columnas[i]);

                if (i < configActual.columnas.length - 1) {
                    sql.append(", ");
                }
            }

            sql.append(" FROM ");
            sql.append(configActual.nombreTabla);
            sql.append(" ORDER BY ");
            sql.append(configActual.columnaPK);

            PreparedStatement ps = con.prepareStatement(sql.toString());
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                ObservableList<String> fila = FXCollections.observableArrayList();

                for (String columna : configActual.columnas) {
                    fila.add(rs.getString(columna));
                }

                datos.add(fila);
            }

            rs.close();
            ps.close();

        } catch (SQLException e) {
            mensaje("Error al cargar tabla: " + e.getMessage());
        }
    }

    private void cargarRegistroSeleccionado(ObservableList<String> fila) {
        for (int i = 0; i < configActual.columnas.length; i++) {
            String columna = configActual.columnas[i];

            if (i < fila.size()) {
                campos.get(columna).setText(fila.get(i));
            }
        }
    }

    private boolean validarCamposObligatorios() {
        for (String columna : configActual.columnas) {
            TextField txt = campos.get(columna);

            if (txt.getText().trim().equals("")) {
                return false;
            }
        }

        return true;
    }

    private void habilitarCampos(boolean clave, boolean datosEditables) {
        for (String columna : configActual.columnas) {
            TextField txt = campos.get(columna);

            if (columna.equals(configActual.columnaPK)) {
                txt.setEditable(clave);
            } else if (columna.equals(configActual.columnaEstado)) {
                txt.setEditable(false);
            } else {
                txt.setEditable(datosEditables);
            }
        }
    }

    private void limpiarCampos() {
        for (TextField txt : campos.values()) {
            txt.clear();
        }
    }

    private void cancelar() {
        limpiarCampos();
        habilitarCampos(false, false);
        tabla.getSelectionModel().clearSelection();

        flaAct = 0;
        operacion = "";
    }

    private void salir() {
        cerrarConexion();
        System.exit(0);
    }

    private void cerrarConexion() {
        try {
            if (con != null && !con.isClosed()) {
                con.close();
            }
        } catch (SQLException e) {
            mensaje("Error al cerrar conexion: " + e.getMessage());
        }
    }

    private void mensaje(String texto) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Mensaje");
        alert.setHeaderText(null);
        alert.setContentText(texto);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }

    private static class TablaConfig {

        String nombreTabla;
        String[] columnas;
        String columnaPK;
        String columnaEstado;

        TablaConfig(String nombreTabla, String[] columnas, String columnaPK, String columnaEstado) {
            this.nombreTabla = nombreTabla;
            this.columnas = columnas;
            this.columnaPK = columnaPK;
            this.columnaEstado = columnaEstado;
        }
    }
}