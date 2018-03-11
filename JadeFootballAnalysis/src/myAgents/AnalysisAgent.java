package myAgents;

import common.IAnalysisPerformedListener;
import common.ITeamReceivedListener;
import common.Utils;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import org.jinq.orm.stream.JinqStream;
import org.jinq.tuples.Pair;

import java.text.MessageFormat;
import java.util.*;

public class AnalysisAgent extends Agent
{
    private List<ITeamReceivedListener> _teamReceivedListeners = new ArrayList<>();
    private List<IAnalysisPerformedListener> _analysisPerformedListeners = new ArrayList<>();

    public void addTeamReceivedListener(ITeamReceivedListener toAdd)
    {
        _teamReceivedListeners.add(toAdd);
    }

    public void addAnalysisPerformedListener(IAnalysisPerformedListener toAdd)
    {
        _analysisPerformedListeners.add(toAdd);
    }

    @Override
    protected void takeDown()
    {
        doDelete();
    }

    private String[][] performAnalysis(String[][] teamMatches)
    {   //     0         1           2             3           4
        // { "Data", "Kolejka", "U siebie", "Na wyjeździe", "Wynik" }
        String team = Utils.arr2DToJinqStream(teamMatches).selectAllList(m -> Utils.toList(new String[] { m[2], m[3] }))
            .group(m -> m, (g, stream) -> stream.count()).sortedDescendingBy(Pair::getTwo).findFirst().get().getOne();
        Integer matches = teamMatches.length;
        Integer wins = Math.toIntExact(Utils.arr2DToJinqStream(teamMatches).where(m -> isMatchWon(team, m)).count());
        Integer loses = Math.toIntExact(Utils.arr2DToJinqStream(teamMatches).where(m -> isMatchLost(team, m)).count());
        Integer draws = Math.toIntExact(Utils.arr2DToJinqStream(teamMatches).where(m -> isMatchDraw(team, m)).count());
        Double percWins = (double) wins / matches * 100;
        Double percLoses = (double) loses / matches * 100;
        Double percDraws = (double) draws / matches * 100;

        Integer goalsScored = Utils.sum(Utils.arr2DToJinqStream(teamMatches)
            .select(m -> Utils.toInt(Objects.equals(m[2], team) ? m[4].split("-")[0] : m[4].split("-")[1])).toArray(Integer[]::new));
        Integer goalsConceded = Utils.sum(Utils.arr2DToJinqStream(teamMatches)
            .select(m -> Utils.toInt(!Objects.equals(m[2], team) ? m[4].split("-")[0] : m[4].split("-")[1])).toArray(Integer[]::new));

        Integer[] goalSums = Utils.toIntArray(Utils.arr2DToJinqStream(teamMatches)
            .select(m -> Utils.toInt(Utils.arrToJinqStream(m[4].split("-")).sumInteger(Utils::toInt))).toList());
        Integer over15 = Math.toIntExact(Utils.arrToJinqStream(goalSums).where(goals -> (double) goals > 1.5).count());
        Integer over25 = Math.toIntExact(Utils.arrToJinqStream(goalSums).where(goals -> (double) goals > 2.5).count());
        Integer over35 = Math.toIntExact(Utils.arrToJinqStream(goalSums).where(goals -> (double) goals > 3.5).count());
        Double percOver15 = (double) over15 / matches * 100;
        Double percOver25 = (double) over25 / matches * 100;
        Double percOver35 = (double) over35 / matches * 100;

        Integer btts = Math.toIntExact(Utils.arr2DToJinqStream(teamMatches).where(m -> Utils.toInt(m[4].split("-")[0]) > 0 && Utils.toInt(m[4].split("-")[1]) > 0).count());
        Double percBtts = (double) btts / matches * 100;

        Integer cleanSheet = Math.toIntExact(Utils.arr2DToJinqStream(teamMatches).where(m ->
            (Objects.equals(m[2], team) && Utils.toInt(m[4].split("-")[1]) == 0) ||
                (Objects.equals(m[3], team) && Utils.toInt(m[4].split("-")[0]) == 0))
            .count());
        Double percCleanSheet = (double) cleanSheet / matches * 100;

        List<List<String>> analysis = new ArrayList<>();
        analysis.add(Utils.toList(new String[] { "% Zwycięstw:", MessageFormat.format("{0}%", Utils.round(percWins, 2).toString()) }));
        analysis.add(Utils.toList(new String[] { "% Porażek:", MessageFormat.format("{0}%", Utils.round(percLoses, 2).toString()) }));
        analysis.add(Utils.toList(new String[] { "% Remisów:", MessageFormat.format("{0}%", Utils.round(percDraws, 2).toString()) }));
        analysis.add(Utils.toList(new String[] { "Liczba strzelonych bramek:", MessageFormat.format("{0}", goalsScored.toString()) }));
        analysis.add(Utils.toList(new String[] { "Liczba straconych bramek:", MessageFormat.format("{0}", goalsConceded.toString()) }));
        analysis.add(Utils.toList(new String[] { "% Powyżej 1.5 bramki:", MessageFormat.format("{0}", MessageFormat.format("{0}%", Utils.round(percOver15, 2).toString())) }));
        analysis.add(Utils.toList(new String[] { "% Powyżej 2.5 bramki:", MessageFormat.format("{0}", MessageFormat.format("{0}%", Utils.round(percOver25, 2).toString())) }));
        analysis.add(Utils.toList(new String[] { "% Powyżej 3.5 bramki:", MessageFormat.format("{0}", MessageFormat.format("{0}%", Utils.round(percOver35, 2).toString())) }));
        analysis.add(Utils.toList(new String[] { "% Obydwa zespoły strzeliły:", MessageFormat.format("{0}%", Utils.round(percBtts, 2).toString()) }));
        analysis.add(Utils.toList(new String[] { "% Czyste Konto:", MessageFormat.format("{0}%", Utils.round(percCleanSheet, 2).toString()) }));

        return Utils.toJagged(analysis);
    }

    private boolean isMatchDraw(String team, String[] row)
    {
        return getMatchResult(team, row) == 0;
    }

    private boolean isMatchWon(String team, String[] row)
    {
        return getMatchResult(team, row) == 1;
    }

    private boolean isMatchLost(String team, String[] row)
    {
        return getMatchResult(team, row) == 2;
    }

    private Integer getMatchResult(String team, String[] row)
    {
        Integer[] goals = Utils.arrToJinqStream(row[4].split("-")).select(g -> Utils.toInt(g)).toArray(Integer[]::new);
        Integer result = -1; // 1x2 - H x A
        if (goals[0] > goals[1])
            result = 1;
        else if (goals[0] < goals[1])
            result = 2;
        else if (Objects.equals(goals[0], goals[1]))
            result = 0;

        if (Objects.equals(row[3], team)) // H x A na 'team' x Przeciwnik
        {
            if (result == 1)
                return 2;
            if (result == 2)
                return 1;
        }
        return result;
    }

    public void addRespondWithTeamToFootballDataAgentCyclicBehavior()
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
                    Map<String, Object> receivedMsg = (Map<String, Object>) Utils.jsonParse(rawReceivedMsg);

                    if (receivedMsg.containsKey("teamLeagueMatchDay"))
                    {
                        Map<String, String> teamLeagueMatchDay = (HashMap<String, String>) receivedMsg.get("teamLeagueMatchDay");
                        String team = teamLeagueMatchDay.get("team");
                        JinqStream.from(_teamReceivedListeners).forEach(l -> l.team_Received(team));

                        ACLMessage aclResponse = new ACLMessage(ACLMessage.INFORM);
                        aclResponse.addReceiver(new AID("FootballDataAgent", AID.ISLOCALNAME));
                        aclResponse.setContent(rawReceivedMsg);
                        send(aclResponse);
                    }
                    else if (receivedMsg.containsKey("teamMatches"))
                    {
                        List<List<String>> teamMatches = (List<List<String>>) receivedMsg.get("teamMatches");
                        String[][] analysis = performAnalysis(Utils.toJagged(teamMatches));
                        JinqStream.from(_analysisPerformedListeners).forEach(l -> l.analysis_Performed(analysis));
                    }
                }
            }
        });
    }
}