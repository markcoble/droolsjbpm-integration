//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2017.07.21 at 01:49:18 PM BST 
//


package org.kie.simulation;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 *  Single row of stack-oriented program
 *       
 * 
 * <p>Java class for StackItem complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="StackItem">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Time" type="{http://org.kie.simulation/types/}TimeType" minOccurs="0"/>
 *         &lt;element name="Scope" type="{http://org.kie.simulation/types/}TimeType" minOccurs="0"/>
 *         &lt;element name="Set" type="{http://org.kie.simulation/types/}TimeType" minOccurs="0"/>
 *         &lt;element name="Out" type="{http://org.kie.simulation/types/}TimeType" minOccurs="0"/>
 *         &lt;element name="Command" type="{http://org.kie.simulation/types/}TimeType"/>
 *         &lt;element name="CommandInputParameter" type="{http://org.kie.simulation/types/}CommandArgumentType" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "StackItem", propOrder = {
    "time",
    "scope",
    "set",
    "out",
    "command",
    "commandInputParameter"
})
public class StackItem {

    @XmlElement(name = "Time")
    protected String time;
    @XmlElement(name = "Scope")
    protected String scope;
    @XmlElement(name = "Set")
    protected String set;
    @XmlElement(name = "Out")
    protected String out;
    @XmlElement(name = "Command", required = true)
    protected String command;
    @XmlElement(name = "CommandInputParameter", required = true)
    protected List<CommandArgumentType> commandInputParameter;

    /**
     * Gets the value of the time property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTime() {
        return time;
    }

    /**
     * Sets the value of the time property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTime(String value) {
        this.time = value;
    }

    /**
     * Gets the value of the scope property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getScope() {
        return scope;
    }

    /**
     * Sets the value of the scope property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setScope(String value) {
        this.scope = value;
    }

    /**
     * Gets the value of the set property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSet() {
        return set;
    }

    /**
     * Sets the value of the set property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSet(String value) {
        this.set = value;
    }

    /**
     * Gets the value of the out property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOut() {
        return out;
    }

    /**
     * Sets the value of the out property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOut(String value) {
        this.out = value;
    }

    /**
     * Gets the value of the command property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCommand() {
        return command;
    }

    /**
     * Sets the value of the command property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCommand(String value) {
        this.command = value;
    }

    /**
     * Gets the value of the commandInputParameter property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the commandInputParameter property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCommandInputParameter().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link CommandArgumentType }
     * 
     * 
     */
    public List<CommandArgumentType> getCommandInputParameter() {
        if (commandInputParameter == null) {
            commandInputParameter = new ArrayList<CommandArgumentType>();
        }
        return this.commandInputParameter;
    }

}
