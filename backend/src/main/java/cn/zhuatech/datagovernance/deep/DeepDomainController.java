/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.datagovernance.deep;
import cn.zhuatech.datagovernance.common.ApiResponse;import jakarta.validation.Valid;import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/governance") public class DeepDomainController {
 private final DeepDomainService service;public DeepDomainController(DeepDomainService service){this.service=service;}
 @GetMapping("/dashboard") ApiResponse<?> dashboard(@RequestParam String organizationCode){return ApiResponse.ok(service.dashboard(organizationCode));}
 @PostMapping("/assets") ApiResponse<?> create(@Valid @RequestBody DeepDomainService.AssetRequest r){return ApiResponse.ok(service.createAsset(r));}
 @PostMapping("/assets/{id}/submit") ApiResponse<?> submit(@PathVariable Long id){return ApiResponse.ok(service.submit(id));}
 @PostMapping("/assets/{id}/rules") ApiResponse<?> rule(@PathVariable Long id,@Valid @RequestBody DeepDomainService.RuleRequest r){return ApiResponse.ok(service.addRule(id,r));}
 @PostMapping("/assets/{id}/quality-runs") ApiResponse<?> run(@PathVariable Long id,@Valid @RequestBody DeepDomainService.QualityRunRequest r){return ApiResponse.ok(service.execute(id,r));}
 @PostMapping("/lineage") ApiResponse<?> lineage(@Valid @RequestBody DeepDomainService.LineageRequest r){return ApiResponse.ok(service.addLineage(r));}
 @PostMapping("/issues/{id}/assign") ApiResponse<?> assign(@PathVariable Long id,@RequestBody DeepDomainService.IssueActionRequest r){return ApiResponse.ok(service.assignIssue(id,r));}
 @PostMapping("/issues/{id}/resolve") ApiResponse<?> resolve(@PathVariable Long id,@RequestBody DeepDomainService.IssueActionRequest r){return ApiResponse.ok(service.resolveIssue(id,r));}
 @PostMapping("/issues/{id}/close") ApiResponse<?> close(@PathVariable Long id){return ApiResponse.ok(service.closeIssue(id));}
}
