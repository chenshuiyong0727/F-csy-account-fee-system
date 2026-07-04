package com.yj.accountfee.weight;

import com.yj.accountfee.common.ApiResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/public/weight")
public class PublicWeightController {
    private final WeightService weightService;

    public PublicWeightController(WeightService weightService) {
        this.weightService = weightService;
    }

    @GetMapping
    public ApiResult<PublicWeightVO> detail(@RequestParam(required = false) Long id) {
        return ApiResult.success(weightService.publicData(id));
    }
}
