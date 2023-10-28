/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.longz.thss.test.jtamapstoreejb.entity;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.longz.thss.test.jtamapstoreejb.factory.EventDataSerializableFactory;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;

import java.io.IOException;
import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.TimeZone;
import java.util.UUID;

/**
 *
 * @author cidylong
 */
@Entity
@Table(catalog = "ozssc", schema = "public")
@NamedQueries({
    @NamedQuery(name = "Event.findAll", query = "SELECT e FROM Event e"),
    @NamedQuery(name = "Event.findByEventId", query = "SELECT e FROM Event e WHERE e.eventId = :eventId"),
    @NamedQuery(name = "Event.findAllIds", query = "SELECT e.eventId FROM Event e order by e.eventId desc"),
    @NamedQuery(name = "Event.findByEventFeederModel", query = "SELECT e FROM Event e WHERE e.eventFeederModel = :eventFeederModel"),
    @NamedQuery(name = "Event.findByEventTitle", query = "SELECT e FROM Event e WHERE e.eventTitle = :eventTitle"),
    @NamedQuery(name = "Event.findByEventInfo", query = "SELECT e FROM Event e WHERE e.eventInfo = :eventInfo"),
    @NamedQuery(name = "Event.findByEventTriger", query = "SELECT e FROM Event e WHERE e.eventTriger = :eventTriger"),
    @NamedQuery(name = "Event.findByEventCreated", query = "SELECT e FROM Event e WHERE e.eventCreated = :eventCreated"),
    @NamedQuery(name = "Event.findByEventApp", query = "SELECT e FROM Event e WHERE e.eventApp = :eventApp")})
public class Event implements IdentifiedDataSerializable, Serializable {
    private static final int CLASS_ID = 1104;
    /*private static final long serialVersionUID = 1L;*/
    @Id
    @Basic(optional = false)
    @Column(name = "event_id")
    private String eventId;
    @Basic(optional = false)
    @Column(name = "event_feeder_model")
    private String eventFeederModel;
    @Basic(optional = false)
    @Column(name = "event_title")
    private String eventTitle;
    @Basic(optional = false)
    @Column(name = "event_info")
    private String eventInfo;
    @Column(name = "event_triger")
    private String eventTriger;
    @Column(name = "event_created")
    /*@Temporal(TemporalType.TIMESTAMP)*/
    private LocalDateTime eventCreated;
    @Basic(optional = false)
    @Column(name = "event_app")
    private String eventApp;

    public Event() {
    }

    public Event(String eventId) {
        this.eventId = eventId;
    }

    public Event(String eventId, String eventFeederModel, String eventTitle, String eventInfo, String eventApp) {
        this.eventId = eventId;
        this.eventFeederModel = eventFeederModel;
        this.eventTitle = eventTitle;
        this.eventInfo = eventInfo;
        this.eventApp = eventApp;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getEventFeederModel() {
        return eventFeederModel;
    }

    public void setEventFeederModel(String eventFeederModel) {
        this.eventFeederModel = eventFeederModel;
    }

    public String getEventTitle() {
        return eventTitle;
    }

    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }

    public String getEventInfo() {
        return eventInfo;
    }

    public void setEventInfo(String eventInfo) {
        this.eventInfo = eventInfo;
    }

    public String getEventTriger() {
        return eventTriger;
    }

    public void setEventTriger(String eventTriger) {
        this.eventTriger = eventTriger;
    }

    public LocalDateTime getEventCreated() {
        return eventCreated;
    }

    public void setEventCreated(LocalDateTime eventCreated) {
        this.eventCreated = eventCreated;
    }

    public String getEventApp() {
        return eventApp;
    }

    public void setEventApp(String eventApp) {
        this.eventApp = eventApp;
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventId,eventFeederModel,eventTitle,eventInfo,eventTriger,eventCreated,eventApp);
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Event)) {
            return false;
        }
        Event other = (Event) object;
        return Objects.equals(this.eventId,other.eventId) &&
                Objects.equals(this.eventFeederModel,other.eventFeederModel) &&
                Objects.equals(this.eventTitle,other.eventTitle) &&
                Objects.equals(this.eventInfo,other.eventInfo) &&
                Objects.equals(this.eventTriger,other.eventTriger) &&
                Objects.equals(this.eventCreated,other.eventCreated) &&
                Objects.equals(this.eventApp,other.eventApp);
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        String NEW_LINE = System.getProperty("line.separator");
        result.append(this.getClass().getName() + " Object {" + NEW_LINE);
        result.append("Id: " + this.eventId + NEW_LINE);
        result.append("Feeder module: " + this.eventFeederModel + NEW_LINE);
        result.append("Ttile: " + this.eventTitle + NEW_LINE);
        result.append("Info: " + this.eventInfo + NEW_LINE);
        result.append("Trigger: " + this.eventTriger + NEW_LINE);

        result.append("Created: " + this.eventCreated + NEW_LINE);
        result.append("Fire Application: " + this.eventApp + "}");
        return result.toString();
    }

    @Override
    public int getFactoryId() {
        return EventDataSerializableFactory.FACTORY_ID;
    }

    @Override
    public int getClassId() {
        return CLASS_ID;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeString(eventId);
        out.writeString(eventFeederModel);
        out.writeString(eventTitle);
        out.writeString(eventInfo);
        out.writeString(eventTriger);

        out.writeLong(ZonedDateTime.of(eventCreated, ZoneId.systemDefault()).toInstant().toEpochMilli());
        out.writeString(eventApp);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        setEventId(in.readString());
        setEventFeederModel(in.readString());
        setEventTitle(in.readString());
        setEventInfo(in.readString());
        setEventTriger(in.readString());

        setEventCreated(LocalDateTime.ofInstant(Instant.ofEpochMilli(in.readLong()), TimeZone.getDefault().toZoneId()));
        setEventApp(in.readString());
    }
    public void initial(){
        if(eventId == null ||eventId.trim().length() ==0){
            setEventId(String.valueOf(UUID.randomUUID()));
            setEventCreated(LocalDateTime.now());
        }
    }
    public void contentSets(String feederModel, String title,String info,String triger,String app){
        this.eventFeederModel = feederModel;
        this.eventTitle = title;
        this.eventInfo = info;
        this.eventTriger = triger;
        this.eventApp = app;
    }
}
