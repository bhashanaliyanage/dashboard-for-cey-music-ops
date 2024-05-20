package com.example.song_finder_fx.Controller;

import com.example.song_finder_fx.DatabasePostgres;
import com.example.song_finder_fx.Model.ArtistReport;
import com.example.song_finder_fx.Model.CoWriterSummary;
import com.example.song_finder_fx.Model.RevenueReport;
import com.example.song_finder_fx.Model.Songs;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public record RevenueReportController(ArtistReport report) {

    public ArtistReport calculateRevenue() throws SQLException {
        // Getting artist name
        // ArtistController artistController = new ArtistController(report.getArtist());
        // String artistName = artistController.fetchArtistName();
        // report.getArtist().setName(artistName);

        // Getting gross revenue and partner share
        RevenueReport grossNPartnerShare = DatabasePostgres.getPayeeGrossRev1(report.getArtist().getName());
        Double grossRevenue = grossNPartnerShare.getReportedRoyalty();
        Double partnerShare = grossNPartnerShare.getAfterDeductionRoyalty();
        report.setGrossRevenue(grossRevenue);
        report.setPartnerShare(partnerShare);

        // Getting top 5 most performing songs
        // TODO: 4/16/2024 Make this query gets data from ReportViewSummary1 view
        ArrayList<Songs> topP_Songs = DatabasePostgres.getTopPerformingSongs(report.getArtist().getName()); // This object only contains ISRC and Revenue for now. Need to get Song Name
        report.setTopPerformingSongs(topP_Songs);

        // Getting report month
        String dateString = DatabasePostgres.getSalesDate();
        String[] date = dateString.split("-");
        String month = date[1];
        report.setMonth(month);

        // Getting Co-Writer Payments
        report.setCoWritterList(DatabasePostgres.getCoWriterPayments(report.getArtist().getName()));
        List<CoWriterSummary> coWriterSummaryList = DatabasePostgres.getCoWriterPaymentSummary(report.getArtist().getName());
        report.setCoWriterPaymentSummary(coWriterSummaryList);

        return report;
    }
}
