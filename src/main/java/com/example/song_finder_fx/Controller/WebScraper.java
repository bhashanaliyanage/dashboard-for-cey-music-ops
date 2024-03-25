package com.example.song_finder_fx.Controller;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class WebScraper {
    public static void main(String[] args) throws IOException {
        Document document = Jsoup.connect("https://www.ssyoutube.com/watch?v=BEvRZUEQ3Dc").get();
        Elements body = document.getAllElements();
        for (Element e : body.select("div")) {
            System.out.println("e.ownText() = " + e);
        }
    }
}
