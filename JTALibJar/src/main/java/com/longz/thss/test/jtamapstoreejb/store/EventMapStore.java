package com.longz.thss.test.jtamapstoreejb.store;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.logging.ILogger;
import com.hazelcast.logging.Logger;
import com.hazelcast.map.MapLoaderLifecycleSupport;
import com.hazelcast.map.MapStore;
import com.longz.thss.test.jtamapstoreejb.entity.Event;
import jakarta.persistence.*;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.sessions.factories.SessionFactory;

import javax.sql.DataSource;
import javax.sql.XADataSource;
import java.io.Serializable;
import java.sql.Connection;
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
        final Map<String, String> overridingProps = new HashMap<>();
        overridingProps.put("jakarta.persistence.provider","org.eclipse.persistence.jpa.PersistenceProvider");
        overridingProps.put("jakarta.persistence.transactionType","JTA");
        overridingProps.put("jakarta.persistence.jtaDataSource","jdbc/PG15_ozssc_150");
        logger.info( "Init EventMapStore");
        if(emf == null){
            emf = Persistence.createEntityManagerFactory(PU_NAME, overridingProps);
            /*emf = Persistence.createEntityManagerFactory(PU_NAME);*/
            logger.info("Entity manager factory is created by Persistence from given PU name");
            /*emf = Persistence.createEntityManagerFactory(PU_NAME);*/
        }else {
            logger.info(PU_NAME+" is injected into "+this.getClass().getName()+" Entity Manager Factory"+NEW_LINE);
        }
        logger.info("PU name: "+emf.getPersistenceUnitUtil().getClass().getName());
        Map<String, Object> prop = emf.getProperties();
        logger.info("entity manager factory properties map size: "+prop.size()+NEW_LINE);
        for (Map.Entry<String,Object> entry : prop.entrySet())
            logger.info("Key = " + entry.getKey() + ", Value = " + entry.getValue().toString()+NEW_LINE);

        if(em == null){
            em = emf.createEntityManager();
            logger.info("Entity manager is created from entity manager(just created.).");
        }else {
            logger.info(PU_NAME+" is injected into "+this.getClass().getName()+" Entity Manager"+NEW_LINE);
        }
        logger.info("JTADataSource:"+em.getEntityManagerFactory().getProperties().get( "jakarta.persistence.jtaDataSource" ).toString());
        /*logger.info("NoneJTADataSource:"+em.getEntityManagerFactory().getProperties().get( "jakarta.persistence.nonJtaDataSource" ).toString());*/
        /*EntityTransaction entityTransaction = em.getTransaction();
        logger.info(entityTransaction.toString()+NEW_LINE);*/
        /*Session session = em.unwrap(Session.class);
        Connection conn = (Connection) em.unwrap(java.sql.Connection.class);*/
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
