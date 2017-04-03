package abnod.ffiler.controller;

import abnod.ffiler.model.FileListClass;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class MainController {

    private FileListClass fileListClass = new FileListClass();
    private FileListClass fileListClassRight = new FileListClass();
    private ObservableList<File> volumesList = fileListClass.getVolumes();
    private ObservableList<File> filesList;
    private ObservableList<File> filesListRight;
    private File lastPathFile;
    private File lastPathFileRight;
    private static File foldertoCreate;
    private static ObservableList<File> listToUpdate;

    @FXML
    private TableView tableFiles;
    @FXML
    private TableView tableFilesRight;
    @FXML
    private TableColumn<File, String> columnName;
    @FXML
    private TableColumn<File, String> columnNameRight;
    @FXML
    private TableColumn<File, String> columnSize;
    @FXML
    private TableColumn<File, String> columnSizeRight;
    @FXML
    private ChoiceBox cbVolumes;
    @FXML
    private ChoiceBox cbVolumesRight;
    @FXML
    private Label labelCount;
    @FXML
    private Label labelCountRight;
    @FXML
    private TextField currentPath;
    @FXML
    private TextField currentPathRight;

    @FXML
    public void initialize(){
        columnName.setCellValueFactory(new PropertyValueFactory<>("name"));
        columnSize.setCellValueFactory(param -> {
            if(param.getValue().isDirectory())
            {
                return new ReadOnlyStringWrapper("<DIR>");
            } else{
                long size = param.getValue().length();
                size = size/1024;
                return new ReadOnlyStringWrapper(String.valueOf(size) + "Kb");
            }
        });
        columnNameRight.setCellValueFactory(new PropertyValueFactory<>("name"));
        columnSizeRight.setCellValueFactory(param -> {
            if(param.getValue().isDirectory())
            {
                return new ReadOnlyStringWrapper("<DIR>");
            } else{
                long size = param.getValue().length();
                size = size/1024;
                return new ReadOnlyStringWrapper(String.valueOf(size) + "Kb");
            }
        });
        cbVolumes.setItems(volumesList);
        cbVolumesRight.setItems(volumesList);
        cbVolumes.setValue(volumesList.get(0));
        cbVolumesRight.setValue(volumesList.get(0));
        lastPathFile = volumesList.get(0);
        lastPathFileRight = volumesList.get(0);
        currentPath.setText(volumesList.get(0).toString());
        currentPathRight.setText(volumesList.get(0).toString());

        filesList = fileListClass.getFiles((File)cbVolumes.getValue());
        filesList.addListener((ListChangeListener<File>) c -> updateCountLabel(labelCount, filesList));
        updateCountLabel(labelCount, filesList);
        tableFiles.setItems(filesList);
        filesListRight = fileListClassRight.getFiles((File)cbVolumesRight.getValue());
        filesListRight.addListener((ListChangeListener<File>) c -> updateCountLabel(labelCountRight, filesListRight));
        updateCountLabel(labelCountRight, filesListRight);
        tableFilesRight.setItems(filesListRight);
        tableFiles.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        tableFilesRight.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    public void closeProgram(){
        Platform.exit();
    }

    public void buttonAction(ActionEvent actionEvent){

        Button button = (Button) actionEvent.getSource();
            switch (button.getId()) {
                case "btnAdd": {
                    setFoldertoCreate(lastPathFile);
                    setListToUpdate(filesList);
                    dialogWindow(actionEvent, "../fxml/AddFrame.fxml", "Create newfolder");
                    break;
                }
                case "btnAddRight": {
                    setFoldertoCreate(lastPathFileRight);
                    setListToUpdate(filesListRight);
                    dialogWindow(actionEvent, "../fxml/AddFrame.fxml", "Create newfolder");
                    break;
                }
                case "btnUpdate" : {
                    fillTable(tableFiles, lastPathFile);
                    break;
                }
                case "btnUpdateRight" : {
                    fillTable(tableFilesRight, lastPathFileRight);
                    break;
                }
                case "btnRename": {
                    dialogWindow(actionEvent,"../fxml/RenameFrame.fxml","Rename file or folder");
                   // Path selectedFile = ((File) tableFiles.getSelectionModel().getSelectedItem()).toPath();
                   // Files.move(selectedFile, selectedFile.resolveSibling(""));
                    //System.out.println(selectedFile.getName() " " + selectedFile.length());
                    break;
                }
                case "btnRenameRight": {
                    File selectedFile = (File) tableFilesRight.getSelectionModel().getSelectedItem();
                    System.out.println(selectedFile.getName() + selectedFile.length());
                    break;
                }
            }
    }

    public void updateVolumes(ActionEvent actionEvent) {
        Button button = (Button) actionEvent.getSource();
        try{
            switch (button.getId()){
                case "btnVolume" : {
                    filesList = fileListClass.getFiles((File) cbVolumes.getValue());
                    tableFiles.setItems(filesList);
                    currentPath.setText(cbVolumes.getValue().toString());
                    break;
                }
                case "btnVolumeRight" :{
                    filesListRight = fileListClassRight.getFiles((File) cbVolumesRight.getValue());
                    tableFilesRight.setItems(filesListRight);
                    currentPathRight.setText(cbVolumesRight.getValue().toString());
                    break;
                }
            }
        } catch (NullPointerException npe){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Access error");
            alert.setHeaderText("Error getting list of files");
            alert.setContentText("Select another drive");

            alert.showAndWait();
        }
    }

    public void updateCountLabel(Label label, ObservableList<File> filesList) {
            label.setText("Object in directory: " + filesList.size());
    }

    public void enterFile(MouseEvent mouseEvent) {
        if (mouseEvent.isPrimaryButtonDown() && (mouseEvent.getClickCount() == 2)){
            try{
                TableView tbv = (TableView) mouseEvent.getSource();
                switch (tbv.getId()){
                    case "tableFiles" : {
                       navigateFiles(tableFiles,filesList, fileListClass);
                       break;
                    }
                    case "tableFilesRight" : {
                        navigateFiles(tableFilesRight, filesListRight, fileListClassRight);
                        break;
                    }
                }
            } catch (NullPointerException npe){
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Access Error");
                alert.setHeaderText(null);
                alert.setContentText("Error getting access to file or folder");

                alert.showAndWait();
            } catch (IOException ex){}
        }
    }

    public void navigateFiles(TableView tableView, ObservableList<File> observableList, FileListClass fileListClass) throws IOException {
        File selectedFile = (File) tableView.getSelectionModel().getSelectedItem();
        if(selectedFile.isFile()){
            Desktop.getDesktop().open(selectedFile);
        } else {
            if(tableView.getId().equals("tableFiles")){lastPathFile = selectedFile; currentPath.setText(lastPathFile.toString());}
            else if (tableView.getId().equals("tableFilesRight")){lastPathFileRight = selectedFile; currentPathRight.setText(lastPathFileRight.toString());}
            observableList = fileListClass.getFiles(selectedFile);
            tableView.setItems(observableList);
        }
    }

    public void upperDir(ActionEvent actionEvent) {
        Button button = (Button) actionEvent.getSource();
        try {
            switch (button.getId()) {
                case "btnBack": {
                    if(lastPathFile.getParentFile() == null){return;}
                    else{
                        File newLastPathFile = lastPathFile.getParentFile();
                        lastPathFile = newLastPathFile;
                        fillTable(tableFiles, lastPathFile);
                        currentPath.setText(lastPathFile.toString());
                        break;
                    }
                }
                case "btnBackRight": {
                    if(lastPathFileRight.getParentFile() == null){return;}
                    else{
                        File newLastPathFile = lastPathFileRight.getParentFile();
                        lastPathFileRight = newLastPathFile;
                        fillTable(tableFilesRight, lastPathFileRight);
                        currentPathRight.setText(lastPathFileRight.toString());
                        break;
                    }
                }
            }
        } catch (Exception e) {}
    }

    public static File getFoldertoCreate() {
        return foldertoCreate;
    }

    public static void setFoldertoCreate(File foldertoCreate) {
        MainController.foldertoCreate = foldertoCreate;
    }

    public void dialogWindow(ActionEvent actionEvent, String pathToFxml, String titleText){
        try{
            Stage stage = new Stage();
            Parent root = FXMLLoader.load(getClass().getResource(pathToFxml));
            stage.setTitle(titleText);
            stage.setMinHeight(100);
            stage.setMinWidth(300);
            stage.setResizable(false);
            stage.setScene(new Scene(root));
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(((Node)actionEvent.getSource()).getScene().getWindow());
            stage.show();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public static ObservableList<File> getFilelistToUpdate(){
        return listToUpdate;
    }

    public static void setListToUpdate(ObservableList<File> listToUpdate) {
        MainController.listToUpdate = listToUpdate;
    }

    public void updateList(ActionEvent actionEvent){
        Button button = (Button) actionEvent.getSource();
        switch (button.getId()) {
            case "btnUpdate" : {
                fillTable(tableFiles, lastPathFile);
                break;
            }
            case "btnUpdateRight" : {
                fillTable(tableFilesRight, lastPathFileRight);
                break;
            }
        }
    }

    public void fillTable(TableView tableView, File file){
        switch (tableView.getId()){
            case "tableFiles" : {
                filesList = fileListClass.getFiles(file);
                tableFiles.setItems(filesList);
                break;
            }
            case "tableFilesRight" : {
                filesListRight = fileListClassRight.getFiles(file);
                tableFilesRight.setItems(filesListRight);
                break;
            }
        }
    }
}
