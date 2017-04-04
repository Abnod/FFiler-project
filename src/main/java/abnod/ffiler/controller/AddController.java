package abnod.ffiler.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.File;

/**
 * Created by Abnod on 4/3/2017.
 */
public class AddController {

    @FXML
    private TextField txtName;
    @FXML
    private Label labelExist;

    public void actionClose(ActionEvent actionEvent) {
        Node node = (Node) actionEvent.getSource();
        Stage stage = (Stage) node.getScene().getWindow();
        stage.close();
    }

    public void actionCreate(ActionEvent actionEvent) {
        File prefix = MainController.getFoldertoCreate();
        String folderName = prefix + "\\" + txtName.getCharacters().toString();
        File dirrectory = new File(folderName);
        if(!dirrectory.exists()){
            dirrectory.mkdir();
            Node node = (Node) actionEvent.getSource();
            Stage stage = (Stage) node.getScene().getWindow();
            stage.close();}
        else {labelExist.setText("Directory already Exist!");}
    }
}
