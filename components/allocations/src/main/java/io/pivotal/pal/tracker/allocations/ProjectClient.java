package io.pivotal.pal.tracker.allocations;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.web.client.RestOperations;

import java.util.concurrent.ConcurrentHashMap;

public class ProjectClient {

    private final RestOperations restOperations;
    private final String registrationServerEndpoint;
    private final ConcurrentHashMap<Long,ProjectInfo> internalCache=new ConcurrentHashMap<>();

    public ProjectClient(RestOperations restOperations, String registrationServerEndpoint) {
        this.restOperations= restOperations;
        this.registrationServerEndpoint = registrationServerEndpoint;
    }
    @CircuitBreaker(name = "project", fallbackMethod = "getProjectFromCache")
    public ProjectInfo getProject(long projectId) {
        System.out.println("getProject"+" "+projectId);
        ProjectInfo projectInfo=restOperations.getForObject(registrationServerEndpoint + "/projects/" + projectId, ProjectInfo.class);
        internalCache.put(projectId,projectInfo);
        return projectInfo;
    }
    public ProjectInfo getProjectFromCache(long projectId,Throwable cause){
        System.out.println("getProjectFromCache"+" "+projectId);

        return internalCache.get(projectId);
    }


}
