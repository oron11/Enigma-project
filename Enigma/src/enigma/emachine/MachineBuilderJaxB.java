package enigma.emachine;

import enigma.generated.Enigma;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class MachineBuilderJaxB {
    private final static String JAXB_XML_PACKAGE_NAME = "enigma.generated";

    public static EnigmaInfo parseXmlToEnigmaMachine(InputStream inputStream) throws JAXBException {
        JAXBContext jc = JAXBContext.newInstance(JAXB_XML_PACKAGE_NAME);
        Unmarshaller unmarshaller = jc.createUnmarshaller();
        Enigma enigma = (Enigma)unmarshaller.unmarshal(inputStream);
        MachineBuilder machineBuilder = new MachineBuilder();

        return machineBuilder.checkEnigmaMachineValidationAndReturnEnigmaInfo(enigma);
    }


    /*public static EnigmaMachine parseXmlToEnigmaMachine(String pathXmlFile) throws FileNotFoundException {
        File file = new File(pathXmlFile);
        if (file.exists()) {
            try {
                JAXBContext jc = JAXBContext.newInstance(JAXB_XML_PACKAGE_NAME);
                Unmarshaller unmarshaller = jc.createUnmarshaller();
                Enigma enigma = (Enigma)unmarshaller.unmarshal(file);
                return MachineBuilder.getInstance().checkEnigmaMachineValidationAndReturnInstance(enigma);

            } catch (JAXBException e) {
                e.printStackTrace();
            }
        }
        else {
            throw new FileNotFoundException("The XML file given by the user does not exist.");
        }

        return null;
    }*/
}
