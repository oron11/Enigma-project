
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
 *         &lt;element name="ABC" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element ref="{}Rotors"/>
 *         &lt;element ref="{}Reflectors"/>
 *       &lt;/sequence>
 *       &lt;attribute name="rotors-count" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "abc",
    "rotors",
    "reflectors"
})
@XmlRootElement(name = "Machine")
public class Machine {

    @XmlElement(name = "ABC", required = true)
    protected String abc;
    @XmlElement(name = "Rotors", required = true)
    protected Rotors rotors;
    @XmlElement(name = "Reflectors", required = true)
    protected Reflectors reflectors;
    @XmlAttribute(name = "rotors-count", required = true)
    protected int rotorsCount;

    /**
     * Gets the value of the abc property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getABC() {
        return abc;
    }

    /**
     * Sets the value of the abc property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setABC(String value) {
        this.abc = value;
    }

    /**
     * Gets the value of the rotors property.
     * 
     * @return
     *     possible object is
     *     {@link Rotors }
     *     
     */
    public Rotors getRotors() {
        return rotors;
    }

    /**
     * Sets the value of the rotors property.
     * 
     * @param value
     *     allowed object is
     *     {@link Rotors }
     *     
     */
    public void setRotors(Rotors value) {
        this.rotors = value;
    }

    /**
     * Gets the value of the reflectors property.
     * 
     * @return
     *     possible object is
     *     {@link Reflectors }
     *     
     */
    public Reflectors getReflectors() {
        return reflectors;
    }

    /**
     * Sets the value of the reflectors property.
     * 
     * @param value
     *     allowed object is
     *     {@link Reflectors }
     *     
     */
    public void setReflectors(Reflectors value) {
        this.reflectors = value;
    }

    /**
     * Gets the value of the rotorsCount property.
     * 
     */
    public int getRotorsCount() {
        return rotorsCount;
    }

    /**
     * Sets the value of the rotorsCount property.
     * 
     */
    public void setRotorsCount(int value) {
        this.rotorsCount = value;
    }

}
