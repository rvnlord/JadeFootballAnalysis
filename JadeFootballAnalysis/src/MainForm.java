import common.Utils;
import jade.core.Agent;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.util.leap.Properties;
import jade.wrapper.*;
import org.netbeans.swing.laf.dark.DarkMetalLookAndFeel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.stream.Stream;


public class MainForm extends JFrame
{
    private JButton btnStartJade;
    private JLabel lblPlatformLaunch;
    private JLabel lblAgentName;
    private JTextField txtAgentName;
    private JLabel lblAgentType;
    private JComboBox ddlAgentType;
    private JLabel lblAddress;
    private JTextField txtAddress;
    private JLabel lblPort;
    private JTextField txtPort;
    private JLabel lblContainer;
    private JTextField txtContainer;
    private JButton btnAddAgent;
    private JButton btnAddAllAgents;

    private JPanel pnlMain;

    private AgentContainer _ac;
    private jade.core.Runtime _jade;
    private ArrayList<JFrame> _openFrames = new ArrayList<>();
    private jade.util.leap.List _activeAgents = new jade.util.leap.ArrayList();

    public static void main(String args[]) throws ClassNotFoundException, IOException, URISyntaxException, IllegalAccessException, UnsupportedLookAndFeelException, InstantiationException
    {
        UIManager.setLookAndFeel(DarkMetalLookAndFeel.class.getCanonicalName());
        new MainForm();
    }

    private MainForm() throws ClassNotFoundException, IOException, URISyntaxException
    {
        initComponents();

        Utils.disableControls(pnlMain, new String[] { "AgentName", "AgentType", "AddAgent", "AddAllAgents" });
        displayAgentName();
    }

    private void initComponents() throws ClassNotFoundException, IOException, URISyntaxException
    {
        pnlMain = new JPanel();
        int margin = 10;
        int gap = margin / 2;

        pnlMain.setLayout(null);
        setResizable(true);
        setContentPane(pnlMain);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        lblPlatformLaunch = new JLabel();
        lblPlatformLaunch.setLayout(null);
        lblPlatformLaunch.setFont(new Font("Serif", Font.PLAIN, 16));
        lblPlatformLaunch.setBounds(margin, margin, 200, 30);
        lblPlatformLaunch.setText("Uruchamianie Platformy:");
        lblPlatformLaunch.setName("lblPlatformLaunch");
        getContentPane().add(lblPlatformLaunch);
        lblPlatformLaunch.setVisible(true);
        lblPlatformLaunch.setHorizontalAlignment(SwingConstants.LEFT);
        lblPlatformLaunch.setVerticalAlignment(SwingConstants.CENTER);

        lblAddress = new JLabel();
        lblAddress.setLayout(null);
        lblAddress.setFont(new Font("Serif", Font.PLAIN, 16));
        lblAddress.setBounds(margin, margin + 30 + gap, 200, 30);
        lblAddress.setText("Adres platformy:");
        lblAddress.setName("lblAddress");
        getContentPane().add(lblAddress);
        lblAddress.setVisible(true);
        lblAddress.setHorizontalAlignment(SwingConstants.LEFT);
        lblAddress.setVerticalAlignment(SwingConstants.CENTER);

        txtAddress = new JTextField();
        txtAddress.setLayout(null);
        txtAddress.setFont(new Font("Serif", Font.PLAIN, 16));
        txtAddress.setBounds(margin + 200 + gap, margin + 30 + gap, 200, 30);
        txtAddress.setBackground(Color.decode("#101010"));
        txtAddress.setForeground(Color.decode("#FFFFFF"));
        txtAddress.setName("txtAddress");
        txtAddress.setText("localhost");
        getContentPane().add(txtAddress);
        txtAddress.setVisible(true);

        lblPort = new JLabel();
        lblPort.setLayout(null);
        lblPort.setFont(new Font("Serif", Font.PLAIN, 16));
        lblPort.setBounds(margin, margin + 30 * 2 + gap * 2, 200, 30);
        lblPort.setText("Port:");
        lblPort.setName("lblPort");
        getContentPane().add(lblPort);
        lblPort.setVisible(true);
        lblPort.setHorizontalAlignment(SwingConstants.LEFT);
        lblPort.setVerticalAlignment(SwingConstants.CENTER);

        txtPort = new JTextField();
        txtPort.setLayout(null);
        txtPort.setFont(new Font("Serif", Font.PLAIN, 16));
        txtPort.setBounds(margin + 200 + gap, margin + 30 * 2 + gap * 2, 200, 30);
        txtPort.setBackground(Color.decode("#101010"));
        txtPort.setForeground(Color.decode("#FFFFFF"));
        txtPort.setName("txtPort");
        txtPort.setText("1099");
        getContentPane().add(txtPort);
        txtPort.setVisible(true);

        lblContainer = new JLabel();
        lblContainer.setLayout(null);
        lblContainer.setFont(new Font("Serif", Font.PLAIN, 16));
        lblContainer.setBounds(margin, margin + 30 * 3 + gap * 3, 200, 30);
        lblContainer.setText("Kontener:");
        lblContainer.setName("lblContainer");
        getContentPane().add(lblContainer);
        lblContainer.setVisible(true);
        lblContainer.setHorizontalAlignment(SwingConstants.LEFT);
        lblContainer.setVerticalAlignment(SwingConstants.CENTER);

        txtContainer = new JTextField();
        txtContainer.setLayout(null);
        txtContainer.setFont(new Font("Serif", Font.PLAIN, 16));
        txtContainer.setBounds(margin + 200 + gap, margin + 30 * 3 + gap * 3, 200, 30);
        txtContainer.setBackground(Color.decode("#101010"));
        txtContainer.setForeground(Color.decode("#FFFFFF"));
        txtContainer.setName("txtContainer");
        txtContainer.setText("Agenci JADE");
        getContentPane().add(txtContainer);
        txtContainer.setVisible(true);

        btnStartJade = new JButton();
        btnStartJade.setBounds(margin + 300 + gap, margin + 30 * 4 + gap * 4, 200, 30);
        btnStartJade.setBackground(Color.decode("#101010"));
        btnStartJade.setForeground(Color.decode("#FFFFFF"));
        btnStartJade.setFont(new Font("Serif", Font.PLAIN, 16));
        btnStartJade.setText("Uruchom Jade");
        btnStartJade.addActionListener(this::btnStartJade_Click);
        btnStartJade.setName("btnStartJade");
        getContentPane().add(btnStartJade);
        btnStartJade.setVisible(true);


        lblAgentName = new JLabel();
        lblAgentName.setLayout(null);
        lblAgentName.setFont(new Font("Serif", Font.PLAIN, 16));
        lblAgentName.setBounds(margin, margin + 30 * 5 + gap * 5, 200, 30);
        lblAgentName.setText("Nazwa Agenta:");
        lblAgentName.setName("lblAgentName");
        getContentPane().add(lblAgentName);
        lblAgentName.setVisible(true);
        lblAgentName.setHorizontalAlignment(SwingConstants.LEFT);
        lblAgentName.setVerticalAlignment(SwingConstants.CENTER);

        txtAgentName = new JTextField();
        txtAgentName.setLayout(null);
        txtAgentName.setFont(new Font("Serif", Font.PLAIN, 16));
        txtAgentName.setBounds(margin + 200 + gap, margin + 30 * 5 + gap * 5, 200, 30);
        txtAgentName.setBackground(Color.decode("#101010"));
        txtAgentName.setForeground(Color.decode("#FFFFFF"));
        txtAgentName.setName("lblAgentName");
        getContentPane().add(txtAgentName);
        txtAgentName.setVisible(true);

        lblAgentType = new JLabel();
        lblAgentType.setLayout(null);
        lblAgentType.setFont(new Font("Serif", Font.PLAIN, 16));
        lblAgentType.setBounds(margin, margin + 30 * 6 + gap * 6, 200, 30);
        lblAgentType.setText("Rodzaj Agenta:");
        lblAgentType.setName("lblAgentType");
        getContentPane().add(lblAgentType);
        lblAgentType.setVisible(true);
        lblAgentType.setHorizontalAlignment(SwingConstants.LEFT);
        lblAgentType.setVerticalAlignment(SwingConstants.CENTER);

        String[] agents = Stream.of(Utils.getClasses("myAgents")).map(Class::getName).filter(n -> !n.contains("$")).toArray(String[]::new);
        ddlAgentType = new JComboBox(agents);
        ddlAgentType.setFont(new Font("Serif", Font.PLAIN, 16));
        ddlAgentType.setBounds(margin + 200 + gap, margin + 30 * 6 + gap * 6, 200, 30);
        ddlAgentType.setBackground(Color.decode("#101010"));
        ddlAgentType.setForeground(Color.decode("#FFFFFF"));
        ddlAgentType.setName("ddlAgentType");
        ddlAgentType.addActionListener(this::ddlAgentType_SelectionChanged);
        getContentPane().add(ddlAgentType);
        ddlAgentType.setVisible(true);


        btnAddAgent = new JButton();
        btnAddAgent.setBounds(margin + 300 + gap, margin + 30 * 7 + gap * 7, 200, 30);
        btnAddAgent.setBackground(Color.decode("#101010"));
        btnAddAgent.setForeground(Color.decode("#FFFFFF"));
        btnAddAgent.setFont(new Font("Serif", Font.PLAIN, 16));
        btnAddAgent.setText("Dodaj Agenta");
        btnAddAgent.addActionListener(this::btnAddAgent_Click);
        btnAddAgent.setName("btnAddAgent");
        getContentPane().add(btnAddAgent);
        btnAddAgent.setVisible(true);

        btnAddAllAgents = new JButton();
        btnAddAllAgents.setBounds(margin + 300 + gap, margin + 30 * 8 + gap * 8, 200, 30);
        btnAddAllAgents.setBackground(Color.decode("#101010"));
        btnAddAllAgents.setForeground(Color.decode("#FFFFFF"));
        btnAddAllAgents.setFont(new Font("Serif", Font.PLAIN, 16));
        btnAddAllAgents.setText("Inicjuj wszystkich Agentów");
        btnAddAllAgents.addActionListener(this::btnAddAllAgents_Click);
        btnAddAllAgents.setName("btnAddAllAgents");
        getContentPane().add(btnAddAllAgents);
        btnAddAllAgents.setVisible(true);

        Utils.sizeToContent(this);

        setLocationByPlatform(true);
        setLocationRelativeTo(null);
        setLocation(10, 10);
        setName("frmMain");
        setTitle("Jade Projekt");
        getContentPane().setBackground(Color.decode("#202020"));

        setVisible(true);
        pnlMain.setFocusable(true);
    }

    private void ddlAgentType_SelectionChanged(ActionEvent e)
    {
        displayAgentName();
    }

    private void btnStartJade_Click(ActionEvent e)
    {
        try
        {
            Properties pp = new Properties();
            pp.setProperty(Profile.GUI, Boolean.TRUE.toString());
            pp.setProperty(Profile.CONTAINER_NAME, txtContainer.getText());
            pp.setProperty(Profile.MAIN_PORT, Utils.isInt(txtPort.getText()) ? txtPort.getText() : "1099");
            Profile p = new ProfileImpl(pp);

            if (_jade == null)
                _jade = jade.core.Runtime.instance();

            _ac = _jade.createMainContainer(p);
            _ac.addPlatformListener(new PlatformController.Listener()
            {
                @Override public void bornAgent(PlatformEvent platformEvent) { }
                @Override public void deadAgent(PlatformEvent platformEvent) { acMain_Close(platformEvent); }
                @Override public void startedPlatform(PlatformEvent platformEvent) { }
                @Override public void suspendedPlatform(PlatformEvent platformEvent) { }
                @Override public void resumedPlatform(PlatformEvent platformEvent) { }
                @Override public void killedPlatform(PlatformEvent platformEvent) { }
            });

            Utils.enableControls(pnlMain, new String[] { "AgentType", "AddAgent", "AddAllAgents" });
            btnStartJade.setEnabled(false);
        }
        catch (ControllerException ex)
        {
            JOptionPane.showMessageDialog(null, ex.getClass().getName() + ": " + ex.getMessage());
        }
    }

    private void acMain_Close(PlatformEvent e)
    {
        Utils.disableControls(pnlMain, new String[] { "AgentType", "AddAgent", "AddAllAgents" });
        btnStartJade.setEnabled(true);
        for (JFrame jFrame : _openFrames)
            jFrame.dispatchEvent(new WindowEvent(jFrame, WindowEvent.WINDOW_CLOSING));
        _openFrames.clear();
    }

    private void btnAddAgent_Click(ActionEvent e)
    {
        try
        {
            String agentClassName = (String)ddlAgentType.getSelectedItem();
            String agentFormClassName = agentClassName.replace("myAgents", "myAgentForms") + "Form";
            createAgent(agentClassName, agentFormClassName);
        }
        catch (InvocationTargetException | NoSuchMethodException | ControllerException | IllegalAccessException | ClassNotFoundException | InstantiationException ex)
        {
            throw new Error(ex);
        }
    }

    private void btnAddAllAgents_Click(ActionEvent e)
    {
        try
        {
            String[] agentClassNames = Stream.of(Utils.getClasses("myAgents")).map(Class::getName).filter(n -> !n.contains("$")).toArray(String[]::new);
            String[] agentFormsClassNames = Utils.arrToJinqStream(agentClassNames).select(n -> n.replace("myAgents", "myAgentForms") + "Form").toArray(String[]::new);
            for (int i = 0; i < agentClassNames.length; i++)
                createAgent(agentClassNames[i], agentFormsClassNames[i]);
            if (!Utils.containsAgent(_ac, "Sniffer"))
                _ac.createNewAgent("Sniffer", "jade.tools.sniffer.Sniffer", null).start();
        }
        catch (InvocationTargetException | NoSuchMethodException | InstantiationException | IllegalAccessException | ClassNotFoundException | ControllerException | IOException | URISyntaxException ex)
        {
            JOptionPane.showMessageDialog(null, ex.getClass().getName() + ": " + ex.getMessage());
        }
    }

    private void createAgent(String agentClassName, String agentFormClassName) throws StaleProxyException, ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException
    {
        String agentName = agentClassName.substring(agentClassName.lastIndexOf(".") + 1).trim();
        if (!Utils.containsAgent(_ac, agentName))
        {
            Agent agent = (Agent)Class.forName(agentClassName).newInstance();
            _activeAgents.add(agent);
            JFrame jFrame = (JFrame)Class.forName(agentFormClassName).getConstructor(Agent.class).newInstance(agent);
            _openFrames.add(jFrame);
            jFrame.setVisible(true);
            _ac.acceptNewAgent(agentName, agent).start();
        }
    }

    private void displayAgentName()
    {
        String[] selItemSplit = Utils.split(ddlAgentType.getSelectedItem().toString(), ".");
        txtAgentName.setText(selItemSplit[selItemSplit.length - 1]);
    }
}
