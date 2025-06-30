package controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.control.cell.PropertyValueFactory;
import view.Disciplina;
import model.Repositorio;

import java.net.URL;
import java.util.ResourceBundle;

public class DisciplinaController extends AbstractCrudController<model.Disciplina, view.Disciplina, Integer> implements Initializable {

    @FXML
    private TableView<view.Disciplina> tabela;

    @FXML
    private TableColumn<view.Disciplina, Integer> idCol;
    @FXML
    private TableColumn<view.Disciplina, String> codigoCol;
    @FXML
    private TableColumn<view.Disciplina, String> tituloCol;
    @FXML
    private TableColumn<view.Disciplina, String> ementaCol;

    @FXML
    private TextField idField;
    @FXML
    private TextField codigoField;
    @FXML
    private TextField tituloField;
    @FXML
    private TextField ementaField;

    @FXML
    private Button adicionarButton;
    @FXML
    private Button atualizarButton;
    @FXML
    private Button deletarButton;
    @FXML
    private Button cancelarButton;
    @FXML
    private Button salvarButton;

    private static final Repositorio<model.Disciplina, Integer> disciplinaRepo = model.Repositorios.DISCIPLINA;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        codigoCol.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        tituloCol.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        ementaCol.setCellValueFactory(new PropertyValueFactory<>("ementa"));
        super.initialize();
    }

    @Override
    protected Repositorio<model.Disciplina, Integer> getRepositorio() {
        return disciplinaRepo;
    }

    @Override
    protected view.Disciplina modelToView(model.Disciplina d) {
        return new view.Disciplina(d.getId(), d.getCodigo(), d.getTitulo(), d.getEmenta());
    }

    @Override
    protected model.Disciplina viewToModel() {
        model.Disciplina d = new model.Disciplina();
        d.setCodigo(codigoField.getText());
        d.setTitulo(tituloField.getText());
        d.setEmenta(ementaField.getText());
        return d;
    }

    @Override
    protected void preencherCampos(view.Disciplina disc) {
        idField.setText(String.valueOf(disc.getId()));
        codigoField.setText(disc.getCodigo());
        tituloField.setText(disc.getTitulo());
        ementaField.setText(disc.getEmenta());
    }

    @Override
    protected void limparCampos() {
        idField.clear();
        codigoField.clear();
        tituloField.clear();
        ementaField.clear();
    }

    @Override
    protected void desabilitarCampos(boolean desabilitado) {
        codigoField.setDisable(desabilitado);
        tituloField.setDisable(desabilitado);
        ementaField.setDisable(desabilitado);
    }

    @Override
    protected void desabilitarBotoes(boolean adicionar, boolean atualizar, boolean deletar, boolean cancelar, boolean salvar) {
        adicionarButton.setDisable(adicionar);
        atualizarButton.setDisable(atualizar);
        deletarButton.setDisable(deletar);
        cancelarButton.setDisable(cancelar);
        salvarButton.setDisable(salvar);
    }

    @Override
    protected TableView<view.Disciplina> getTabela() {
        return tabela;
    }

    @Override
    protected Integer getIdFromViewModel(view.Disciplina viewModel) {
        return viewModel.getId();
    }

    @Override
    protected void setIdOnEntity(model.Disciplina entidade, Integer id) {
        entidade.setId(id);
    }

    // Métodos que chamam a superclasse
    @FXML
    public void onAdicionar() {
        super.onAdicionar();
    }

    @FXML
    public void onSalvar() {
        super.onSalvar();
    }

    @FXML
    public void onAtualizar() {
        super.onAtualizar();
    }

    @FXML
    public void onDeletar() {
        super.onDeletar();
    }

    @FXML
    public void onCancelar() {
        super.onCancelar();
    }
}
