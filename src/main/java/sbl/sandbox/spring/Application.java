package sbl.sandbox.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import sbl.sandbox.spring.async.GitHubLookupService;
import sbl.sandbox.spring.async.User;

import java.util.concurrent.Future;

@SpringBootApplication
@EnableScheduling
@EnableAsync
public class Application implements CommandLineRunner {

    @Autowired
    private GitHubLookupService gitHubLookupService;

    @Override
    public void run(String... args) throws Exception {
        testAsync();
    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(Application.class);
    }

    private void testAsync() throws Exception {
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
}
