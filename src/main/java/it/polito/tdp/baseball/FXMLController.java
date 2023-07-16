package it.polito.tdp.baseball;

import java.net.URL;


import java.util.List;
import java.util.ResourceBundle;
import java.util.*;

import it.polito.tdp.baseball.model.GradoVertice;
//import it.polito.tdp.baseball.model.Grado;
import it.polito.tdp.baseball.model.Model;
import it.polito.tdp.baseball.model.People;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class FXMLController {
	
	private Model model;

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button btnConnesse;

    @FXML
    private Button btnCreaGrafo;

    @FXML
    private Button btnDreamTeam;

    @FXML
    private Button btnGradoMassimo;

    @FXML
    private TextArea txtResult;

    @FXML
    private TextField txtSalary;

    @FXML
    private TextField txtYear;

    
    
    @FXML
    void doCalcolaConnesse(ActionEvent event) {
    	
    	txtResult.appendText("Ci sono "+model.getComponentiConnesse()+"componenti connesse!");
    	
    }

    
    
    @FXML
    void doCreaGrafo(ActionEvent event) {
    	
    	int anno=Integer.parseInt(txtYear.getText());
    	double salario=Double.parseDouble(txtSalary.getText())*1000000;
    	
    	if(txtYear.getText().length()==0 || txtSalary.getText().length()==0 ) {
    		txtResult.appendText("Parametro mancante in salario o anno");
    		return;
    	}
    	if(anno<1871 || anno>2019) {
    		txtResult.appendText("inserire un anno valido");
    		return;
    	}
    	
    	model.creaGrafo(anno,salario);
    	
    	if(model.grafoCreato()!=false) {
    		txtResult.appendText(" Grafo creato!"+"\n");
    		txtResult.appendText(" Numero vertici: "+model.getNumeroVertici()+"\n");
    		txtResult.appendText(" Numero archi: "+model.getNumeroArchi()+"\n");
    		btnGradoMassimo.setDisable(false);
        	btnConnesse.setDisable(false);
        	btnDreamTeam.setDisable(false);
    	}
    	else {
    		txtResult.appendText(" Grafo non creato!"+"\n");
    	}
    	
    }

    
    @FXML
    void doDreamTeam(ActionEvent event) {
    	
    	this.model.calcolaDreamTeam();
    	this.txtResult.setText(String.format("\nIl salario del dream team è %4.2f M$\n",  this.model.getSalarioDreamTeam()));
    	this.txtResult.appendText("I " + this.model.getDreamTeam().size() + " giocatori del dream team sono\n");
    	for (People p : this.model.getDreamTeam()) {
    		this.txtResult.appendText(p + " " + this.model.getPeopleTeams(p));
    		this.txtResult.appendText(String.format("   %3.2f M$\n", this.model.getSalarioPlayer(p)));
    	}

    }

    
    @FXML
    void doGradoMassimo(ActionEvent event) {
    	
    	if(model.grafoCreato()==false) {
    		txtResult.appendText("Grafo non ancora creato");
    		return ;
    	}
    	
    	List<GradoVertice> listaGradiVertice = model.getVerticeGradoMassimo();
    	for(GradoVertice g : listaGradiVertice) {
    		txtResult.appendText("Il vertice con il grado massimo è"+g);
    	}
    	

    }

    
    @FXML
    void initialize() {
        assert btnConnesse != null : "fx:id=\"btnConnesse\" was not injected: check your FXML file 'Scene.fxml'.";
        assert btnCreaGrafo != null : "fx:id=\"btnCreaGrafo\" was not injected: check your FXML file 'Scene.fxml'.";
        assert btnDreamTeam != null : "fx:id=\"btnDreamTeam\" was not injected: check your FXML file 'Scene.fxml'.";
        assert btnGradoMassimo != null : "fx:id=\"btnGradoMassimo\" was not injected: check your FXML file 'Scene.fxml'.";
        assert txtResult != null : "fx:id=\"txtResult\" was not injected: check your FXML file 'Scene.fxml'.";
        assert txtSalary != null : "fx:id=\"txtSalary\" was not injected: check your FXML file 'Scene.fxml'.";
        assert txtYear != null : "fx:id=\"txtYear\" was not injected: check your FXML file 'Scene.fxml'.";

    }
    
    public void setModel(Model model) {
    	this.model = model;
    }

}
