package com.longz.thss.test.jtamapstoreejb.store;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.logging.ILogger;
import com.hazelcast.logging.Logger;
import com.hazelcast.map.MapLoaderLifecycleSupport;
import com.hazelcast.map.MapStore;
import com.longz.thss.test.jtamapstoreejb.entity.Event;
import jakarta.annotation.Resource;
import jakarta.annotation.sql.DataSourceDefinition;
import jakarta.ejb.TimerService;
import jakarta.inject.Inject;
import jakarta.persistence.*;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.sessions.factories.SessionFactory;

import javax.naming.InitialContext;
import javax.sql.DataSource;
import javax.sql.XADataSource;
import java.io.Serializable;
import java.sql.Connection;
import java.util.*;

import static com.longz.thss.test.jtamapstoreejb.utils.Utils.NEW_LINE;

public class EventMapStore implements MapStore<String, Event>, MapLoaderLifecycleSupport, Serializable {
    private static final ILogger logger = Logger.getLogger(EventMapStore.class.getName());
    private static final String DATA_SOURCE = "jdbc/PG15_ozssc_150";
    private static final String XA_DATASOURCE = "java:global/jdbc/xaDataSource";
    private static final String PU_NAME = "PG15_OZSSC_JTAPU";
    @PersistenceUnit(unitName = PU_NAME)
    private EntityManagerFactory emf;
    @PersistenceContext(unitName = PU_NAME)
    private EntityManager em;
    @Resource(lookup=DATA_SOURCE)
    private EntityManager entityManager;
    @Inject ThssXADataSourceProducer ds;
    /*@Resource
    private TimerService timerService;*/
    @Override
    public void init(HazelcastInstance hazelcastInstance, Properties properties, String s) {
        final Map<String, String> overridingProps = new HashMap<>();
        overridingProps.put("jakarta.persistence.provider","org.eclipse.persistence.jpa.PersistenceProvider");
        overridingProps.put("jakarta.persistence.transactionType","JTA");
        overridingProps.put("jakarta.persistence.jtaDataSource","jdbc/PG15_ozssc_150");
        logger.info( "Init EventMapStore");
        if(ds!=null){
            logger.info("XADataSource @inject-ed: "+ds.getDatasource().getClass().getName());
        }else{
            logger.info("XADataSource not @inject");
        }
        if(entityManager!= null) {
            logger.info("EM by @Resource injection: " + entityManager.toString());
        }else {
            logger.info("EM inject by using @Resource annotation not work.");
        }
        if(emf == null){
            logger.info("Entity manager factory inject by @PersistenceUnit(unitName = PU_NAME) not work. Have to create by Persistence");
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
            em = emf.createEntityManager(SynchronizationType.SYNCHRONIZED);
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
        try {
            InitialContext  namingContext=new InitialContext();
            XADataSource xaDataSource = (XADataSource) namingContext.lookup(DATA_SOURCE);
            logger.info("DataSource class name by lookup from context: "+xaDataSource.getClass().getName());
            /*XADataSource xads =(XADataSource)namingContext.lookup(DATA_SOURCE);
            if(xads instanceof XADataSource){
                logger.info("XADataSource lookup from context successfully.");
            }
            final java.sql.Connection con;
            boolean usingDataSource=false;
            boolean isXADataSource=false;
            con= (Connection) ds.getXAConnection();*/
            /*ds.unwrap(javax.sql.XADataSource.class);
            isXADataSource=true;*/
        } catch (Exception e) {
            logger.info("DataSource lookup from context exception caught. " + e.toString());
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
