package de.itech.netcalc.gui;

import de.itech.netcalc.net.*;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Optional;

public class TreePanel extends JPanel implements TreeSelectionListener {
    private NetworkTreeModel networkTreeModel;
    private JSplitPane infoPane;
    private JTree networkTree;
    private HostPanel hostPanel;

    TreePanel() {
        super(new GridLayout(1,0));
        JSplitPane mainPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        infoPane = new FixedSplitPanel(JSplitPane.VERTICAL_SPLIT);
        mainPane.setDividerSize(3);
        infoPane.setDividerSize(3);
        mainPane.setLeftComponent(infoPane);
        mainPane.setRightComponent(hostPanel = new HostPanel());
        mainPane.setResizeWeight(0.3);
        infoPane.setResizeWeight(0.5);

        networkTree = new JTree(networkTreeModel = new NetworkTreeModel());
        networkTree.addTreeSelectionListener(this);
        networkTree.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleTreeRightClick(e);
            }
        });
        infoPane.setTopComponent(new JScrollPane(networkTree));
        add(mainPane);

        try {
            initWithLocalInterfaces();
        } catch (SocketException e) {
            e.printStackTrace();
        }

        Network testNetwork1 = Network.parse("192.168.254.0/24", null);
        Network testNetwork2 = Network.parse("10.0.5.0/24", null);
        Network testNetwork3 = Network.parse("178.34.0.0/16", null);
        Network testNetwork4 = new Network(null, IPAddress.parseIPv4("1.2.3.0"),IPAddress.parseIPv4("255.255.255.0"), IPAddress.parseIPv6("2001:db8::"), 64);
        testNetwork1.splitBySize(126);
        testNetwork2.splitBySize(30);
        testNetwork3.splitBySize(14);
        testNetwork3.addSubnet(24);
        networkTreeModel.addNetwork(testNetwork1);
        networkTreeModel.addNetwork(testNetwork2);
        networkTreeModel.addNetwork(testNetwork3);
        networkTreeModel.addNetwork(testNetwork4);

        networkTree.expandRow(0);

    }

    public void initWithLocalInterfaces() throws SocketException {
        for (NetworkInterface netint : getInterfacesWithIPv4()) {
            try {
                addExternalNetwork(netint);
            } catch(Exception e) {
                e.printStackTrace();
            }
            //mimi
        }
    }

    private ArrayList<NetworkInterface> getInterfacesWithIPv4() throws SocketException {
        ArrayList<NetworkInterface> interfaces = new ArrayList<>();
        Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
        for (NetworkInterface netint : Collections.list(nets))
            Collections.list(netint.getInetAddresses()).stream()
                    .filter(net-> Inet4Address.class.isAssignableFrom(net.getClass()))
                    .forEach(net -> {
                        try {
                            interfaces.add(NetworkInterface.getByInetAddress(net));
                        } catch (SocketException e) {
                            e.printStackTrace();
                        }
                    });

        return interfaces;
    }

    private void addExternalNetwork(NetworkInterface netint) throws Exception {
        if(netint.isLoopback()) return;
        Optional<InterfaceAddress> address = netint.getInterfaceAddresses().stream().filter(a -> a.getAddress() instanceof Inet4Address).findFirst();
        if(!address.isPresent()) return;
        String ip = address.get().getAddress().getHostAddress();
        String prefix = String.valueOf(address.get().getNetworkPrefixLength());

        Network network = Network.parse(ip+"/"+prefix, null);
        network.setName(netint.getDisplayName());
        network.addAllHosts();
        networkTreeModel.addNetwork(network);
    }


    @Override
    public void valueChanged(TreeSelectionEvent e) {
        Object node = networkTree.getLastSelectedPathComponent();
        if(node instanceof NetworkTreeNode)
        {
            Network network = ((NetworkTreeNode)node).getNetwork();
            fillInfoPanel(network);
            fillHostPanel(network);
        }
        else
        {
            infoPane.setBottomComponent(null);
        }
    }

    private void fillInfoPanel(Network networkBase) {
        JPanel infoPanel = new JPanel(new GridLayout(networkBase.getNetworkIdV6() == null ? 5 : 8,2));
        infoPanel.add(new JLabel("Netzwerk"));
        infoPanel.add(new JLabel(networkBase.getName()));
        infoPanel.add(new JLabel("IPv4"));
        infoPanel.add(new JLabel());
        infoPanel.add(new JLabel("Netzwerk Id:"));
        infoPanel.add(new JLabel(networkBase.getNetworkIdV4().toString()));
        infoPanel.add(new JLabel("Subnetzmaske:"));
        infoPanel.add(new JLabel(networkBase.getNetworkIdV4().toString()));
        infoPanel.add(new JLabel("Broadcastaddresse:"));
        infoPanel.add(new JLabel(networkBase.getBroadcastAddress().toString()));
        if(networkBase.getNetworkIdV6() != null)
        {
            infoPanel.add(new JLabel("IPv6"));
            infoPanel.add(new JLabel());
            infoPanel.add(new JLabel("Netzwerk Id:"));
            infoPanel.add(new JLabel(networkBase.getNetworkIdV6().toString()));
            infoPanel.add(new JLabel("Prefix:"));
            infoPanel.add(new JLabel(String.valueOf(networkBase.getPrefixV6())));
        }
        infoPane.setBottomComponent(infoPanel);
    }

    private void fillHostPanel(Network network) {
        hostPanel.setNetwork(network);
    }

    private void handleTreeRightClick(MouseEvent e){
        if (SwingUtilities.isRightMouseButton(e)) {
            int row = networkTree.getClosestRowForLocation(e.getX(), e.getY());
            networkTree.setSelectionRow(row);
            JPopupMenu menu = new JPopupMenu();
            Object element = networkTree.getPathForRow(row).getLastPathComponent();
            if(element == networkTreeModel.getRoot())
            {
                menu.add(new AbstractAction("Neues Netzwerk") {
                    public void actionPerformed (ActionEvent e) {
                        handleCreateNetwork(null);
                    }
                });
                menu.addSeparator();
                if(networkTreeModel.getRootIPv6Prefix() == null) {
                    menu.add(new AbstractAction("Globalen IPv6 Prefix hinzufügen") {
                        public void actionPerformed (ActionEvent e) {
                            handleAssignGlobalIPv6(null);
                        }
                    });
                } else {
                    menu.add(new AbstractAction("Globalen IPv6 Prefix bearbeiten") {
                        public void actionPerformed (ActionEvent e) {
                            handleAssignGlobalIPv6(networkTreeModel.getRootIPv6Prefix().toString(true) + "/" + networkTreeModel.getRootIPv6PrefixLength());
                        }
                    });
                    menu.add(new AbstractAction("Globalen IPv6 Prefix entfernen") {
                        public void actionPerformed (ActionEvent e) {
                            handleRemoveGlobalIPv6();
                        }
                    });
                }
            }
            else if(element instanceof NetworkTreeNode)
            {
                NetworkTreeNode networkNode = (NetworkTreeNode)element;
                Network.SubnetStatus status = networkNode.getNetwork().getStatus();
                if(status != Network.SubnetStatus.HAS_HOSTS)
                {
                    menu.add(new AbstractAction("Neues Subnetz") {
                        public void actionPerformed (ActionEvent e) {
                            handleCreateNetwork(networkNode, null);
                        }
                    });
                    menu.add(new AbstractAction("Neues Subnetz nach Größe") {
                        public void actionPerformed (ActionEvent e) {
                            handleCreateNetworkBySize(networkNode, null);
                        }
                    });
                    menu.add(new AbstractAction("Gleichmäßig nach Größe") {
                        public void actionPerformed (ActionEvent e) {
                            handleSplitBySize(networkNode);
                        }
                    });
                    menu.add(new AbstractAction("Gleichmäßig nach Anzahl") {
                        public void actionPerformed (ActionEvent e) {
                            handleSplitByCount(networkNode);
                        }
                    });
                    menu.addSeparator();
                }
                if(networkNode.getNetwork().getNetworkIdV6() == null) {
                    menu.add(new AbstractAction("IPv6 zuweisen") {
                        public void actionPerformed (ActionEvent e) {
                            handleAssignIPv6(networkNode, null);
                        }
                    });
                    menu.addSeparator();
                }
                else {
                    menu.add(new AbstractAction("IPv6 ändern") {
                        public void actionPerformed (ActionEvent e) {
                            handleAssignIPv6(networkNode, networkNode.getNetwork().toString(true));
                        }
                    });
                    menu.add(new AbstractAction("IPv6 entfernen") {
                        public void actionPerformed (ActionEvent e) {
                            handleRemoveIPv6(networkNode);
                        }
                    });
                    menu.addSeparator();
                }
                if(status == Network.SubnetStatus.HAS_SUBNETS)
                {
                    menu.add(new AbstractAction("Alle Subnetze entfernen") {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            handleClearSubnets(networkNode);
                        }
                    });
                    menu.addSeparator();
                }
                if(status != Network.SubnetStatus.HAS_SUBNETS)
                {
                    menu.add(new AbstractAction("Host hinzufügen") {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            handleAddHost(networkNode);
                        }
                    });
                    menu.add(new AbstractAction("Host mit IP Adresse hinzufügen") {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            handleAddHostWithIP(networkNode);
                        }
                    });
                    menu.add(new AbstractAction("Alle Hosts hinzufügen") {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            handleAddAllHosts(networkNode);
                        }
                    });
                    menu.addSeparator();
                }
                if(status == Network.SubnetStatus.HAS_HOSTS)
                {
                    menu.add(new AbstractAction("Alle Hosts entfernen") {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            handleClearHosts(networkNode);
                        }
                    });
                    menu.addSeparator();
                }
                menu.add(new AbstractAction("Umbenennen") {
                    public void actionPerformed (ActionEvent e) {
                        handleRename(networkNode);
                    }
                });
                menu.add(new AbstractAction("Netzwerk/Subnetz löschen") {
                    public void actionPerformed (ActionEvent e) {
                        networkTreeModel.deleteNetwork(networkNode);
                    }
                });
            }
            menu.show(e.getComponent(), e.getX(), e.getY());
        }
    }

    private void handleAssignGlobalIPv6(String initialValue) {
        Object obj = JOptionPane.showInputDialog(
                SubnetCalculatorFrame.Instance,
                "IPv6 Network und Prefix:",
                "IPv6 zuweisen",
                JOptionPane.PLAIN_MESSAGE,
                null, null, initialValue);
        if(obj == null) return;

        String input = obj.toString();
        if(!IPAddress.isValidIPv6WithPrefix(input, 0, 128)) {
            if(!IPAddress.isValidIPv6(input)) {
                GuiUtils.error("Bitte IPv6 Prefix angebenen.", null);
            }
            else {
                GuiUtils.error("Die eingebenene IPv6 Adresse order der Prefix sind ungültig.", null);
            }
            handleAssignGlobalIPv6(input);
        }
        IPv6Address address = IPAddress.parseIPv6(input);
        int prefix = IPAddress.parseIPv6Prefix(input);
        networkTreeModel.setRootIPv6Prefix(address, prefix);
    }

    private void handleRemoveGlobalIPv6() {
        if(!GuiUtils.confirmation("Sicherheitsabfrage - IPv6 entfernen",
                "Der globale IPv6 Prefix wird entfernt. Alle Netzwerke behalten ihre IPv6 Konfiguration."))
            return;
        networkTreeModel.setRootIPv6Prefix(null, 0);
    }

    private void handleRemoveIPv6(NetworkTreeNode networkNode) {
        if(!GuiUtils.confirmation("Sicherheitsabfrage - IPv6 entfernen",
                "IPv6 wird aus dem Netzwerk und allen Hosts entfernt."))
            return;
        Network network = networkNode.getNetwork();
        removeIPv6FromNetwork(network);
        networkTreeModel.nodeStructureChanged(networkNode);
        hostPanel.reloadHosts();
        fillInfoPanel(network);
    }

    private void removeIPv6FromNetwork(Network network) {
        if(network.getStatus() == Network.SubnetStatus.HAS_HOSTS) {
            for (Host host : network.getHosts()) {
                host.setIpv6Address(null);
            }
        }
        if(network.getStatus() == Network.SubnetStatus.HAS_SUBNETS) {
            for (Network subnet : network.getSubnets()) {
                removeIPv6FromNetwork(subnet);
            }
        }
        network.setIPv6(null, 0);
    }

    private void handleAssignIPv6(NetworkTreeNode networkNode, String initialValue) {
        Network parentNetwork = networkNode.getNetwork().getParent();
        IPv6Address parentIPv6Address = parentNetwork == null ? null : parentNetwork.getNetworkIdV6();
        int parentPrefix = parentIPv6Address == null ? networkTreeModel.getRootIPv6PrefixLength() : parentNetwork.getPrefixV6();

        Object obj = JOptionPane.showInputDialog(
                SubnetCalculatorFrame.Instance,
                "IPv6 Network und Prefix (>" + parentPrefix + "):",
                "IPv6 zuweisen",
                JOptionPane.PLAIN_MESSAGE,
                null, null, initialValue);

        if(obj == null) return;

        String input = obj.toString();
        if(!IPAddress.isValidIPv6WithPrefix(input, parentPrefix + 1, 64)) {
            if(IPAddress.isValidIPv6(input)) {
                GuiUtils.error("Bitte IPv6 Prefix angebenen.", null);
            }
            else {
                GuiUtils.error("Die eingebenene IPv6 Adresse order der Prefix sind ungültig.", null);
            }
            handleAssignIPv6(networkNode, input);
        }
        IPv6Address address = IPAddress.parseIPv6(input);
        int prefix = IPAddress.parseIPv6Prefix(input);
        networkNode.getNetwork().setIPv6(address, prefix);
        fillInfoPanel(networkNode.getNetwork());
        if(networkNode.getNetwork().getHosts().length > 0 && GuiUtils.confirmation(
                "IPv6 anwenden,", "Soll allen Hosts eine IPv6 Adresse hinzugefügt werden?")) {
            networkNode.getNetwork().assginIPv6ToAllHosts();
            fillHostPanel(networkNode.getNetwork());
        }
    }

    private void handleCreateNetwork(String initialValue) {
        Object obj = JOptionPane.showInputDialog(
                SubnetCalculatorFrame.Instance,
                "Netzwerk Id und Prefix:",
                "Netzwerk hinzufügen",
                JOptionPane.PLAIN_MESSAGE,
                null, null, initialValue);

        if(obj == null) {
            return;
        }

        String input = obj.toString();
        if(input == null) return;
        try{
            Network network = Network.parse(input, null);
            networkTreeModel.addNetwork(network);
        }catch (UnsupportedOperationException e){
            GuiUtils.error(e.getMessage(),this);
            handleCreateNetwork(input);
        }
    }

    private void handleCreateNetwork(NetworkTreeNode parent, String initialValue) {
        String input = (String)JOptionPane.showInputDialog(
                SubnetCalculatorFrame.Instance,
                "Netzwerk Id und Prefix:",
                "Subnetz hinzufügen",
                JOptionPane.PLAIN_MESSAGE,
                null,
                null,
                initialValue == null ? GuiUtils.getInitialSubnetString(parent.getNetwork()) : initialValue);
        if(input == null) return;
        try{
            Network network = Network.parse(input, parent.getNetwork());
            networkTreeModel.addNetwork(network, parent);
        }catch (UnsupportedOperationException e){
            GuiUtils.error(e.getMessage(),this);
            handleCreateNetwork(parent, input);
        }
    }

    private void handleCreateNetworkBySize(NetworkTreeNode parent, String initialValue) {
        ArrayList<Integer> deviders = parent.getNetwork().possibleDividers();

        String input = (String)JOptionPane.showInputDialog(
                SubnetCalculatorFrame.Instance,
                "Netzwerk Id und Prefix:",
                "Subnetz hinzufügen",
                JOptionPane.PLAIN_MESSAGE,
                null,
                null,
                initialValue == null ? GuiUtils.getInitialSubnetString(parent.getNetwork()) : initialValue);

        if(input == null) return;


        networkTreeModel.addNetwork(parent,Integer.valueOf(input));
    }

    private void handleSplitBySize(NetworkTreeNode networkTreeNode) {
        ArrayList<Integer> deviders = networkTreeNode.getNetwork().possibleDividers();

        Object input = JOptionPane.showInputDialog(
                SubnetCalculatorFrame.Instance,
                "Größe der Subnetze angeben",
                "Netzwerk teilen",
                JOptionPane.QUESTION_MESSAGE, null,
                deviders.toArray(), // Array of choices
                deviders.get(0)); // Initial choice

        if(input == null){
            return;
        }

        networkTreeModel.splitBySize(networkTreeNode, (int)input);
    }

    private void handleSplitByCount(NetworkTreeNode networkTreeNode) {
        ArrayList<Integer> sizes = new ArrayList<>();

        networkTreeNode.getNetwork().possibleDividers()
                .forEach(x -> sizes.add((networkTreeNode.getNetwork().getMaxHosts()+2) / (x+2)));
        //sizes.add(1);

        Object input = JOptionPane.showInputDialog(
                SubnetCalculatorFrame.Instance,
                "Anzahl der Subnetze angeben",
                "Netzwerk teilen",
                JOptionPane.QUESTION_MESSAGE, null,
                sizes.toArray(), // Array of choices
                sizes.get(0)); // Initial choice

        if(input == null){
            return;
        }

        networkTreeModel.splitByCount(networkTreeNode, (int) input);
    }

    private void handleAddHost(NetworkTreeNode networkNode) {
        networkNode.getNetwork().addHost();
        hostPanel.reloadHosts();
    }

    private void handleAddHostWithIP(NetworkTreeNode networkNode) {
        Network network = networkNode.getNetwork();
        String networkString = network.getNetworkIdV4().toString() + "/" +
                NetUtils.maskToPrefix(network.getNetworkMaskV4());
        String input = (String) JOptionPane.showInputDialog(
                SubnetCalculatorFrame.Instance,
                "IP Adresse (" + networkString + ")",
                "Host mit IP Adresse hinzufügen",
                JOptionPane.PLAIN_MESSAGE,
                null,
                null,
                GuiUtils.getInitialSubnetString(network));

        network.addHost(IPAddress.parseIPv4(input));
        hostPanel.reloadHosts();
    }

    private void handleAddAllHosts(NetworkTreeNode networkNode) {
        networkNode.getNetwork().addAllHosts();
        hostPanel.reloadHosts();
    }

    private void handleClearHosts(NetworkTreeNode networkNode) {
        networkNode.getNetwork().clearHosts();
        hostPanel.reloadHosts();
    }

    private void handleClearSubnets(NetworkTreeNode networkNode) {
        networkTreeModel.clearSubnets(networkNode);
    }

    private void handleRename(NetworkTreeNode networkNode) {
        String initialValue = networkNode.getNetwork().getName();
        String input = (String) JOptionPane.showInputDialog(
                SubnetCalculatorFrame.Instance,
                "Name",
                "Netzwerk/Subnetz umbenennen",
                JOptionPane.PLAIN_MESSAGE,
                null,
                null,
                initialValue);
        networkNode.getNetwork().setName(input);
        networkTreeModel.nodeChanged(networkNode);
        fillInfoPanel(networkNode.getNetwork());
    }
}