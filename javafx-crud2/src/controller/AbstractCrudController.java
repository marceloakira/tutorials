package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.TableView;

/**
 * Controlador base genérico para operações CRUD.
 *
 * @param <E>  Entidade de domínio
 * @param <V>  ViewModel usado na TableView
 * @param <ID> Tipo da chave primária
 */
public abstract class AbstractCrudController<E, V, ID> {

    // Fornece o repositório específico
    protected abstract model.Repositorio<E, ID> getRepositorio();

    // Converte entidade em ViewModel
    protected abstract V modelToView(E entidade);

    // Converte dados da tela em entidade
    protected abstract E viewToModel();

    // Preenche os campos da tela com dados
    protected abstract void preencherCampos(V item);

    // Limpa os campos da tela
    protected abstract void limparCampos();

    // Habilita ou desabilita os campos
    protected abstract void desabilitarCampos(boolean desabilitado);

    // Habilita ou desabilita botões
    protected abstract void desabilitarBotoes(
            boolean adicionar, boolean atualizar,
            boolean deletar, boolean cancelar, boolean salvar);

    // Retorna a tabela
    protected abstract TableView<V> getTabela();

    // Extrai a chave primária do ViewModel
    protected abstract ID getIdFromViewModel(V viewModel);

    // Seta o ID na entidade
    protected abstract void setIdOnEntity(E entidade, ID id);

    // Inicializa dados e vincula seleção
    public void initialize() {
        getTabela().setItems(loadAll());
        getTabela().getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        preencherCampos(newSelection);
                        desabilitarBotoes(false, false, false, true, true);
                    }
                });
        desabilitarCampos(true);
    }

    // Carrega todos os registros
    protected ObservableList<V> loadAll() {
        ObservableList<V> lista = FXCollections.observableArrayList();
        for (E entidade : getRepositorio().loadAll()) {
            lista.add(modelToView(entidade));
        }
        return lista;
    }

    // Ação Adicionar
    public void onAdicionar() {
        getTabela().getSelectionModel().clearSelection();
        desabilitarCampos(false);
        desabilitarBotoes(true, true, true, false, false);
        limparCampos();
    }

    // Ação Salvar
    public void onSalvar() {
        V selecionado = getTabela().getSelectionModel().getSelectedItem();
        try {
            E entidade = viewToModel();
    
            if (selecionado != null) {
                // Update
                ID id = getIdFromViewModel(selecionado);
                setIdOnEntity(entidade, id);
                getRepositorio().update(entidade);
                V atualizado = modelToView(entidade);
                int index = getTabela().getItems().indexOf(selecionado);
                getTabela().getItems().set(index, atualizado);
                getTabela().getSelectionModel().select(atualizado);
            } else {
                // Create
                E salvo = getRepositorio().create(entidade);
                V viewItem = modelToView(salvo);
                getTabela().getItems().add(viewItem);
                getTabela().getSelectionModel().select(viewItem);
            }
    
            desabilitarCampos(true);
            desabilitarBotoes(false, true, true, true, true);
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Erro ao salvar: " + e.getMessage()).show();
        }
    }

    // Ação Atualizar
    public void onAtualizar() {
        V selecionado = getTabela().getSelectionModel().getSelectedItem();
        if (selecionado == null) {
            new Alert(Alert.AlertType.WARNING, "Nenhum item selecionado para atualizar.").show();
            return;
        }
        try {
            E entidade = viewToModel();
            ID id = getIdFromViewModel(selecionado);
            setIdOnEntity(entidade, id);
            getRepositorio().update(entidade);
            V atualizado = modelToView(entidade);
            int index = getTabela().getItems().indexOf(selecionado);
            getTabela().getItems().set(index, atualizado);
            getTabela().getSelectionModel().select(atualizado);
            desabilitarCampos(false);
            desabilitarBotoes(true, true, true, false, false);
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Erro ao atualizar: " + e.getMessage()).show();
        }
    }
    
    public void onDeletar() {
        V selecionado = getTabela().getSelectionModel().getSelectedItem();
        if (selecionado == null) {
            new Alert(Alert.AlertType.WARNING, "Nenhum item selecionado para deletar.").show();
            return;
        }
        try {
            ID id = getIdFromViewModel(selecionado);
    
            // Carrega a entidade completa do banco
            E entidade = getRepositorio().loadFromId(id);
    
            if (entidade == null) {
                new Alert(Alert.AlertType.WARNING, "Registro não encontrado no banco.").show();
                return;
            }
    
            // Deleta
            getRepositorio().delete(entidade);
    
            // Remove da tabela
            getTabela().getItems().remove(selecionado);
            limparCampos();
            desabilitarCampos(true);
            desabilitarBotoes(false, true, true, true, true);
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Erro ao deletar: " + e.getMessage()).show();
        }
    }


    // Ação Cancelar
    public void onCancelar() {
        desabilitarCampos(true);
        desabilitarBotoes(false, true, true, true, true);
        limparCampos();
        getTabela().getSelectionModel().clearSelection();
    }
}
