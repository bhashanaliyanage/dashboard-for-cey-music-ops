package com.example.song_finder_fx.Controller;

import com.example.song_finder_fx.DatabasePostgres;
import com.example.song_finder_fx.Model.UpcData;

import java.util.ArrayList;
import java.util.List;

public class UPCGenarator {

    DatabasePostgres db = new DatabasePostgres();


    //Get Available UPC Count
    public int getUpcAvailableCount() {

        int i = db.getUpcCount();

        return i;
    }


    //UPC DATA INSERT
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



    //GET UPC NUMBER LIST FROM UPC
    public List<String> viewUpcList(int count,int uid){
        List<String> list =  new ArrayList<>();
        list =db.viewUpcList(count,uid);
        return list;
    }
    private boolean allocateUser(int uid, List<String> list) {
        boolean bl = false;
        bl = db.allocateUser(uid, list);
        return  bl;
    }



    public  List<String> upcListByAllocateUser(int uid){
            List<String> list =  new ArrayList<>();
        list = db.upcListByAllocateUser(uid);


     return  list;
    }


}
