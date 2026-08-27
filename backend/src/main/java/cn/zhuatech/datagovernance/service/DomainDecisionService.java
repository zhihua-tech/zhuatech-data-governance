/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.datagovernance.service;
import jakarta.validation.constraints.*;
import org.springframework.stereotype.Service;
import java.util.*;
@Service public class DomainDecisionService {
 public DecisionResult assess(DecisionRequest request) { int score=(int)Math.round((request.completenessRate()+request.accuracyRate()+request.lineageCoverage())/3); List<String> actions=new ArrayList<>(); if(!request.hasOwner()){score-=20;actions.add("指定数据责任人");} if(!request.hasClassification()){score-=20;actions.add("完成数据分级分类");} if(request.issueCount()>0){score-=Math.min(20,request.issueCount()*3);actions.add("关闭未解决数据质量问题");} return result(score,actions,"READY","REMEDIATE","BLOCKED",Map.of("completenessRate",request.completenessRate(),"accuracyRate",request.accuracyRate(),"lineageCoverage",request.lineageCoverage(),"openIssues",request.issueCount())); }
 private DecisionResult result(int raw,List<String> actions,String good,String warn,String bad,Map<String,Object> metrics) { int score=Math.max(0,Math.min(100,raw));String decision=score>=80?good:score>=50?warn:bad;return new DecisionResult(decision,score,metrics,List.copyOf(actions)); }
 private DecisionResult riskResult(int raw,List<String> actions,String good,String warn,String bad,Map<String,Object> metrics) { int score=Math.max(0,Math.min(100,raw));String decision=score>=70?bad:score>=40?warn:good;return new DecisionResult(decision,score,metrics,List.copyOf(actions)); }
 public record DecisionRequest(
        @NotBlank String datasetCode,
        @DecimalMin("0") @DecimalMax("100") double completenessRate,
        @DecimalMin("0") @DecimalMax("100") double accuracyRate,
        @DecimalMin("0") @DecimalMax("100") double lineageCoverage,
        @PositiveOrZero int issueCount,
        boolean hasOwner,
        boolean hasClassification) {}
 public record DecisionResult(String decision,int score,Map<String,Object> metrics,List<String> actions) {}
}
