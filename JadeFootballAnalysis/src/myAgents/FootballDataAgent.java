package myAgents;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import common.ILeagueTableUpdatedListener;
import common.IMatchDayMatchesUpdatedListener;
import common.ITeamMatchesUpdatedListener;
import common.Utils;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import org.jinq.orm.stream.JinqStream;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;

public class FootballDataAgent extends Agent
{
    private List<ILeagueTableUpdatedListener> _leagueTableUpdatedListeners = new ArrayList<>();
    private List<ITeamMatchesUpdatedListener> _teamMatchesUpdatedListeners = new ArrayList<>();
    private List<IMatchDayMatchesUpdatedListener> _matchDayMatchesUpdatedListeners = new ArrayList<>();

    public void addLeagueTableUpdatedListener(ILeagueTableUpdatedListener toAdd)
    {
        _leagueTableUpdatedListeners.add(toAdd);
    }

    public void addTeamMatchesUpdatedListener(ITeamMatchesUpdatedListener toAdd)
    {
        _teamMatchesUpdatedListeners.add(toAdd);
    }

    public void addMatchDayMatchesMatchesUpdatedListener(IMatchDayMatchesUpdatedListener toAdd)
    {
        _matchDayMatchesUpdatedListeners.add(toAdd);
    }

    @Override
    protected void takeDown()
    {
        doDelete();
    }

    private String[] extractTeams(String[][] teamMatches)
    {
        return Arrays.stream(Utils.arr2DToJinqStream(teamMatches).selectAllList(r -> Utils.toList(new String[] { r[1], r[2] })).sortedBy(m -> m).toArray(String[]::new)).distinct().toArray(String[]::new);
    }

    private Integer extractMatchDay(String[][] leagueTable)
    {
        return Utils.arr2DToJinqStream(leagueTable).select(r -> Utils.toInt(r[2])).sortedBy(md -> md).toList().get(0);
    }

    private String[][] getLeagueTable(String league)
    {
        String rawLeagueTable = getRawLeagueTable(leagueNameToLeagueId(league));
        Map<String, Object> hmapLeagueTable = (Map<String, Object>)Utils.jsonParse(rawLeagueTable);
        List<Map<String, String>> hmapStanding = (List<Map<String, String>>) hmapLeagueTable.get("standing"); // NIE List<Map<String, String>> bo rank, team itd są double, mogę dopiero po zmodyfikowaniu deserializera, że json był zawsze stringiem

        String[][] leagueTable = Utils.toJinqStream(hmapStanding).select(s -> new String[]
        {
            s.get("rank"),
            s.get("team"),
            s.get("playedGames"),
            s.get("points"),
            s.get("goals")
        }).toArray(String[][]::new);

        JinqStream.from(_leagueTableUpdatedListeners).forEach(l -> l.leagueTable_Updated(leagueTable));

        return leagueTable;
    }

    private String[][] getMatchDayMatches(String league, int matchDay)
    {
        int leagueId = leagueNameToLeagueId(league);
        String rawTeamMatches = getRawMatchDayMatches(leagueId, matchDay);
        Map<String, Object> hmapTeamMatches = (Map<String, Object>)Utils.jsonParse(rawTeamMatches);
        List<Map<String, Object>> hmapFixtures = (ArrayList<Map<String, Object>>) hmapTeamMatches.get("fixtures");

        String[][] matchDayMatches = Utils.toJinqStreamLso(hmapFixtures).select(m ->
        {
            Date date = Date.from(Instant.parse(m.get("date").toString()));
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
            String formattedDate = formatter.format(date);

            Map<String, String> hmapResult = (HashMap<String, String>)m.get("result");
            String result = hmapResult.get("goalsHomeTeam") + "-" + hmapResult.get("goalsAwayTeam");

            return new String[]
            {
                formattedDate,
                m.get("homeTeamName").toString(),
                m.get("awayTeamName").toString(),
                result
            };
        }).toArray(String[][]::new);

        JinqStream.from(_matchDayMatchesUpdatedListeners).forEach(l -> l.matchDayMatches_Updated(matchDayMatches));

        return matchDayMatches;
    }

    private String[][] getTeamMatches(String team, String league, Integer matchDay)
    {
        int leagueId = leagueNameToLeagueId(league);
        Map<String, Object> hmapTeamsResponse = (Map<String, Object>)Utils.jsonParse(getRawTeams(leagueId));
        List<Map<String, String>> hmapTeams = (List<Map<String, String>>)hmapTeamsResponse.get("teams");
        Integer teamId = Utils.toInt(Utils.toJinqStream(hmapTeams).select(t -> new String[] {
            t.get("id"),
            t.get("name")
        }).where(t -> Objects.equals(t[1], team)).toArray(String[][]::new)[0][0]);
        String rawTeamMatches = getRawTeamMatches(teamId);
        Map<String, Object> hmapTeamMatches = (Map<String, Object>)Utils.jsonParse(rawTeamMatches);
        List<Map<String, Object>> hmapFixtures = (ArrayList<Map<String, Object>>) hmapTeamMatches.get("fixtures");
        String[][] teamMatches =  Utils.toJinqStreamLso(hmapFixtures)
            .where(f -> Utils.toInt(f.get("competitionId").toString()) == leagueId && Utils.toInt(f.get("matchday")) <= matchDay)
            .select(m ->
            {
                Date date = Date.from(Instant.parse(m.get("date").toString()));
                SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
                String formattedDate = formatter.format(date);

                Map<String, String> hmapResult = (HashMap<String, String>) m.get("result");
                String result = hmapResult.get("goalsHomeTeam") + "-" + hmapResult.get("goalsAwayTeam");

                return new String[]
                {
                    formattedDate,
                    m.get("matchday").toString(),
                    m.get("homeTeamName").toString(),
                    m.get("awayTeamName").toString(),
                    result
                };
            })
            .sortedDescendingBy(m -> Utils.toInt(m[1]))
            .toArray(String[][]::new);

        JinqStream.from(_teamMatchesUpdatedListeners).forEach(l -> l.teamMatches_Updated(teamMatches));

        return teamMatches;
    }

    // curl -H 'X-Response-Control: minified' -H 'X-Auth-Token: cf33dbc36839445aab955953bd2c2807' -X GET http://api.football-data.org/v1/competitions/445/leagueTable | jq '.'
    private String getRawLeagueTable(int leagueId)
    {
        String uri = "http://api.football-data.org/v1/competitions/" + leagueId + "/leagueTable";
        return queryFootballDataApi(uri);
    }

    // curl -H 'X-Response-Control: minified' -H 'X-Auth-Token: cf33dbc36839445aab955953bd2c2807' -X GET http://api.football-data.org/v1/competitions/445/fixtures?matchday=28 | jq '.'
    private String getRawMatchDayMatches(int leagueId, int matchDay)
    {
        String uri = "http://api.football-data.org/v1/competitions/" + leagueId + "/fixtures/?matchday=" + matchDay;
        return queryFootballDataApi(uri);
    }

    // curl -H 'X-Response-Control: minified' -H 'X-Auth-Token: cf33dbc36839445aab955953bd2c2807' -X GET "http://api.football-data.org/v1/teams/66/fixtures?timeFrame=n14&venue=home" | jq '.'
    // curl -H 'X-Response-Control: minified' -H 'X-Auth-Token: cf33dbc36839445aab955953bd2c2807' -X GET http://api.football-data.org/v1/competitions/ | jq '.'
    private String getRawTeamMatches(int teamId)
    {
        String uri = "http://api.football-data.org/v1/teams/" + teamId + "/fixtures";
        return queryFootballDataApi(uri);
    }

    private String getRawTeams(int leagueId)
    {
        String uri = "http://api.football-data.org/v1/competitions/" + leagueId + "/teams";
        return queryFootballDataApi(uri);
    }

    private String queryFootballDataApi(String uri)
    {
        try
        {
            HttpURLConnection con = (HttpURLConnection) new URL(uri).openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("X-Auth-Token", "cf33dbc36839445aab955953bd2c2807");
            con.setRequestProperty("X-Response-Control", "minified");

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null)
                response.append(inputLine);
            in.close();
            return response.toString();
        }
        catch (IOException ex)
        {
            throw new Error(ex);
        }
    }

    private int leagueNameToLeagueId(String league)
    {
        try
        {
            if ("1 Liga Angielska".toLowerCase().equals(league.toLowerCase()))
                return 445;
            if ("1 Liga Niemiecka".toLowerCase().equals(league.toLowerCase()))
                return 452;
            if ("1 Liga Włoska".toLowerCase().equals(league.toLowerCase()))
                return 456;
            if ("1 Liga Hiszpańska".toLowerCase().equals(league.toLowerCase()))
                return 455;
            if ("1 Liga Francuska".toLowerCase().equals(league.toLowerCase()))
                return 450;
            throw new Exception("Liga nie jest wspierana");
        }
        catch (Exception ex)
        {
            throw new Error(ex);
        }
    }

    public void addSendDataToControllerAgentBehavior()
    {
        addBehaviour(new CyclicBehaviour(this)
        {
            @Override
            public void action()
            {
                ACLMessage aclMsg = receive();
                if (aclMsg == null)
                    block();
                else
                {
                    String rawReceivedMsg = aclMsg.getContent();
                    Map<String, Object> receivedMsg = (Map<String, Object>)Utils.jsonParse(rawReceivedMsg);
                    Gson gson = new GsonBuilder().create();

                    if (receivedMsg.containsKey("league"))
                    {
                        String league = receivedMsg.get("league").toString();
                        ACLMessage aclResponse = new ACLMessage(ACLMessage.INFORM);
                        aclResponse.addReceiver(new AID("ControllerAgent", AID.ISLOCALNAME));

                        Map<String, String> hmapMatchDay = new HashMap<>();
                        hmapMatchDay.put("matchDay", extractMatchDay(getLeagueTable(league)).toString());
                        aclResponse.setContent(gson.toJson(hmapMatchDay));
                        send(aclResponse);
                    }
                    else if (receivedMsg.containsKey("leagueMatchDay"))
                    {
                        Map<String, String> leagueMatchDay = (HashMap<String, String>) receivedMsg.get("leagueMatchDay");
                        String league = leagueMatchDay.get("league");
                        Integer matchDay = Utils.toInt(leagueMatchDay.get("matchDay"));

                        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
                        msg.addReceiver(new AID("ControllerAgent", AID.ISLOCALNAME));

                        Map<String, ArrayList<String>> teams = new HashMap<>();
                        teams.put("teams", Utils.toList(extractTeams(getMatchDayMatches(league, matchDay))));
                        msg.setContent(gson.toJson(teams));
                        send(msg);
                    }
                    else if (receivedMsg.containsKey("teamLeagueMatchDay"))
                    {
                        Map<String, String> hmapTeamLeagueMatchDay = (HashMap<String, String>) receivedMsg.get("teamLeagueMatchDay");
                        String team = hmapTeamLeagueMatchDay.get("team");
                        String league = hmapTeamLeagueMatchDay.get("league");
                        Integer matchDay = Utils.toInt(hmapTeamLeagueMatchDay.get("matchDay"));

                        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
                        msg.addReceiver(new AID("AnalysisAgent", AID.ISLOCALNAME));

                        Map<String, List<List<String>>> hmapTeamMatches = new HashMap<>();
                        String[][] teamMatches = getTeamMatches(team, league, matchDay);
                        hmapTeamMatches.put("teamMatches", Utils.to2DList(teamMatches));
                        msg.setContent(gson.toJson(hmapTeamMatches));
                        send(msg);
                    }
                }
            }
        });
    }
}