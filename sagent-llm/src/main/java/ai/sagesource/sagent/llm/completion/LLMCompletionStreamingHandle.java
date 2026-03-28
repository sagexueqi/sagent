package ai.sagesource.sagent.llm.completion;

import lombok.Getter;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 流式输出的控制句柄，调用方持有它来控制流的生命周期
 *
 * @author: sage.xue
 * @time: 2026/3/28
 */
@Getter
public class LLMCompletionStreamingHandle implements AutoCloseable {

    @FunctionalInterface
    public interface StreamCloser {
        void close() throws Exception;
    }

    private final    AtomicBoolean  cancelled       = new AtomicBoolean(false);
    private volatile StreamCloser   streamCloser;
    private final    CountDownLatch completionLatch = new CountDownLatch(1);

    public void cancel() {
        if (cancelled.compareAndSet(false, true)) {
            closeQuietly();
        }
    }

    public boolean isCancelled() {
        return cancelled.get();
    }

    /**
     * 由各 LLM 实现层调用，注入具体的流关闭逻辑
     */
    public void bindCloser(StreamCloser closer) {
        this.streamCloser = closer;
        if (cancelled.get()) {
            closeQuietly();
        }
    }

    public void markComplete() {
        completionLatch.countDown();
    }

    public boolean awaitCompletion(long timeout, TimeUnit unit) throws InterruptedException {
        return completionLatch.await(timeout, unit);
    }

    private void closeQuietly() {
        try {
            StreamCloser c = this.streamCloser;
            if (c != null) c.close();
        } catch (Exception ignored) {
        }
    }

    @Override
    public void close() {
        cancel();
    }
}
