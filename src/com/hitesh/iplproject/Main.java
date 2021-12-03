package com.hitesh.iplproject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;

public class Main {

    public static final int MATCH_ID = 0;
    public static final int INNING = 1;
    public static final int BATTING_TEAM = 2;
    public static final int BOWLING_TEAM = 3;
    public static final int OVER = 4;
    public static final int BALL = 5;
    public static final int BATSMAN = 6;
    public static final int NON_STRIKER = 7;
    public static final int BOWLER = 8;
    public static final int IS_SUPER_OVER = 9;
    public static final int WIDE_RUNS = 10;
    public static final int BYE_RUNS = 11;
    public static final int LEGBYE_RUNS = 12;
    public static final int NO_BALL_RUNS = 13;
    public static final int PANELTY_RUNS = 14;
    public static final int BATSMAN_RUNS = 15;
    public static final int EXTRA_RUNS = 16;
    public static final int TOTAL_RUNS = 17;
    public static final int PLAYER_DISMISSED = 18;
    public static final int DISMISSAL_KIND = 19;
    public static final int FIELDER = 20;

    public static final int ID = 0;
    public static final int SEASON = 1;
    public static final int CITY = 2;
    public static final int DATE = 3;
    public static final int TEAM_1 = 4;
    public static final int TEAM_2 = 5;
    public static final int TOSS_WINNER = 6;
    public static final int TOSS_DECISION = 7;
    public static final int RESULT = 8;
    public static final int DL_APPLIED = 9;
    public static final int WINNER = 10;
    public static final int WIN_BY_RUNS = 11;
    public static final int WIN_BY_WICKETS = 12;
    public static final int PLAYER_OF_MATCH = 13;
    public static final int VENUE = 14;
    public static final int UMPIRE_1 = 15;
    public static final int UMPIRE_2 = 16;
    public static final int UMPIRE_3 = 17;

    public static void main(String[] args) throws IOException {
        List<Match> matches = getMatchesData();
        List<Delivery> deliveries = getDeliveriesData();

        findNumberOfMatchesPlayed(matches);
        findNumberOfMatchesWonPerTeam(matches);
        findExtraRunsConcededPerTeamIn2016(matches, deliveries);
        findTheMostEconomicalBowlerIn2015(matches, deliveries);
        findLegbyeRunsConcededPerTeamin2013(matches, deliveries);
    }

    private static void findNumberOfMatchesPlayed(List<Match> matches) {
        HashMap<Integer, Integer> matchBySeason = new HashMap<Integer, Integer>();

        for (Match match : matches) {
            if (matchBySeason.containsKey(match.getSeason())) {
                matchBySeason.put(match.getSeason(), matchBySeason.get(match.getSeason()) + 1);
            } else {
                matchBySeason.put(match.getSeason(), 1);
            }
        }
        int firstYear = 2015;
        int secondYear = 2013;

        Set<Entry<Integer, Integer>> matchesSet = matchBySeason.entrySet();
        Iterator<Entry<Integer, Integer>> iterateSet = matchesSet.iterator();
        Entry<Integer, Integer> oneEntry = null;
        int year = 0;

        int totalMatches = 0;
        while(iterateSet.hasNext()){
              oneEntry = iterateSet.next();

              year = oneEntry.getKey();

              if((year >= firstYear &&  year <= secondYear) || (year >= secondYear && year <= firstYear)){
                  totalMatches += oneEntry.getValue();
              }
        }
        System.out.println(totalMatches);

        System.out.println(matchBySeason);
    }

    private static void findNumberOfMatchesWonPerTeam(List<Match> matches) {
        HashMap<String, HashMap<Integer, Integer>> matchWonPerTeam = new HashMap<String, HashMap<Integer, Integer>>();

        for (Match match : matches) {
            if (matchWonPerTeam.containsKey(match.getWinner())) {
                HashMap<Integer, Integer> subMap = matchWonPerTeam.get(match.getWinner());
                if (subMap.containsKey(match.getSeason())) {
                    subMap.put(match.getSeason(), subMap.get(match.getSeason()) + 1);
                } else {
                    subMap.put(match.getSeason(), 1);
                }
                matchWonPerTeam.put(match.getWinner(), subMap);
            } else {
                HashMap<Integer, Integer> yearCount = new HashMap<>();
                yearCount.put(match.getSeason(), 1);
                matchWonPerTeam.put(match.getWinner(), yearCount);
            }
        }
        System.out.println(matchWonPerTeam);
    }

    private static void findExtraRunsConcededPerTeamIn2016(List<Match> matches, List<Delivery> deliveries) {
        List<Integer> matchesIdList = findIdOfMatchesOfYear(matches, 2015);
        HashMap<String, Integer> teamExtraRuns = new HashMap<>();

        for (int i = 0; i < matchesIdList.size(); i++) {
            int deliveriesId = matchesIdList.get(i);
            Iterator<Delivery> deliveriesIterator = deliveries.iterator();

            while (deliveriesIterator.hasNext()) {
                Delivery delivery = deliveriesIterator.next();
                if (deliveriesId == delivery.getMatchId()) {
                    if (teamExtraRuns.containsKey(delivery.getBattingTeam())) {
                        teamExtraRuns.put(delivery.getBattingTeam(),
                                teamExtraRuns.get(delivery.getBattingTeam()) + delivery.getExtraRuns());
                    } else {
                        teamExtraRuns.put(delivery.getBattingTeam(), delivery.getExtraRuns());
                    }
                }
            }
        }
        System.out.println(teamExtraRuns);
    }

    private static void findTheMostEconomicalBowlerIn2015(List<Match> matches, List<Delivery> deliveries) {
        List<Integer> match = findIdOfMatchesOfYear(matches, 2015);
        HashMap<String, Bowler> bowlerTotalBallsRuns = new HashMap<>();

        Bowler bowler = null;
        int totalRuns = 0;
        int totalBalls = 0;
        for (int i = 0; i < match.size(); i++) {
            Iterator<Delivery> deliveryIterator = deliveries.iterator();
            int deliveryId = match.get(i);
            while (deliveryIterator.hasNext()) {
                Delivery delivery = deliveryIterator.next();
                if (deliveryId == delivery.getMatchId()) {
                    if (bowlerTotalBallsRuns.containsKey(delivery.getBowler())) {
                        bowler = bowlerTotalBallsRuns.get(delivery.getBowler());
                        bowler.setBalls(bowler.getBalls() + 1);
                        bowler.setTotalRun(bowler.getTotalRun() + delivery.getTotalRuns());
                        bowlerTotalBallsRuns.put(delivery.getBowler(), bowler);
                    } else {
                        totalRuns = delivery.getTotalRuns();
                        totalBalls = 1;
                        bowler = new Bowler();
                        bowler.setTotalRun(totalRuns);
                        bowler.setBalls(totalBalls);
                        bowlerTotalBallsRuns.put(delivery.getBowler(), bowler);
                    }
                }
            }
        }

        HashMap<String, Double> economicBowler = new HashMap<>();
        Double economicValue = 0.0;

        Set<Map.Entry<String, Bowler>> setHashMap = bowlerTotalBallsRuns.entrySet();
        Iterator<Map.Entry<String, Bowler>> itrHashMap = setHashMap.iterator();

        while (itrHashMap.hasNext()) {
            Map.Entry<String, Bowler> entryHashMap = itrHashMap.next();
            Bowler bowlerObj = entryHashMap.getValue();

            economicValue = (bowlerObj.getTotalRun() / (bowlerObj.getBalls() / 6d));
            economicBowler.put(entryHashMap.getKey(), economicValue);
        }

        SortedSet<Entry<String,Double>> sortedEntries = new TreeSet<Entry<String,Double>>(
                new Comparator<Entry<String,Double>>() {
                    @Override
                    public int compare(Entry<String, Double> e1, Entry<String, Double> e2) {
                        return e1.getValue().compareTo(e2.getValue());
                    }
                }
        );

        sortedEntries.addAll(economicBowler.entrySet());
        System.out.println(sortedEntries);
    }

    private static void findLegbyeRunsConcededPerTeamin2013(List<Match> matches, List<Delivery> deliveries) {
        List<Integer> matchesIdList = findIdOfMatchesOfYear(matches, 2013);
        HashMap<String, Integer> teamLegbyeRuns = new HashMap<>();

        for (int i = 0; i < matchesIdList.size(); i++) {
            Iterator<Delivery> itrList = deliveries.iterator();
            int deliveriesId = matchesIdList.get(i);

            while (itrList.hasNext()) {
                Delivery delivery = itrList.next();
                if (deliveriesId == delivery.getMatchId()) {
                    if (teamLegbyeRuns.containsKey(delivery.getBattingTeam())) {
                        teamLegbyeRuns.put(delivery.getBattingTeam(),
                                teamLegbyeRuns.get(delivery.getBattingTeam()) + delivery.getLegbyeRuns());
                    } else {
                        teamLegbyeRuns.put(delivery.getBattingTeam(), delivery.getLegbyeRuns());
                    }
                }
            }
        }
        System.out.println(teamLegbyeRuns);
    }

    public static List<Integer> findIdOfMatchesOfYear(List<Match> matches, int givenYear) {
        List<Integer> matchYearIDs = new ArrayList<>();
        Iterator<Match> itr = matches.iterator();

        while (itr.hasNext()) {
            Match match = itr.next();
           int season = match.getSeason();

            if (season == givenYear) {
               int id = match.getId();
                matchYearIDs.add(id);
            }
        }
        return matchYearIDs;
    }

    public static List<Delivery> getDeliveriesData() throws IOException {
        String line = "";
        String[] data = null;
        List<Delivery> deliveries = new ArrayList<>();
        BufferedReader bufferedReader =
                new BufferedReader(new FileReader("src/com/hitesh/iplproject/deliveries.csv"));
        bufferedReader.readLine();

        while ((line = bufferedReader.readLine()) != null) {
            data = line.split(",");
            Delivery delivery = new Delivery();
            delivery.setMatchId(Integer.parseInt(data[MATCH_ID]));
            delivery.setInning(Integer.parseInt(data[INNING]));
            delivery.setBattingTeam(data[BATTING_TEAM]);
            delivery.setBowlingTeam(data[BOWLING_TEAM]);
            delivery.setOver(Integer.parseInt(data[OVER]));
            delivery.setBall(Integer.parseInt(data[BALL]));
            delivery.setBatsman(data[BATSMAN]);
            delivery.setNonStriker(data[NON_STRIKER]);
            delivery.setBowler(data[BOWLER]);
            delivery.setIsSuperOver(Integer.parseInt(data[IS_SUPER_OVER]));
            delivery.setWideRuns(Integer.parseInt(data[WIDE_RUNS]));
            delivery.setByeRuns(Integer.parseInt(data[BYE_RUNS]));
            delivery.setLegbyeRuns(Integer.parseInt(data[LEGBYE_RUNS]));
            delivery.setNoballRuns(Integer.parseInt(data[NO_BALL_RUNS]));
            delivery.setPaneltyRuns(Integer.parseInt(data[PANELTY_RUNS]));
            delivery.setBatsmanRuns(Integer.parseInt(data[BATSMAN_RUNS]));
            delivery.setExtraRuns(Integer.parseInt(data[EXTRA_RUNS]));
            delivery.setTotalRuns(Integer.parseInt(data[TOTAL_RUNS]));
            if (data.length > 18)
                delivery.setPlayerDismissed(data[PLAYER_DISMISSED]);
            if (data.length > 19)
                delivery.setDismissalKind(data[DISMISSAL_KIND]);
            if (data.length > 20)
                delivery.setFielder(data[FIELDER]);

            deliveries.add(delivery);
        }
        bufferedReader.close();
        return deliveries;
    }

    public static List<Match> getMatchesData() throws IOException {
        String line = "";
        String[] data = null;
        List<Match> matches = new ArrayList<>();
        BufferedReader bufferedReader =
                new BufferedReader(new FileReader("src/com/hitesh/iplproject/matches.csv"));

        bufferedReader.readLine();

        while ((line = bufferedReader.readLine()) != null) {
            data = line.split(",");
            Match match = new Match();

            match.setId(Integer.parseInt(data[ID]));
            match.setSeason(Integer.parseInt(data[SEASON]));
            match.setCity(data[CITY]);
            match.setDate(data[DATE]);
            match.setTeam1(data[TEAM_1]);
            match.setTeam2(data[TEAM_2]);
            match.setTossWinner(data[TOSS_WINNER]);
            match.setTossDecision(data[TOSS_DECISION]);
            match.setResult(data[RESULT]);
            match.setDlApplied(Integer.parseInt(data[DL_APPLIED]));
            match.setWinner(data[WINNER]);
            match.setWinByRuns(Integer.parseInt(data[WIN_BY_RUNS]));
            match.setWinByWickets(Integer.parseInt(data[WIN_BY_WICKETS]));
            match.setPlayerOfMatch(data[PLAYER_OF_MATCH]);
            match.setVenue(data[VENUE]);
            if (data.length > 15)
                match.setUmpire1(data[UMPIRE_1]);
            if (data.length > 16)
                match.setUmpire2(data[UMPIRE_2]);
            if (data.length > 17)
                match.setUmpire3(data[UMPIRE_3]);

            matches.add(match);
        }
        bufferedReader.close();
        return matches;
    }

}
