package bus;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TypeSafeEventBusTest {
    @Test
    public void testSmoke() {
        TypeSafeEventBus<String> eventBus = new TypeSafeEventBus<>();
        Consumer consumer = new Consumer();
        eventBus.registerConsumer(consumer);
        String message = "tada";
        eventBus.postEvent(message);
        assertEquals(message, consumer.getSample());
    }

    public class Consumer implements Subscriber<String> {
        private String sample;

        public String getSample() {
            return sample;
        }

        @Override
        public void invoke(String... events) {
            this.sample = events[0];
        }
    }
}
