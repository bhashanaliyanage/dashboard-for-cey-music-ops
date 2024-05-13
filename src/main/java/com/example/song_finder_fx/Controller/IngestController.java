package com.example.song_finder_fx.Controller;

import com.example.song_finder_fx.DatabasePostgres;
import com.example.song_finder_fx.Model.Ingest;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class IngestController {

    public List<Ingest> getCreatedIngests() throws SQLException, IOException {
        return DatabasePostgres.getAllIngests();
    }
}
