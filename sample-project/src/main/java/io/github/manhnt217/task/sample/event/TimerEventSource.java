package io.github.manhnt217.task.sample.event;

import io.github.manhnt217.task.core.container.EventSource;

import java.time.Instant;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author manh nguyen
 */
public class TimerEventSource extends EventSource<Long, Long, String> {

    private long interval;
    private ScheduledExecutorService executor;

    @Override
    protected Class<? extends Long> getPropsType() {
        return Long.class;
    }

    @Override
    public Class<? extends Long> getDispatcherEventType() {
        return Long.class;
    }

    @Override
    public Class<? extends String> getDispatcherReturnType() {
        return String.class;
    }

    @Override
    protected void startInternal(Long interval /* millisecond */) throws Exception {
        if (interval == null || interval <= 0) {
            this.interval = 1000L; // default
        } else {
            this.interval = interval;
        }
        this.executor = Executors.newScheduledThreadPool(4);
        executor.scheduleAtFixedRate(this::fireEvent, 0, this.interval, TimeUnit.MILLISECONDS);
    }

    @Override
    protected void shutdownInternal() throws Exception {
        this.executor.shutdownNow();
    }

    private void fireEvent() {
        try {
            String result = dispatch(Instant.now().getEpochSecond());
            if ("EXIT".equals(result)) {
                throw new RuntimeException("Terminate the source");
            }
        } catch (Exception e) {
            try {
                shutdownSelf(true);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
    }
}
