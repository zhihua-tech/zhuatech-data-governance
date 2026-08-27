/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.datagovernance.deep;
import cn.zhuatech.datagovernance.common.ApiResponse;import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/admin/governance") public class DeepAdminController {private final DeepDomainService service;public DeepAdminController(DeepDomainService service){this.service=service;}@PostMapping("/assets/{id}/publish") ApiResponse<?> publish(@PathVariable Long id){return ApiResponse.ok(service.publish(id));}}
