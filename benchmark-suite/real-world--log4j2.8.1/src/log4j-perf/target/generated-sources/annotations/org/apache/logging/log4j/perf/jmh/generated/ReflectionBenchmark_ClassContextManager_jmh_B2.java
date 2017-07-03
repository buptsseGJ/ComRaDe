package org.apache.logging.log4j.perf.jmh.generated;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
public class ReflectionBenchmark_ClassContextManager_jmh_B2 extends ReflectionBenchmark_ClassContextManager_jmh_B1 {
    public volatile int setupTrialMutex;
    public volatile int tearTrialMutex;
    public final static AtomicIntegerFieldUpdater setupTrialMutexUpdater = AtomicIntegerFieldUpdater.newUpdater(ReflectionBenchmark_ClassContextManager_jmh_B2.class, "setupTrialMutex");
    public final static AtomicIntegerFieldUpdater tearTrialMutexUpdater = AtomicIntegerFieldUpdater.newUpdater(ReflectionBenchmark_ClassContextManager_jmh_B2.class, "tearTrialMutex");

    public volatile int setupIterationMutex;
    public volatile int tearIterationMutex;
    public final static AtomicIntegerFieldUpdater setupIterationMutexUpdater = AtomicIntegerFieldUpdater.newUpdater(ReflectionBenchmark_ClassContextManager_jmh_B2.class, "setupIterationMutex");
    public final static AtomicIntegerFieldUpdater tearIterationMutexUpdater = AtomicIntegerFieldUpdater.newUpdater(ReflectionBenchmark_ClassContextManager_jmh_B2.class, "tearIterationMutex");

    public volatile int setupInvocationMutex;
    public volatile int tearInvocationMutex;
    public final static AtomicIntegerFieldUpdater setupInvocationMutexUpdater = AtomicIntegerFieldUpdater.newUpdater(ReflectionBenchmark_ClassContextManager_jmh_B2.class, "setupInvocationMutex");
    public final static AtomicIntegerFieldUpdater tearInvocationMutexUpdater = AtomicIntegerFieldUpdater.newUpdater(ReflectionBenchmark_ClassContextManager_jmh_B2.class, "tearInvocationMutex");

    public volatile boolean readyTrial;
    public volatile boolean readyIteration;
    public volatile boolean readyInvocation;
}
