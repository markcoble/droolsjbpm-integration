package org.kie.simulation.fluent;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "ContextFluent")
public class ContextFluent<T, E>{

    /**
     * The last executed command result is set to a name in this executing context. Default Scope is Request
     * @param name
     * @return this
     */
    @XmlElement()
    Object set(String name){
        return null;}

//    T set(String name, Scope scope){
//        return null;}
//
//    T get(String name) {
//        return null;
//    }
//
//    T get(String name, Scope scope) {
//        return null;
//    }
//
//    /**
//     * This sets an instance, for a given cls key, on the registry for commands to execute against.
//     * This method will call "end" if within the context of a given registry command
//     * @param name
//     * @param cls
//     * @param <K>
//     * @return
//     */
//    <K>  K get(String name, Class<K> cls) {
//        return null;
//    }
//
//    /**
//     * The output from the last command should be returned via the out results. It uses the last used name identifer for the previous
//     * get or set.
//     * @return this
//     */
//    T out() {
//        return null;
//    }
//
//    /**
//     * The output from the last executed command should be returned and set to the given name in the context. It uses the specified
//     * named identifierl
//     * @param name
//     * @return this
//     */
//    T out(String name) {
//        return null;
//    }
//
//    T newApplicationContext(String name) {
//        return null;
//    }
//
//    T getApplicationContext(String name) {
//        return null;
//    }
//
//    T startConversation() {
//        return null;
//    }
//
//    T joinConversation(String uuid) {
//        return null;
//    }
//
//    T leaveConversation() {
//        return null;
//    }
//
//    T endConversation(String uuid) {
//        return null;
//    }
//
//    /**
//     * End the scope of the current Command set
//     * @return
//     */
//    E end() {
//        return null;
//    }
} 