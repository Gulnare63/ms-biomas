package com.example.employee.controller;

import com.example.employee.model.request.FingerUpsertRequest;
import com.example.employee.model.request.RegisterFingerRequest;
import com.example.employee.model.response.FingerInfoResponse;
import com.example.employee.service.abstraction.FingerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/fingers")
@RequiredArgsConstructor
public class FingerController {

    private final FingerService fingerService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.OK)
    public void registerFinger(@RequestBody RegisterFingerRequest request) {
        fingerService.registerFinger(request);
    }

    @GetMapping("/employee/{employeeId}")
    public List<FingerInfoResponse> getByEmployee(@PathVariable Long employeeId) {
        return fingerService.getFingers(employeeId);
    }


    // CRUD: birbaşa template göndərib DB-yə yazmaq (optional)
    @PutMapping("/upsert")
    @ResponseStatus(HttpStatus.OK)
    public void upsert( @RequestBody FingerUpsertRequest request) {
        fingerService.upsertFinger(request);
    }

//    // list by employee
//    @GetMapping
//    public List<FingerInfoResponse> list(@RequestParam Long employeeId) {
//        return fingerService.getFingers(employeeId);
//    }

    // get by id
    @GetMapping("/{fingerId}")
    public FingerInfoResponse get(@PathVariable Long fingerId) {
        return fingerService.getFinger(fingerId);
    }

    // delete by id
    @DeleteMapping("/{fingerId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long fingerId) {
        fingerService.deleteFinger(fingerId);
    }

    // delete by employee + index + hand
    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteByKey(@RequestParam Long employeeId,
                            @RequestParam Integer fingerIndex,
                            @RequestParam String hand) {
        fingerService.deleteFinger(employeeId, fingerIndex, hand);
    }
}
