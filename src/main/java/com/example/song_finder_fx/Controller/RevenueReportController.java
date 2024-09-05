package com.example.song_finder_fx.Controller;

import com.example.song_finder_fx.DatabasePostgres;
import com.example.song_finder_fx.Model.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public record RevenueReportController(ArtistReport report) {

    public ArtistReport calculateRevenue(boolean includeTerritoryAndDSPBreakdown) throws SQLException {
        // Refreshing Tables
        // DatabasePostgres.refreshSummaryTable(report.getMonthInt(), report.getYear());

        // Getting gross revenue and partner share
        System.out.println("Getting gross revenue and partner share");
        RevenueReport grossNPartnerShare = DatabasePostgres.getPayeeGrossRevNew(report);
        Double grossRevenue = grossNPartnerShare.getReportedRoyalty();
        Double partnerShare = grossNPartnerShare.getAfterDeductionRoyalty();
        report.setGrossRevenue(grossRevenue);
        report.setPartnerShare(partnerShare);

        // Getting top 5 most performing songs
        System.out.println("Getting top performing songs");
        ArrayList<Songs> topP_Songs = DatabasePostgres.getTopPerformingSongs(report.getArtist().getName()); // This object only contains ISRC and Revenue for now. Need to get Song Name
        report.setTopPerformingSongs(topP_Songs);

        // Getting Co-Writer Payments
        System.out.println("Getting Asset Breakdown");
        report.setAssetBreakdown(DatabasePostgres.getAssetBreakdown(report.getArtist().getName()));
        System.out.println("Summarizing co-writer payments");
        List<CoWriterSummary> coWriterSummaryList = DatabasePostgres.getCoWriterPaymentSummary(report.getArtist().getName());
        report.setCoWriterPaymentSummary(coWriterSummaryList);

        if (includeTerritoryAndDSPBreakdown) {
            System.out.println("Getting Territory Breakdown");
            List<TerritoryBreakdown> territoryBreakdownList = DatabasePostgres.getTerritoryBreakdown(report);
            report.setTerritoryBreakdown(territoryBreakdownList);

            System.out.println("Getting DSP Breakdown");
            List<DSPBreakdown> dspBreakdownList = DatabasePostgres.getDSPBreakdown(report);
            report.setDSPBreakdown(dspBreakdownList);
        }

        return report;
    }
}
