package com.example.song_finder_fx.Controller;

import com.example.song_finder_fx.DatabasePostgres;
import com.example.song_finder_fx.Model.Ingest;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class IngestController {

    public List<Ingest> getCreatedIngests() throws SQLException, IOException {
        return DatabasePostgres.getAllIngests();
    }

        public List<String> getMissingPayeeList() {

        List<String> stringList = new ArrayList<>();
            Connection con = DatabasePostgres.getConn();
            List<String> isrcList = new ArrayList<String>();
            String sql = "select asset_isrc FROM report WHERE asset_isrc NOT IN(SELECT isrc FROM isrc_payees )";
            try {
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    String isrc = rs.getString(1);
                    isrcList.add(isrc);

                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                DatabasePostgres.closeConnection(con);
            }
            return isrcList;
        }

        public List<String>  getMissingSongs(){

            List<String> isrcList = new ArrayList<String>();
            Connection con = DatabasePostgres.getConn();
            String sql = "SELECT asset_isrc FROM report WHERE asset_isrc NOT IN (SELECT isrc FROM songs)";
            try {
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery();

                while (rs.next()) {
                    String isrc = rs.getString(1);
                    isrcList.add(isrc);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                DatabasePostgres.closeConnection(con);
            }
            return isrcList;

        }

}
