package com.example.song_finder_fx;

import com.example.song_finder_fx.Controller.CSVController;
import com.example.song_finder_fx.Controller.ItemSwitcher;
import com.example.song_finder_fx.Controller.ReportPDF;
import com.example.song_finder_fx.Model.ArtistReport;
import com.example.song_finder_fx.Model.CsvSong;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;
import com.opencsv.exceptions.CsvValidationException;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ControllerRevenueGenerator {
    //<editor-fold desc="Buttons">
    public Label lblReportProgress;
    public Label lbl_import;
    //</editor-fold>

    //<editor-fold desc="Labels">
    public Label lblUpdateSongsDatabase;
    public Label lblWriter01;
    public Label lblWriter02;
    public Label lblWriter03;
    public Label lblWriter04;
    public Label lblWriter05;
    public Label lblWriter01Streams;
    public Label lblWriter02Streams;
    public Label lblWriter03Streams;
    public Label lblWriter04Streams;
    public Label lblWriter05Streams;
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
    public ImageView imgLoading;
    public ImageView lblIC_Caution;
    public ImageView imgImportCaution;
    public ImageView imgSongDB_Status;
    public VBox vbArtistReports;
    public VBox vboxUpdateSongDB;
    public ComboBox<String> comboPayees;
    public TextField txtRate;
    private final UIController mainUIController;
    private final ArtistReport report = new ArtistReport();
    CSVController csvController = new CSVController();

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

        mainUIController.mainVBox.getChildren().setAll(newContentMain);
        mainUIController.sideVBox.getChildren().setAll(newContentSide);

        Thread threadLoadAssets = getThreadLoadAssets();
        threadLoadAssets.start();
    }

    private Thread getThreadLoadAssets() {
        Task<Void> taskLoadAssets;

        taskLoadAssets = new Task<>() {
            @Override
            protected Void call() {
                Platform.runLater(() -> {
                    if (InitPreloader.revenue.getMonth() != null) {
                        lblTitleMonth.setText(InitPreloader.revenue.getMonth());
                    }

                    if (InitPreloader.revenue.getAssetCount() != null) {
                        lblTotalAssets.setText(InitPreloader.revenue.getAssetCount());
                    }

                    try {
                        loadTopStreamedAssets(InitPreloader.revenue.getTop5StreamedAssets());
                        loadTop5Territories(InitPreloader.revenue.getTop5Territories());
                        loadTop4DSPs(InitPreloader.revenue.getTop4DSPs());
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
            currency = "EUR";
            lblCountry01Streams.setText(currency + " " + df.format(revenue));

            rs.next();
            lblCountry02.setText(itemSwitcher.setCountry(rs.getString(1)));
            revenue = rs.getDouble(2);
            currency = "EUR";
            lblCountry02Streams.setText(currency + " " + df.format(revenue));

            rs.next();
            lblCountry03.setText(itemSwitcher.setCountry(rs.getString(1)));
            revenue = rs.getDouble(2);
            currency = "EUR";
            lblCountry03Streams.setText(currency + " " + df.format(revenue));

            rs.next();
            lblCountry04.setText(itemSwitcher.setCountry(rs.getString(1)));
            revenue = rs.getDouble(2);
            currency = "EUR";
            lblCountry04Streams.setText(currency + " " + df.format(revenue));

            rs.next();
            lblCountry05.setText(itemSwitcher.setCountry(rs.getString(1)));
            revenue = rs.getDouble(2);
            currency = "EUR";
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
            currency = "EUR";
            lblAmountDSP01.setText(currency + " " + df.format(revenue));
            imgDSP01.setImage(itemSwitcher.setImage(rs.getString(1)));

            rs.next();
            lblDSP02.setText(rs.getString(1));
            revenue = rs.getDouble(2);
            currency = "EUR";
            lblAmountDSP02.setText(currency + " " + df.format(revenue));
            imgDSP02.setImage(itemSwitcher.setImage(rs.getString(1)));

            rs.next();
            lblDSP03.setText(rs.getString(1));
            revenue = rs.getDouble(2);
            currency = "EUR";
            lblAmountDSP03.setText(currency + " " + df.format(revenue));
            imgDSP03.setImage(itemSwitcher.setImage(rs.getString(1)));

            rs.next();
            lblDSP04.setText(rs.getString(1));
            revenue = rs.getDouble(2);
            currency = "EUR";
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
            currency = "EUR";
            lblAsset01Streams.setText(currency + " " + df.format(revenue));

            rs.next();
            lblAsset02.setText(rs.getString(2));
            revenue = rs.getDouble(3);
            currency = "EUR";
            lblAsset02Streams.setText(currency + " " + df.format(revenue));

            rs.next();
            lblAsset03.setText(rs.getString(2));
            revenue = rs.getDouble(3);
            currency = "EUR";
            lblAsset03Streams.setText(currency + " " + df.format(revenue));

            rs.next();
            lblAsset04.setText(rs.getString(2));
            revenue = rs.getDouble(3);
            currency = "EUR";
            lblAsset04Streams.setText(currency + " " + df.format(revenue));

            rs.next();
            lblAsset05.setText(rs.getString(2));
            revenue = rs.getDouble(3);
            currency = "EUR";
            lblAsset05Streams.setText(currency + " " + df.format(revenue));
        }
    }

    public void onSaveListLblClick(MouseEvent event) throws IOException {
        Node node = (Node) event.getSource();
        Scene scene = node.getScene();

        File destination = Main.browseLocationNew(scene.getWindow());

        if (destination != null) {
            csvController.copyMissingISRCList(destination);
        }
    }

    public void onGenerateFullBreakdownBtnClick(MouseEvent mouseEvent) {
        Platform.runLater(() -> {
            try {
                // ResultSet rs = DatabaseMySQL.getFullBreakdown();
                ResultSet rs = DatabasePostgres.getFullBreakdown();
                Path tempDir = Files.createTempDirectory("ceymusic_dashboard");
                Path csvFile = tempDir.resolve("revenue_breakdown_full.csv");

                CSVWriter writer = new CSVWriter(new FileWriter(csvFile.toFile()));
                List<String[]> rows = new ArrayList<>();
                String[] header = new String[]{"ISRC", "Reported Royalty Summary", "AU Earnings", "After GST Deduction", "Rest of the world Earnings", "Reported Royalty After GST", "Reported Royalty for CeyMusic"};
                rows.add(header);

                while (rs.next()) {
                    System.out.println("rs.getString(1) = " + rs.getString(1));

                    String[] row = new String[]{rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getString(6), rs.getString(7)};

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
            } catch (SQLException | IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void onGenerateReportDSPClicked(MouseEvent mouseEvent) {
        Platform.runLater(() -> {
            try {
                ResultSet rs = DatabaseMySQL.getBreakdownByDSP();
//              ResultSet rs = DatabasePostgre.getBreakdownByDSP();   //Connection for Postgress

                Path tempDir = Files.createTempDirectory("ceymusic_dashboard");
                Path csvFile = tempDir.resolve("revenue_breakdown_dsp.csv");
                CSVWriter writer = new CSVWriter(new FileWriter(csvFile.toFile()));
                List<String[]> rows = new ArrayList<>();
                String[] header = new String[]{"ISRC", "Reported Royalty Summary", "Youtube Ad Supported", "Youtube Music", "Spotify", "TikTok", "Apple Music", "Facebook", "Others"};
                rows.add(header);

                while (rs.next()) {
                    System.out.println("rs.getString(1) = " + rs.getString(1));

                    String[] row = new String[]{rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getString(6), rs.getString(7), rs.getString(8), rs.getString(9)};

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
//                ResultSet rs = DatabasePostgre.getBreakdownByTerritory();     //Connection for Postgress

                Path tempDir = Files.createTempDirectory("ceymusic_dashboard");
                Path csvFile = tempDir.resolve("revenue_breakdown_territory.csv");
                CSVWriter writer = new CSVWriter(new FileWriter(csvFile.toFile()));
                List<String[]> rows = new ArrayList<>();
                String[] header = new String[]{"ISRC", "Reported Royalty Summary", "AU", "US", "GB", "IT", "KR", "LK", "AE", "JP", "CA", "Rest"};
                rows.add(header);

                while (rs.next()) {
                    System.out.println("rs.getString(1) = " + rs.getString(1));

                    String[] row = new String[]{rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getString(6), rs.getString(7), rs.getString(8), rs.getString(9), rs.getString(10), rs.getString(11), rs.getString(12)};

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
//                    boolean status = DatabasePostgre.updateSongsTable(file, lblSongDB_Update, lblSongDB_Progress);        //Postgress

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

    public void onSidePanelUpdateSongsDatabaseBtnClick(MouseEvent mouseEvent) {
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
                    boolean status = DatabaseMySQL.updateSongsTable(file, lblUpdateSongsDatabase, lblUpdateSongsDatabase);
//                    boolean status = DatabasePostgre.updateSongsTable(file, lblUpdateSongsDatabase, lblUpdateSongsDatabase);      //Postgress

                    if (status) {
                        Platform.runLater(() -> lblUpdateSongsDatabase.setText("Done"));
                    } else {
                        Platform.runLater(() -> lblUpdateSongsDatabase.setText("Error"));
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

        Task<Void> task;

        task = new Task<>() {
            @Override
            protected Void call() throws SQLException, ClassNotFoundException {
                // lblStatus.setText("Loading Payees...");
                // ResultSet rsPayees = DatabaseMySQL.getPayees();
                // ResultSet rsPayees = DatabasePostgres.getPayees(); // Postgres
                List<String> payees = DatabasePostgres.getPayees();

                /*while (rsPayees.next()) {
                    comboPayees.getItems().add(rsPayees.getString(1));
                }
                lblStatus.setVisible(false);
                imgLoading.setVisible(false);
                comboPayees.setDisable(false);*/

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

    public void onLoadReportBtnClick() {
        report.clear();
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
                initializeArtistReportUILabels();

                doubleConvertedRate = Double.parseDouble(userInputRate);
                Task<Void> taskGrossRevenue = new Task<>() {
                    @Override
                    protected Void call() {
                        try {
//                            royalty[0] = DatabaseMySQL.getPayeeGrossRev(selectedItem);
                            royalty[0] = DatabasePostgres.getPayeeGrossRev(selectedItem);      //Postgress

                        } catch (SQLException | ClassNotFoundException e) {
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setTitle("Error");
                            alert.setHeaderText("An error occurred");
                            alert.setContentText(String.valueOf(e));
                            Platform.runLater(alert::showAndWait);
                        }

                        // Converting to LKR
                        double grossRevenueInLKR = royalty[0].getFirst() * doubleConvertedRate;
                        double partnerShareInLKR = royalty[0].get(1) * doubleConvertedRate;

                        if (grossRevenueInLKR > 100000.00) {
                            tax[0] = grossRevenueInLKR * 0.14;
                        }

                        // Calculating amount payable
                        amountPayable[0] = grossRevenueInLKR - tax[0];

                        // Testing values before assigning
                        Platform.runLater(() -> {
                            System.out.println("selectedItem = " + selectedItem);
                            System.out.println("grossRevenueInLKR = " + grossRevenueInLKR);
                            System.out.println("partnerShareInLKR = " + partnerShareInLKR);
                            System.out.println("tax[0] = " + tax[0]);
                            System.out.println("amountPayable[0] = " + amountPayable[0]);
                            System.out.println("InitPreloader.revenue.getMonth() = " + InitPreloader.revenue.getMonth());
                        });
                        report.setGrossRevenue(selectedItem, grossRevenueInLKR, partnerShareInLKR, tax[0], amountPayable[0], InitPreloader.revenue.getMonth());

                        // Update UI
                        Platform.runLater(() -> {
                            lblGross.setText(report.getGrossRevenueInLKR());
                            lblP_Share.setText(report.getPartnerShareInLKR());
                            lblTax.setText(report.getTaxAmount());
                            lblAmtPayable.setText(report.getAmountPayable());
                        });
                        return null;
                    }
                };

                Task<Void> taskCoWriterShare = new Task<>() {
                    @Override
                    protected Void call() throws SQLException, ClassNotFoundException {
                        // Get the co-writer name and share in EUR from the database
                        ResultSet rsCoWriterShares = DatabaseMySQL.getCoWriterShares(selectedItem);
//                        ResultSet rsCoWriterShares = DatabasePostgre.getCoWriterShares(selectedItem);     //Postgress

                        int count = 0;

                        while (rsCoWriterShares.next()) { // Looping through writer 01 - 05
                            count++;
                            String artist = rsCoWriterShares.getString(1);
                            double share = rsCoWriterShares.getDouble(2) * doubleConvertedRate;
                            report.addCoWriter(artist);
                            report.addCoWriterShare(share);

                            if (count == 1) {
                                Platform.runLater(() -> {
                                    lblWriter01.setText(artist);
                                    lblWriter01Streams.setText(df.format(share));
                                });
                            } else if (count == 2) {
                                Platform.runLater(() -> {
                                    lblWriter02.setText(artist);
                                    lblWriter02Streams.setText(df.format(share));
                                });
                            } else if (count == 3) {
                                Platform.runLater(() -> {
                                    lblWriter03.setText(artist);
                                    lblWriter03Streams.setText(df.format(share));
                                });
                            } else if (count == 4) {
                                Platform.runLater(() -> {
                                    lblWriter04.setText(artist);
                                    lblWriter04Streams.setText(df.format(share));
                                });
                            } else if (count == 5) {
                                Platform.runLater(() -> {
                                    lblWriter05.setText(artist);
                                    lblWriter05Streams.setText(df.format(share));
                                });
                            }
                        }

                        return null;
                    }
                };

                Task<Void> taskTopPerformingSongs = new Task<>() {
                    @Override
                    protected Void call() throws Exception {
                        ResultSet rsTopPerformingSongs = DatabaseMySQL.getTopPerformingSongsEdit(selectedItem);
//                        ResultSet rsTopPerformingSongs = DatabasePostgre.getTopPerformingSongsEdit(selectedItem);     //Postgress

                        int count = 0;

                        while (rsTopPerformingSongs.next()) {
                            count++;

                            String assetTitle = rsTopPerformingSongs.getString(1);
                            String payee = rsTopPerformingSongs.getString(2);
                            double reportedRoyalty = rsTopPerformingSongs.getDouble(4);

                            report.setTopPerformingSongDetails(assetTitle, payee, df.format(reportedRoyalty * doubleConvertedRate));

                            if (count == 1) {
                                Platform.runLater(() -> {
                                    lblAsset01.setText(assetTitle + " (" + payee + ")");
                                    lblAsset01Streams.setText(df.format(reportedRoyalty * doubleConvertedRate));
                                });
                            } else if (count == 2) {
                                Platform.runLater(() -> {
                                    lblAsset02.setText(assetTitle + " (" + payee + ")");
                                    lblAsset02Streams.setText(df.format(reportedRoyalty * doubleConvertedRate));
                                });
                            } else if (count == 3) {
                                Platform.runLater(() -> {
                                    lblAsset03.setText(assetTitle + " (" + payee + ")");
                                    lblAsset03Streams.setText(df.format(reportedRoyalty * doubleConvertedRate));
                                });
                            } else if (count == 4) {
                                Platform.runLater(() -> {
                                    lblAsset04.setText(assetTitle + " (" + payee + ")");
                                    lblAsset04Streams.setText(df.format(reportedRoyalty * doubleConvertedRate));
                                });
                            } else if (count == 5) {
                                Platform.runLater(() -> {
                                    lblAsset05.setText(assetTitle + " (" + payee + ")");
                                    lblAsset05Streams.setText(df.format(reportedRoyalty * doubleConvertedRate));
                                });
                            }
                        }

                        return null;
                    }
                };

                taskCoWriterShare.setOnSucceeded(event -> {
                    Thread threadTopPerformingSongs = new Thread(taskTopPerformingSongs);
                    threadTopPerformingSongs.start();
                });

                taskGrossRevenue.setOnSucceeded(event -> {
                    Thread threadCoWriterShare = new Thread(taskCoWriterShare);
                    threadCoWriterShare.start();
                });

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

    private void initializeArtistReportUILabels() {
        // Top bar labels
        txtRate.setStyle("-fx-border-color: '#e9ebee';");
        lblGross.setText("Loading...");
        lblP_Share.setText("Loading...");
        lblTax.setText("Loading...");
        lblAmtPayable.setText("Loading...");

        // Co-Writer labels
        lblWriter01.setText("-");
        lblWriter02.setText("-");
        lblWriter03.setText("-");
        lblWriter04.setText("-");
        lblWriter05.setText("-");
        lblWriter01Streams.setText("-");
        lblWriter02Streams.setText("-");
        lblWriter03Streams.setText("-");
        lblWriter04Streams.setText("-");
        lblWriter05Streams.setText("-");

        // Top performing songs block labels
        lblAsset01.setText("-");
        lblAsset02.setText("-");
        lblAsset03.setText("-");
        lblAsset04.setText("-");
        lblAsset05.setText("-");
        lblAsset01Streams.setText("-");
        lblAsset02Streams.setText("-");
        lblAsset03Streams.setText("-");
        lblAsset04Streams.setText("-");
        lblAsset05Streams.setText("-");
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
//                    DatabasePostgre.updatePayees(reader);     //Postgress

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

    private void generateCsv() {
        //
        File report = null;
//        Task<Void> taskLoadReport = loadRepo(report);

    }


    //read and write csv main file

    /**
     * private Task<Void> loadRepo(File report, String primaryArtist) throws IOException, CsvException {
     * <p>
     * List<CsvSong> songList = readCSVFile("/filepath", primaryArtist);
     * <p>
     * for (CsvSong sng : songList) {
     * sng.setComposer("test");
     * }
     * <p>
     * <p>
     * return null;
     * <p>
     * }
     */
    public static void main(String[] args) throws IOException, CsvValidationException {
        File csv = new File("resources/com/example/song_finder_fx/CSV Template First.csv");
        File csv1 = new File("D:\\Sudesh\\newrepo1.csv");
        int UPC = Integer.parseInt("0");
        List<CsvSong> songList = new ArrayList<>();
        System.out.println("main");
        String[] upcArray = {"value0", "value1", "value5", "value3"};
//        int upc = 1;

        String catelogNum = "tectCat";
        String albumTitle = "Golden hits";
        songList = songlistRead1(csv);
        String PrimaryArtist = "Dharmasiri Gamage";
        String isrc = "LKA0W2213413";
        writeCSVFile1(songList, csv1, upcArray, albumTitle, 1, catelogNum, PrimaryArtist, isrc);
    }

    //Read Csv
    public static List<CsvSong> songlistRead1(File csv) throws CsvValidationException, IOException {
//        File csv = new File("D:\\Work\\dashboard-for-cey-music-ops\\CSV Template First.csv");
        List<CsvSong> songList = new ArrayList<>();

        try (CSVReader csvReader = new CSVReaderBuilder(new FileReader(csv)).withSkipLines(1).build()) {
            String[] record;
            while ((record = csvReader.readNext()) != null) {
                if (record.length >= 2) { // Ensure at least two columns are present
                    String title = record[0].trim(); // Trim whitespace from name
                    String FileName = record[1].trim(); // Trim whitespace from email
                    String singer = record[2].trim();
                    String lyrics = record[3].trim();
                    String composer = record[4].trim();
                    CsvSong cs = new CsvSong();
                    cs.setSongTitle(title);
                    cs.setFileName(FileName);
                    cs.setSinger(singer);
                    cs.setLyrics(lyrics);
                    cs.setComposer(composer);


//                    System.out.println(title);

                    songList.add(cs);
                }
            }
        } catch (IOException | CsvValidationException e) {
            e.printStackTrace();
        }


        return songList;
    }

    //Write Csv file
    public static void writeCSVFile1(List<CsvSong> sgList, File csvFilePath, String[] upcArray, String albumTitle, int startNum, String catelog, String PrimaryArtist, String ISRC) {

        String UPC1 = null;
        String titl = null;
        int UPC = 0;
        String s = ISRC.substring(7);
        int isr = Integer.parseInt(s);
        String isrcName = ISRC.substring(0, 8);

        System.out.println("write method");
        try (CSVWriter csvWriter = new CSVWriter(new FileWriter(csvFilePath))) {
            String[] headerRecord = {"Fname", "Album title", "Album version", "UPC", "Catalog number", "Primary Artists", "Featuring artists", "Release date", "Main genre", "Main subgenre", "Alternate genre", "Alternate subgenre", "Label", "CLine year", "CLine name", "PLine year", "PLine name", "Parental advisory", "Recording year", "Recording location", "Album format", "Number of volumes", "Territories", "Excluded territories", "Language (Metadata)", "Catalog tier", "Track title", "Track version", "ISRC", "Track primary artists", "Featuring artists", "Volume number", "Track main genre", "Track main subgenre", "Track alternate genre", "Track alternate subgenre", "Track language (Metadata)", "Audio language", "Lyrics", "Available separately", "Track parental advisory", "Preview start", "Preview length", "Track recording year", "Track recording location", "Contributing artists", "Composer", "Lyrics", "Remixers", "Performers", "Producers", "Writers", "Publishers", "Track sequence", "Track catalog tier", "Original file name", "Original release date"}; // CSV Head
            csvWriter.writeNext(headerRecord);
            int count = 0;
            int titlenum1 = startNum;
            String upc = null;
//        int u = Integer.parseInt(String.valueOf(UPC));
            int u = UPC;
            for (CsvSong cs : sgList) {
                String sg = cs.getSinger();
                String ly = cs.getLyrics();
                String com = cs.getComposer();
                String artist = null;
                if (ly.equalsIgnoreCase(PrimaryArtist) && com.equalsIgnoreCase(PrimaryArtist) && sg.equalsIgnoreCase(PrimaryArtist)) {
                    artist = PrimaryArtist;
//                    System.out.println(artist+"1");
                } else if (ly.equalsIgnoreCase(PrimaryArtist) && com.equalsIgnoreCase(PrimaryArtist)) {
                    artist = ly + "|" + com + "|" + sg;
//                    System.out.println(artist+"2");
                } else if (sg.equalsIgnoreCase(PrimaryArtist) && ly.equalsIgnoreCase(PrimaryArtist)) {

                    artist = ly + "|" + sg + "|" + com;
//                    System.out.println(artist+"3");
                } else if (com.equalsIgnoreCase(PrimaryArtist) && sg.equalsIgnoreCase(PrimaryArtist)) {
                    artist = com + "|" + sg + "|" + ly;
                } else if (com.equalsIgnoreCase(PrimaryArtist)) {
                    artist = com + "|" + sg + "|" + ly;
                } else if (ly.equalsIgnoreCase(PrimaryArtist)) {
                    artist = ly + "|" + sg + "|" + com;
                } else if (sg.equalsIgnoreCase(PrimaryArtist)) {
                    artist = sg + "|" + ly + "|" + com;
                } else {
                    artist = null;
                }
                //add UPC
//                UPC1 = String.valueOf(u);
                UPC1 = upcArray[u];
                titl = String.valueOf(titlenum1);
                count++;
                isr++;


//                String catelog = "TEST CATELOG";
//                String PrimaryArtist = artist;
                String albumTitle1 = albumTitle + ", Vol. 0" + titl;
                LocalDate currentDate = LocalDate.now();
                DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
                String releasedate = currentDate.format(dateFormatter);
                String year[] = releasedate.split("/");
                String curruntYear = year[0];

//                String s= ISRC.substring(7);
//                int isr = Integer.parseInt(s);
//                String isrcName = ISRC.substring(0,7);

                String isrc = isrcName + isr;

                String trackPrimaryArtist = ly + " | " + sg + " | " + com;
                String compoNlyrics = com + " | " + ly;
                if (count % 25 == 0) {
                    u = u + 1;
                    titlenum1 = titlenum1 + 1;
//                    albumTitle1 = albumTitle+". Vol. "+titl;
                }

                String[] dataRecord = {"", albumTitle1, "", UPC1, catelog, PrimaryArtist, "", releasedate, "pop", "", "", "", "CeyMusic Records", curruntYear, "CeyMusic Publishing", curruntYear, "CeyMusic Records", "N", curruntYear, "Sri Lanka", "Album", "1", "World", "", "Si", "", cs.getSongTitle(), "", isrc, trackPrimaryArtist, cs.getSinger(), "1", "pop", "", "", "", "SI", "SI", "", "Y", "N", "0", "120", curruntYear, "Sri Lanka", "", cs.getComposer(), cs.getLyrics(), "", "", "", compoNlyrics, "CeyMusic Publishing", String.valueOf(count), "Mid", cs.getFileName(), releasedate};
                csvWriter.writeNext(dataRecord);

//COMMENTED FOR TEST WORKING CODE
//            String[] dataRecord = {cs.getSongTitle(), cs.getFileName(),cs.getSinger(),cs.getLyrics(),cs.getComposer(),artist,UPC };
//            csvWriter.writeNext(dataRecord);
                //PU TO  THIS
            }

            System.out.println("CSV file written successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static int getCount(int count) {
        int i = 1;
        for (int a = 0; a <= 200; a++) {
            if (count % 25 == 0) {
                i++;
            }
        }

        return i;
    }

    private static CsvSong getReport(String[] nextLine) {
        CsvSong repo = new CsvSong();
        repo.setSongTitle(nextLine[0]);
        repo.setFileName(nextLine[1]);
        repo.setSinger(nextLine[2]);
        repo.setLyrics(nextLine[3]);
        repo.setComposer(nextLine[4]);

        return repo;

    }


    //new method to read CSV FILE
    /**
     public static List<CsvSong> readCSVFile(String csvFilePath, String primaryArt) {
     List<CsvSong> songList = new ArrayList<>();
     System.out.println("read");
     System.out.println(csvFilePath+"-"+"file path");
     try (CSVReader csvReader = new CSVReaderBuilder(new FileReader(csvFilePath)).withSkipLines(1).build()) {

     List<String[]> records = csvReader.readAll();


     String[] header = records.get(0);
     int titleIndex = -1;
     int filenameIndex = -1;
     int singerIndex = -1;
     int lyricsIndex = -1;
     int composerIndex = -1;


     // Find the indices of the columns
     for (int i = 0; i < header.length; i++) {
     //                System.out.println(header.length);
     if ("title".equalsIgnoreCase(header[i])) {
     titleIndex = i;
     //                    System.out.println(titleIndex);
     } else if ("filename".equalsIgnoreCase(header[i])) {
     filenameIndex = i;
     //                    System.out.println(filenameIndex);
     } else if ("singer".equalsIgnoreCase(header[i])) {
     singerIndex = i;
     } else if ("lyrics".equalsIgnoreCase(header[i])) {
     lyricsIndex = i;
     } else if ("composer".equalsIgnoreCase(header[i])) {
     composerIndex = i;
     }

     }

     System.out.println(titleIndex);
     System.out.println(filenameIndex);
     System.out.println(singerIndex);
     System.out.println(lyricsIndex);
     System.out.println(composerIndex);

     if (titleIndex != -1 && filenameIndex != -1 && singerIndex != -1 && lyricsIndex != -1 && composerIndex != -1) {
     for (String[] record : records.subList(1, records.size())) {
     CsvSong song = new CsvSong(record[titleIndex], record[filenameIndex], record[singerIndex], record[lyricsIndex], record[composerIndex]);
     System.out.println(song+"song");
     System.out.println(song.getSongTitle());
     System.out.println(song.getLyrics());
     System.out.println(song.getComposer());
     System.out.println(song.getSinger());
     System.out.println(song.getTrackPrimaryArtist());

     songList.add(song);
     }
     } else {
     System.out.println("Columns not found in the CSV file.");
     }

     } catch (IOException | CsvException e) {
     e.printStackTrace();
     }

     return songList;
     }
     */
//    --------

    /**
     * public static List<CsvSong> readCSVFile(String csvFilePath, String primaryArt) {
     * //    public static List<CsvSong> readSongsFromCSV(String filePath) {
     * List<CsvSong> songs = new ArrayList<>();
     * System.out.println("CSV READER METHOD");
     * <p>
     * <p>
     * try (CSVReader csvReader = new CSVReaderBuilder(new FileReader(csvFilePath)).withSkipLines(1).build()) {
     * String[] headers = csvReader.readNext(); // Read the header row to get column names
     * int titleIndex = getIndex1(headers, "title");
     * int fileNameIndex = getIndex1(headers, "fileName");
     * int singerIndex = getIndex1(headers, "singer");
     * int lyricsIndex = getIndex1(headers, "lyrics");
     * int composerIndex = getIndex1(headers, "composer");
     * System.out.println(titleIndex+ "-"+"title index");
     * System.out.println(fileNameIndex);
     * <p>
     * <p>
     * <p>
     * if (titleIndex == -1 || fileNameIndex == -1 || singerIndex == -1 || lyricsIndex == -1 || composerIndex == -1) {
     * throw new CsvValidationException("One or more required columns not found in CSV file.");
     * }
     * <p>
     * String[] record;
     * while ((record = csvReader.readNext()) != null) {
     * String title = record[titleIndex];
     * String fileName = record[fileNameIndex];
     * String singer = record[singerIndex];
     * String lyrics = record[lyricsIndex];
     * String composer = record[composerIndex];
     * <p>
     * CsvSong song = new CsvSong(title, fileName, singer, lyrics, composer);
     * songs.add(song);
     * }
     * } catch (IOException | CsvException e) {
     * e.printStackTrace();
     * }
     * <p>
     * return songs;
     */
//}

// Get index method
    private static int getIndex1(String[] headers, String columnName) {
        for (int i = 0; i < headers.length; i++) {
            System.out.println(headers.length);
            System.out.println(columnName);
            if (columnName.equalsIgnoreCase(headers[i])) {
                return i;
            }
        }
        return -1; // Column not found
    }

    //new -----
    public static List<String> readColumnData(String csvFilePath, String columnName) {
        List<String> columnData = new ArrayList<>();

        try (CSVReader csvReader = new CSVReaderBuilder(new FileReader(csvFilePath)).withSkipLines(1).build()) {
            String[] headers = csvReader.readNext(); // Read the header row to get column names
            int columnIndex = getIndex1(headers, columnName);

            if (columnIndex == -1) {
                throw new CsvException("Column '" + columnName + "' not found in CSV file.");
            }

            String[] record;
            while ((record = csvReader.readNext()) != null) {
                String data = record[columnIndex].trim(); // Trim whitespace from the data
                columnData.add(data);
            }
        } catch (IOException | CsvException e) {
            e.printStackTrace();
        }

        return columnData;
    }

    //----

    //    ------------
    //new method for csv read
    public static void writeCSVFile(String csvFilePath, List<CsvSong> songList, int upc) throws IOException {
        try (CSVWriter csvWriter = new CSVWriter(new FileWriter("csvFilePath"))) {
            csvWriter.writeNext(new String[]{"title", "filename", "singer", "lyrics", "composer"});

            for (CsvSong song : songList) {
                csvWriter.writeNext(new String[]{song.getSongTitle(), song.getFileName(), song.getSinger(), song.getLyrics(), song.getComposer()

                });
            }

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

                int size = csvController.writeMissingISRCs();

                Platform.runLater(() -> {
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
                csvController.setFUGAReport(report);
                int status = csvController.loadFUGAReport(lblReportProgress, imgImportCaution, lbl_import);
                // boolean status = DatabaseMySQL.loadReport(report, lblReportProgress, lbl_import, imgImportCaution);

                Platform.runLater(() -> {
                    if (status > 0) {
                        lblReportProgress.setText("CSV Imported to Database");
                        imgImportCaution.setVisible(true);
                    }
                });
                return null;
            }
        };
        return task;
    }

    private Task<Void> loadReport1(File report) {
        Task<Void> task;
        return null;

    }

    public void onGetReportBtnClick(MouseEvent mouseEvent) throws IOException {
        System.out.println("ControllerRevenueGenerator.onGetReportBtnClick");
        String payee = comboPayees.getSelectionModel().getSelectedItem();

        comboPayees.setStyle("-fx-border-color: '#e9ebee';");

        Node node = (Node) mouseEvent.getSource();
        Scene scene = node.getScene();
        Window window = scene.getWindow();

        if (!Objects.equals(payee, null)) {
            ReportPDF reportPDF = new ReportPDF();
            try {
                reportPDF.generateReport(window, report);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            // If no Payee Selected
            comboPayees.setStyle("-fx-border-color: red;");
        }
    }
}
