package bus;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class AnnotationEventBusTest {
    @Test
    public void testBasic() {
        AnnotationEventBus annotationEventBus = new AnnotationEventBus();
        Consumer consumer = new Consumer();
        annotationEventBus.registerConsumer(consumer);
        String message = "tada";
        annotationEventBus.postEvent(message);
        assertEquals(message, consumer.getSample());
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
}
