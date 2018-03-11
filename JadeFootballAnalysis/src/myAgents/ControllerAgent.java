package myAgents;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import common.IMatchDayReceivedListener;
import common.ITeamsReceivedListener;
import common.Utils;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import org.jinq.orm.stream.JinqStream;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ControllerAgent extends Agent
{
    private List<IMatchDayReceivedListener> _matchDayReceivedListeners = new ArrayList<>();
    private List<ITeamsReceivedListener> _teamsReceivedListeners = new ArrayList<>();

    public void addMatchDayReceivedListener(IMatchDayReceivedListener toAdd)
    {
        _matchDayReceivedListeners.add(toAdd);
    }

    public void addTeamsReceivedListener(ITeamsReceivedListener toAdd)
    {
        _teamsReceivedListeners.add(toAdd);
    }

    @Override
    protected void takeDown()
    {
        doDelete();
    }

    public void addSendLeagueToFootballDataAgentOneShotBehavior(String league)
    {
        addBehaviour(new OneShotBehaviour(this)
        {
            @Override
            public void action()
            {
                ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
                msg.addReceiver(new AID("FootballDataAgent", AID.ISLOCALNAME));
                Map<String, String> hmapLeague = new HashMap<>();
                hmapLeague.put("league", league);
                Gson gson = new GsonBuilder().create();
                msg.setContent(gson.toJson(hmapLeague));
                send(msg);
            }
        });
    }

    public void addSendMatchDayToFootballDataAgentOneShotBehavior(String league, Integer matchDay)
    {
        addBehaviour(new OneShotBehaviour(this)
        {
            @Override
            public void action()
            {
                ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
                msg.addReceiver(new AID("FootballDataAgent", AID.ISLOCALNAME));

                Map<String, String> hmapLeagueMatchDay = new HashMap<>();
                hmapLeagueMatchDay.put("league", league);
                hmapLeagueMatchDay.put("matchDay", matchDay.toString());

                Map<String, Map<String, String>> hmapMessage = new HashMap<>();
                hmapMessage.put("leagueMatchDay", hmapLeagueMatchDay);

                Gson gson = new GsonBuilder().create();
                msg.setContent(gson.toJson(hmapMessage));
                send(msg);
            }
        });
    }

    public void addSendTeamToAnalysisAgentOneShotBehavior(String team, String league, Integer matchDay)
    {
        addBehaviour(new OneShotBehaviour(this)
        {
            @Override
            public void action()
            {
                ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
                msg.addReceiver(new AID("AnalysisAgent", AID.ISLOCALNAME));

                Map<String, String> hmapTeamLeagueMatchDay = new HashMap<>();
                hmapTeamLeagueMatchDay.put("team", team);
                hmapTeamLeagueMatchDay.put("league", league);
                hmapTeamLeagueMatchDay.put("matchDay", matchDay.toString());

                Map<String, Map<String, String>> hmapMessage = new HashMap<>();
                hmapMessage.put("teamLeagueMatchDay", hmapTeamLeagueMatchDay);

                Gson gson = new GsonBuilder().create();
                msg.setContent(gson.toJson(hmapMessage));
                send(msg);
            }
        });
    }

    public void addRespondWithDataToFootballDataAgentCyclicBehavior()
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

                    if (receivedMsg.containsKey("matchDay"))
                    {
                        Integer matchDay = Utils.toInt(receivedMsg.get("matchDay"));
                        JinqStream.from(_matchDayReceivedListeners).forEach(l -> l.matchDay_Received(matchDay));
                    }
                    else if (receivedMsg.containsKey("teams"))
                    {
                        String[] teams = ((ArrayList<String>) receivedMsg.get("teams")).toArray(new String[0]);
                        JinqStream.from(_teamsReceivedListeners).forEach(l -> l.teams_Received(teams));
                    }
                }
            }
        });
    }
}