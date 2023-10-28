package com.longz.thss.test.jtamapstoreejb.abst;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.logging.ILogger;
import com.hazelcast.logging.Logger;
import com.hazelcast.map.IMap;
import com.longz.thss.test.jtamapstoreejb.utils.Utils;
import jakarta.inject.Inject;

import java.util.Locale;

public abstract class AbstractHzMapBean<T> {
    private static final ILogger logger = Logger.getLogger(AbstractHzMapBean.class.getName());
    private Class<T> mappedClass;
    private IMap<String, T> entityMap;
    private String entityName;
    @Inject
    private HazelcastInstance instance;
    public IMap<String, T> getEntityMap(){
        return this.entityMap;
    }
    public String getEntityName(){return this.entityName;}
    public HazelcastInstance getInstance(){return this.instance;}

    public AbstractHzMapBean(){ }
    public AbstractHzMapBean(Class<T> theClass){
        this.mappedClass = theClass;
        entityName = theClass.getSimpleName();
        logger.info("----------------------Bean Abstract constructor kicked in from Bean injection. Entity Name: "+entityName);
        init(theClass);
    }

    public T find(Object id){
        return (T)entityMap.get(id);
    }

    /*@PostConstruct*/
    protected void init(Class<T> theClass){
        if (instance == null){
            instance = Utils.fetchHzInstanceFromContext();
            logger.info("--------Found HZInstance from context: "+instance.getName());
        }else{
            logger.info("-------Injected into abstract bean instance: "+instance.getName());
        }
        if(instance != null){
            entityMap = instance.getMap(entityName.toLowerCase(Locale.ROOT));
            logger.info("---------Entity map found as name: "+entityMap.getName()+"| size: "+entityMap.size());
        }
    }
}
