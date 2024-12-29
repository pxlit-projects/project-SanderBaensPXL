package be.pxl.services.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "post-service")
public interface PostClient {

    @PutMapping("api/post/approve/{id}")
    void approvePost(@PathVariable("id") long id, @RequestHeader("X-Role") String role, @RequestHeader("X-Name") String name);

    @GetMapping("api/post/{id}/email")
    String getEmailByPostId(@PathVariable("id") long id);
}
