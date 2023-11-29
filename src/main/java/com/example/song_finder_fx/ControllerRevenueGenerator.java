package com.example.song_finder_fx;

import com.opencsv.exceptions.CsvValidationException;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;

public class ControllerRevenueGenerator {
    public Button btnLoadReport;
    public Label lblAsset01;
    public Label lblAsset02;
    public Label lblAsset03;
    public Label lblAsset04;
    public Label lblAsset05;
    public Label lblAsset01Streams;
    public Label lblAsset02Streams;
    public Label lblAsset03Streams;
    public Label lblAsset04Streams;
    public Label lblAsset05Streams;
    public Label lblTotalAssets;
    public Label lblCountry01;
    public Label lblCountry02;
    public Label lblCountry03;
    public Label lblCountry04;
    public Label lblCountry05;
    public Label lblCountry01Streams;
    public Label lblCountry02Streams;
    public Label lblCountry03Streams;
    public Label lblCountry04Streams;
    public Label lblCountry05Streams;
    public Label lblDSP01;
    public Label lblDSP02;
    public Label lblDSP03;
    public Label lblDSP04;
    public Label lblAmountDSP01;
    public Label lblAmountDSP02;
    public Label lblAmountDSP03;
    public Label lblAmountDSP04;
    public ImageView imgDSP01;
    public ImageView imgDSP02;
    public ImageView imgDSP03;
    public ImageView imgDSP04;
    private UIController mainUIController = null;

    public ControllerRevenueGenerator(UIController uiController) {
        mainUIController = uiController;
    }

    public void loadRevenueGenerator() throws IOException, SQLException, ClassNotFoundException {
        FXMLLoader loader = new FXMLLoader(ControllerSettings.class.getResource("layouts/revenue-generator.fxml"));
        loader.setController(this);
        Parent newContent = loader.load();

        mainUIController.mainVBox.getChildren().clear();
        mainUIController.mainVBox.getChildren().add(newContent);


        Task<Void> task;

        task = new Task<>() {
            @Override
            protected Void call() {
                Platform.runLater(() -> {
                    try {
                        loadTopStreamedAssets();
                        loadTotalAssetCount();
                        loadTop5Territories();
                        loadTop4DSPs();
                    } catch (SQLException | ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                });
                return null;
            }
        };

        Thread t = new Thread(task);
        t.start();
    }

    private void loadTop5Territories() throws SQLException, ClassNotFoundException {
        ResultSet rs = DatabaseMySQL.getTop5Territories();
        DecimalFormat df = new DecimalFormat("0.00");
        double revenue;
        String currency;
        ItemSwitcher itemSwitcher = new ItemSwitcher();

        rs.next();
        lblCountry01.setText(itemSwitcher.setCountry(rs.getString(1)));
        revenue = rs.getDouble(2);
        currency = rs.getString(3);
        lblCountry01Streams.setText(currency + " " + df.format(revenue));

        rs.next();
        lblCountry02.setText(itemSwitcher.setCountry(rs.getString(1)));
        revenue = rs.getDouble(2);
        currency = rs.getString(3);
        lblCountry02Streams.setText(currency + " " + df.format(revenue));

        rs.next();
        lblCountry03.setText(itemSwitcher.setCountry(rs.getString(1)));
        revenue = rs.getDouble(2);
        currency = rs.getString(3);
        lblCountry03Streams.setText(currency + " " + df.format(revenue));

        rs.next();
        lblCountry04.setText(itemSwitcher.setCountry(rs.getString(1)));
        revenue = rs.getDouble(2);
        currency = rs.getString(3);
        lblCountry04Streams.setText(currency + " " + df.format(revenue));

        rs.next();
        lblCountry05.setText(itemSwitcher.setCountry(rs.getString(1)));
        revenue = rs.getDouble(2);
        currency = rs.getString(3);
        lblCountry05Streams.setText(currency + " " + df.format(revenue));
    }

    private void loadTop4DSPs() throws SQLException, ClassNotFoundException {
        ResultSet rs = DatabaseMySQL.getTop4DSPs();
        DecimalFormat df = new DecimalFormat("0.00");
        ItemSwitcher itemSwitcher = new ItemSwitcher();
        double revenue;
        String currency;

        rs.next();
        lblDSP01.setText(rs.getString(1));
        revenue = rs.getDouble(2);
        currency = rs.getString(3);
        lblAmountDSP01.setText(currency + " " + df.format(revenue));
        imgDSP01.setImage(itemSwitcher.setImage(rs.getString(1)));

        rs.next();
        lblDSP02.setText(rs.getString(1));
        revenue = rs.getDouble(2);
        currency = rs.getString(3);
        lblAmountDSP02.setText(currency + " " + df.format(revenue));
        imgDSP02.setImage(itemSwitcher.setImage(rs.getString(1)));

        rs.next();
        lblDSP03.setText(rs.getString(1));
        revenue = rs.getDouble(2);
        currency = rs.getString(3);
        lblAmountDSP03.setText(currency + " " + df.format(revenue));
        imgDSP03.setImage(itemSwitcher.setImage(rs.getString(1)));

        rs.next();
        lblDSP04.setText(rs.getString(1));
        revenue = rs.getDouble(2);
        currency = rs.getString(3);
        lblAmountDSP04.setText(currency + " " + df.format(revenue));
        imgDSP04.setImage(itemSwitcher.setImage(rs.getString(1)));
    }

    private void loadTotalAssetCount() throws SQLException, ClassNotFoundException {
        String count = DatabaseMySQL.getTotalAssetCount();
        lblTotalAssets.setText(count);
    }

    private void loadTopStreamedAssets() throws SQLException, ClassNotFoundException {
        ResultSet rs = DatabaseMySQL.getTop5StreamedAssets();
        DecimalFormat df = new DecimalFormat("0.00");
        double revenue;
        String currency;

        rs.next();
        lblAsset01.setText(rs.getString(2));
        revenue = rs.getDouble(3);
        currency = rs.getString(4);
        lblAsset01Streams.setText(currency + " " + df.format(revenue));

        rs.next();
        lblAsset02.setText(rs.getString(2));
        revenue = rs.getDouble(3);
        currency = rs.getString(4);
        lblAsset02Streams.setText(currency + " " + df.format(revenue));

        rs.next();
        lblAsset03.setText(rs.getString(2));
        revenue = rs.getDouble(3);
        currency = rs.getString(4);
        lblAsset03Streams.setText(currency + " " + df.format(revenue));

        rs.next();
        lblAsset04.setText(rs.getString(2));
        revenue = rs.getDouble(3);
        currency = rs.getString(4);
        lblAsset04Streams.setText(currency + " " + df.format(revenue));

        rs.next();
        lblAsset05.setText(rs.getString(2));
        revenue = rs.getDouble(3);
        currency = rs.getString(4);
        lblAsset05Streams.setText(currency + " " + df.format(revenue));
    }

    public void onLoadReportButtonClick() throws CsvValidationException, SQLException, IOException, ClassNotFoundException {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Select FUGA Report");

        File report = chooser.showOpenDialog(mainUIController.mainVBox.getScene().getWindow());

        btnLoadReport.setText("Working on...");

        boolean status = DatabaseMySQL.loadReport(report);
        /*Task<Void> task;
        task = new Task<>() {
            @Override
            protected Void call() throws Exception {

                Platform.runLater(() -> {
                    if (status) {
                        btnLoadReport.setText("CSV Imported to Database");
                    }
                });
                return null;
            }
        };

        Thread t = new Thread(task);
        t.start();*/
    }

    public void onGetReportedRoyaltyPerISRCbtnClick() throws SQLException, ClassNotFoundException {
        btnLoadReport.setText("Generating...");

        DatabaseMySQL.getReportedRoyalty();
        /*Task<Void> task;
        task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                boolean status = DatabaseMySQL.loadReport(report);

                Platform.runLater(() -> {
                    if (status) {
                        btnLoadReport.setText("CSV Imported to Database");
                    }
                });
                return null;
            }
        };

        Thread t = new Thread(task);
        t.start();*/
    }
}
