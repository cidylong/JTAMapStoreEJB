package com.longz.thss.test.jtamapstoreejb.ejb.schedule;

import com.hazelcast.logging.ILogger;
import com.hazelcast.logging.Logger;
import com.longz.thss.test.jtamapstoreejb.bean.EventBean;
import com.longz.thss.test.jtamapstoreejb.entity.Event;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import jakarta.ejb.*;

import java.time.LocalDateTime;
@Singleton
/*@Stateless*/
@Startup
/*@DependsOn("thssXADataSourceProducer")*/
public class Scheduler {
    private final ILogger logger = Logger.getLogger(this.getClass().getName());
    /*@Resource
    private TimerService timerService;*/
    @Resource
    private SessionContext context;
    private EventBean eventBean;
    public Scheduler(){}
    @PostConstruct
    protected void init(){
        if (eventBean == null){
            eventBean = new EventBean();
        }
        /*context.getTimerService().createTimer(10000, message);*/
        TimerConfig timerConfig = new TimerConfig();
        timerConfig.setInfo("IntervalTimerDemo_Info_Event");
        context.getTimerService().createTimer(10000, 3600000, timerConfig.getInfo());/*start after 10 seconds and repeat every hour.*/
        TimerConfig sTimerConfig = new TimerConfig();
        timerConfig.setInfo("CalendarProgTimerDemo_Info_Event");
        ScheduleExpression schedule = new ScheduleExpression();
        schedule.hour("*").minute("7").second("13,34");/*Every hour at minute 7 with multiple seconds.*/
        context.getTimerService().createCalendarTimer(schedule, sTimerConfig);
    }
    /*@Schedule(hour = "*", minute = "14", second = "5", info = "Lunch time event scratcher.")
    public void eventScratcher() {
        logger.info("Event scratcher fired.");
        Event lunchEvent = new Event();
        lunchEvent.initial();
        lunchEvent.contentSets(this.getClass().getName(),"event scratcher","Scratched on "+ LocalDateTime.now().toString(),"Scheduler","Scheduler on MigServer Instance");
        if(eventBean != null){
            eventBean.getEntityMap().put(lunchEvent.getEventId(),lunchEvent);
        }
    }*/
    /*@Timeout
    public void execute(Timer timer) {
        logger.info("Event scratcher fired from timer: "+timer.toString());
        StringBuilder builder = new StringBuilder();
        builder.append("Current Time : " + LocalDateTime.now()).append("|").append("Next Timeout : " + timer.getNextTimeout()).append("|").append("Time Remaining : " + timer.getTimeRemaining());
        Event lunchEvent = new Event();
        lunchEvent.initial();
        lunchEvent.contentSets(this.getClass().getName(),timer.toString(),builder.toString(),"Timer","Scheduler/Timer on MigServer Instance");
        if(eventBean != null){
            eventBean.getEntityMap().put(lunchEvent.getEventId(),lunchEvent);
        }
    }*/
    @Timeout
    public void execute(Timer timer){
        logger.info("Starting.........................");
        logger.info(String.valueOf(timer.getInfo()));
        /*context.getTimerService().getAllTimers().stream().forEach(timer -> logger.info(String.valueOf(timer.getInfo())));*/
        logger.info("....................End.");
    }
}
