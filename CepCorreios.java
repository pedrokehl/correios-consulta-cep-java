package cep.correios;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class CepCorreios {

    public static void main(String[] args) {

        String cep = "92420170";

        try {
            // Create the connection
            SOAPConnectionFactory scf = SOAPConnectionFactory.newInstance();
            SOAPConnection conn = scf.createConnection();
            // Create message
            MessageFactory mf = MessageFactory.newInstance();
            SOAPMessage msg = mf.createMessage();
            // Object for message parts
            SOAPPart sp = msg.getSOAPPart();
            SOAPEnvelope env = sp.getEnvelope();
            env.addNamespaceDeclaration("cli", "http://cliente.bean.master.sigep.bsb.correios.com.br/");
            SOAPBody bd = env.getBody();
            // Populate body
            // Main element
            SOAPElement be = bd.addChildElement(env.createName("cli:consultaCEP"));
            // Add content
            be.addChildElement("cep").addTextNode(cep);
            // Save message
            msg.saveChanges();
            // Send
            String urlval = "https://apps.correios.com.br/SigepMasterJPA/AtendeClienteService/AtendeCliente?wsdl";
            SOAPMessage rp = conn.call(msg, urlval);

            // slice SOAP response
            NodeList returnNodes = rp.getSOAPBody().getElementsByTagName("return");

            if (returnNodes.getLength() > 0) {
                Node returnNode = returnNodes.item(0);
                NodeList elements = returnNode.getChildNodes();
                for (int i = 0; i < elements.getLength(); i++) {
                    Node node = elements.item(i);
                    System.out.println(node.getNodeName() + " - " + node.getTextContent());
                }
            }
            else {
                returnNodes = rp.getSOAPBody().getElementsByTagName("detail");
                Node returnNode = returnNodes.item(0);
                NodeList elements = returnNode.getChildNodes();

                for (int i = 0; i < elements.getLength(); i++) {
                    Node node = elements.item(i);
                    System.out.println(node.getTextContent());
                }
            }
            conn.close();
        } catch (SOAPException ex) {
            System.out.println("connection error - " + ex.getMessage());
        }
    }
}