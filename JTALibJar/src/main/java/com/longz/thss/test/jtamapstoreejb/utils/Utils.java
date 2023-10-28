package com.longz.thss.test.jtamapstoreejb.utils;

import com.hazelcast.cluster.Member;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.logging.ILogger;
import com.hazelcast.logging.Logger;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Set;

public class Utils {
    private static final ILogger logger = Logger.getLogger(Utils.class.getName());
    public static final String NEW_LINE = System.lineSeparator();
    public static HazelcastInstance fetchHzInstanceFromContext(){
        HazelcastInstance hazelcastInstance =null;
        try {
            Context ctx = new InitialContext();
            hazelcastInstance = (HazelcastInstance) ctx.lookup("payara/Hazelcast");
            if(hazelcastInstance == null){
                logger.info("-------------------------There is no Instance found from context.");
            }else {
                /*setInstanceConfig();*/
                /*instanceName = hazelcastInstance.getName();*/
                Set<Member> allmembers = hazelcastInstance.getCluster().getMembers();
                logger.info("-------------------------Hazelcast Instance found from context. one of cluster members "+allmembers.size());
                if (allmembers.size()>0){
                    for (Member m : allmembers){
                        logger.info("Cluster member from context look up: "+m.toString());
                    }
                }
            }
        } catch (NamingException ex) {
            logger.info(ex.toString());
        }
        return hazelcastInstance;
    }
}
