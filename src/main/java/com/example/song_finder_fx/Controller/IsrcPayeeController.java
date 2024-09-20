package com.example.song_finder_fx.Controller;

import com.example.song_finder_fx.DatabasePostgres;
import com.example.song_finder_fx.Model.Payee;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.List;

public class IsrcPayeeController {


    public static void main(String[] args) {
        Payee p = new Payee();
        String s = "ATR981202329";
        p = getPayeeByIsc(s);
        System.out.println(p.getPayee1() + "  " + p.getPayee2() + "  " + p.getPayee3());
    }


    //GET ISRC PAYEE DETAIL BY ISRC
    public static Payee getPayeeByIsc(String isrc) {
        String sql = "SELECT isrc,payee,share,payee01,payee01share,payee02,payee02share,payee03,payee03share  from isrc_payees where isrc = ? ";
//        Connection con = DatabasePostgres.getConn();
        Payee py = new Payee();
        Connection con = DatabasePostgres.getConn();

        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, isrc);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                py.setIsrc(rs.getString(1));
                py.setPayee1(rs.getString(2));
                py.setShare1(rs.getString(3));
                py.setPayee2(rs.getString(4));
                py.setShare2(rs.getString(5));
                py.setPayee3(rs.getString(6));
                py.setShare3(rs.getString(7));

            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DatabasePostgres.closeConnection(con);
        }
        return py;
    }


    //GET ISRCPAYEE LIST BY PAYEE NAME
    public List<Payee> getPayeeByAtrist(String atrist) {
        String sql = "SELECT isrc,payee,share,payee01,payee01share,payee02,payee02share,payee03,payee03share  from isrc_payees " +
                "where payee = ? or payee01 = ? or payee02 = ? ";
        Connection con = DatabasePostgres.getConn();
        List<Payee> list = new ArrayList<>();
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, atrist);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Payee py= new Payee()  ;
                py.setIsrc(rs.getString(1));
                py.setPayee1(rs.getString(2));
                py.setShare1(rs.getString(3));
                py.setPayee2(rs.getString(4));
                py.setShare2(rs.getString(5));
                py.setPayee3(rs.getString(6));
                py.setShare3(rs.getString(7));

                list.add(py);
            }

        } catch (Exception e) {
            e.printStackTrace();

        }


        return list;
    }

    public void updateIsrcPayee(Payee py) {
        Connection con = DatabasePostgres.getConn();
        String sql = "UPDATE isrc_payees SET payee = ?,share = ?,payee01 = ?,payee01share = ?,payee02 = ?,payee02share = ? where isrc = ? ";
        boolean bl =false;
        try {
            PreparedStatement ps =  con.prepareStatement(sql);
            ps.setString(1, py.getPayee1() != null ? py.getPayee1() : "");
            ps.setString(2, py.getShare1() != null ? py.getShare1() : "");
            ps.setString(3, py.getPayee2() != null ? py.getPayee2() : "");
            ps.setString(4, py.getShare2() != null ? py.getShare2() : "");
            ps.setString(5, py.getPayee3() != null ? py.getPayee3() : "");
            ps.setString(6, py.getShare3() != null ? py.getShare3() : "");
            ps.setString(7, py.getIsrc() != null ? py.getIsrc() : "");
            bl = ps.executeUpdate()>0 ? true : false;
        } catch (Exception e) {
            e.printStackTrace();


        }
    }

}


