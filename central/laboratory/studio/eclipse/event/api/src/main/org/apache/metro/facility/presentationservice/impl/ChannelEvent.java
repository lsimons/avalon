/*
 * TemperatureChangeEvent.java
 *
 * Created on January 2, 2002, 7:31 PM
 */

package org.apache.metro.facility.presentationservice.impl;
import java.util.Hashtable;
/**
 *
 * @author  default
 * @version 
 */
public class ChannelEvent extends java.util.EventObject {

    /** Holds value of property newTemperature. */
    private String topic = null;
    /** Holds value of property oldTemperature. */
    private Hashtable data = null;
    
    /** Creates new TemperatureChangeEvent */
    public ChannelEvent(Object source, String topic, Hashtable data){
        super(source);
        this.topic = topic;
        this.data = data;
    }
    
    /** 
     * @return topic of the event
     */
    public String getTopic() {
        return topic;
    }
    
    /** 
     */
    public Hashtable getData(){
        return data;
    }
    
    public void putValue(String key, String value)
    {
        if(value==null)
        {
            value="";
        }
        data.put(key, value);
    }
    
    public void putValueArray(String key, String[] value)
    {
        data.put(key, value);
    }

    /**
     * @param event
     */
    public void setTopic(String topic)
    {
        this.topic = topic;
        
    }

    /**
     * @param string
     * @return
     */
    public String getValue(String string)
    {
        return (String)data.get(string);
    }

    /**
     * @param string
     * @return
     */
    public String[] getValueArray(String string)
    {
        return (String[])data.get(string);
    }
}
