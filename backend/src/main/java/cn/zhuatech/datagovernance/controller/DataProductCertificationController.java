/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.datagovernance.controller;

import cn.zhuatech.datagovernance.common.ApiResponse;
import cn.zhuatech.datagovernance.service.DataProductCertificationService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
 */
@RestController
@RequestMapping("/api/enterprise/data-governance")
public class DataProductCertificationController {
    private final DataProductCertificationService service;
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public DataProductCertificationController(DataProductCertificationService service) { this.service = service; }

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @PostMapping("/data-product-certification")
    public ApiResponse<?> assess(@RequestBody DataProductCertificationService.Request request) {
        return ApiResponse.ok(service.assess(request));
    }
}
