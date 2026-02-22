package com.example.employee.client;

import com.example.employee.model.request.FingerEnrollRequest;
import com.example.employee.model.response.FingerEnrollResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name="device", url="${device.service.url}")
//@FeignClient(name="device", url="http://${device.service.url}")
public interface DeviceFingerClient {

    @PostMapping("/v1/devices/fingers/enroll")
    FingerEnrollResponse enrollFinger(@RequestBody FingerEnrollRequest request);

}
