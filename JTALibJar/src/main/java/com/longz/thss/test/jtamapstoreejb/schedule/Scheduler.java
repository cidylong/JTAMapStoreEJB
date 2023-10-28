package com.longz.thss.test.jtamapstoreejb.schedule;

import com.hazelcast.logging.ILogger;
import com.hazelcast.logging.Logger;
import com.longz.thss.test.jtamapstoreejb.bean.EventBean;
import com.longz.thss.test.jtamapstoreejb.entity.Event;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.Schedule;

import java.time.LocalDateTime;

public class Scheduler {
    private final ILogger logger = Logger.getLogger(this.getClass().getName());
    private EventBean eventBean;
    public Scheduler(){}
    @PostConstruct
    protected void init(){
        if (eventBean == null){
            eventBean = new EventBean();
        }
    }
    @Schedule(hour = "12", minute = "4", second = "5", info = "Lunch time event scratcher.")
    public void eventScratcher() {
        logger.info("Event scratcher fired.");
        Event lunchEvent = new Event();
        lunchEvent.initial();
        lunchEvent.contentSets(this.getClass().getName(),"event scratcher","Scratched on "+ LocalDateTime.now().toString(),"Scheduler","Scheduler on MigServer Instance");
        if(eventBean != null){
            eventBean.getEntityMap().put(lunchEvent.getEventId(),lunchEvent);
        }
    }
}
