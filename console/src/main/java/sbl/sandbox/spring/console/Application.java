package sbl.sandbox.spring.console;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import reactor.Environment;
import reactor.bus.EventBus;
import sbl.sandbox.spring.console.async.GitHubLookupService;
import sbl.sandbox.spring.console.async.User;
import sbl.sandbox.spring.console.reactor.Publisher;
import sbl.sandbox.spring.console.reactor.Receiver;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;

import static reactor.bus.selector.Selectors.$;

@SpringBootApplication
@EnableScheduling
@EnableAsync
public class Application implements CommandLineRunner {

    @Override
    public void run(String... args) throws Exception {
        testAsync();
        testReactor();
    }

    public static void main(String... args) throws Exception {
        ApplicationContext app = SpringApplication.run(Application.class, args);

        app.getBean(Environment.class).shutdown();
    }

    // ===== Async sandbox =====
    @Autowired
    private GitHubLookupService gitHubLookupService;

    private void testAsync() throws Exception {
        System.out.println("==================== Async test ====================");

        // Start the clock
        long start = System.currentTimeMillis();

        // Kick of multiple asynchronous lookups
        Future<User> page1 = gitHubLookupService.findUserAsync("artavd");
        Future<User> page2 = gitHubLookupService.findUserAsync("dotnet");
        Future<User> page3 = gitHubLookupService.findUserAsync("docker");

        while(!(page1.isDone() && page2.isDone() && page3.isDone())) {
            Thread.sleep(10);
        }

        System.out.println("Elapsed time: " + (System.currentTimeMillis() - start));
        System.out.println(page1.get());
        System.out.println(page2.get());
        System.out.println(page3.get());
    }

    // ===== Reactor sandbox =====
    private static final int NUMBER_OF_QUOTES = 10;

    @Bean
    Environment env() {
        return Environment.initializeIfEmpty().assignErrorJournal();
    }

    @Bean
    EventBus createEventBus(Environment env) {
        return EventBus.create(env, Environment.THREAD_POOL);
    }

    @Bean(name = "quotesLatch")
    CountDownLatch quotesLatch() {
        return new CountDownLatch(NUMBER_OF_QUOTES);
    }

    @Autowired
    private EventBus eventBus;

    @Autowired
    private Publisher publisher;

    @Autowired
    private Receiver receiver;

    private void testReactor() throws Exception {
        System.out.println("==================== Reactor test ====================");

        eventBus.on($("quotes"), receiver);
        publisher.publishQuotes(NUMBER_OF_QUOTES);
    }
}
