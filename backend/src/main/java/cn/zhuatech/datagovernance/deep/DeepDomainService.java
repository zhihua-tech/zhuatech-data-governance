/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.datagovernance.deep;

import cn.zhuatech.datagovernance.model.AuditLog;
import cn.zhuatech.datagovernance.repository.AuditLogRepository;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.time.*;
import java.util.*;

@Service @Transactional(readOnly=true)
public class DeepDomainService {
 private final EntityManager em; private final AuditLogRepository audits;
 public DeepDomainService(EntityManager em,AuditLogRepository audits){this.em=em;this.audits=audits;}
 public GovernanceDashboard dashboard(String org){requireOrg(org);return new GovernanceDashboard(assets(org),issues(org),runs(org),lineage(org));}
 public List<AssetView> assets(String org){return em.createQuery("select a from DataAsset a where a.organizationCode=:org order by a.updatedAt desc",DataAsset.class).setParameter("org",org).getResultList().stream().map(this::view).toList();}
 public List<IssueView> issues(String org){return em.createQuery("select i from GovernanceIssue i where i.assetId in (select a.id from DataAsset a where a.organizationCode=:org) order by i.createdAt desc",GovernanceIssue.class).setParameter("org",org).getResultList().stream().map(this::view).toList();}
 public List<RunView> runs(String org){return em.createQuery("select r from QualityRun r where r.assetId in (select a.id from DataAsset a where a.organizationCode=:org) order by r.executedAt desc",QualityRun.class).setParameter("org",org).setMaxResults(50).getResultList().stream().map(r->new RunView(r.id,r.assetId,r.runNo,r.totalRows,r.failedRows,r.score,r.result,r.executedAt)).toList();}
 public List<LineageView> lineage(String org){return em.createQuery("select e from LineageEdge e where e.organizationCode=:org order by e.createdAt desc",LineageEdge.class).setParameter("org",org).getResultList().stream().map(e->new LineageView(e.id,e.sourceCode,e.targetCode,e.transformation,e.validated)).toList();}
 @Transactional public AssetView createAsset(AssetRequest r){if(assetByCode(r.organizationCode(),r.assetCode())!=null)throw conflict("数据资产编号已存在");DataAsset a=new DataAsset(r.organizationCode(),r.assetCode(),r.name(),r.dataDomain(),r.owner(),r.classification());em.persist(a);audit("DATA_ASSET","创建资产",a.assetCode,a.name);return view(a);}
 @Transactional public AssetView submit(Long id){DataAsset a=asset(id);state(a,"DRAFT");a.status="IN_REVIEW";audit("DATA_ASSET","提交评审",a.assetCode,a.owner);return view(a);}
 @Transactional public AssetView publish(Long id){DataAsset a=asset(id);state(a,"IN_REVIEW");long rules=em.createQuery("select count(r) from QualityRule r where r.assetId=:id and r.active=true",Long.class).setParameter("id",id).getSingleResult();if(rules==0)throw conflict("至少配置一条有效质量规则后才能发布");a.status="PUBLISHED";audit("DATA_ASSET","批准发布",a.assetCode,"qualityRules="+rules);return view(a);}
 @Transactional public RuleView addRule(Long assetId,RuleRequest r){asset(assetId);QualityRule rule=new QualityRule(assetId,r.ruleCode(),r.ruleType(),r.expression(),r.threshold());em.persist(rule);audit("DATA_QUALITY","新增规则",r.ruleCode(),r.expression());return new RuleView(rule.id,rule.assetId,rule.ruleCode,rule.ruleType,rule.expressionText,rule.thresholdValue,rule.active);}
 @Transactional public QualityResult execute(Long assetId,QualityRunRequest r){DataAsset a=asset(assetId);if(!"PUBLISHED".equals(a.status))throw conflict("只有已发布数据资产可以执行质量任务");if(r.failedRows()>r.totalRows())throw bad("失败行数不能超过总行数");double score=r.totalRows()==0?100:Math.round((r.totalRows()-r.failedRows())*10000d/r.totalRows())/100d;double threshold=em.createQuery("select coalesce(max(q.thresholdValue),0) from QualityRule q where q.assetId=:id and q.active=true",Double.class).setParameter("id",assetId).getSingleResult();String result=score>=threshold?"PASSED":"FAILED";QualityRun run=new QualityRun(assetId,r.runNo(),r.totalRows(),r.failedRows(),score,result);em.persist(run);a.qualityScore=score;if("FAILED".equals(result)){GovernanceIssue issue=new GovernanceIssue(assetId,"DQI-"+r.runNo(),score<threshold-20?"HIGH":"MEDIUM","质量得分 "+score+" 低于阈值 "+threshold,LocalDate.now().plusDays(7));em.persist(issue);}audit("DATA_QUALITY","执行质量任务",r.runNo(),result+" score="+score);return new QualityResult(result,score,threshold,r.failedRows(),"FAILED".equals(result));}
 @Transactional public LineageView addLineage(LineageRequest r){DataAsset source=assetByCode(r.organizationCode(),r.sourceCode()),target=assetByCode(r.organizationCode(),r.targetCode());if(source==null||target==null)throw bad("来源和目标数据资产必须存在于同一组织");if(source.id.equals(target.id))throw bad("血缘来源与目标不能相同");LineageEdge edge=new LineageEdge(r.organizationCode(),r.sourceCode(),r.targetCode(),r.transformation());em.persist(edge);long incoming=em.createQuery("select count(e) from LineageEdge e where e.organizationCode=:org and e.targetCode=:code",Long.class).setParameter("org",r.organizationCode()).setParameter("code",r.targetCode()).getSingleResult();target.lineageCoverage=Math.min(100,incoming*25d);audit("DATA_LINEAGE","登记血缘",r.targetCode(),r.sourceCode()+" -> "+r.targetCode());return new LineageView(edge.id,edge.sourceCode,edge.targetCode,edge.transformation,edge.validated);}
 @Transactional public IssueView assignIssue(Long id,IssueActionRequest r){GovernanceIssue i=issue(id);if(!"OPEN".equals(i.status))throw conflict("只有待处理问题可以分派");i.assignee=r.assignee();i.status="IN_PROGRESS";audit("DATA_ISSUE","分派问题",i.issueNo,r.assignee());return view(i);}
 @Transactional public IssueView resolveIssue(Long id,IssueActionRequest r){GovernanceIssue i=issue(id);if(!"IN_PROGRESS".equals(i.status))throw conflict("问题必须先分派再解决");if(r.resolution()==null||r.resolution().isBlank())throw bad("解决说明不能为空");i.resolution=r.resolution();i.status="RESOLVED";i.resolvedAt=LocalDateTime.now();audit("DATA_ISSUE","解决问题",i.issueNo,r.resolution());return view(i);}
 @Transactional public IssueView closeIssue(Long id){GovernanceIssue i=issue(id);if(!"RESOLVED".equals(i.status))throw conflict("只有已解决问题可以关闭");i.status="CLOSED";audit("DATA_ISSUE","复核关闭",i.issueNo,i.resolution);return view(i);}
 private DataAsset asset(Long id){DataAsset a=em.find(DataAsset.class,id);if(a==null)throw notFound("数据资产不存在");return a;} private GovernanceIssue issue(Long id){GovernanceIssue i=em.find(GovernanceIssue.class,id);if(i==null)throw notFound("治理问题不存在");return i;}
 private DataAsset assetByCode(String org,String code){return em.createQuery("select a from DataAsset a where a.organizationCode=:org and a.assetCode=:code",DataAsset.class).setParameter("org",org).setParameter("code",code).getResultStream().findFirst().orElse(null);}
 private void state(DataAsset a,String expected){if(!expected.equals(a.status))throw conflict("当前资产状态不能执行该操作");}private void requireOrg(String org){if(org==null||org.isBlank())throw bad("organizationCode不能为空");}
 private AssetView view(DataAsset a){return new AssetView(a.id,a.organizationCode,a.assetCode,a.name,a.dataDomain,a.owner,a.classification,a.status,a.qualityScore,a.lineageCoverage,a.version,a.updatedAt);}
 private IssueView view(GovernanceIssue i){return new IssueView(i.id,i.assetId,i.issueNo,i.severity,i.description,i.assignee,i.status,i.dueDate,i.resolution);}
 private void audit(String module,String action,String no,String detail){audits.save(new AuditLog(module,action,no,"domain-service",detail));}
 private ResponseStatusException bad(String m){return new ResponseStatusException(HttpStatus.BAD_REQUEST,m);}private ResponseStatusException conflict(String m){return new ResponseStatusException(HttpStatus.CONFLICT,m);}private ResponseStatusException notFound(String m){return new ResponseStatusException(HttpStatus.NOT_FOUND,m);}
 public record AssetRequest(@NotBlank String organizationCode,@NotBlank String assetCode,@NotBlank String name,@NotBlank String dataDomain,@NotBlank String owner,@NotBlank String classification){}
 public record RuleRequest(@NotBlank String ruleCode,@NotBlank String ruleType,@NotBlank String expression,@DecimalMin("0") @DecimalMax("100") double threshold){}
 public record QualityRunRequest(@NotBlank String runNo,@PositiveOrZero long totalRows,@PositiveOrZero long failedRows){}
 public record LineageRequest(@NotBlank String organizationCode,@NotBlank String sourceCode,@NotBlank String targetCode,@NotBlank String transformation){}
 public record IssueActionRequest(String assignee,String resolution){}
 public record AssetView(Long id,String organizationCode,String assetCode,String name,String dataDomain,String owner,String classification,String status,double qualityScore,double lineageCoverage,long version,LocalDateTime updatedAt){}
 public record RuleView(Long id,Long assetId,String ruleCode,String ruleType,String expression,double threshold,boolean active){}
 public record RunView(Long id,Long assetId,String runNo,long totalRows,long failedRows,double score,String result,LocalDateTime executedAt){}
 public record IssueView(Long id,Long assetId,String issueNo,String severity,String description,String assignee,String status,LocalDate dueDate,String resolution){}
 public record LineageView(Long id,String sourceCode,String targetCode,String transformation,boolean validated){}
 public record QualityResult(String result,double score,double threshold,long failedRows,boolean issueCreated){}
 public record GovernanceDashboard(List<AssetView> assets,List<IssueView> issues,List<RunView> runs,List<LineageView> lineage){}
}
