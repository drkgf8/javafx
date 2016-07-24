/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package notecards3;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

/**
 *
 * @author derek
 */
public class noteController implements Initializable {
    
    @FXML
    private BorderPane borderPane;
    
    private Stage stage;
    
  
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
    public void ready(Stage stage){
    this.stage = stage;
      
    Group g = new Group();
    g.setId("notecardsGroup");
    
    for (int i = 0; i < 5; i++) {
        g.getChildren().add(createRec(i));
        }
        
        borderPane.setCenter(g);
      
      
      
  }
    
    @FXML
    public Rectangle createRec(int i) {
        Rectangle r = new Rectangle();
        r.setY(3*i+200);
        r.setX(3*i+200);
        r.setWidth(100);
        r.setHeight(200);
        r.setFill(Color.web("#1f6fd9"));
        r.setStyle("-fx-border:2px solid black;");
        
        return r;
    }
    
  
    
    
    
}
