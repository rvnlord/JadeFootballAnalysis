package myAgentForms;

import common.IMatchDayReceivedListener;
import common.ITeamsReceivedListener;
import common.Utils;
import jade.core.Agent;
import myAgents.ControllerAgent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.URISyntaxException;


public class ControllerAgentForm extends JFrame implements IMatchDayReceivedListener, ITeamsReceivedListener
{
    private final ControllerAgent _controllerAgent;
    private String[] _leagues = new String[] { "1 Liga Hiszpańska", "1 Liga Angielska", "1 Liga Francuska", "1 Liga Włoska", "1 Liga Niemiecka" };

    private JPanel pnlMain;
    private JComboBox ddlLeague;
    private JButton btnLoadMatchDays;
    private JComboBox ddlMatchDay;
    private JButton btnLoadTeams;
    private JComboBox ddlTeam;
    private JButton btnAnalyze;
    private JLabel lblLeague;
    private JLabel lblMatchDay;
    private JLabel lblTeam;

    public ControllerAgentForm(Agent agent) throws ClassNotFoundException, IOException, URISyntaxException
    {
        initComponents();

        _controllerAgent = (ControllerAgent)agent;
        _controllerAgent.addMatchDayReceivedListener(this);
        _controllerAgent.addTeamsReceivedListener(this);

        Utils.arrToJinqStream(pnlMain.getComponents()).forEach(c -> c.setEnabled(false));
        Utils.enableControls(new Component[] { lblLeague, ddlLeague, btnLoadMatchDays });
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

        lblLeague = new JLabel();
        lblLeague.setLayout(null);
        lblLeague.setFont(new Font("Serif", Font.PLAIN, 16));
        lblLeague.setBounds(margin, margin, 200, 30);
        lblLeague.setText("Liga:");
        lblLeague.setName("lblLeague");
        getContentPane().add(lblLeague);
        lblLeague.setVisible(true);
        lblLeague.setHorizontalAlignment(SwingConstants.LEFT);
        lblLeague.setVerticalAlignment(SwingConstants.CENTER);

        ddlLeague = new JComboBox();
        ddlLeague.setModel(new DefaultComboBoxModel(_leagues));
        ddlLeague.setFont(new Font("Serif", Font.PLAIN, 16));
        ddlLeague.setBounds(margin + 200 + gap, margin, 200, 30);
        ddlLeague.setBackground(Color.decode("#101010"));
        ddlLeague.setForeground(Color.decode("#FFFFFF"));
        ddlLeague.setName("ddlLeague");
        ddlLeague.addActionListener(this::ddlLeague_SelectionChanged);
        pnlMain.add(ddlLeague);
        ddlLeague.setVisible(true);

        btnLoadMatchDays = new JButton();
        btnLoadMatchDays.setBounds(margin + 200 * 2 + gap * 2, margin, 200, 30);
        btnLoadMatchDays.setBackground(Color.decode("#101010"));
        btnLoadMatchDays.setForeground(Color.decode("#FFFFFF"));
        btnLoadMatchDays.setFont(new Font("Serif", Font.PLAIN, 16));
        btnLoadMatchDays.setText("Wczytaj kolejki");
        btnLoadMatchDays.addActionListener(this::btnLoadMatchDays_Click);
        btnLoadMatchDays.setName("btnLoadMatchDays");
        pnlMain.add(btnLoadMatchDays);
        btnLoadMatchDays.setVisible(true);

        lblMatchDay = new JLabel();
        lblMatchDay.setLayout(null);
        lblMatchDay.setFont(new Font("Serif", Font.PLAIN, 16));
        lblMatchDay.setBounds(margin, margin + 30 + gap, 200, 30);
        lblMatchDay.setText("Kolejka:");
        lblMatchDay.setName("lblMatchDay");
        getContentPane().add(lblMatchDay);
        lblMatchDay.setVisible(true);
        lblMatchDay.setHorizontalAlignment(SwingConstants.LEFT);
        lblMatchDay.setVerticalAlignment(SwingConstants.CENTER);

        ddlMatchDay = new JComboBox();
        ddlMatchDay.setFont(new Font("Serif", Font.PLAIN, 16));
        ddlMatchDay.setBounds(margin + 200 + gap, margin + 30 + gap, 200, 30);
        ddlMatchDay.setBackground(Color.decode("#101010"));
        ddlMatchDay.setForeground(Color.decode("#FFFFFF"));
        ddlMatchDay.setName("ddlMatchDay");
        ddlMatchDay.addActionListener(this::ddlMatchDay_SelectionChanged);
        pnlMain.add(ddlMatchDay);
        ddlMatchDay.setVisible(true);

        btnLoadTeams = new JButton();
        btnLoadTeams.setBounds(margin + 200 * 2 + gap * 2, margin + 30 + gap, 200, 30);
        btnLoadTeams.setBackground(Color.decode("#101010"));
        btnLoadTeams.setForeground(Color.decode("#FFFFFF"));
        btnLoadTeams.setFont(new Font("Serif", Font.PLAIN, 16));
        btnLoadTeams.setText("Wczytaj zespoły");
        btnLoadTeams.addActionListener(this::btnLoadTeams_Click);
        btnLoadTeams.setName("btnLoadTeams");
        pnlMain.add(btnLoadTeams);
        btnLoadTeams.setVisible(true);

        lblTeam = new JLabel();
        lblTeam.setLayout(null);
        lblTeam.setFont(new Font("Serif", Font.PLAIN, 16));
        lblTeam.setBounds(margin, margin + 30 * 2 + gap * 2, 200, 30);
        lblTeam.setText("Zespół:");
        lblTeam.setName("lblTeam");
        getContentPane().add(lblTeam);
        lblTeam.setVisible(true);
        lblTeam.setHorizontalAlignment(SwingConstants.LEFT);
        lblTeam.setVerticalAlignment(SwingConstants.CENTER);

        ddlTeam = new JComboBox();
        ddlTeam.setFont(new Font("Serif", Font.PLAIN, 16));
        ddlTeam.setBounds(margin + 200 + gap, margin + 30 * 2 + gap * 2, 200, 30);
        ddlTeam.setBackground(Color.decode("#101010"));
        ddlTeam.setForeground(Color.decode("#FFFFFF"));
        ddlTeam.setName("ddlTeam");
        pnlMain.add(ddlTeam);
        ddlTeam.setVisible(true);

        btnAnalyze = new JButton();
        btnAnalyze.setBounds(margin + 200 * 2 + gap * 2, margin + 30 * 2 + gap * 2, 200, 30);
        btnAnalyze.setBackground(Color.decode("#101010"));
        btnAnalyze.setForeground(Color.decode("#FFFFFF"));
        btnAnalyze.setFont(new Font("Serif", Font.PLAIN, 16));
        btnAnalyze.setText("Analizuj");
        btnAnalyze.addActionListener(this::btnAnalyze_Click);
        btnAnalyze.setName("btnAnalyze");
        pnlMain.add(btnAnalyze);
        btnAnalyze.setVisible(true);

        Utils.sizeToContent(this);

        setLocationByPlatform(true);
        setLocationRelativeTo(null);
        setLocation(10, 400);
        setName("frmControllerAgentForm");
        setTitle("(1) Agent - Kontroler");
        getContentPane().setBackground(Color.decode("#202020"));

        setVisible(true);
        pnlMain.setFocusable(true);
    }

    private void ddlLeague_SelectionChanged(ActionEvent e)
    {
        Utils.disableControls(new Component[] { lblMatchDay, ddlMatchDay, btnLoadTeams, lblTeam, ddlTeam, btnAnalyze });
    }

    private void ddlMatchDay_SelectionChanged(ActionEvent e)
    {
        Utils.disableControls(new Component[] { lblTeam, ddlTeam, btnAnalyze });
    }

    private void btnLoadMatchDays_Click(ActionEvent e)
    {
        _controllerAgent.addSendLeagueToFootballDataAgentOneShotBehavior(ddlLeague.getSelectedItem().toString());
        _controllerAgent.addRespondWithDataToFootballDataAgentCyclicBehavior();
    }

    private void btnLoadTeams_Click(ActionEvent e)
    {
        _controllerAgent.addSendMatchDayToFootballDataAgentOneShotBehavior(
            ddlLeague.getSelectedItem().toString(),
            Utils.toInt(ddlMatchDay.getSelectedItem()));
    }

    private void btnAnalyze_Click(ActionEvent e)
    {
        _controllerAgent.addSendTeamToAnalysisAgentOneShotBehavior(
            ddlTeam.getSelectedItem().toString(),
            ddlLeague.getSelectedItem().toString(),
            Utils.toInt(ddlMatchDay.getSelectedItem()));
    }

    @Override
    public void matchDay_Received(Integer matchDay)
    {
        ddlMatchDay.setModel(new DefaultComboBoxModel(Utils.jinqStreamRange(1, matchDay).toArray(Integer[]::new)));
        Utils.enableControls(new Component[] { lblMatchDay, ddlMatchDay, btnLoadTeams });
        Utils.disableControls(new Component[] { lblTeam, ddlTeam, btnAnalyze });
    }

    @Override
    public void teams_Received(String[] teams)
    {
        ddlTeam.setModel(new DefaultComboBoxModel(teams));
        Utils.enableControls(new Component[] { lblTeam, ddlTeam, btnAnalyze });
    }
}
