package sbl.sandbox.spring.async;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.Future;

@Service
public class GitHubLookupService {

    private RestTemplate restTemplate = new RestTemplate();

    @Async
    public Future<User> findUserAsync(String user) throws InterruptedException {
        System.out.println("Looking up " + user);
        User result = restTemplate.getForObject("https://api.github.com/users/" + user, User.class);

        // for demonstration purposes
        Thread.sleep(1000L);
        return new AsyncResult<User>(result);
    }
}
