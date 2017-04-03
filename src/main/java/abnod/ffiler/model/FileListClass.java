package abnod.ffiler.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.File;

/**
 * Created by Abnod on 4/3/2017.
 */
public class FileListClass {

    private ObservableList<File> filesList = FXCollections.observableArrayList();
    private ObservableList<File> volumesList = FXCollections.observableArrayList();

    public ObservableList<File>  getVolumes(){volumesList.clear(); volumesList.addAll(File.listRoots()); return volumesList;}

    public ObservableList<File>  getFiles(File file){filesList.clear(); filesList.addAll(file.listFiles()); return filesList;}

}
