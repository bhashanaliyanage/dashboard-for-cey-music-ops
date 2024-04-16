package com.example.song_finder_fx.Controller;

import com.example.song_finder_fx.DatabasePostgres;
import com.example.song_finder_fx.Model.ArtistReport;
import com.example.song_finder_fx.Model.Songs;

import java.sql.SQLException;
import java.util.ArrayList;

public class RevenueReportController {
    private final ArtistReport report;

    public RevenueReportController(ArtistReport report) {
        this.report = report;
    }

    public ArtistReport getReport() {
        return report;
    }

    public ArtistReport calculateRevenue() throws SQLException, ClassNotFoundException {
        // Getting artist name
        ArtistController artistController = new ArtistController(report.getArtist());
        String artistName = artistController.fetchArtistName();
        report.getArtist().setName(artistName);

        // Getting gross revenue and partner share
        // TODO: 4/16/2024 Make this checks all three columns in the database
        ArrayList<Double> grossNPartnerShare = DatabasePostgres.getPayeeGrossRev(artistName);
        Double grossRevenue = grossNPartnerShare.get(0);
        Double partnerShare = grossNPartnerShare.get(1);
        report.setGrossRevenue(grossRevenue);
        report.setPartnerShare(partnerShare);

        // Getting top 5 most performing songs
        // TODO: 4/16/2024 Make this query gets data from ReportViewSummary1 view
        ArrayList<Songs> topP_Songs = DatabasePostgres.getTopPerformingSongs(artistName); // This object only contains ISRC and Revenue for now. Need to get Song Name
        report.setTopPerformingSongs(topP_Songs);

        // Co-Writer Share
        // TODO: 4/16/2024 Get Co-Writer Payment Summary from PostgresSQL database

        return report;
    }
}
