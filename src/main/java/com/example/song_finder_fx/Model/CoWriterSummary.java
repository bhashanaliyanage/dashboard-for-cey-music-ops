package com.example.song_finder_fx.Model;

public class CoWriterSummary {
    public String getContributor() {
        return contributor;
    }

    public double getRoyalty() {
        return royalty/2;
    }

    private final String contributor;
    private final double royalty;

    public CoWriterSummary(String contributor, double royalty) {
        this.contributor = contributor;
        this.royalty = royalty;
    }
}
