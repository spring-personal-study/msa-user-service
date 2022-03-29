package com.example.msauserservice.client;

import com.example.msauserservice.model.ResponseOrder;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "order-service") // 호출하려는 서비스의 이름
public interface OrderServiceClient {

    @GetMapping("/order-service/{userId}/orders") // 예외처리가 되어있으므로 해당 url이 잘못되어도 다른 데이터들에는 영향을 주지않게 된다.
    List<ResponseOrder> getOrders(@PathVariable("userId") String userId);

}
