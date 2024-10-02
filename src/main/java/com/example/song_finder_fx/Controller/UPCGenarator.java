package com.example.song_finder_fx.Controller;

import com.example.song_finder_fx.DatabasePostgres;
import com.example.song_finder_fx.Model.UpcData;

import java.util.List;

public class UPCGenarator {

    DatabasePostgres db = new DatabasePostgres();

    public int getUpcAvailableCount() {

        int i = db.getUpcCount();

        return i;
    }

    public String getUpc(UpcData upcData) {
        String s = "";
        boolean bl = db.addDataToUpc(upcData);
        if (bl == true) {
            s = "Success";
        }
        return s;
    }

    public String removeUpcData(List<String> upcList) {
        String s = "";

        Boolean bl = db.removeUpc(upcList);
        if (bl == true) {
            s="Succesfully Removed";

        }else{
            s="Failed";
        }
        return s;
    }

}
