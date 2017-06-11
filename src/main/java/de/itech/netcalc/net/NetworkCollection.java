package de.itech.netcalc.net;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.File;
import java.util.ArrayList;

@XmlRootElement
public class NetworkCollection {
    private ArrayList<Network> networks = new ArrayList<>();

    private IPv6Address globalIPv6Prefix;

    private int globalIPv6PrefixLength;

    public NetworkCollection(){}

    @XmlElement(name = "Network")
    public ArrayList<Network> getNetworks() {
        return networks;
    }

    public void setNetworks(ArrayList<Network> networks) {
        this.networks = networks;
    }

    public IPv6Address getGlobalIPv6Prefix() {
        return globalIPv6Prefix;
    }

    public void setGlobalIPv6Prefix(IPv6Address globalIPv6Prefix) {
        this.globalIPv6Prefix = globalIPv6Prefix;
    }

    public int getGlobalIPv6PrefixLength() {
        return globalIPv6PrefixLength;
    }

    public void setGlobalIPv6PrefixLength(int globalIPv6PrefixLength) {
        this.globalIPv6PrefixLength = globalIPv6PrefixLength;
    }

    public void save(File file){
        try {

            JAXBContext jaxbContext = JAXBContext.newInstance(NetworkCollection.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

            // output pretty printed
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            jaxbMarshaller.marshal(this, file);
            jaxbMarshaller.marshal(this, System.out);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static NetworkCollection fromXML(File file){
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(NetworkCollection.class);

            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            NetworkCollection networkCollection = (NetworkCollection) jaxbUnmarshaller.unmarshal(file);

            return networkCollection;
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        return null;
    }
}
