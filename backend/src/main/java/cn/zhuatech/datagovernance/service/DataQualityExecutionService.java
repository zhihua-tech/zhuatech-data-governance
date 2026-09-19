/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.datagovernance.service;
import jakarta.validation.Valid;import jakarta.validation.constraints.*;import org.springframework.stereotype.Service;import java.math.*;import java.util.*;
/**
 * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
 */
@Service public class DataQualityExecutionService{
 /**
  * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
  */
 public ExecutionResult execute(@Valid ExecutionRequest request){Set<String>codes=new HashSet<>();List<RuleResult>results=new ArrayList<>();int blocking=0;BigDecimal weighted=BigDecimal.ZERO,totalWeight=BigDecimal.ZERO;
  for(RuleExecution rule:request.rules()){if(!codes.add(rule.ruleCode()))throw new IllegalArgumentException("质量规则编码不能重复: "+rule.ruleCode());if(rule.passedRows()>request.rowCount())throw new IllegalArgumentException("规则通过行数不能超过数据集总行数: "+rule.ruleCode());BigDecimal rate=request.rowCount()==0?new BigDecimal("100"):BigDecimal.valueOf(rule.passedRows()).multiply(new BigDecimal("100")).divide(BigDecimal.valueOf(request.rowCount()),2,RoundingMode.HALF_UP);String status=rate.compareTo(rule.minimumPassRate())>=0?"PASS":"FAIL";boolean blocked="FAIL".equals(status)&&"BLOCKER".equals(rule.severity());if(blocked)blocking++;BigDecimal weight=switch(rule.severity()){case "BLOCKER"->new BigDecimal("3");case "MAJOR"->new BigDecimal("2");default->BigDecimal.ONE;};weighted=weighted.add(rate.multiply(weight));totalWeight=totalWeight.add(weight);results.add(new RuleResult(rule.ruleCode(),rule.severity(),rate,status,request.rowCount()-rule.passedRows(),blocked));}
  BigDecimal score=weighted.divide(totalWeight,2,RoundingMode.HALF_UP);String decision=blocking>0?"BLOCK_RELEASE":results.stream().anyMatch(r->"FAIL".equals(r.status()))?"REMEDIATE":"CERTIFY";return new ExecutionResult(request.datasetCode(),decision,score,blocking,results);
 }
 /**
  * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
  */
 public record ExecutionRequest(@NotBlank String datasetCode,@PositiveOrZero long rowCount,@NotEmpty List<@Valid RuleExecution>rules){}
 /**
  * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
  */
 public record RuleExecution(@NotBlank String ruleCode,@Pattern(regexp="BLOCKER|MAJOR|MINOR") String severity,@PositiveOrZero long passedRows,@NotNull @DecimalMin("0") @DecimalMax("100") BigDecimal minimumPassRate){}
 /**
  * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
  */
 public record RuleResult(String ruleCode,String severity,BigDecimal passRate,String status,long failedRows,boolean releaseBlocked){}
 /**
  * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
  */
 public record ExecutionResult(String datasetCode,String decision,BigDecimal qualityScore,int blockingFailures,List<RuleResult>rules){}
}
