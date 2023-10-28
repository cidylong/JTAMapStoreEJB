package com.longz.thss.test.jtamapstoreejb.store;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.logging.ILogger;
import com.hazelcast.logging.Logger;
import com.hazelcast.map.MapLoaderLifecycleSupport;
import com.hazelcast.map.MapStore;
import com.longz.thss.test.jtamapstoreejb.entity.Event;
import jakarta.persistence.*;

import java.io.Serializable;
import java.util.*;

import static com.longz.thss.test.jtamapstoreejb.utils.Utils.NEW_LINE;

public class EventMapStore implements MapStore<String, Event>, MapLoaderLifecycleSupport, Serializable {
    private static final ILogger logger = Logger.getLogger(EventMapStore.class.getName());
    private static final String PU_NAME = "PG15_OZSSC_JTAPU";
    @PersistenceUnit(unitName = PU_NAME)
    private EntityManagerFactory emf;
    @PersistenceContext(unitName = PU_NAME)
    private EntityManager em;
    @Override
    public void init(HazelcastInstance hazelcastInstance, Properties properties, String s) {
        logger.info( "Init EventMapStore");
        if(emf == null){
            emf = Persistence.createEntityManagerFactory(PU_NAME);
        }
        if(em == null){
            em = emf.createEntityManager();
        }
    }
    @Override
    public void destroy() {
        logger.info("Destroy EventMapStore");
        em.close();
        emf.close();
    }
    @Override
    public synchronized void store(String s, Event entity) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("--------------------------------------event store called---------------------------------------------------").append(NEW_LINE);
        Event exist = em.find(Event.class,s);
        if (exist != null){
            stringBuilder.append("exist event found.").append(NEW_LINE);
            if (!entity.equals(exist)){
                em.merge(entity);
                stringBuilder.append("exist event merged.").append(NEW_LINE);
            }
        }else{
            stringBuilder.append("New Event persist start.").append(NEW_LINE);
            em.persist(entity);
            stringBuilder.append("Persisted.").append(NEW_LINE);
        }
        stringBuilder.append("--------------------------------------event store ended---------------------------------------------------");
        logger.info(stringBuilder.toString());
    }
    @Override
    public synchronized void storeAll(Map<String, Event> map) {
        for(Map.Entry<String, Event> entry : map.entrySet()) {
            store(entry.getKey(),entry.getValue());
        }
    }
    @Override
    public synchronized void delete(String s) {
        Event exist =em.find(Event.class,s);
        if(exist !=null){
            em.remove(exist);
        }
    }
    @Override
    public void deleteAll(Collection<String> collection) {
        for(String s : collection) {
            Event exist = em.find(Event.class, s);
            if (exist != null) {
                em.remove(exist);
            }
        }
    }
    @Override
    public synchronized Event load(String s) {
        return em.find(Event.class,s);
    }
    @Override
    public synchronized Map<String, Event> loadAll(Collection<String> collection) {
        Map<String,Event> returnMap = new HashMap<>();
        Query query = em.createNamedQuery("Event.findAll", Event.class);
        List<Event> entities = query.getResultList();
        for (Event entity : entities){
            if(collection.contains(entity.getEventId())){
                returnMap.put(entity.getEventId(),entity);
            }
        }
        return returnMap;
    }
    @Override
    public synchronized Iterable<String> loadAllKeys() {
        Query query = em.createNamedQuery("Event.findAllIds",Event.class);
        Set<String> keys = new HashSet<String>(query.getResultList());
        return keys;
    }
}
