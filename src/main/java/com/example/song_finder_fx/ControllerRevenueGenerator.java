package com.example.song_finder_fx;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.awt.*;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ControllerRevenueGenerator {
    //<editor-fold desc="Buttons">
    public Label lblReportProgress;
    public Label lbl_import;
    //</editor-fold>

    //<editor-fold desc="Labels">
    public Label lblSongDB_Update;
    public Label lblSongDB_Progress;
    public Label lblCountMissingISRCs;
    public Label lblUpdateNote;
    public Label lblIC_Save;
    public Label lblISRC_Check;
    public Label lblGross;
    public Label lblP_Share;
    public Label lblTax;
    public Label lblAmtPayable;
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
    public Label lblTitleMonth;
    //</editor-fold>

    public ImageView imgDSP01;
    public ImageView imgDSP02;
    public ImageView imgDSP03;
    public ImageView imgDSP04;
    public ImageView lblIC_Caution;
    public ImageView imgImportCaution;
    public ImageView imgSongDB_Status;
    public VBox vbArtistReports;
    public VBox vboxUpdateSongDB;
    public ComboBox<String> comboPayees;
    public TextField txtRate;
    private final UIController mainUIController;
    private final Path tempDir = Files.createTempDirectory("missing_isrcs");
    private final Path csvFile = tempDir.resolve("missing_isrcs.csv");

    public ControllerRevenueGenerator(UIController uiController) throws IOException {
        mainUIController = uiController;
    }

    public void loadRevenueGenerator() throws IOException {
        FXMLLoader loaderMain = new FXMLLoader(ControllerSettings.class.getResource("layouts/revenue-generator.fxml"));
        FXMLLoader loaderSide = new FXMLLoader(ControllerSettings.class.getResource("layouts/sidepanel-revenue-analysis.fxml"));
        loaderMain.setController(this);
        loaderSide.setController(this);
        Parent newContentMain = loaderMain.load();
        Parent newContentSide = loaderSide.load();
        ItemSwitcher itemSwitcher = new ItemSwitcher();

        mainUIController.mainVBox.getChildren().setAll(newContentMain);
        mainUIController.sideVBox.getChildren().setAll(newContentSide);

        Thread threadLoadAssets = getThreadLoadAssets(itemSwitcher);
        threadLoadAssets.start();
    }

    private Thread getThreadLoadAssets(ItemSwitcher itemSwitcher) {
        Task<Void> taskLoadAssets;

        taskLoadAssets = new Task<>() {
            @Override
            protected Void call() {
                Platform.runLater(() -> {
                    if (InitPreloader.month != null) {
                        lblTitleMonth.setText(itemSwitcher.setMonth(InitPreloader.month));
                    }

                    if (InitPreloader.count != null) {
                        lblTotalAssets.setText(InitPreloader.count);
                    }

                    try {
                        loadTopStreamedAssets(InitPreloader.top5StreamedAssets);
                        loadTop5Territories(InitPreloader.top5Territories);
                        loadTop4DSPs(InitPreloader.top4DSPs);
                    } catch (SQLException | ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                });
                return null;
            }
        };

        return new Thread(taskLoadAssets);
    }

    private void loadTop5Territories(ResultSet rs) throws SQLException, ClassNotFoundException {
        if (rs != null) {
            rs.beforeFirst();
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
    }

    private void loadTop4DSPs(ResultSet rs) throws SQLException, ClassNotFoundException {
        if (rs != null) {
            rs.beforeFirst();
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
    }

    private void loadTopStreamedAssets(ResultSet rs) throws SQLException, ClassNotFoundException {
        if (rs != null) {
            rs.beforeFirst();
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
    }


    private Task<Void> checkMissingISRCs() {
        Task<Void> task;
        task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                Platform.runLater(() -> {
                    lblISRC_Check.setVisible(true);
                    lblISRC_Check.setText("Searching Missing ISRCs");
                    lblIC_Caution.setVisible(false);
                });

                ResultSet resultSet = DatabaseMySQL.checkMissingISRCs();

                CSVWriter csvWriter = new CSVWriter(new FileWriter(csvFile.toFile()));

                List<String[]> rows = new ArrayList<>();

                while (resultSet.next()) {
                    if ((!Objects.equals(resultSet.getString(1), "")) && (resultSet.getString(2) == null) && (resultSet.getString(3) == null)) {
                        String[] row = new String[]{
                                resultSet.getString(1)
                        };
                        rows.add(row);
                    }
                }

                csvWriter.writeAll(rows);
                csvWriter.close();

                Platform.runLater(() -> {
                    int size = rows.size();
                    if (size > 0) {
                        lblIC_Save.setText(size + " Missing ISRCs. (Click Here to Save List)");
                        lblCountMissingISRCs.setText(size + " Missing ISRCs");
                        Image imgCaution = new Image("com/example/song_finder_fx/images/caution.png");
                        lblIC_Caution.setImage(imgCaution);
                        lblIC_Caution.setVisible(true);
                        lblIC_Save.setVisible(true);
                        lblUpdateNote.setVisible(true);
                        vboxUpdateSongDB.setVisible(true);
                    } else {
                        lblIC_Save.setText("Report and Song Databases Synced");
                        Image imgDone = new Image("com/example/song_finder_fx/images/done.png");
                        lblIC_Caution.setImage(imgDone);
                        lblIC_Caution.setVisible(true);
                    }
                });

                return null;
            }
        };
        return task;
    }

    private Task<Void> loadReport(File report) {
        Task<Void> task;
        task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                boolean status = DatabaseMySQL.loadReport(report, lblReportProgress, lbl_import, imgImportCaution);

                Platform.runLater(() -> {
                    if (status) {
                        lblReportProgress.setText("CSV Imported to Database");
                        imgImportCaution.setVisible(true);
                    }
                });
                return null;
            }
        };
        return task;
    }

    public void onSaveListLblClick(MouseEvent event) throws IOException {
        Node node = (Node) event.getSource();
        Scene scene = node.getScene();

        File destination = Main.browseLocationNew(scene.getWindow());

        if (destination != null) {
            Path destinationPath = destination.toPath().resolve(csvFile.getFileName());
            Files.copy(csvFile, destinationPath, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    public void onGenerateFullBreakdownBtnClick(MouseEvent mouseEvent) {
        Platform.runLater(() -> {
            try {
                ResultSet rs = DatabaseMySQL.getFullBreakdown();
                Path tempDir = Files.createTempDirectory("ceymusic_dashboard");
                Path csvFile = tempDir.resolve("revenue_breakdown_full.csv");

                CSVWriter writer = new CSVWriter(new FileWriter(csvFile.toFile()));
                List<String[]> rows = new ArrayList<>();
                String[] header = new String[]{
                        "ISRC",
                        "Reported Royalty Summary",
                        "AU Earnings",
                        "After GST Deduction",
                        "Rest of the world Earnings",
                        "Reported Royalty After GST",
                        "Reported Royalty for CeyMusic"
                };
                rows.add(header);

                while (rs.next()) {
                    System.out.println("rs.getString(1) = " + rs.getString(1));

                    String[] row = new String[]{
                            rs.getString(1),
                            rs.getString(2),
                            rs.getString(3),
                            rs.getString(4),
                            rs.getString(5),
                            rs.getString(6),
                            rs.getString(7)
                    };

                    rows.add(row);
                }

                writer.writeAll(rows);
                writer.close();

                Node node = (Node) mouseEvent.getSource();
                Scene scene = node.getScene();
                File destination = Main.browseLocationNew(scene.getWindow());
                Path destinationPath = destination.toPath().resolve(csvFile.getFileName());
                Files.copy(csvFile, destinationPath, StandardCopyOption.REPLACE_EXISTING);
                System.out.println("File copied successfully to " + destinationPath);
            } catch (SQLException | ClassNotFoundException | IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void onGenerateReportDSPClicked(MouseEvent mouseEvent) {
        Platform.runLater(() -> {
            try {
                ResultSet rs = DatabaseMySQL.getBreakdownByDSP();
                Path tempDir = Files.createTempDirectory("ceymusic_dashboard");
                Path csvFile = tempDir.resolve("revenue_breakdown_dsp.csv");
                CSVWriter writer = new CSVWriter(new FileWriter(csvFile.toFile()));
                List<String[]> rows = new ArrayList<>();
                String[] header = new String[]{
                        "ISRC",
                        "Reported Royalty Summary",
                        "Youtube Ad Supported",
                        "Youtube Music",
                        "Spotify",
                        "TikTok",
                        "Apple Music",
                        "Facebook",
                        "Others"
                };
                rows.add(header);

                while (rs.next()) {
                    System.out.println("rs.getString(1) = " + rs.getString(1));

                    String[] row = new String[]{
                            rs.getString(1),
                            rs.getString(2),
                            rs.getString(3),
                            rs.getString(4),
                            rs.getString(5),
                            rs.getString(6),
                            rs.getString(7),
                            rs.getString(8),
                            rs.getString(9)
                    };

                    rows.add(row);
                }

                writer.writeAll(rows);
                writer.close();

                Node node = (Node) mouseEvent.getSource();
                Scene scene = node.getScene();
                File destination = Main.browseLocationNew(scene.getWindow());
                Path destinationPath = destination.toPath().resolve(csvFile.getFileName());
                Files.copy(csvFile, destinationPath, StandardCopyOption.REPLACE_EXISTING);
                System.out.println("File copied successfully to " + destinationPath);
            } catch (SQLException | ClassNotFoundException | IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void onGenerateReportTerritoryBtnClicked(MouseEvent mouseEvent) {
        Platform.runLater(() -> {
            try {
                ResultSet rs = DatabaseMySQL.getBreakdownByTerritory();

                Path tempDir = Files.createTempDirectory("ceymusic_dashboard");
                Path csvFile = tempDir.resolve("revenue_breakdown_territory.csv");
                CSVWriter writer = new CSVWriter(new FileWriter(csvFile.toFile()));
                List<String[]> rows = new ArrayList<>();
                String[] header = new String[]{
                        "ISRC",
                        "Reported Royalty Summary",
                        "AU",
                        "US",
                        "GB",
                        "IT",
                        "KR",
                        "LK",
                        "AE",
                        "JP",
                        "CA",
                        "Rest"
                };
                rows.add(header);

                while (rs.next()) {
                    System.out.println("rs.getString(1) = " + rs.getString(1));

                    String[] row = new String[]{
                            rs.getString(1),
                            rs.getString(2),
                            rs.getString(3),
                            rs.getString(4),
                            rs.getString(5),
                            rs.getString(6),
                            rs.getString(7),
                            rs.getString(8),
                            rs.getString(9),
                            rs.getString(10),
                            rs.getString(11),
                            rs.getString(12)
                    };

                    rows.add(row);
                }

                writer.writeAll(rows);
                writer.close();

                Node node = (Node) mouseEvent.getSource();
                Scene scene = node.getScene();
                File destination = Main.browseLocationNew(scene.getWindow());
                Path destinationPath = destination.toPath().resolve(csvFile.getFileName());
                Files.copy(csvFile, destinationPath, StandardCopyOption.REPLACE_EXISTING);
                System.out.println("File copied successfully to " + destinationPath);
            } catch (SQLException | ClassNotFoundException | IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void onUpdateSongsDatabaseBtnClick(MouseEvent mouseEvent) {
        Node node = (Node) mouseEvent.getSource();
        Scene scene = node.getScene();
        Window window = scene.getWindow();
        File file = Main.browseForCSV(window);

        if (file != null) {
            // System.out.println("Check");

            Task<Void> taskUpdateSongDatabase;

            taskUpdateSongDatabase = new Task<>() {
                @Override
                protected Void call() throws Exception {
                    boolean status = DatabaseMySQL.updateSongsTable(file, lblSongDB_Update, lblSongDB_Progress);

                    if (status) {
                        Platform.runLater(() -> {
                            imgSongDB_Status.setVisible(true);
                            lblSongDB_Progress.setText("Done");
                        });
                    } else {
                        Platform.runLater(() -> lblSongDB_Progress.setText("Error"));
                    }

                    return null;
                }
            };

            Thread threadUpdateSongDatabase = new Thread(taskUpdateSongDatabase);
            threadUpdateSongDatabase.start();
        }
    }

    public void loadArtistReports() throws IOException {
        FXMLLoader loaderMain = new FXMLLoader(ControllerSettings.class.getResource("layouts/artist-reports.fxml"));
        loaderMain.setController(this);
        Parent newContentMain = loaderMain.load();

        mainUIController.mainVBox.getChildren().setAll(newContentMain);
        System.out.println("Here!");

        /*Scene scene = mainUIController.mainVBox.getScene();
        vbArtistReports = (VBox) scene.lookup("#vbArtistReports");*/

        Task<Void> task;

        task = new Task<>() {
            @Override
            protected Void call() throws SQLException, ClassNotFoundException {
                ResultSet resultSet = DatabaseMySQL.checkMissingISRCs();
                int rowCount = 0;

                while (resultSet.next() && ((resultSet.getString(2) == null) && (resultSet.getString(3) == null))) {
                    rowCount++;
                }

                System.out.println("rowCount = " + rowCount);

                if (rowCount > 0) {
                    Platform.runLater(() -> {
                        vbArtistReports.setDisable(true);
                        try {
                            NotificationBuilder.displayTrayError("ISRC Sync Error", "Please Update Missing ISRCs in Song Database to Sync Payee List");
                        } catch (AWTException e) {
                            throw new RuntimeException(e);
                        }
                    });
                } else {
                    Platform.runLater(() -> {
                        try {
                            ResultSet rsPayees = DatabaseMySQL.getPayees();
                            while (rsPayees.next()) {
                                comboPayees.getItems().add(rsPayees.getString(1));
                            }
                        } catch (SQLException | ClassNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                    });
                }

                return null;
            }
        };

        Thread t = new Thread(task);
        t.start();
    }

    public void onSidePanelAddNewReportBtnClick() throws IOException {
        FXMLLoader loaderMain = new FXMLLoader(ControllerSettings.class.getResource("layouts/revenue-report-processing.fxml"));
        loaderMain.setController(this);
        Parent newContentMain = loaderMain.load();

        mainUIController.mainVBox.getChildren().setAll(newContentMain);

        Task<Void> taskCheckMissingISRCs = checkMissingISRCs();
        Thread threadCheckMissingISRCs = new Thread(taskCheckMissingISRCs);
        threadCheckMissingISRCs.start();
    }

    public void OnComboPayeeKeyPress(KeyEvent event) {
        String s = jumpTo(event.getText(), comboPayees.getValue(), comboPayees.getItems());
        if (s != null) {
            comboPayees.setValue(s);
        }
    }

    static String jumpTo(String keyPressed, String currentlySelected, List<String> items) {
        String key = keyPressed.toUpperCase();
        if (key.matches("^[A-Z]$")) {
            // Only act on letters so that navigating with cursor keys does not
            // try to jump somewhere.
            boolean letterFound = false;
            boolean foundCurrent = currentlySelected == null;
            for (String s : items) {
                if (s.toUpperCase().startsWith(key)) {
                    letterFound = true;
                    if (foundCurrent) {
                        return s;
                    }
                    foundCurrent = s.equals(currentlySelected);
                }
            }
            if (letterFound) {
                return jumpTo(keyPressed, null, items);
            }
        }
        return null;
    }

    public void comboPayeeOnAction() {
        /*DecimalFormat df = new DecimalFormat("0.00");
        final ArrayList<Double>[] royalty = new ArrayList[]{new ArrayList<Double>()};

        // Getting Selected Item
        String selectedItem = comboPayees.getSelectionModel().getSelectedItem();

        // Get the gross revenue for the selected artist
        comboPayees.setDisable(true);
        lblGross.setText("Loading...");
        lblP_Share.setText("Loading...");

        Thread tGrossRevenue = new Thread(() -> {
            try {
                royalty[0] = DatabaseMySQL.getPayeeGrossRev(selectedItem);

            } catch (SQLException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }

            String formattedGrossRevenue = df.format(royalty[0].getFirst());
            String formattedPartnerShare = df.format(royalty[0].get(1));

            Platform.runLater(() -> {
                lblGross.setText("EUR " + formattedGrossRevenue);
                lblP_Share.setText("EUR " + formattedPartnerShare);
                comboPayees.setDisable(false);
            });
        });

        tGrossRevenue.start();*/
    }

    public void onLoadReportBtnClick() {
        String userInputRate = txtRate.getText();
        double doubleConvertedRate;
        String selectedItem = comboPayees.getSelectionModel().getSelectedItem();
        DecimalFormat df = new DecimalFormat("0.00");
        final ArrayList<Double>[] royalty = new ArrayList[]{new ArrayList<Double>()};
        final double[] tax = {0};
        final double[] amountPayable = new double[1];

        if (!Objects.equals(selectedItem, null)) {
            comboPayees.setStyle("-fx-border-color: '#e9ebee';");

            if (userInputRate.matches("\\d+(\\.\\d+)?")) {
                // When user input is only numbers
                txtRate.setStyle("-fx-border-color: '#e9ebee';");
                lblGross.setText("Loading...");
                lblP_Share.setText("Loading...");
                lblTax.setText("Loading...");
                lblAmtPayable.setText("Loading...");

                doubleConvertedRate = Double.parseDouble(userInputRate);
                Task<Void> taskGrossRevenue = new Task<>() {
                    @Override
                    protected Void call() {
                        try {
                            royalty[0] = DatabaseMySQL.getPayeeGrossRev(selectedItem);

                        } catch (SQLException | ClassNotFoundException e) {
                            throw new RuntimeException(e);
                        }

                        double grossRevenueInLKR = royalty[0].getFirst() * doubleConvertedRate;
                        double partnerShareInLKR = royalty[0].get(1) * doubleConvertedRate;

                        /*{
                            // This is a temporary block
                            grossRevenueInLKR = 120000.00;
                        }*/

                        if (grossRevenueInLKR > 100000.00) {
                            tax[0] = grossRevenueInLKR * 0.14;
                        }

                        amountPayable[0] = grossRevenueInLKR - tax[0];

                        String formattedGrossRevenue = df.format(grossRevenueInLKR);
                        String formattedPartnerShare = df.format(partnerShareInLKR);
                        String formattedTax = df.format(tax[0]);
                        String formattedAmountPayable = df.format(amountPayable[0]);


                        Platform.runLater(() -> {
                            lblGross.setText("LKR " + formattedGrossRevenue);
                            lblP_Share.setText("LKR " + formattedPartnerShare);
                            lblTax.setText("LKR " + formattedTax);
                            lblAmtPayable.setText("LKR " + formattedAmountPayable);
                        });
                        return null;
                    }
                };

                Task<Void> taskCoWriterShare;
                /*SELECT `isrc_payees`.`PAYEE`,
                ((((SUM(CASE WHEN `report`.`Territory` = 'AU' THEN `report`.`Reported_Royalty` ELSE 0 END)) * 0.9) + (SUM(CASE WHEN `report`.`Territory` != 'AU' THEN `report`.`Reported_Royalty` ELSE 0 END))) * 0.85) * `isrc_payees`.`SHARE`/100 AS REPORTED_ROYALTY
                FROM `isrc_payees`
                JOIN `report` ON `isrc_payees`.`ISRC` = `report`.`Asset_ISRC`
                WHERE `isrc_payees`.`ISRC` IN (
                        SELECT ISRC
                        FROM isrc_payees
                        WHERE PAYEE = 'Sarath De Alwis'
                ) AND `isrc_payees`.`PAYEE` != 'Sarath De Alwis'
                GROUP BY `report`.`Asset_ISRC`;*/

                Thread threadGrossRevenue = new Thread(taskGrossRevenue);
                threadGrossRevenue.start();
            } else {
                // When User Input Contains Texts
                txtRate.setStyle("-fx-border-color: red;");
            }
        } else {
            // If no Payee Selected
            comboPayees.setStyle("-fx-border-color: red;");
        }
    }

    public void onUpdatePayeeDetailsBtnClick() {
        // Browsing for CSV
        File file = Main.browseForCSV(mainUIController.mainVBox.getScene().getWindow());

        if (file != null) {
            Task<Void> taskUpdatePayeeList = new Task<>() {
                @Override
                protected Void call() throws IOException, CsvValidationException, SQLException, ClassNotFoundException {
                    CSVReader reader = new CSVReader(new FileReader(file.getAbsolutePath()));

                    DatabaseMySQL.updatePayees(reader);
                    return null;
                }
            };

            Thread threadUpdatePayeeList = new Thread(taskUpdatePayeeList);
            threadUpdatePayeeList.start();
        }
    }

    public void onLoadReportButtonClick() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Select FUGA Report");

        File report = chooser.showOpenDialog(mainUIController.mainVBox.getScene().getWindow());

        if (report != null) {
            lblReportProgress.setText("Working on...");
            lbl_import.setText("Importing");
            lbl_import.setStyle("-fx-text-fill: '#000000'");
            lblReportProgress.setVisible(true);
            imgImportCaution.setVisible(false);

            Task<Void> taskLoadReport = loadReport(report);
            Task<Void> taskCheckMissingISRCs = checkMissingISRCs();
            taskLoadReport.setOnSucceeded(event -> {
                Thread threadCheckMissingISRCs = new Thread(taskCheckMissingISRCs);
                threadCheckMissingISRCs.start();
            });

            Thread threadLoadReport = new Thread(taskLoadReport);
            threadLoadReport.start();

        } else {
            System.out.println("No Report Imported");
        }
    }
}
