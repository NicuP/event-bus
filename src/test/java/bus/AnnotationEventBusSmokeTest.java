package bus;

import org.junit.Test;

import java.util.concurrent.Semaphore;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class AnnotationEventBusSmokeTest {
    public static final String SAMPLE_MESSAGE = "tada";
    private EventBus<Object, Object> annotationEventBus = new AnnotationEventBus();

    @Test
    public void testSameThread() {
        Consumer consumer = new Consumer();
        annotationEventBus.registerConsumer(consumer);
        annotationEventBus.postEvent(SAMPLE_MESSAGE);
        assertEquals(SAMPLE_MESSAGE, consumer.getSample());
    }

    public class Consumer {
        private String sample;
        @Consume
        public void consume(String s) {
            sample = s;
        }

        public String getSample() {
            return sample;
        }
    }

    @Test
    public void testNewThread() {
        NewThreadConsumer newThreadConsumer = new NewThreadConsumer();
        annotationEventBus.registerConsumer(newThreadConsumer);
        annotationEventBus.postEvent(SAMPLE_MESSAGE);
        assertEquals(SAMPLE_MESSAGE, newThreadConsumer.getSample());
        assertFalse(Thread.currentThread().getName()
                .equals(newThreadConsumer.getNewThreadName()));
    }

    public class NewThreadConsumer {
        private volatile String sample;
        private String newThreadName;
        private Semaphore semaphore = new Semaphore(0);

        @Consume(threadType = ThreadType.NEW_THREAD)
        public void consume(String s) {
            sample = s;
            semaphore.release();
            newThreadName = Thread.currentThread().getName();
        }

        String getSample() {
            try {
                semaphore.acquire();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            return sample;
        }

        String getNewThreadName() {
            return newThreadName;
        }
    }

    @Test
    public void testPooledThread() {
        PooledThreadConsumer newThreadConsumer = new PooledThreadConsumer();
        annotationEventBus.registerConsumer(newThreadConsumer);
        annotationEventBus.postEvent(SAMPLE_MESSAGE);
        assertEquals(SAMPLE_MESSAGE, newThreadConsumer.getSample());
        assertFalse(Thread.currentThread().getName()
                .equals(newThreadConsumer.getNewThreadName()));
    }

    public class PooledThreadConsumer {
        private volatile String sample;
        private String newThreadName;
        private final Semaphore semaphore = new Semaphore(0);

        @Consume(threadType = ThreadType.POOLED_THREAD)
        public void consume(String s) {
            sample = s;
            newThreadName = Thread.currentThread().getName();
            semaphore.release();
        }

        String getSample() {
            try {
                semaphore.acquire();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            return sample;
        }

        String getNewThreadName() {
            return newThreadName;
        }
    }

    @Test
    public void testAvoidStackOverflow() {
        StackOverflowConsumer consumer = new StackOverflowConsumer();
        annotationEventBus.registerConsumer(consumer);
        annotationEventBus.postEvent(SAMPLE_MESSAGE);
        assertEquals(SAMPLE_MESSAGE, consumer.getSample());
    }

    public class StackOverflowConsumer {
        private String sample;

        @Consume(rePost = true)
        public String consume(String s) {
            sample = s;
            return s;
        }

        public String getSample() {
            return sample;
        }
    }
}
