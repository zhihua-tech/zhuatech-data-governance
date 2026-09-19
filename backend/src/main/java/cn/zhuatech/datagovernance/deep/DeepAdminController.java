/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.datagovernance.deep;
import cn.zhuatech.datagovernance.common.ApiResponse;import org.springframework.web.bind.annotation.*;
/**
 * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
 */
@RestController @RequestMapping("/api/admin/governance") public class DeepAdminController {private final DeepDomainService service;/**
                                                                                                                                    * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
                                                                                                                                    */
public DeepAdminController(DeepDomainService service){this.service=service;}/**
                                                                                                                                                                                                                * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
                                                                                                                                                                                                                */
@PostMapping("/assets/{id}/publish") ApiResponse<?> publish(@PathVariable Long id){return ApiResponse.ok(service.publish(id));}}
