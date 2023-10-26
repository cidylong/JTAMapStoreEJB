package com.longz.thss.test.jtamapstoreejb.bean;

import com.hazelcast.logging.ILogger;
import com.hazelcast.logging.Logger;
import com.hazelcast.query.Predicate;
import com.hazelcast.query.Predicates;
import com.longz.thss.test.jtamapstoreejb.abst.AbstractHzMapBean;
import com.longz.thss.test.jtamapstoreejb.entity.Event;
import com.longz.thss.test.jtamapstoreejb.store.EventMapStore;
import jakarta.enterprise.context.SessionScoped;
import jakarta.enterprise.inject.Model;
import jakarta.inject.Named;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;

@Model
@Named(value = "eventBean")
@SessionScoped
public class EventBean extends AbstractHzMapBean<Event> implements Serializable {
    private static final ILogger logger = Logger.getLogger(EventBean.class.getName());
    private Event currentEntity = null;
    private List<Event> entityList = null;
    public Event getCurrentEntity(){return currentEntity;}
    public List<Event> getEntityList(){return entityList;}
    public void setCurrentEntity(Event entity){currentEntity = entity;}
    public void setEntityList(List<Event> entities){entityList = entities;}

    public EventBean(){
        super(Event.class);
        entityList = new LinkedList<>();
    }
    public EventBean(Class<Event> theClass){
        super(theClass);
        entityList = new LinkedList<>();
    }
    public void popupMapFromDatabase(){
        EventMapStore mapStore = new EventMapStore();
        Iterable<String> keys = mapStore.loadAllKeys();
        if(keys != null){
            mapStore.loadAll((Collection<String>) keys);
        }
    }
    public void findAll(){
        if (getEntityMap() != null){
            Set<String> keys = getEntityMap().keySet();
            if(entityList.size()>0){
                entityList = new LinkedList<>();
            }
            if(keys.size()>0){
                for (String key : keys){
                    Event entity = getEntityMap().get(key);
                    if(entity != null){
                        entityList.add(entity);
                    }
                }
            }
        }
        entityList.sort(Comparator.comparing(Event::getEventCreated).reversed());
    }
    public void find(String id){
        if (getEntityMap() != null){
            Event entity = getEntityMap().get(id);
            if(entity != null){
                currentEntity = entity;
            }
        }
    }
    public Set<Event> findStaledEvents(LocalDateTime borderStamp){
        Predicate stalePredicate = Predicates.lessThan("eventCreated", borderStamp);
        return (Set<Event>) getEntityMap().values(stalePredicate);
    }
    public Set<Event> findRecentEventToBeMigrate(LocalDateTime cuttingTime){
        Predicate cuttingPredicate = Predicates.greaterThan("eventCreated", cuttingTime);
        return (Set<Event>) getEntityMap().values(cuttingPredicate);
    }
}
