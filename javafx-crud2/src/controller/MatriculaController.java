package controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Button;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.scene.control.ListCell;
import model.Repositorio;
import model.Matricula;
import model.Estudante;
import model.Turma;
import model.StatusMatricula;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.List;

public class MatriculaController extends AbstractCrudController<model.Matricula, view.Matricula, Integer> implements Initializable {

    @FXML
    private TableView<view.Matricula> tabela;

    @FXML
    private TableColumn<view.Matricula, Integer> idCol;
    @FXML
    private TableColumn<view.Matricula, String> estudanteCol;
    @FXML
    private TableColumn<view.Matricula, String> turmaCol;
    @FXML
    private TableColumn<view.Matricula, String> statusCol;

    @FXML
    private TextField idField;
    @FXML
    private ComboBox<Estudante> estudanteComboBox;
    @FXML
    private ComboBox<Turma> turmaComboBox;
    @FXML
    private ComboBox<StatusMatricula> statusComboBox;

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

    private static final Repositorio<Matricula, Integer> matriculaRepo = model.Repositorios.MATRICULA;
    private static final Repositorio<Estudante, Integer> estudanteRepo = model.Repositorios.ESTUDANTE;
    private static final Repositorio<Turma, Integer> turmaRepo = model.Repositorios.TURMA;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Configurar colunas
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        estudanteCol.setCellValueFactory(new PropertyValueFactory<>("estudanteNome"));
        turmaCol.setCellValueFactory(new PropertyValueFactory<>("turmaCodigo"));
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Estudantes
        List<Estudante> estudantes = estudanteRepo.loadAll();
        estudanteComboBox.setItems(FXCollections.observableArrayList(estudantes));
        estudanteComboBox.setCellFactory(cb -> new ListCell<>() {
            @Override
            protected void updateItem(Estudante e, boolean empty) {
                super.updateItem(e, empty);
                setText(empty || e == null ? "" : e.getNomeCompleto());
            }
        });
        estudanteComboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Estudante e, boolean empty) {
                super.updateItem(e, empty);
                setText(empty || e == null ? "" : e.getNomeCompleto());
            }
        });

        System.out.println("Estudantes carregados: " + estudantes.size());

        // Turmas
        List<Turma> turmas = turmaRepo.loadAll();
        turmaComboBox.setItems(FXCollections.observableArrayList(turmas));
        turmaComboBox.setCellFactory(cb -> new ListCell<>() {
            @Override
            protected void updateItem(Turma t, boolean empty) {
                super.updateItem(t, empty);
                setText(empty || t == null ? "" : t.getCodigo());
            }
        });
        turmaComboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Turma t, boolean empty) {
                super.updateItem(t, empty);
                setText(empty || t == null ? "" : t.getCodigo());
            }
        });

        System.out.println("Turmas carregadas: " + turmas.size());

        // Status enum
        statusComboBox.setItems(FXCollections.observableArrayList(StatusMatricula.values()));

        super.initialize();
    }

    @Override
    protected Repositorio<Matricula, Integer> getRepositorio() {
        return matriculaRepo;
    }

    @Override
    protected view.Matricula modelToView(Matricula m) {
        return new view.Matricula(
            m.getId(),
            m.getEstudante() != null ? m.getEstudante().getNomeCompleto() : "",
            m.getEstudante() != null ? m.getEstudante().getId() : 0,
            m.getTurma() != null ? m.getTurma().getCodigo() : "",
            m.getTurma() != null ? m.getTurma().getId() : 0,
            m.getStatus() != null ? m.getStatus().name() : ""
        );
    }

    @Override
    protected Matricula viewToModel() {
        Matricula m = new Matricula();
        m.setEstudante(estudanteComboBox.getValue());
        m.setTurma(turmaComboBox.getValue());
        m.setStatus(statusComboBox.getValue());
        return m;
    }

    @Override
    protected void preencherCampos(view.Matricula matricula) {
        idField.setText(String.valueOf(matricula.getId()));

        // Estudante
        Estudante e = estudanteRepo.loadFromId(matricula.getEstudanteId());
        estudanteComboBox.setValue(e);

        // Turma
        Turma t = turmaRepo.loadFromId(matricula.getTurmaId());
        turmaComboBox.setValue(t);

        System.out.println("Preenchendo campos: estudanteId=" + matricula.getEstudanteId() + " turmaId=" + matricula.getTurmaId());

        // Status
        if (matricula.getStatus() != null && !matricula.getStatus().isBlank()) {
            StatusMatricula status = StatusMatricula.valueOf(matricula.getStatus());
            statusComboBox.setValue(status);
        } else {
            statusComboBox.setValue(null);
        }
    }

    @Override
    protected void limparCampos() {
        idField.clear();
        estudanteComboBox.setValue(null);
        turmaComboBox.setValue(null);
        statusComboBox.setValue(null);
    }

    @Override
    protected void desabilitarCampos(boolean desabilitado) {
        estudanteComboBox.setDisable(desabilitado);
        turmaComboBox.setDisable(desabilitado);
        statusComboBox.setDisable(desabilitado);
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
    protected TableView<view.Matricula> getTabela() {
        return tabela;
    }

    @Override
    protected Integer getIdFromViewModel(view.Matricula viewModel) {
        return viewModel.getId();
    }

    @Override
    protected void setIdOnEntity(Matricula entidade, Integer id) {
        entidade.setId(id);
    }

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
