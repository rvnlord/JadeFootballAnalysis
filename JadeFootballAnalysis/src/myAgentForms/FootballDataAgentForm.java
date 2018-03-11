package myAgentForms;

import common.ILeagueTableUpdatedListener;
import common.IMatchDayMatchesUpdatedListener;
import common.ITeamMatchesUpdatedListener;
import common.Utils;
import jade.core.Agent;
import myAgents.FootballDataAgent;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.IOException;
import java.net.URISyntaxException;


public class FootballDataAgentForm extends JFrame implements ILeagueTableUpdatedListener, ITeamMatchesUpdatedListener, IMatchDayMatchesUpdatedListener
{
    private final FootballDataAgent _footballDataAgent;

    private String[] _leagueTableHeaders = new String[] { "Pozycja", "Zespół", "Spotkania", "Punkty", "Gole" };
    private String[] _matchDayMatchesHeaders = new String[] { "Data", "U siebie", "Na wyjeździe", "Wynik" };
    private String[] _teamMatchesHeaders = new String[] { "Data", "Kolejka", "U siebie", "Na wyjeździe", "Wynik" };

    private JPanel pnlMain;
    private JTable tblLeagueTable;
    private JTable tblMatchDayMatches;
    private JTable tblTeamMatches;

    private JScrollPane spLeagueTable;
    private JScrollPane spMatchDayMatches;
    private JScrollPane spTeamMatches;

    public FootballDataAgentForm(Agent agent) throws ClassNotFoundException, IOException, URISyntaxException
    {
        initComponents();

        _footballDataAgent = (FootballDataAgent)agent;

        _footballDataAgent.addLeagueTableUpdatedListener(this);
        _footballDataAgent.addTeamMatchesUpdatedListener(this);
        _footballDataAgent.addMatchDayMatchesMatchesUpdatedListener(this);

        _footballDataAgent.addSendDataToControllerAgentBehavior();
    }

    private void initComponents() throws ClassNotFoundException, IOException, URISyntaxException
    {
        pnlMain = new JPanel();
        int margin = 10;
        int gap = margin / 2;

        pnlMain.setLayout(null);
        setResizable(false);
        setContentPane(pnlMain);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        spLeagueTable = new JScrollPane();
        spLeagueTable.setBounds(margin, margin, 640, 240);
        spLeagueTable.setBackground(Color.decode("#101010"));
        spLeagueTable.setForeground(Color.decode("#FFFFFF"));
        spLeagueTable.setFont(new Font("Serif", Font.PLAIN, 16));
        spLeagueTable.setName("spLeagueTable");
        pnlMain.add(spLeagueTable);
        spLeagueTable.setVisible(true);

        tblLeagueTable = new JTable();
        tblLeagueTable.setFont(new Font("Serif", Font.PLAIN, 16));
        tblLeagueTable.setName("tblFootballData");
        tblLeagueTable.setModel(new DefaultTableModel(null, _leagueTableHeaders));
        spLeagueTable.setViewportView(tblLeagueTable);
        tblLeagueTable.setVisible(true);

        spMatchDayMatches = new JScrollPane();
        spMatchDayMatches.setBounds(margin, margin + 240 + gap, 640, 240);
        spMatchDayMatches.setBackground(Color.decode("#101010"));
        spMatchDayMatches.setForeground(Color.decode("#FFFFFF"));
        spMatchDayMatches.setFont(new Font("Serif", Font.PLAIN, 16));
        spMatchDayMatches.setName("spMatchDayMatches");
        pnlMain.add(spMatchDayMatches);
        spMatchDayMatches.setVisible(true);

        tblMatchDayMatches = new JTable();
        tblMatchDayMatches.setFont(new Font("Serif", Font.PLAIN, 16));
        tblMatchDayMatches.setName("tblMatchDayMatches");
        tblMatchDayMatches.setModel(new DefaultTableModel(null, _matchDayMatchesHeaders));
        spMatchDayMatches.setViewportView(tblMatchDayMatches);
        tblMatchDayMatches.setVisible(true);
        
        spTeamMatches = new JScrollPane();
        spTeamMatches.setBounds(margin, margin + 240 * 2 + gap * 2, 640, 240);
        spTeamMatches.setBackground(Color.decode("#101010"));
        spTeamMatches.setForeground(Color.decode("#FFFFFF"));
        spTeamMatches.setFont(new Font("Serif", Font.PLAIN, 16));
        spTeamMatches.setName("spTeamMatches");
        pnlMain.add(spTeamMatches);
        spTeamMatches.setVisible(true);

        tblTeamMatches = new JTable();
        tblTeamMatches.setFont(new Font("Serif", Font.PLAIN, 16));
        tblTeamMatches.setName("spTeamMatches");
        tblTeamMatches.setModel(new DefaultTableModel(null, _teamMatchesHeaders));
        spTeamMatches.setViewportView(tblTeamMatches);
        tblTeamMatches.setVisible(true);

        Utils.sizeToContent(this);

        setLocationByPlatform(true);
        setLocationRelativeTo(null);
        setLocation(700, 10);
        setName("frmFootballDataAgentForm");
        setTitle("(2) Agent - Zarządzający Danymi o Meczach");
        getContentPane().setBackground(Color.decode("#202020"));

        setVisible(true);
        pnlMain.setFocusable(true);
    }

    @Override
    public void leagueTable_Updated(String[][] leagueTable)
    {
        tblLeagueTable.setModel(new DefaultTableModel(leagueTable, _leagueTableHeaders));
    }

    @Override
    public void matchDayMatches_Updated(String[][] matchDayMatches)
    {
        tblMatchDayMatches.setModel(new DefaultTableModel(matchDayMatches, _matchDayMatchesHeaders));
    }
    
    @Override
    public void teamMatches_Updated(String[][] teamMatches)
    {
        tblTeamMatches.setModel(new DefaultTableModel(teamMatches, _teamMatchesHeaders));
    }
}
