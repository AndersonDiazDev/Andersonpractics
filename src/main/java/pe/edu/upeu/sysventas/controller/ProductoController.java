
package pe.edu.upeu.sysventas.controller;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import pe.edu.upeu.sysventas.components.*;
import pe.edu.upeu.sysventas.dto.ComboBoxOption;
import pe.edu.upeu.sysventas.enums.TipoProducto;
import pe.edu.upeu.sysventas.model.Producto;
import pe.edu.upeu.sysventas.service.ICategoriaService;
import pe.edu.upeu.sysventas.service.IMarcaService;
import pe.edu.upeu.sysventas.service.IProductoService;
import pe.edu.upeu.sysventas.service.IUnidadMedidaService;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class ProductoController {

    @FXML
    private TextField txtNombreProducto;

    @FXML
    private TextField txtPUnit;

    @FXML
    private TextField txtPUnitOld;

    @FXML
    private TextField txtUtilidad;

    @FXML
    private TextField txtStock;

    @FXML
    private TextField txtStockOld;

    @FXML
    private TextField txtFiltroDato;

    @FXML
    private ComboBox<ComboBoxOption> cbxTipoProducto;

    @FXML
    private ComboBox<ComboBoxOption> cbxMarca;

    @FXML
    private ComboBox<ComboBoxOption> cbxCategoria;

    @FXML
    private ComboBox<ComboBoxOption> cbxUnidMedida;

    @FXML
    private TableView<Producto> tableView;

    @FXML
    private Label lbnMsg;

    private final IMarcaService ms;
    private final ICategoriaService cs;
    private final IProductoService ps;
    private final IUnidadMedidaService ums;

    private Validator validator;

    private ObservableList<Producto> listarProducto;

    private Producto formulario;

    private Long idProductoCE = 0L;

    private final ToltipCustom ttc = new ToltipCustom();

    public ProductoController(
            IMarcaService ms,
            ICategoriaService cs,
            IProductoService ps,
            IUnidadMedidaService ums) {

        this.ms = ms;
        this.cs = cs;
        this.ps = ps;
        this.ums = ums;
    }

    @FXML
    public void initialize() {

        ValidatorFactory factory =
                Validation.buildDefaultValidatorFactory();

        validator = factory.getValidator();

        cargarCombobox();

        configurarTabla();

        txtFiltroDato.textProperty().addListener(
                (obs, oldValue, newValue) ->
                        filtrarProductos(newValue)
        );

        listar();
    }

    private void cargarCombobox() {

        cbxTipoProducto.getItems().clear();

        cbxTipoProducto.getItems().addAll(
                ps.listarTipoProducto()
        );

        new ComboBoxAutoComplete<>(
                cbxTipoProducto
        );

        cbxMarca.getItems().clear();

        cbxMarca.getItems().addAll(
                ms.listarCombobox()
        );

        new ComboBoxAutoComplete<>(
                cbxMarca
        );

        cbxCategoria.getItems().clear();

        cbxCategoria.getItems().addAll(
                cs.listarCombobox()
        );

        new ComboBoxAutoComplete<>(
                cbxCategoria
        );

        cbxUnidMedida.getItems().clear();

        cbxUnidMedida.getItems().addAll(
                ums.listarCombobox()
        );

        new ComboBoxAutoComplete<>(
                cbxUnidMedida
        );
    }

    private void configurarTabla() {

        TableViewHelper<Producto> tableViewHelper =
                new TableViewHelper<>();

        LinkedHashMap<String, ColumnInfo> columns =
                new LinkedHashMap<>();

        columns.put(
                "ID Pro.",
                new ColumnInfo(
                        "idProducto",
                        60.0
                )
        );

        columns.put(
                "Tipo Producto",
                new ColumnInfo(
                        "tipoProducto",
                        150.0
                )
        );

        columns.put(
                "Nombre Producto",
                new ColumnInfo(
                        "nombre",
                        200.0
                )
        );

        columns.put(
                "P. Unitario",
                new ColumnInfo(
                        "pu",
                        150.0
                )
        );

        columns.put(
                "Utilidad",
                new ColumnInfo(
                        "utilidad",
                        100.0
                )
        );

        columns.put(
                "Marca",
                new ColumnInfo(
                        "idMarca.nombre",
                        150.0
                )
        );

        columns.put(
                "Categoria",
                new ColumnInfo(
                        "idCategoria.nombre",
                        150.0
                )
        );

        Consumer<Producto> updateAction =
                producto -> editForm(producto);

        Consumer<Producto> deleteAction =
                producto -> eliminarProducto(producto);

        tableViewHelper.addColumnsInOrderWithSize(
                tableView,
                columns,
                updateAction,
                deleteAction
        );

        tableView.setTableMenuButtonVisible(true);
    }

    public void listar() {

        try {

            listarProducto =
                    FXCollections.observableArrayList(
                            ps.findAll()
                    );

            tableView.getItems().setAll(
                    listarProducto
            );

        } catch (Exception e) {

            mostrarMensaje(
                    "Error al listar productos: "
                            + e.getMessage(),
                    true
            );
        }
    }
    @FXML
    @FXML
    public void buscarProducto() {
        String filtro = txtFiltroDato.getText();

        if (filtro == null || filtro.trim().isEmpty()) {
            listar();
            mostrarMensaje("", false);
            return;
        }

        filtrarProductos(filtro);
    }

    private void filtrarProductos(String filtro) {

        if (listarProducto == null) {
            listar();
            return;
        }

        if (filtro == null || filtro.trim().isEmpty()) {
            tableView.getItems().clear();
            tableView.getItems().addAll(listarProducto);
            tableView.refresh();
            return;
        }

        String f = filtro.trim().toLowerCase();

        List<Producto> filtrados = listarProducto.stream()
                .filter(producto -> {

                    boolean nombre =
                            producto.getNombre() != null &&
                                    producto.getNombre().toLowerCase().contains(f);

                    boolean tipo =
                            producto.getTipoProducto() != null &&
                                    producto.getTipoProducto()
                                            .name()
                                            .toLowerCase()
                                            .contains(f);

                    boolean precio =
                            producto.getPu() != null &&
                                    String.valueOf(producto.getPu()).contains(f);

                    boolean utilidad =
                            producto.getUtilidad() != null &&
                                    String.valueOf(producto.getUtilidad()).contains(f);

                    boolean marca =
                            producto.getIdMarca() != null &&
                                    producto.getIdMarca().getNombre() != null &&
                                    producto.getIdMarca()
                                            .getNombre()
                                            .toLowerCase()
                                            .contains(f);

                    boolean categoria =
                            producto.getIdCategoria() != null &&
                                    producto.getIdCategoria().getNombre() != null &&
                                    producto.getIdCategoria()
                                            .getNombre()
                                            .toLowerCase()
                                            .contains(f);

                    return nombre || tipo || precio || utilidad || marca || categoria;
                })
                .collect(Collectors.toList());

        tableView.getItems().clear();
        tableView.getItems().addAll(filtrados);
        tableView.refresh();

        mostrarMensaje(
                "Productos encontrados: " + filtrados.size(),
                false
        );
    }
    @FXML
    public void validarFormulario() {

        formulario = new Producto();

        formulario.setNombre(
                txtNombreProducto.getText()
        );

        formulario.setPu(
                parseDoubleSafe(
                        txtPUnit.getText()
                )
        );

        formulario.setPuold(
                parseDoubleSafe(
                        txtPUnitOld.getText()
                )
        );

        formulario.setUtilidad(
                parseDoubleSafe(
                        txtUtilidad.getText()
                )
        );

        formulario.setStock(
                parseDoubleSafe(
                        txtStock.getText()
                )
        );

        formulario.setStockold(
                parseDoubleSafe(
                        txtStockOld.getText()
                )
        );

        String idxTP =
                cbxTipoProducto
                        .getSelectionModel()
                        .getSelectedItem() == null
                        ? ""
                        : cbxTipoProducto
                        .getSelectionModel()
                        .getSelectedItem()
                        .getKey();

        formulario.setTipoProducto(
                idxTP.isEmpty()
                        ? null
                        : TipoProducto.valueOf(idxTP)
        );

        String idxM =
                cbxMarca
                        .getSelectionModel()
                        .getSelectedItem() == null
                        ? "0"
                        : cbxMarca
                        .getSelectionModel()
                        .getSelectedItem()
                        .getKey();

        formulario.setIdMarca(
                idxM.equals("0")
                        ? null
                        : ms.findById(
                        Long.parseLong(idxM)
                )
        );

        String idxC =
                cbxCategoria
                        .getSelectionModel()
                        .getSelectedItem() == null
                        ? "0"
                        : cbxCategoria
                        .getSelectionModel()
                        .getSelectedItem()
                        .getKey();

        formulario.setIdCategoria(
                idxC.equals("0")
                        ? null
                        : cs.findById(
                        Long.parseLong(idxC)
                )
        );

        String idxUM =
                cbxUnidMedida
                        .getSelectionModel()
                        .getSelectedItem() == null
                        ? "0"
                        : cbxUnidMedida
                        .getSelectionModel()
                        .getSelectedItem()
                        .getKey();

        formulario.setIdUnidad(
                idxUM.equals("0")
                        ? null
                        : ums.findById(
                        Long.parseLong(idxUM)
                )
        );

        Set<ConstraintViolation<Producto>> violaciones =
                validator.validate(formulario);

        if (violaciones.isEmpty()) {

            procesarFormulario();

        } else {

            mostrarErroresValidacion(
                    violaciones
            );
        }
    }

    private void procesarFormulario() {

        if (idProductoCE > 0L) {

            formulario.setIdProducto(
                    idProductoCE
            );

            ps.update(
                    idProductoCE,
                    formulario
            );

            mostrarMensaje(
                    "Producto actualizado correctamente",
                    false
            );

        } else {

            ps.save(
                    formulario
            );

            mostrarMensaje(
                    "Producto guardado correctamente",
                    false
            );
        }

        clearForm();

        listar();
    }

    private void eliminarProducto(
            Producto producto) {

        if (producto == null ||
                producto.getIdProducto() == null) {
            return;
        }

        Alert alert =
                new Alert(
                        Alert.AlertType.CONFIRMATION
                );

        alert.setTitle(
                "Eliminar producto"
        );

        alert.setHeaderText(
                "¿Deseas eliminar este producto?"
        );

        alert.setContentText(
                "Producto: "
                        + producto.getNombre()
        );

        Optional<ButtonType> resultado =
                alert.showAndWait();

        if (resultado.isPresent()
                &&
                resultado.get()
                        == ButtonType.OK) {

            ps.delete(
                    producto.getIdProducto()
            );

            mostrarMensaje(
                    "Producto eliminado correctamente",
                    false
            );

            listar();
        }
    }

    public void editForm(
            Producto producto) {

        if (producto == null) {
            return;
        }

        txtNombreProducto.setText(
                producto.getNombre()
        );

        txtPUnit.setText(
                String.valueOf(
                        producto.getPu()
                )
        );

        txtPUnitOld.setText(
                String.valueOf(
                        producto.getPuold()
                )
        );

        txtUtilidad.setText(
                String.valueOf(
                        producto.getUtilidad()
                )
        );

        txtStock.setText(
                String.valueOf(
                        producto.getStock()
                )
        );

        txtStockOld.setText(
                String.valueOf(
                        producto.getStockold()
                )
        );

        if (producto.getTipoProducto() != null) {

            cbxTipoProducto
                    .getSelectionModel()
                    .select(
                            cbxTipoProducto
                                    .getItems()
                                    .stream()
                                    .filter(item ->
                                            item.getKey()
                                                    .equals(
                                                            producto
                                                                    .getTipoProducto()
                                                                    .name()
                                                    )
                                    )
                                    .findFirst()
                                    .orElse(null)
                    );
        }

        if (producto.getIdMarca() != null) {

            cbxMarca
                    .getSelectionModel()
                    .select(
                            cbxMarca
                                    .getItems()
                                    .stream()
                                    .filter(item ->
                                            Long.parseLong(
                                                    item.getKey()
                                            )
                                                    ==
                                                    producto
                                                            .getIdMarca()
                                                            .getIdMarca()
                                    )
                                    .findFirst()
                                    .orElse(null)
                    );
        }

        if (producto.getIdCategoria() != null) {

            cbxCategoria
                    .getSelectionModel()
                    .select(
                            cbxCategoria
                                    .getItems()
                                    .stream()
                                    .filter(item ->
                                            Long.parseLong(
                                                    item.getKey()
                                            )
                                                    ==
                                                    producto
                                                            .getIdCategoria()
                                                            .getIdCategoria()
                                    )
                                    .findFirst()
                                    .orElse(null)
                    );
        }

        if (producto.getIdUnidad() != null) {

            cbxUnidMedida
                    .getSelectionModel()
                    .select(
                            cbxUnidMedida
                                    .getItems()
                                    .stream()
                                    .filter(item ->
                                            Long.parseLong(
                                                    item.getKey()
                                            )
                                                    ==
                                                    producto
                                                            .getIdUnidad()
                                                            .getIdUnidad()
                                    )
                                    .findFirst()
                                    .orElse(null)
                    );
        }

        idProductoCE =
                producto.getIdProducto();

        lbnMsg.setText(
                "Editando producto ID: "
                        + idProductoCE
        );

        lbnMsg.setStyle(
                "-fx-text-fill: #d97706; -fx-font-size: 14px;"
        );

        limpiarError();
    }

    @FXML
    public void clearForm() {

        txtNombreProducto.clear();

        txtPUnit.clear();

        txtPUnitOld.clear();

        txtUtilidad.clear();

        txtStock.clear();

        txtStockOld.clear();

        txtFiltroDato.clear();

        cbxTipoProducto
                .getSelectionModel()
                .clearSelection();

        cbxMarca
                .getSelectionModel()
                .clearSelection();

        cbxCategoria
                .getSelectionModel()
                .clearSelection();

        cbxUnidMedida
                .getSelectionModel()
                .clearSelection();

        idProductoCE = 0L;

        limpiarError();

        lbnMsg.setText("");
    }

    private double parseDoubleSafe(
            String value) {

        if (value == null ||
                value.trim().isEmpty()) {

            return 0.0;
        }

        try {

            return Double.parseDouble(
                    value.trim()
            );

        } catch (NumberFormatException e) {

            return 0.0;
        }
    }

    private void mostrarErroresValidacion(
            Set<ConstraintViolation<Producto>>
                    violaciones) {

        limpiarError();

        Map<String, Control> campos =
                new LinkedHashMap<>();

        campos.put(
                "nombre",
                txtNombreProducto
        );

        campos.put(
                "tipoProducto",
                cbxTipoProducto
        );

        campos.put(
                "pu",
                txtPUnit
        );

        campos.put(
                "puold",
                txtPUnitOld
        );

        campos.put(
                "utilidad",
                txtUtilidad
        );

        campos.put(
                "stock",
                txtStock
        );

        campos.put(
                "stockold",
                txtStockOld
        );

        campos.put(
                "idMarca",
                cbxMarca
        );

        campos.put(
                "idCategoria",
                cbxCategoria
        );

        campos.put(
                "idUnidad",
                cbxUnidMedida
        );

        ConstraintViolation<Producto> primerError =
                violaciones.stream()
                        .sorted(
                                Comparator.comparing(
                                        v ->
                                                v.getPropertyPath()
                                                        .toString()
                                )
                        )
                        .findFirst()
                        .orElse(null);

        if (primerError == null) {
            return;
        }

        String campo =
                primerError
                        .getPropertyPath()
                        .toString();

        String mensaje =
                primerError.getMessage();

        Control control =
                campos.get(campo);

        if (control != null) {

            ttc.marcarError(
                    control,
                    mensaje
            );

            Platform.runLater(
                    control::requestFocus
            );
        }

        lbnMsg.setText(
                mensaje
        );

        lbnMsg.setStyle(
                "-fx-text-fill: red; -fx-font-size: 16px;"
        );
    }

    public void limpiarError() {

        List<Control> controles =
                List.of(
                        txtNombreProducto,
                        cbxTipoProducto,
                        txtPUnit,
                        txtPUnitOld,
                        txtUtilidad,
                        txtStock,
                        txtStockOld,
                        cbxMarca,
                        cbxCategoria,
                        cbxUnidMedida
                );

        controles.forEach(control -> {

            control.getStyleClass()
                    .remove(
                            "text-field-error"
                    );

            ttc.limpiarCampo(
                    control
            );
        });
    }

    private void mostrarMensaje(
            String mensaje,
            boolean error) {

        lbnMsg.setText(
                mensaje
        );

        if (error) {

            lbnMsg.setStyle(
                    "-fx-text-fill: red; -fx-font-size: 14px;"
            );

        } else {

            lbnMsg.setStyle(
                    "-fx-text-fill: green; -fx-font-size: 14px;"
            );
        }
    }
}