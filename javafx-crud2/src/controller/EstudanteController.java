package controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.control.cell.PropertyValueFactory;
import view.Estudante;
import model.Repositorio;

import java.net.URL;
import java.util.ResourceBundle;

public class EstudanteController extends AbstractCrudController<model.Estudante, view.Estudante, Integer> implements Initializable {

    @FXML
    private TableView<view.Estudante> tabela;

    @FXML
    private TableColumn<view.Estudante, Integer> idCol;
    @FXML
    private TableColumn<view.Estudante, String> nomeCompletoCol;
    @FXML
    private TableColumn<view.Estudante, String> dataDeNascimentoCol;
    @FXML
    private TableColumn<view.Estudante, Integer> matriculaCol;

    @FXML
    private TextField idField;
    @FXML
    private TextField nomeCompletoField;
    @FXML
    private TextField dataDeNascimentoField;
    @FXML
    private TextField matriculaField;

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

    private static final Repositorio<model.Estudante, Integer> estudanteRepo = model.Repositorios.ESTUDANTE;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        nomeCompletoCol.setCellValueFactory(new PropertyValueFactory<>("nomeCompleto"));
        dataDeNascimentoCol.setCellValueFactory(new PropertyValueFactory<>("dataDeNascimento"));
        matriculaCol.setCellValueFactory(new PropertyValueFactory<>("matricula"));
        super.initialize();
    }

    @Override
    protected Repositorio<model.Estudante, Integer> getRepositorio() {
        return estudanteRepo;
    }

    @Override
    protected view.Estudante modelToView(model.Estudante e) {
        return new view.Estudante(e.getId(), e.getNomeCompleto(), e.getDataDeNascimento(), e.getMatricula());
    }

    @Override
    protected model.Estudante viewToModel() {
        model.Estudante e = new model.Estudante();
        e.setNomeCompleto(nomeCompletoField.getText());
        e.setDataDeNascimento(dataDeNascimentoField.getText());
        e.setMatricula(Integer.parseInt(matriculaField.getText()));
        return e;
    }

    @Override
    protected void preencherCampos(view.Estudante est) {
        idField.setText(String.valueOf(est.getId()));
        nomeCompletoField.setText(est.getNomeCompleto());
        dataDeNascimentoField.setText(est.getDataDeNascimento());
        matriculaField.setText(String.valueOf(est.getMatricula()));
    }

    @Override
    protected void limparCampos() {
        idField.clear();
        nomeCompletoField.clear();
        dataDeNascimentoField.clear();
        matriculaField.clear();
    }

    @Override
    protected void desabilitarCampos(boolean desabilitado) {
        nomeCompletoField.setDisable(desabilitado);
        dataDeNascimentoField.setDisable(desabilitado);
        matriculaField.setDisable(desabilitado);
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
    protected TableView<view.Estudante> getTabela() {
        return tabela;
    }

    @Override
    protected Integer getIdFromViewModel(view.Estudante viewModel) {
        return viewModel.getId();
    }

    @Override
    protected void setIdOnEntity(model.Estudante entidade, Integer id) {
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