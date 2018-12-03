
package enigma.generated;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
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
 *         &lt;element ref="{}Machine"/>
 *         &lt;element ref="{}Decipher" minOccurs="0"/>
 *         &lt;element ref="{}Battlefield" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "machine",
    "decipher",
    "battlefield"
})
@XmlRootElement(name = "Enigma")
public class Enigma {

    @XmlElement(name = "Machine", required = true)
    protected Machine machine;
    @XmlElement(name = "Decipher")
    protected Decipher decipher;
    @XmlElement(name = "Battlefield")
    protected Battlefield battlefield;

    /**
     * Gets the value of the machine property.
     * 
     * @return
     *     possible object is
     *     {@link Machine }
     *     
     */
    public Machine getMachine() {
        return machine;
    }

    /**
     * Sets the value of the machine property.
     * 
     * @param value
     *     allowed object is
     *     {@link Machine }
     *     
     */
    public void setMachine(Machine value) {
        this.machine = value;
    }

    /**
     * Gets the value of the decipher property.
     * 
     * @return
     *     possible object is
     *     {@link Decipher }
     *     
     */
    public Decipher getDecipher() {
        return decipher;
    }

    /**
     * Sets the value of the decipher property.
     * 
     * @param value
     *     allowed object is
     *     {@link Decipher }
     *     
     */
    public void setDecipher(Decipher value) {
        this.decipher = value;
    }

    /**
     * Gets the value of the battlefield property.
     * 
     * @return
     *     possible object is
     *     {@link Battlefield }
     *     
     */
    public Battlefield getBattlefield() {
        return battlefield;
    }

    /**
     * Sets the value of the battlefield property.
     * 
     * @param value
     *     allowed object is
     *     {@link Battlefield }
     *     
     */
    public void setBattlefield(Battlefield value) {
        this.battlefield = value;
    }

}
