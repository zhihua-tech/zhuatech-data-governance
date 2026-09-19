/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.datagovernance.controller;import cn.zhuatech.datagovernance.common.ApiResponse;import cn.zhuatech.datagovernance.service.DataQualityExecutionService;import jakarta.validation.Valid;import org.springframework.web.bind.annotation.*;
/**
 * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
 */
@RestController @RequestMapping("/api/advanced/data-governance") public class DataQualityExecutionController{private final DataQualityExecutionService service;/**
                                                                                                                                                                * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
                                                                                                                                                                */
public DataQualityExecutionController(DataQualityExecutionService service){this.service=service;}/**
                                                                                                                                                                                                                                                                 * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
                                                                                                                                                                                                                                                                 */
@PostMapping("/quality-run") public ApiResponse<DataQualityExecutionService.ExecutionResult> execute(@Valid @RequestBody DataQualityExecutionService.ExecutionRequest request){return ApiResponse.ok(service.execute(request));}}
