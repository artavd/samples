package sbl.sandbox.spring.console.reactor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import reactor.bus.Event;
import reactor.fn.Consumer;

import java.util.concurrent.CountDownLatch;

@Service
public class Receiver implements Consumer<Event<Integer>> {

    @Autowired
    @Qualifier("quotesLatch")
    private CountDownLatch latch;
    private RestTemplate restTemplate = new RestTemplate();

    @Override
    public void accept(Event<Integer> event) {
        QuoteResource resource = restTemplate.getForObject(
                "http://gturnquist-quoters.cfapps.io/api/random",
                QuoteResource.class);

        System.out.println("Quote " + event.getData() + ": " + resource.getValue().getQuote());
        latch.countDown();
    }
}
