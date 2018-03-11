package myAgentForms;

import common.IAnalysisPerformedListener;
import common.ITeamReceivedListener;
import common.Utils;
import jade.core.Agent;
import myAgents.AnalysisAgent;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.IOException;
import java.net.URISyntaxException;


public class AnalysisAgentForm extends JFrame implements ITeamReceivedListener, IAnalysisPerformedListener
{
    private final AnalysisAgent _analysisAgent;

    private String[] _analysisHeaders = new String[] { "Statystyka", "Wartość" };

    private JPanel pnlMain;
    private JLabel lblTeam;
    private JTextField txtTeam;
    private JTable tblAnalysis;
    private JScrollPane spAnalysis;

    public AnalysisAgentForm(Agent agent) throws ClassNotFoundException, IOException, URISyntaxException
    {
        initComponents();

        _analysisAgent = (AnalysisAgent)agent;

        _analysisAgent.addTeamReceivedListener(this);
        _analysisAgent.addAnalysisPerformedListener(this);

        _analysisAgent.addRespondWithTeamToFootballDataAgentCyclicBehavior();

        Utils.disableControls(new Component[] { lblTeam, txtTeam });
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

        lblTeam = new JLabel();
        lblTeam.setLayout(null);
        lblTeam.setFont(new Font("Serif", Font.PLAIN, 16));
        lblTeam.setBounds(margin, margin, 200, 30);
        lblTeam.setText("Zespół:");
        lblTeam.setName("lblTeam");
        getContentPane().add(lblTeam);
        lblTeam.setVisible(true);
        lblTeam.setHorizontalAlignment(SwingConstants.LEFT);
        lblTeam.setVerticalAlignment(SwingConstants.CENTER);

        txtTeam = new JTextField();
        txtTeam.setLayout(null);
        txtTeam.setFont(new Font("Serif", Font.PLAIN, 16));
        txtTeam.setBounds(margin + 200 + gap, margin, 200, 30);
        txtTeam.setBackground(Color.decode("#101010"));
        txtTeam.setForeground(Color.decode("#FFFFFF"));
        txtTeam.setName("txtTeam");
        getContentPane().add(txtTeam);
        txtTeam.setVisible(true);

        spAnalysis = new JScrollPane();
        spAnalysis.setBounds(margin, margin + 30 + gap, 405, 200);
        spAnalysis.setBackground(Color.decode("#101010"));
        spAnalysis.setForeground(Color.decode("#FFFFFF"));
        spAnalysis.setFont(new Font("Serif", Font.PLAIN, 16));
        spAnalysis.setName("spAnalysis");
        pnlMain.add(spAnalysis);
        spAnalysis.setVisible(true);

        tblAnalysis = new JTable();
        tblAnalysis.setFont(new Font("Serif", Font.PLAIN, 16));
        tblAnalysis.setName("tblMatchDayMatches");
        tblAnalysis.setModel(new DefaultTableModel(null, _analysisHeaders));
        spAnalysis.setViewportView(tblAnalysis);
        tblAnalysis.setVisible(true);

        Utils.sizeToContent(this);

        setLocationByPlatform(true);
        setLocationRelativeTo(null);
        setLocation(10, 600);
        setName("frmAnalysisAgentForm");
        setTitle("(3) Agent - Analiza");
        getContentPane().setBackground(Color.decode("#202020"));

        setVisible(true);
        pnlMain.setFocusable(true);
    }

    @Override
    public void team_Received(String team)
    {
        txtTeam.setText(team);
    }

    @Override
    public void analysis_Performed(String[][] analysis)
    {
        tblAnalysis.setModel(new DefaultTableModel(analysis, _analysisHeaders));
    }
}
