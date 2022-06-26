/**
 * Sample Skeleton for 'Scene.fxml' Controller Class
 */

package it.polito.tdp.yelp;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import it.polito.tdp.yelp.model.Business;
import it.polito.tdp.yelp.model.LocaleDistanza;
import it.polito.tdp.yelp.model.Model;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class FXMLController {
	
	private Model model;

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="btnCreaGrafo"
    private Button btnCreaGrafo; // Value injected by FXMLLoader

    @FXML // fx:id="btnDistante"
    private Button btnDistante; // Value injected by FXMLLoader

    @FXML // fx:id="btnCalcolaPercorso"
    private Button btnCalcolaPercorso; // Value injected by FXMLLoader

    @FXML // fx:id="txtX2"
    private TextField txtX2; // Value injected by FXMLLoader

    @FXML // fx:id="cmbCitta"
    private ComboBox<String> cmbCitta; // Value injected by FXMLLoader

    @FXML // fx:id="cmbB1"
    private ComboBox<Business> cmbB1; // Value injected by FXMLLoader

    @FXML // fx:id="cmbB2"
    private ComboBox<Business> cmbB2; // Value injected by FXMLLoader

    @FXML // fx:id="txtResult"
    private TextArea txtResult; // Value injected by FXMLLoader
    
    @FXML
    void doCreaGrafo(ActionEvent event) {
    	txtResult.clear();
    	String city = cmbCitta.getValue();
    	if(city==null) {
    		txtResult.setText("Selezionare una città");
    		return;
    	}
    	
    	String msg = model.creaGrafo(city);
    	txtResult.setText(msg);
    	
    	cmbB1.getItems().clear();
    	cmbB1.getItems().addAll(model.getVertici());
    	cmbB2.getItems().clear();
    	cmbB2.getItems().addAll(model.getVertici());
    }

    @FXML
    void doCalcolaLocaleDistante(ActionEvent event) {
    	txtResult.clear();
    	Business b1 = cmbB1.getValue();
    	if(b1==null) {
    		txtResult.setText("Selezionare un locale (b1)");
    		return;
    	}
    	
    	LocaleDistanza locale = model.getLocaleDistante(b1);
    	if(locale==null) {
    		txtResult.setText("Nessun risultato. Seleziona un altro locale");
    		return;
    	}
    	
    	txtResult.appendText("LOCALE PIU' DISTANTE:\n");
    	txtResult.appendText(String.format("%s = %f", locale.getB(), locale.getDistanza()));
    }

    @FXML
    void doCalcolaPercorso(ActionEvent event) {
    	txtResult.clear();
    	Business b1 = cmbB1.getValue();
    	Business b2 = cmbB2.getValue();
    	if(b1==null || b2==null) {
    		txtResult.setText("Selezionare due locali");
    		return;
    	}
    	
    	int soglia;
    	try {
    		soglia = Integer.parseInt(txtX2.getText());
    	} catch(NumberFormatException e) {
    		txtResult.setText("La soglia deve essere un numero intero");
    		return;
    	}
    	
    	List<Business> percorso = model.calcolaPercorso(b1, b2, soglia);
    	if(percorso==null) {
    		txtResult.setText("Nessun risultato");
    		return;
    	}
    	
    	txtResult.appendText("PERCORSO MIGLIORE:\n");
    	for(Business locale : percorso) {
    		txtResult.appendText(locale+"\n");
    	}
    	txtResult.appendText("KM PERCORSI: "+model.calcolaKmPercorsi(percorso));
    }


    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert btnCreaGrafo != null : "fx:id=\"btnCreaGrafo\" was not injected: check your FXML file 'Scene.fxml'.";
        assert btnDistante != null : "fx:id=\"btnDistante\" was not injected: check your FXML file 'Scene.fxml'.";
        assert btnCalcolaPercorso != null : "fx:id=\"btnCalcolaPercorso\" was not injected: check your FXML file 'Scene.fxml'.";
        assert txtX2 != null : "fx:id=\"txtX2\" was not injected: check your FXML file 'Scene.fxml'.";
        assert cmbCitta != null : "fx:id=\"cmbCitta\" was not injected: check your FXML file 'Scene.fxml'.";
        assert cmbB1 != null : "fx:id=\"cmbB1\" was not injected: check your FXML file 'Scene.fxml'.";
        assert cmbB2 != null : "fx:id=\"cmbB2\" was not injected: check your FXML file 'Scene.fxml'.";
        assert txtResult != null : "fx:id=\"txtResult\" was not injected: check your FXML file 'Scene.fxml'.";

    }
    
    public void setModel(Model model) {
    	this.model = model;
    	cmbCitta.getItems().addAll(model.getCities());
    }
}
