package com.example.song_finder_fx.Session;

import com.example.song_finder_fx.DatabasePostgres;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Random;

public class UserSummary {
    private final UserSession userSession;

    public UserSummary(UserSession userSession) {
        this.userSession = userSession;
    }

    public String getGreeting() {
        String[] GREETINGS = {
                "Hello, %s!",
                "Hi there, %s!",
                "Greetings, %s!",
                "Hey, %s!",
                "Howdy, %s!",
                "Bonjour, %s!",
                "Salut, %s!",
                "Hola, %s!",
                "Namaste, %s!"
        };

        Random random = new Random();
        int index = random.nextInt(GREETINGS.length);
        String greeting = GREETINGS[index];
        return String.format(greeting, userSession.getNickName());
    }

    public String getManualClaimCount() {
        String[] manualClaimsSubText = {
                "%s claims are waiting for your review. Let's dive in!",
                "You have %s claims to ingest today. Ready to begin?",
                "%s manual claims need your attention today.",
                "There are %s claims to be ingested today.",
                "%s claims are queued for ingestion today.",
                "You have %s manual claims to review today.",
                "%s manual claims await your input.",
                "%s claims need to be ingested today.",
                "There are %s claims to ingest today.",
                "%s manual claims pending for today."
        };
        Random random = new Random();
        int index = random.nextInt(manualClaimsSubText.length);
        String text = manualClaimsSubText[index];
        String finalText;
        try {
            String claimCount = DatabasePostgres.getManualClaimCount();
            finalText = String.format(text, claimCount);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return finalText;
    }

    public String getReportDayCount() {
        String[] reportsDueSubText = {
                "Your next report is due in %s days.",
                "You need to import the next report in %s days.",
                "Only %s days left to import the next report.",
                "Just %s days remaining to import the next report.",
                "Report import deadline is in %s days.",
                "You have %s days to import the next report.",
                "Import the next report in %s days.",
                "Next report needs to be imported in %s days.",
                "%s days left to submit the next report.",
                "Deadline for the next report import is in %s days."
        };

        LocalDate today = LocalDate.now();
        LocalDate twentiethOfMonth = today.withDayOfMonth(20);
        long daysLeft = ChronoUnit.DAYS.between(today, twentiethOfMonth);
        Random random = new Random();
        int index = random.nextInt(reportsDueSubText.length);
        String text = reportsDueSubText[index];
        return String.format(text, daysLeft);
    }
}
