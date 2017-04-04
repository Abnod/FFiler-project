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
import org.codehaus.plexus.util.FileUtils;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.EnumSet;
import java.util.Optional;

public class MainController {

    private FileListClass fileListClass = new FileListClass();
    private FileListClass fileListClassRight = new FileListClass();
    private ObservableList<File> volumesList = fileListClass.getVolumes();
    private ObservableList<File> filesList;
    private ObservableList<File> filesListRight;
    private File lastPathFile;
    private File lastPathFileRight;
    private static File foldertoCreate;
    public static Path selectedFile;

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
    private Button btnMove;
    @FXML
    private Button btnMoveRight;
    @FXML
    private Button btnCopy;
    @FXML
    private Button btnCopyRight;

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
                    dialogWindow(actionEvent, "/fxml/AddFrame.fxml", "Create newfolder");
                    fillTable(tableFiles, lastPathFile);
                    break;
                }
                case "btnAddRight": {
                    setFoldertoCreate(lastPathFileRight);
                    dialogWindow(actionEvent, "/fxml/AddFrame.fxml", "Create newfolder");
                    fillTable(tableFilesRight, lastPathFileRight);
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
                    if(tableFiles.getSelectionModel().getSelectedItem() !=null){
                        setSelectedFile(((File) tableFiles.getSelectionModel().getSelectedItem()).toPath());
                        dialogWindow(actionEvent,"/fxml/RenameFrame.fxml","Rename file or folder");
                        fillTable(tableFiles, lastPathFile);
                    }
                    break;
                }
                case "btnRenameRight": {
                    if(tableFilesRight.getSelectionModel().getSelectedItem() !=null){
                        setSelectedFile(((File) tableFilesRight.getSelectionModel().getSelectedItem()).toPath());
                        dialogWindow(actionEvent,"/fxml/RenameFrame.fxml","Rename file or folder");
                        fillTable(tableFilesRight, lastPathFileRight);
                    }
                    break;
                }
            }
    }

    public void deleteEntries(ActionEvent actionEvent){
        Button button = (Button) actionEvent.getSource();
        switch (button.getId()){
            case "btnDelete" : {
                ObservableList<File> removeList = tableFiles.getSelectionModel().getSelectedItems();
                if (!removeList.isEmpty() && removeList != null){
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Remove files");
                    alert.setHeaderText("Are you sure?");
                    alert.setContentText("Remove " + removeList.size() + "files?");

                    Optional<ButtonType> result = alert.showAndWait();
                    if (result.get() == ButtonType.OK){
                        for(File file : removeList){
                            try {
                                if(file.isDirectory()){FileUtils.deleteDirectory(file);}
                                else if(file.isFile()){FileUtils.fileDelete(file.toString());}
                            } catch (IOException e) {
                                errorWindow("Deletion Error", null, "Not all files have been deleted...");
                            }
                        }
                        fillTable(tableFiles,lastPathFile);
                    } else {
                        alert.close();
                    }
                }
                break;
            }
            case "btnDeleteRight" : {
                ObservableList<File> removeList = tableFilesRight.getSelectionModel().getSelectedItems();
                if (!removeList.isEmpty() && removeList != null){
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Remove files");
                    alert.setHeaderText("Are you sure?");
                    alert.setContentText("Remove " + removeList.size() + "files?");

                    Optional<ButtonType> result = alert.showAndWait();
                    if (result.get() == ButtonType.OK){
                        for(File file : removeList){
                            try {
                                if(file.isDirectory()){FileUtils.deleteDirectory(file);}
                                else if(file.isFile()){FileUtils.fileDelete(file.toString());}
                            } catch (IOException e) {
                                errorWindow("Deletion Error", null, "Not all files have been deleted...");
                            }
                        }
                        fillTable(tableFilesRight,lastPathFileRight);
                    } else {
                        alert.close();
                    }
                }
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
                    lastPathFile = (File)cbVolumes.getValue();
                    currentPath.setText(cbVolumes.getValue().toString());
                    break;
                }
                case "btnVolumeRight" :{
                    filesListRight = fileListClassRight.getFiles((File) cbVolumesRight.getValue());
                    tableFilesRight.setItems(filesListRight);
                    lastPathFileRight = (File) cbVolumesRight.getValue();
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

    private void updateCountLabel(Label label, ObservableList<File> filesList) {
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

    private void navigateFiles(TableView tableView, ObservableList<File> observableList, FileListClass fileListClass) throws IOException {
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

    private static void setFoldertoCreate(File foldertoCreate) {
        MainController.foldertoCreate = foldertoCreate;
    }

    private void dialogWindow(ActionEvent actionEvent, String pathToFxml, String titleText){
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
            stage.showAndWait();
        } catch (IOException e){
            e.printStackTrace();
        }
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

    private void fillTable(TableView tableView, File file){
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

    public static Path getSelectedFile() {
        return selectedFile;
    }

    private static void setSelectedFile(Path selectedFile) {
        MainController.selectedFile = selectedFile;
    }

    public void copyEntries(ActionEvent actionEvent){
        Button button = (Button) actionEvent.getSource();

        switch (button.getId()){
            case "btnCopy" : {
                ObservableList<File> copyList = tableFiles.getSelectionModel().getSelectedItems();
                btnCopy.setText("Copying...");
                copy(copyList, lastPathFile, lastPathFileRight);
                fillTable(tableFilesRight, lastPathFileRight);
                btnCopy.setText("Copy");
                break;
            }
            case "btnCopyRight" : {
                ObservableList<File> copyList = tableFilesRight.getSelectionModel().getSelectedItems();
                btnCopyRight.setText("Copying...");
                copy(copyList, lastPathFileRight, lastPathFile);
                fillTable(tableFiles, lastPathFile);
                btnCopyRight.setText("Copy");
                break;
            }
        }
    }

    private void copy(ObservableList<File> list, File pathFrom, File pathTo){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Copying");
        alert.setHeaderText("Prepare to copy " + list.size() + " objects from " + pathFrom + " to " + pathTo);
        alert.setContentText("Choose copy mode option.");

        ButtonType buttonTypeOne = new ButtonType("Keep existing files");
        ButtonType buttonTypeTwo = new ButtonType("Override existing files");
        ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeTwo, buttonTypeCancel);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == buttonTypeOne){
            for(File sourceFile : list){
                File checkFile = new File (pathTo.toString() + "\\" + sourceFile.getName());
                if(!checkFile.exists()){
                    try {
                        Files.walkFileTree(sourceFile.toPath(), EnumSet.of(FileVisitOption.FOLLOW_LINKS), Integer.MAX_VALUE,
                                new SimpleFileVisitor<Path>() {
                                    @Override
                                    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException  {
                                        Path target = checkFile.toPath().resolve(sourceFile.toPath().relativize(dir));
                                        try {
                                            Files.copy(dir, target);
                                        } catch (FileAlreadyExistsException e) {
                                            if (!Files.isDirectory(target))
                                                throw e;
                                        }
                                        return FileVisitResult.CONTINUE;
                                    }
                                    @Override
                                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                                        Files.copy(file, checkFile.toPath().resolve(sourceFile.toPath().relativize(file)));
                                        return FileVisitResult.CONTINUE;
                                    }
                                });
                    } catch (IOException e) {
                        errorWindow("Copy Error", null, "Some files were not copied...");
                    }
                }
            }
        } else if (result.get() == buttonTypeTwo) {
            for(File sourceFile : list){
                File checkFile = new File (pathTo.toString() + "\\" + sourceFile.getName());
                try {
                    Files.walkFileTree(sourceFile.toPath(), EnumSet.of(FileVisitOption.FOLLOW_LINKS), Integer.MAX_VALUE,
                            new SimpleFileVisitor<Path>() {
                                @Override
                                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException  {
                                    Path target = checkFile.toPath().resolve(sourceFile.toPath().relativize(dir));
                                    try {
                                        Files.copy(dir, target);
                                    } catch (FileAlreadyExistsException e) {
                                        if (!Files.isDirectory(target))
                                            throw e;
                                    }
                                    return FileVisitResult.CONTINUE;
                                }
                                @Override
                                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                                    Files.copy(file, checkFile.toPath().resolve(sourceFile.toPath().relativize(file)));
                                    return FileVisitResult.CONTINUE;
                                }
                            });
                    } catch (IOException e) {
                        errorWindow("Copy Error", null, "Some files were not copied...");
                    }
            }
        } else {
            alert.close();
        }
    }

    public void moveEntries(ActionEvent actionEvent){
        Button button = (Button) actionEvent.getSource();

        switch (button.getId()){
            case "btnMove" : {
                ObservableList<File> copyList = tableFiles.getSelectionModel().getSelectedItems();
                btnMove.setText("Moving...");
                move(copyList, lastPathFile, lastPathFileRight);
                btnCopy.setText("Move");
                break;
            }
            case "btnMoveRight" : {
                ObservableList<File> copyList = tableFilesRight.getSelectionModel().getSelectedItems();
                btnMoveRight.setText("Moving...");
                move(copyList, lastPathFileRight, lastPathFile);
                btnCopy.setText("Move");
                break;
            }
        }
        fillTable(tableFilesRight, lastPathFileRight);
        fillTable(tableFiles, lastPathFile);
    }

    private void move(ObservableList<File> list, File pathFrom, File pathTo){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Move");
        alert.setHeaderText("Prepare to move " + list.size() + " objects from " + pathFrom + " to " + pathTo);
        alert.setContentText("Choose moving mode option.");

        ButtonType buttonTypeOne = new ButtonType("Keep existing files");
        ButtonType buttonTypeTwo = new ButtonType("Override existing files");
        ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeTwo, buttonTypeCancel);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == buttonTypeOne){
            for(File sourceFile : list){
                File checkFile = new File (pathTo.toString() + "\\" + sourceFile.getName());
                if(!checkFile.exists()){
                    try {
                        Files.walkFileTree(sourceFile.toPath(), EnumSet.of(FileVisitOption.FOLLOW_LINKS), Integer.MAX_VALUE,
                                new SimpleFileVisitor<Path>() {
                                    @Override
                                    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException  {
                                        Path target = checkFile.toPath().resolve(sourceFile.toPath().relativize(dir));
                                        try {
                                            Files.copy(dir, target);
                                        } catch (FileAlreadyExistsException e) {
                                            if (!Files.isDirectory(target))
                                                throw e;
                                        }
                                        return FileVisitResult.CONTINUE;
                                    }
                                    @Override
                                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                                        Files.copy(file, checkFile.toPath().resolve(sourceFile.toPath().relativize(file)));
                                        return FileVisitResult.CONTINUE;
                                    }
                                });
                        if(sourceFile.isDirectory()){FileUtils.deleteDirectory(sourceFile);}
                        else if(sourceFile.isFile()){FileUtils.fileDelete(sourceFile.toString());}
                    } catch (IOException e) {
                        errorWindow("Move Error", null, "Some files were not moved...");
                    }
                }
            }
        } else if (result.get() == buttonTypeTwo) {
            for(File sourceFile : list){
                File checkFile = new File (pathTo.toString() + "\\" + sourceFile.getName());
                try {
                    Files.walkFileTree(sourceFile.toPath(), EnumSet.of(FileVisitOption.FOLLOW_LINKS), Integer.MAX_VALUE,
                            new SimpleFileVisitor<Path>() {
                                @Override
                                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException  {
                                    Path target = checkFile.toPath().resolve(sourceFile.toPath().relativize(dir));
                                    try {
                                        Files.copy(dir, target);
                                    } catch (FileAlreadyExistsException e) {
                                        if (!Files.isDirectory(target))
                                            throw e;
                                    }
                                    return FileVisitResult.CONTINUE;
                                }
                                @Override
                                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                                    Files.copy(file, checkFile.toPath().resolve(sourceFile.toPath().relativize(file)));
                                    return FileVisitResult.CONTINUE;
                                }
                            });
                    if(sourceFile.isDirectory()){FileUtils.deleteDirectory(sourceFile);}
                    else if(sourceFile.isFile()){FileUtils.fileDelete(sourceFile.toString());}
                } catch (IOException e) {
                    errorWindow("Move Error", null, "Some files were not moved...");
                }
            }
        } else {
            alert.close();
        }
    }

    private void errorWindow(String title, String header, String text){
        Alert alert2 = new Alert(Alert.AlertType.ERROR);
        alert2.setTitle(title);
        alert2.setHeaderText(header);
        alert2.setContentText(text);
        alert2.showAndWait();
    }
}
