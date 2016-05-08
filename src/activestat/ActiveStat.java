/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package activestat;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import jfxtras.scene.control.agenda.Agenda;

/**
 *
 * @author qjot
 */
public class ActiveStat extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
        AnchorPane root = FXMLLoader.load(getClass().getResource("/view/MainView.fxml"));
       Agenda ag= new Agenda();
       //Node t=  root.getChildren().get(0);
       
              //  root.getChildren().add(ag);
       // System.out.println(t.get);
        Parent p=root;
        Scene scene = new Scene(ag);     
        
         
        stage.setScene(scene);
        
        stage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
