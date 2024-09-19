package com.example.song_finder_fx.Controller;

import com.example.song_finder_fx.DatabasePostgres;

import java.sql.SQLException;
import java.util.List;

public class ISRCDispatcher {
    public String dispatchSingleISRC(String assetType) throws SQLException {
        String lastISRC = DatabasePostgres.getLastISRC(assetType);
        return incrementISRC(lastISRC, assetType);
    }

    public List<String> dispatchMultipleISRCs(int count, String assetType) {
        return null;
    }

    public boolean updateLastISRC(String isrc, String assetType) throws SQLException {
        return DatabasePostgres.updateLastISRC(isrc, assetType);
    }

    private String incrementISRC(String lastISRC, String assetType) {
        if (lastISRC != null && assetType != null) {
            String prefix = assetType.equals("UGC") ? "LKA0U" : "LKA0W";
            int serialNumber = Integer.parseInt(lastISRC.substring(5));
            serialNumber++;
            return prefix + String.format("%07d", serialNumber);
        } else {
            System.out.println("Cannot increment ISRC because one or more inputs are null");
            System.out.println("Last ISRC: " + lastISRC);
            System.out.println("Asset Type: " + assetType);
            return null;
        }
    }
}
