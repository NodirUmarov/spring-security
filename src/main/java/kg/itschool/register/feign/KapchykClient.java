package kg.itschool.register.feign;

import kg.itschool.register.model.request.CreateClientUserRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "kapchyk", url = "http://localhost:8082")
public interface KapchykClient {

    @PostMapping(value = "/api/v1/wallet-holder/create",
            consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> create(@RequestBody CreateClientUserRequest request);

}
