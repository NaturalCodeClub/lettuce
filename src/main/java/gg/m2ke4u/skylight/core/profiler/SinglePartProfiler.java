package gg.m2ke4u.skylight.core.profiler;

import gg.m2ke4u.skylight.core.TickTask;

public class SinglePartProfiler {
    private long avgTime;
    private int finishedTimes;
    private long currentStartedTime;
    private long allOfTime;
    private final String profilerName;

    public SinglePartProfiler(String profilerName){
        this.profilerName = profilerName;
    }

    public synchronized void postSection(){
        this.currentStartedTime = System.currentTimeMillis();
    }

    public synchronized void finishedTask(){
        this.finishedTimes++;
        this.allOfTime+=System.currentTimeMillis() - this.currentStartedTime;
        if (this.finishedTimes>3){
            this.avgTime = this.allOfTime / this.finishedTimes;
        }
    }

    public synchronized long getAvgTime(){
        return this.avgTime;
    }

    public String getProfilerName(){
        return this.profilerName;
    }
}
