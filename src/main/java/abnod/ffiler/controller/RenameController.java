package abnod.ffiler.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Created by Abnod on 4/3/2017.
 */
public class RenameController {

    @FXML
    private TextField txtName;
    @FXML
    private Label labelExist;

    @FXML
    public void initialize(){
        txtName.setText(MainController.getSelectedFile().getFileName().toString());
    }

    public void actionRename(ActionEvent actionEvent) {
        Path selectedFile = MainController.getSelectedFile();
        if(!txtName.getCharacters().toString().equals(null) && !txtName.getCharacters().toString().equals("")){
            File checkFile = new File(selectedFile.getParent().toString()+"\\"+txtName.getCharacters().toString());
            System.out.println(checkFile);
            if(!checkFile.exists()){
                try {
                    Files.move(selectedFile, selectedFile.resolveSibling(txtName.getCharacters().toString()));
                    Node node = (Node) actionEvent.getSource();
                    Stage stage = (Stage) node.getScene().getWindow();
                    stage.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {labelExist.setText("File with this name already Exist!");}
        } else {labelExist.setText("File name must be not empty!");}
    }

    public void actionClose(ActionEvent actionEvent) {
        Node node = (Node) actionEvent.getSource();
        Stage stage = (Stage) node.getScene().getWindow();
        stage.close();
    }
}
