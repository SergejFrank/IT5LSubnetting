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

    @XmlElement
    ArrayList<Network> networks = new ArrayList<>();

    public NetworkCollection(){}

    public ArrayList<Network> getNetworks() {
        return networks;
    }

    public void setNetworks(ArrayList<Network> networks) {
        this.networks = networks;
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
            JAXBContext jaxbContext = JAXBContext.newInstance(Network.class);

            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            NetworkCollection networkCollection = (NetworkCollection) jaxbUnmarshaller.unmarshal(file);

            return networkCollection;
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        return null;
    }
}
