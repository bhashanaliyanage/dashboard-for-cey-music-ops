package com.example.song_finder_fx;

import com.example.song_finder_fx.Controller.AlertBuilder;
import com.example.song_finder_fx.Controller.SceneController;
import com.example.song_finder_fx.Model.Ingest;
import com.example.song_finder_fx.Model.IngestCSVData;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class ControllerUnApprovedIngestEntry {

    @FXML
    public Label lblIngestID;

    @FXML
    private Label lblAssetCount;

    @FXML
    private Label lblDate;

    @FXML
    private Label lblImportedBy;

    @FXML
    private Label lblSongName;

    public static Ingest ingest;

    @FXML
    void onApprove(ActionEvent event) {
        // AlertBuilder.sendInfoAlert("Quick Approval", "Quick Approval Comming S");
    }

    @FXML
    void onIngestClick(MouseEvent event) {
        System.out.println("ControllerUnApprovedIngestEntry.onIngestClick");


        try {
            ingest = DatabasePostgres.getIngest(Integer.parseInt(lblIngestID.getText()));

            if (ingest != null) {
                List<IngestCSVData> dataList = ingest.getIngestCSVDataList();

                Node node = SceneController.loadLayout("layouts/ingests/ingest-view.fxml");
                UIController.mainVBoxStatic.getChildren().setAll(node);

                /*for (IngestCSVData data : dataList) {

                }*/

                Label lblIngestName = (Label) node.lookup("#lblIngestName");
                lblIngestName.setText("> " + ingest.getName());

                TableView<IngestCSVData> tableView = (TableView<IngestCSVData>) node.lookup("#tableIngest");

                // Define the columns
                TableColumn<IngestCSVData, String> albumTitleCol = new TableColumn<>("Album Title");
                albumTitleCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAlbumTitle()));

                TableColumn<IngestCSVData, String> upcCol = new TableColumn<>("UPC");
                upcCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getUpc()));

                TableColumn<IngestCSVData, String> catalogNumberCol = new TableColumn<>("Catalog Number");
                catalogNumberCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCatalogNumber()));

                TableColumn<IngestCSVData, String> primaryArtistCol = new TableColumn<>("Track Primary Artist");
                primaryArtistCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTrackPrimaryArtist()));

                TableColumn<IngestCSVData, String> isrcCol = new TableColumn<>("ISRC");
                isrcCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getIsrc()));

                TableColumn<IngestCSVData, String> trackTitleCol = new TableColumn<>("Track Title");
                trackTitleCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTrackTitle()));

                TableColumn<IngestCSVData, String> composerCol = new TableColumn<>("Composer");
                composerCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getComposer()));

                TableColumn<IngestCSVData, String> lyricistsCol = new TableColumn<>("Lyricists");
                lyricistsCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getLyricist()));

                // Add columns to the table
                tableView.getColumns().setAll(albumTitleCol, upcCol, catalogNumberCol, isrcCol, trackTitleCol, primaryArtistCol, composerCol, lyricistsCol);

                // Add data to the table
                ObservableList<IngestCSVData> observableDataList = FXCollections.observableArrayList(dataList);
                tableView.getItems().setAll(observableDataList);
            }
        } catch (IOException e) {
            AlertBuilder.sendErrorAlert("Error", "Error Initializing UI", e.toString());
        } catch (SQLException e) {
            AlertBuilder.sendErrorAlert("Error", "Error Getting Ingests", e.toString());
        }
    }

}
