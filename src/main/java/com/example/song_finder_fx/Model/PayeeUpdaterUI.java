package com.example.song_finder_fx.Model;

import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;

public class PayeeUpdaterUI {

    private CheckBox cbEntry;

    private Label lblContributor01;

    private Label lblContributor02;

    private Label lblISRC;

    private Label lblPayee01;

    private Label lblPayee02;

    // private Label lblPayee03;

    private Label lblTrackName;

    private IngestCSVData data;

    public PayeeUpdaterUI(CheckBox cbEntry, Label lblContributor01, Label lblContributor02, Label lblISRC, Label lblPayee01, Label lblPayee02, Label lblTrackName, IngestCSVData data) {

        this.cbEntry = cbEntry;

        this.lblContributor01 = lblContributor01;

        this.lblContributor02 = lblContributor02;

        this.lblISRC = lblISRC;

        this.lblPayee01 = lblPayee01;

        this.lblPayee02 = lblPayee02;

        // this.lblPayee03 = lblPayee03;

        this.lblTrackName = lblTrackName;

        this.data = data;
    }

    public CheckBox getCbEntry() {
        return cbEntry;
    }

    public void setCbEntry(CheckBox cbEntry) {
        this.cbEntry = cbEntry;
    }

    public Label getLblContributor01() {
        return lblContributor01;
    }

    public void setLblContributor01(Label lblContributor01) {
        this.lblContributor01 = lblContributor01;
    }

    public Label getLblContributor02() {
        return lblContributor02;
    }

    public void setLblContributor02(Label lblContributor02) {
        this.lblContributor02 = lblContributor02;
    }

    public Label getLblISRC() {
        return lblISRC;
    }

    public void setLblISRC(Label lblISRC) {
        this.lblISRC = lblISRC;
    }

    public Label getLblPayee01() {
        return lblPayee01;
    }

    public void setLblPayee01(Label lblPayee01) {
        this.lblPayee01 = lblPayee01;
    }

    public Label getLblPayee02() {
        return lblPayee02;
    }

    public void setLblPayee02(Label lblPayee02) {
        this.lblPayee02 = lblPayee02;
    }

    /*public Label getLblPayee03() {
        return lblPayee03;
    }*/

    /*public void setLblPayee03(Label lblPayee03) {
        this.lblPayee03 = lblPayee03;
    }*/

    public Label getLblTrackName() {
        return lblTrackName;
    }

    public void setLblTrackName(Label lblTrackName) {
        this.lblTrackName = lblTrackName;
    }

    public IngestCSVData getData() {
        return data;
    }

    public void setData(IngestCSVData data) {
        this.data = data;
    }
}
