package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.application.Application;
import javafx.fxml.Initializable;
import javafx.scene.control.Tab;
import javafx.scene.Parent;

import java.net.URL;
import java.util.ResourceBundle;

import view.AppView;

public class AppController implements Initializable {
    @FXML
    private Tab tabEstudante;
    @FXML
    private Tab tabDisciplina;
    @FXML
    private Tab tabTurma;
    @FXML
    private Tab tabMatricula;
    
    AppView appView;
    
    public AppController() {
        this.appView = new AppView();
    }
    
    public static void main(String[] args) throws Exception {
        AppController appController = new AppController();
        appController.appView.run(args);
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            // Carrega estudante.fxml
            Parent estudanteContent = FXMLLoader.load(getClass().getResource("/view/estudante.fxml"));
            tabEstudante.setContent(estudanteContent);

            // Carrega disciplina.fxml
            Parent disciplinaContent = FXMLLoader.load(getClass().getResource("/view/disciplina.fxml"));
            tabDisciplina.setContent(disciplinaContent);
            
            // Carrega turma.fxml
            Parent turmaContent = FXMLLoader.load(getClass().getResource("/view/turma.fxml"));
            tabTurma.setContent(turmaContent);

            // Carrega matricula.fxml
            Parent matriculaContent = FXMLLoader.load(getClass().getResource("/view/matricula.fxml"));
            tabMatricula.setContent(matriculaContent);            
        } catch (Exception e) {
            e.printStackTrace();
        }       
    }
}