/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.datagovernance.controller;

import cn.zhuatech.datagovernance.common.ApiResponse;
import cn.zhuatech.datagovernance.service.DataProductCertificationService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/enterprise/data-governance")
public class DataProductCertificationController {
    private final DataProductCertificationService service;
    public DataProductCertificationController(DataProductCertificationService service) { this.service = service; }

    @PostMapping("/data-product-certification")
    public ApiResponse<?> assess(@RequestBody DataProductCertificationService.Request request) {
        return ApiResponse.ok(service.assess(request));
    }
}
