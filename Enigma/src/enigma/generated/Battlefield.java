
package enigma.generated;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
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
 *       &lt;attribute name="rounds" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="level" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;enumeration value="Easy"/>
 *             &lt;enumeration value="Medium"/>
 *             &lt;enumeration value="Hard"/>
 *             &lt;enumeration value="Insane"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="battle-name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="allies" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "Battlefield")
public class Battlefield {

    @XmlAttribute(name = "rounds", required = true)
    protected int rounds;
    @XmlAttribute(name = "level", required = true)
    protected String level;
    @XmlAttribute(name = "battle-name", required = true)
    protected String battleName;
    @XmlAttribute(name = "allies", required = true)
    protected int allies;

    /**
     * Gets the value of the rounds property.
     * 
     */
    public int getRounds() {
        return rounds;
    }

    /**
     * Sets the value of the rounds property.
     * 
     */
    public void setRounds(int value) {
        this.rounds = value;
    }

    /**
     * Gets the value of the level property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLevel() {
        return level;
    }

    /**
     * Sets the value of the level property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLevel(String value) {
        this.level = value;
    }

    /**
     * Gets the value of the battleName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBattleName() {
        return battleName;
    }

    /**
     * Sets the value of the battleName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBattleName(String value) {
        this.battleName = value;
    }

    /**
     * Gets the value of the allies property.
     * 
     */
    public int getAllies() {
        return allies;
    }

    /**
     * Sets the value of the allies property.
     * 
     */
    public void setAllies(int value) {
        this.allies = value;
    }

}
