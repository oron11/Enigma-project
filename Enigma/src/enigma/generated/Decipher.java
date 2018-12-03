
package enigma.generated;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{}Dictionary"/>
 *       &lt;/sequence>
 *       &lt;attribute name="agents" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "dictionary"
})
@XmlRootElement(name = "Decipher")
public class Decipher {

    @XmlElement(name = "Dictionary", required = true)
    protected Dictionary dictionary;
    @XmlAttribute(name = "agents", required = true)
    protected int agents;

    /**
     * Gets the value of the dictionary property.
     * 
     * @return
     *     possible object is
     *     {@link Dictionary }
     *     
     */
    public Dictionary getDictionary() {
        return dictionary;
    }

    /**
     * Sets the value of the dictionary property.
     * 
     * @param value
     *     allowed object is
     *     {@link Dictionary }
     *     
     */
    public void setDictionary(Dictionary value) {
        this.dictionary = value;
    }

    /**
     * Gets the value of the agents property.
     * 
     */
    public int getAgents() {
        return agents;
    }

    /**
     * Sets the value of the agents property.
     * 
     */
    public void setAgents(int value) {
        this.agents = value;
    }

}
