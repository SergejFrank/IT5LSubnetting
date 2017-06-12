package de.itech.netcalc.gui;

import de.itech.netcalc.net.*;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.net.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Optional;

/**
 * The TreePanel class defines the main work space.
 */
class TreePanel extends JPanel implements TreeSelectionListener {
    /**
     * Stores the view model that contains all information about the networks.
     */
    private final NetworkTreeModel networkTreeModel;

    /**
     * Stores the info panel, that displays the information about the currently selected network.
     */
    private final InfoPanel infoPanel;

    /**
     * Stores the JTree that displays the information from the networkTreeModel.
     */
    private final JTree networkTree;

    /**
     * Stores the host panel that displays the hosts of the currently selected network.
     */
    private final HostPanel hostPanel;

    /**
     * Creates and initializes a new TreePanel instance.
     */
    TreePanel() {
        super(new GridLayout(1,0));
        JSplitPane mainPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        JPanel infoPane = new JPanel(new BorderLayout());
        infoPane.setMinimumSize(new Dimension(350,600));

        mainPane.setDividerSize(3);
        mainPane.setLeftComponent(infoPane);
        mainPane.setRightComponent(hostPanel = new HostPanel());

        networkTree = new JTree(networkTreeModel = new NetworkTreeModel());
        networkTree.addTreeSelectionListener(this);
        networkTree.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleTreeRightClick(e);
            }
        });
        networkTree.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                handleTreeKeyTyped(e);
            }
        });
        infoPane.add(infoPanel = new InfoPanel(), BorderLayout.PAGE_END);
        infoPane.add(new JScrollPane(networkTree), BorderLayout.CENTER);
        add(mainPane);
        try {
            initWithLocalInterfaces();
        } catch (SocketException e) {
            e.printStackTrace();
        }

        networkTree.expandRow(0);

    }

    /**
     * Fills the view model with all network interfaces provided by the operating system.
     * @throws SocketException internal os network interface error
     */
    private void initWithLocalInterfaces() throws SocketException {
        for (NetworkInterface netint : getInterfacesWithIPv4()) {
            try {
                addExternalNetwork(netint);
            } catch(Exception e) {
                e.printStackTrace();
            }
            //mimi
        }
    }

    NetworkTreeModel getNetworkTreeModel(){
        return networkTreeModel;
    }

    /**
     * Gets all network interfaces of the operating system that has IPv4 configured.
     * @return IPv4 enabled OS network interfaces
     */
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

    /**
     * Adds an OS network interface to the view model.
     * @param netint the network interface to add
     * @throws Exception gets thrown in interface loop back check
     */
    private void addExternalNetwork(NetworkInterface netint) throws Exception {
        if(netint.isLoopback()) return;
        Optional<InterfaceAddress> address = netint.getInterfaceAddresses().stream().filter(a -> a.getAddress() instanceof Inet4Address).findFirst();
        if(!address.isPresent()) return;
        String ip = address.get().getAddress().getHostAddress();
        String prefix = String.valueOf(address.get().getNetworkPrefixLength());

        Network network = Network.parse(ip + "/" + prefix, null);
        network.setName(netint.getDisplayName());
        networkTreeModel.addNetwork(network);
    }

    /**
     * Gets executed, when the JTree selection changes.
     * @param e event data
     */
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
            infoPanel.fill(null);
            hostPanel.setNetwork(null);
        }
    }

    /**
     * Fills the info panel with the information from the given network.
     * @param networkBase the network to display
     */
    private void fillInfoPanel(Network networkBase) {
        infoPanel.fill(networkBase);
    }

    /**
     * Fills the host panel with the hosts found in the passed network.
     * @param network the network containing the hosts to display
     */
    private void fillHostPanel(Network network) {
        hostPanel.setNetwork(network);
    }

    /**
     * Refresh all elements in the Panel.
     */
    void refresh() {
        NetworkTreeNode selected = getSelectedNetworkTreeNode();
        if(selected == null) {
            infoPanel.fill(null);
            fillHostPanel(null);
        } else {
            fillInfoPanel(selected.getNetwork());
            fillHostPanel(selected.getNetwork());
        }
    }

    void clear() {
        networkTreeModel.clear();
        infoPanel.fill(null);
        fillHostPanel(null);
    }

    /**
     * Gets the currently selected NetworkTreeNode. Returns null, if root or nothing is selected.
     * @return the selected NetworkTreeNode
     */
    private NetworkTreeNode getSelectedNetworkTreeNode() {
        int row = networkTree.getMinSelectionRow();
        TreePath treePath = networkTree.getPathForRow(row);
        if(treePath == null) return null;
        Object element = treePath.getLastPathComponent();
        return element instanceof NetworkTreeNode ? (NetworkTreeNode)element : null;
    }

    /**
     * Gets executed when a key is typed with JTree focused.
     * @param e event data
     */
    private void handleTreeKeyTyped(KeyEvent e) {
        NetworkTreeNode networkTreeNode = getSelectedNetworkTreeNode();
        if(e.getKeyCode() == KeyEvent.VK_DELETE) {
            if(networkTreeNode == null) return;
            handleRemoveNetwork(networkTreeNode);
        }
        if(e.getKeyCode() == KeyEvent.VK_N && e.getModifiers() == InputEvent.CTRL_MASK) {
            if(networkTreeNode == null)
                handleCreateNetwork(null);
            else
                handleCreateNetwork(networkTreeNode, null);
        }
    }

    /**
     * Gets executed when a click on the JTree is detected.
     * On right click creates a context menu containing the main actions of the application.
     * @param e event data
     */
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
                menu.add(new AbstractAction("Netzwerk laden") {
                    public void actionPerformed (ActionEvent e) {
                        handleLoadNetworkToRoot();
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
                            handleAssignGlobalIPv6(Format.format(networkTreeModel.getRootIPv6Prefix(),Format.IPv6Format.SHORTHAND) + "/" + networkTreeModel.getRootIPv6PrefixLength());
                        }
                    });
                }
                menu.add(new AbstractAction("Globalen IPv6 Prefix entfernen") {
                    public void actionPerformed (ActionEvent e) {
                        handleRemoveGlobalIPv6();
                    }
                });
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
                            handleCreateNetworkBySize(networkNode);
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
                            handleAssignIPv6(networkNode, Format.format(networkNode.getNetwork().getNetworkIdV6(), Format.IPv6Format.SHORTHAND));
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
                    JMenuItem addHost = menu.add(new AbstractAction("Host hinzufügen") {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            handleAddHost(networkNode);
                        }
                    });
                    JMenuItem addHostWithIp = menu.add(new AbstractAction("Host mit IP Adresse hinzufügen") {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            handleAddHostWithIP(networkNode);
                        }
                    });
                    JMenuItem addAllHosts = menu.add(new AbstractAction("Alle Hosts hinzufügen") {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            handleAddAllHosts(networkNode);
                        }
                    });
                    if(networkNode.getNetwork().getHostCount() >= networkNode.getNetwork().getMaxHosts()) {
                        addHost.setEnabled(false);
                        addHostWithIp.setEnabled(false);
                        addAllHosts.setEnabled(false);
                    }
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
                menu.add(new AbstractAction("Netzwerk laden") {
                    public void actionPerformed (ActionEvent e) {
                        handleLoadNetworkToNode(networkNode);
                    }
                });
                menu.add(new AbstractAction("Speichern unter...") {
                    public void actionPerformed (ActionEvent e) {
                        handleSaveNetworkAs(networkNode.getNetwork());
                    }
                });
                menu.addSeparator();
                menu.add(new AbstractAction("Umbenennen") {
                    public void actionPerformed (ActionEvent e) {
                        handleRename(networkNode);
                    }
                });
                menu.add(new AbstractAction("Netzwerk/Subnetz löschen") {
                    public void actionPerformed (ActionEvent e) {
                        handleRemoveNetwork(networkNode);
                    }
                });
            }
            menu.show(e.getComponent(), e.getX(), e.getY());
        }
    }

    /**
     * Handles the user input and file load progress to load a network from a file to a sub node.
     * @param networkTreeNode the node to load the network to
     */
    private void handleLoadNetworkToNode(NetworkTreeNode networkTreeNode) {
        FileDialog dialog = new java.awt.FileDialog((Frame) null, "Netzwerk laden...", FileDialog.LOAD);
        dialog.setVisible(true);
        String fileName = dialog.getFile();
        if(fileName == null) return;
        File file = new File(dialog.getDirectory(), fileName);
        if(!file.exists() || file.isDirectory()) {
            GuiUtils.error("Die angegebene Datei konnte nicht gefunden werden.");
            handleLoadNetworkToNode(networkTreeNode);
        } else {
            Network network = Network.fromXML(file);
            if(network == null) return;
            Network parent = networkTreeNode.getNetwork();
            if(!NetUtils.isInSubnet(parent.getNetworkIdV4(), parent.getSubnetMaskV4(), network.getNetworkIdV4())) {
                GuiUtils.error("Das Netzwerk liegt nicht im übergeordneten Netzwerk.");
                return;
            }
            Optional<Network> colliding = networkTreeNode.getNetwork().getSubnets().stream().filter(n -> n.isColliding(network)).findFirst();
            if(colliding.isPresent()) {
                GuiUtils.error("Das Netzwerk überschneidet sich mit dem Netzwerk '" + colliding.get().toString(true) + "'");
                return;
            }
            networkTreeModel.addNetwork(network, networkTreeNode);
        }
    }

    /**
     * Handles the confirmation and removal of a network node.
     * @param networkTreeNode the network to remove
     */
    private void handleRemoveNetwork(NetworkTreeNode networkTreeNode) {
        if(!GuiUtils.confirmation("Netzwerk löschen.", "Soll das Netzwerk wirklich gelöscht werden?"))
            return;
        networkTreeModel.removeNetwork(networkTreeNode);
    }

    /**
     * Handles the user input and file load progress to load a network from a file.
     */
    private void handleLoadNetworkToRoot() {
        FileDialog dialog = new java.awt.FileDialog((Frame) null, "Netzwerk laden...", FileDialog.LOAD);
        dialog.setVisible(true);
        String fileName = dialog.getFile();
        if(fileName == null) return;
        File file = new File(dialog.getDirectory(), fileName);
        if(!file.exists() || file.isDirectory()) {
            GuiUtils.error("Die angegebene Datei konnte nicht gefunden werden.");
            handleLoadNetworkToRoot();
        } else {
            Network network = Network.fromXML(file);
            Optional<Network> colliding = networkTreeModel.getNetworks().stream().filter(n -> n.isColliding(network)).findFirst();
            if(colliding.isPresent()) {
                GuiUtils.error("Das Netzwerk überschneidet sich mit dem Netzwerk '" + colliding.get().toString(true) + "'");
                return;
            }
            networkTreeModel.addNetwork(network);
        }
    }

    /**
     * Handles the user input and file save progress to store a network to a file.
     */
    private void handleSaveNetworkAs(Network network) {
        FileDialog dialog = new FileDialog((Frame)null, "Netzwerk speichern unter...", FileDialog.SAVE);
        dialog.setFile(network.getName() == null ? "Netzwerk.xml" : network.getName() + ".xml");
        dialog.setVisible(true);
        String fileName = dialog.getFile();
        if(fileName == null) return;
        Path filePath = Paths.get(dialog.getDirectory(), fileName);
        network.save(filePath.toFile());
    }

    /**
     * Handles the user input and assignment of global IPv6.
     * @param initialValue the inout value to display
     */
    private void handleAssignGlobalIPv6(String initialValue) {
        Object obj = JOptionPane.showInputDialog(null,
                "IPv6 Network und Prefix:",
                "IPv6 zuweisen",
                JOptionPane.PLAIN_MESSAGE,
                null, null, initialValue);
        if(obj == null) return;

        String input = obj.toString();
        if(!IPAddress.isValidIPv6WithPrefix(input, 0, 128)) {
            if(!IPAddress.isValidIPv6(input)) {
                GuiUtils.error("Bitte IPv6 Prefix angebenen.");
            }
            else {
                GuiUtils.error("Die eingebenene IPv6 Adresse order der Prefix sind ungültig.");
            }
            handleAssignGlobalIPv6(input);
        }
        IPv6Address address = IPAddress.parseIPv6(input);
        int prefix = IPAddress.parseIPv6Prefix(input);

        Optional<Network> notInSubnet = networkTreeModel.getNetworks().stream()
                .filter(s -> s.isIPv6Enabled() && !NetUtils.isInSubnet(address, prefix, s.getNetworkIdV6()))
                .findFirst();
        if(notInSubnet.isPresent()) {
            GuiUtils.error("Das Netzwerk " + notInSubnet.get().toString() + " liegt nicht in der neuen IPv6 Konfiguration.");
            handleAssignGlobalIPv6(input);
            return;
        }

        networkTreeModel.setRootIPv6Prefix(address, prefix);
    }

    /**
     * Handles the confirmation and removal of global IPv6.
     */
    private void handleRemoveGlobalIPv6() {
        if(!GuiUtils.confirmation("Sicherheitsabfrage - IPv6 entfernen",
                "Der globale IPv6 Prefix wird entfernt. Alle Netzwerke behalten ihre IPv6 Konfiguration."))
            return;
        networkTreeModel.setRootIPv6Prefix(null, 0);
    }

    /**
     * Handles the confirmation and removal of IPv6 from a network.
     * @param networkNode the network node containing the network to change
     */
    private void handleRemoveIPv6(NetworkTreeNode networkNode) {
        if(!GuiUtils.confirmation("Sicherheitsabfrage - IPv6 entfernen",
                "IPv6 wird aus dem Netzwerk und allen Hosts entfernt."))
            return;
        Network network = networkNode.getNetwork();
        removeIPv6FromNetwork(network);
        networkTreeModel.nodeStructureChanged(networkNode);
        hostPanel.reload();
        fillInfoPanel(network);
    }

    /**
     * Removed the IPv6 configuration from a network.
     * @param network the network to change
     */
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

    /**
     * Handles the user input and configuration of IPv6 to a network.
     * @param networkNode the network node containing the network to change
     * @param initialValue the initial input value to display
     */
    private void handleAssignIPv6(NetworkTreeNode networkNode, String initialValue) {
        Network parentNetwork = networkNode.getNetwork().getParent();
        if(parentNetwork != null && !parentNetwork.isIPv6Enabled()) {
            GuiUtils.error("Bitte erst im übergeordneten Netzwerk IPv6 konfigurieren.");
            return;
        }
        IPv6Address parentIPv6Address = parentNetwork != null
                ? parentNetwork.getNetworkIdV6() : networkTreeModel.getRootIPv6Prefix();
        int parentPrefix = parentNetwork != null
                ? parentNetwork.getPrefixV6() : networkTreeModel.getRootIPv6PrefixLength();

        if(initialValue == null) initialValue = GuiUtils.getInitialSubnetV6String(parentIPv6Address, parentPrefix);
        Object obj = JOptionPane.showInputDialog(null,
                "IPv6 Network und Prefix (>" + parentPrefix + "):",
                "IPv6 zuweisen",
                JOptionPane.PLAIN_MESSAGE,
                null, null, initialValue);
        if(obj == null) return;

        String input = obj.toString();
        if(!IPAddress.isValidIPv6WithPrefix(input, parentPrefix + 1, 64)) {
            if(IPAddress.isValidIPv6(input)) {
                GuiUtils.error("Bitte IPv6 Prefix angebenen.");
            }
            else {
                GuiUtils.error("Die eingebenene IPv6 Adresse order der Prefix sind ungültig.");
            }
            handleAssignIPv6(networkNode, input);
            return;
        }
        IPv6Address address = IPAddress.parseIPv6(input);
        int prefix = IPAddress.parseIPv6Prefix(input);
        if(!NetUtils.isInSubnet(parentIPv6Address, parentPrefix, address)) {
            GuiUtils.error("Die angegebene Adresse liegt nicht im übergeordneten Nezwerk.");
            handleAssignIPv6(networkNode, input);
            return;
        }

        Optional<Network> notInSubnet = networkNode.getNetwork().getSubnets().stream()
                .filter(s -> s.isIPv6Enabled() && !NetUtils.isInSubnet(address, prefix, s.getNetworkIdV6()))
                .findFirst();
        if(notInSubnet.isPresent()) {
            GuiUtils.error("Das Subnetzwerk " + notInSubnet.get().toString() + " liegt nicht in der neuen IPv6 Konfiguration.");
            handleAssignIPv6(networkNode, input);
            return;
        }

        networkNode.getNetwork().setIPv6(address, prefix);
        fillInfoPanel(networkNode.getNetwork());
        if(networkNode.getNetwork().getHostCount() > 0 && GuiUtils.confirmation(
                "IPv6 anwenden,", "Soll allen Hosts eine IPv6 Adresse hinzugefügt werden?")) {
            networkNode.getNetwork().assignIPv6ToAllHosts();
            fillHostPanel(networkNode.getNetwork());
        }
    }

    /**
     * Handles the user input and creation of a new root network.
     * @param initialValue the initial input value to display
     */
    private void handleCreateNetwork(String initialValue) {
        Object obj = JOptionPane.showInputDialog(null,
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
            GuiUtils.error(e.getMessage());
            handleCreateNetwork(input);
        }
    }

    /**
     * Handles the user input and creation of a sub-network.
     * @param parent the parent network
     * @param initialValue the initial input value to display
     */
    private void handleCreateNetwork(NetworkTreeNode parent, String initialValue) {
        String input = (String)JOptionPane.showInputDialog(null,
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
            GuiUtils.error(e.getMessage());
            handleCreateNetwork(parent, input);
        }
    }

    /**
     * Handles the user input and creation of a sub-network specified by network size.
     * @param parent the parent network
     */
    private void handleCreateNetworkBySize(NetworkTreeNode parent) {
        String input = JOptionPane.showInputDialog(null,
                "Host Anzahl (1-" + parent.getNetwork().getMaxHosts() +  "):",
                "Subnetz hinzufügen",
                JOptionPane.PLAIN_MESSAGE);
        if(input == null) return;
        int value;
        try {
            value = Integer.valueOf(input);
        } catch (NumberFormatException e) {
            GuiUtils.error("Bitte geben Sie eine gültige Anzahl an.");
            handleCreateNetworkBySize(parent);
            return;
        }
        if(value < 1 || value > parent.getNetwork().getMaxHosts()) {
            GuiUtils.error("Bitte geben Sie eine gültige Anzahl an.");
            handleCreateNetworkBySize(parent);
            return;
        }
        networkTreeModel.addNetwork(parent,value);
    }

    /**
     * Handles the user input and splitting of a network in sub-networks by sub-network size.
     * @param networkTreeNode the network to split
     */
    private void handleSplitBySize(NetworkTreeNode networkTreeNode) {
        ArrayList<Integer> deviders = networkTreeNode.getNetwork().possibleDividers();
        Collections.reverse(deviders);

        Object input = JOptionPane.showInputDialog(null,
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

    /**
     * Handles the user input and splitting of a network in sub-networks by sub-network count.
     * @param networkTreeNode the network to split
     */
    private void handleSplitByCount(NetworkTreeNode networkTreeNode) {
        ArrayList<Integer> sizes = new ArrayList<>();

        networkTreeNode.getNetwork().possibleDividers()
                .forEach(x -> sizes.add((networkTreeNode.getNetwork().getMaxHosts()+2) / (x+2)));
        Collections.reverse(sizes);

        Object input = JOptionPane.showInputDialog(null,
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

    /**
     * Adds a host to the passed network.
     * @param networkNode the network to fill.
     */
    private void handleAddHost(NetworkTreeNode networkNode) {
        if(networkNode.getNetwork().getHostCount() >= networkNode.getNetwork().getMaxHosts()) {
            GuiUtils.error("Zu diesem Netzwerk können keine weiteren Hosts mehr hinzugefügt werden.");
            return;
        }
        networkNode.getNetwork().addHost();
        hostPanel.reload();
    }

    /**
     * Handles the user input and the creation of a host specified by an ip address.
     * @param networkNode the network to fill with the host
     */
    private void handleAddHostWithIP(NetworkTreeNode networkNode) {
        if(networkNode.getNetwork().getHostCount() >= networkNode.getNetwork().getMaxHosts()) {
            GuiUtils.error("Zu diesem Netzwerk können keine weiteren Hosts mehr hinzugefügt werden.");
            return;
        }
        Network network = networkNode.getNetwork();
        String networkString = network.getNetworkIdV4().toString() + "/" +
                NetUtils.maskToPrefix(network.getSubnetMaskV4());
        String input = (String) JOptionPane.showInputDialog(null,
                "IP Adresse (" + networkString + ")",
                "Host mit IP Adresse hinzufügen",
                JOptionPane.PLAIN_MESSAGE,
                null,
                null,
                GuiUtils.getInitialSubnetString(network));

        if(input == null) return;

        IPv4Address address;
        try {
            address = IPAddress.parseIPv4(input);
        } catch (Exception e) {
            GuiUtils.error("Bitte geben Sie eine gültige IPv4 Addresse an.");
            handleAddHostWithIP(networkNode);
            return;
        }

        if(!NetUtils.isInSubnet(network.getNetworkIdV4(), network.getSubnetMaskV4(), address)) {
            GuiUtils.error("Die eingegebene IPv4 Adresse liegt nicht im übergeordneten Netzwerk.");
            handleAddHostWithIP(networkNode);
        } else if(address.equals(network.getNetworkIdV4())) {
            GuiUtils.error("Die IPv4 Netzwerk ID des Netzwerkes kann nicht benutzt werden.");
            handleAddHostWithIP(networkNode);
        } else if(address.equals(network.getBroadcastAddress())) {
            GuiUtils.error("Die IPv4 Broadcast Adresse des Netzwerkes kann nicht benutzt werden.");
            handleAddHostWithIP(networkNode);
        } else if(network.getHost(address) != null) {
            GuiUtils.error("Die eingegebene IPv4 Adresse wird bereits genutzt.");
            handleAddHostWithIP(networkNode);
        } else {
            network.addHost(IPAddress.parseIPv4(input));
            hostPanel.reload();
        }
    }

    /**
     * Fill all remaining slots in a network with their corresponding hosts.
     * @param networkNode the network node to fill
     */
    private void handleAddAllHosts(NetworkTreeNode networkNode) {
        boolean assignIPv6 = networkNode.getNetwork().isIPv6Enabled() &&
                GuiUtils.confirmationYesNo("Hosts IPv6 zuweisen", "Soll allen Hosts eine zufällige IPv6 Adresse zugewiesen werden?");
        networkNode.getNetwork().addAllHosts(assignIPv6);
        hostPanel.reload();
    }

    /**
     * Removes all hosts from a network.
     * @param networkNode the network to clear hosts from
     */
    private void handleClearHosts(NetworkTreeNode networkNode) {
        networkNode.getNetwork().clearHosts();
        hostPanel.reload();
    }

    /**
     * Removes all sub-networks from a network.
     * @param networkNode the network to clear subnets from
     */
    private void handleClearSubnets(NetworkTreeNode networkNode) {
        networkTreeModel.clearSubnets(networkNode);
    }

    /**
     * Handles the user input and renaming of a network.
     * @param networkNode the network node to rename
     */
    private void handleRename(NetworkTreeNode networkNode) {
        String initialValue = networkNode.getNetwork().getName();
        String input = (String) JOptionPane.showInputDialog(null,
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