
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
 *       &lt;attribute name="input" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="output" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "Reflect")
public class Reflect {

    @XmlAttribute(name = "input", required = true)
    protected int input;
    @XmlAttribute(name = "output", required = true)
    protected int output;

    /**
     * Gets the value of the input property.
     * 
     */
    public int getInput() {
        return input;
    }

    /**
     * Sets the value of the input property.
     * 
     */
    public void setInput(int value) {
        this.input = value;
    }

    /**
     * Gets the value of the output property.
     * 
     */
    public int getOutput() {
        return output;
    }

    /**
     * Sets the value of the output property.
     * 
     */
    public void setOutput(int value) {
        this.output = value;
    }

}
