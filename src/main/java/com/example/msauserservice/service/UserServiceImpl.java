package com.example.msauserservice.service;

import com.example.msauserservice.client.OrderServiceClient;
import com.example.msauserservice.model.ResponseOrder;
import com.example.msauserservice.model.UserDto;
import com.example.msauserservice.model.UserEntity;
import com.example.msauserservice.repository.UserRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.core.env.Environment;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder;
    private final Environment env;
    private final RestTemplate restTemplate;
    private final OrderServiceClient feignClient;

    @Override
    public UserDto createUser(UserDto userDto) {
        userDto.setUserId(UUID.randomUUID().toString());
        userDto.setEncryptedPwd(encoder.encode(userDto.getPwd()));
        UserEntity userEntity = new UserEntity();
        BeanUtils.copyProperties(userDto, userEntity);
        userRepository.save(userEntity);

        UserDto returnUserDto = new UserDto();
        BeanUtils.copyProperties(userEntity, returnUserDto);

        return returnUserDto;
    }

    @Override
    public UserDto getUserByUserId(String userId) {
        UserEntity userEntity = userRepository.findByUserId(userId);
        if (userEntity == null) {
            throw new UsernameNotFoundException("User not found");
        }
        UserDto userDto = new UserDto();
        BeanUtils.copyProperties(userEntity, userDto);
        // using RestTemplate
//        String orderUrl = String.format(Objects.requireNonNull(env.getProperty("order_service.url")), userId);
//        ResponseEntity<List<ResponseOrder>> orderListResponse = restTemplate.exchange(orderUrl, HttpMethod.GET, null,
//                new ParameterizedTypeReference<List<ResponseOrder>>() {
//                });
//        List<ResponseOrder> ordersList = orderListResponse.getBody();

        // using FeignClient with try catch
        // with Feign Exception Handling: 없는 url 로 요청하여 404과 같은 에러들이 발생할 경우를 대비하여 예외처리가 필요하다.
        // 예외처리가 되어있지 못하면, 데이터 결과값 중 일부만 잘못되어도 전체 데이터가 영향을 받아 사용자에게 올바른 데이터조차 전달하지 못하게 된다.
        // 예외가 발생한 구간에만 예외처리를 함으로서 올바른 데이터는 사용자에게 전달될 수 있도록 한다.
//        List<ResponseOrder> ordersList = null;
//        try {
//            ordersList = feignClient.getOrders(userId);
//        } catch (FeignException ex) {
//            log.error(ex.getMessage());
//        }

        // using FeignClient with bean (feignErrorDecoder)
        List<ResponseOrder> ordersList = feignClient.getOrders(userId);
        userDto.setOrders(ordersList);

        return userDto;
    }

    @Override
    public Iterable<UserEntity> getUserByAll() {
        return userRepository.findAll();
    }

    @Override
    public UserDto getUserDetailsByEmail(String email) {
        UserEntity userEntity = userRepository.findByEmail(email);
        if (userEntity == null) throw new UsernameNotFoundException(email);

        UserDto userDto = new UserDto();
        BeanUtils.copyProperties(userEntity, userDto);

        return userDto;

    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        UserEntity userEntity = userRepository.findByEmail(email);
        if (userEntity == null) throw new UsernameNotFoundException(email);

        return new User(userEntity.getEmail(),
                userEntity.getEncryptedPwd(),
                true,
                true,
                true,
                true,
                new ArrayList<>());
    }
}
