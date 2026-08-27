/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.datagovernance.domain;
import org.springframework.stereotype.Component;
import java.util.*;
@Component
public class DomainCatalog {
    private final Map<String, WorkflowAction> actions = new LinkedHashMap<>();
    public DomainCatalog() {
        actions.put("SUBMIT", new WorkflowAction("SUBMIT", "提交治理评审", List.of("草稿"), "待评审", "OPERATOR"));
        actions.put("APPROVE", new WorkflowAction("APPROVE", "批准发布", List.of("待评审"), "已发布", "ADMIN"));
        actions.put("RETIRE", new WorkflowAction("RETIRE", "下线资产", List.of("已发布"), "已下线", "ADMIN"));
    }
    public String systemName() { return "知华科技企业数据治理平台"; }
    public String scene() { return "数据目录、元数据、标准、质量、血缘、分级分类、资产运营与安全审计"; }
    public String initialStatus() { return "草稿"; }
    public String partyLabel() { return "数据资产/责任域"; }
    public String amountLabel() { return "数据价值"; }
    public String quantityLabel() { return "数据对象数"; }
    public String dueLabel() { return "治理期限"; }
    public List<ModuleDefinition> modules() { return List.of(
            new ModuleDefinition("CATALOG", "数据目录", "登记数据集、表、字段与业务术语"),
            new ModuleDefinition("METADATA", "元数据管理", "采集技术、业务与管理元数据"),
            new ModuleDefinition("STANDARD", "数据标准", "管理代码集、指标口径和字段标准"),
            new ModuleDefinition("QUALITY", "数据质量", "配置规则、执行检查并闭环整改"),
            new ModuleDefinition("LINEAGE", "数据血缘", "追踪来源、加工过程与消费关系"),
            new ModuleDefinition("CLASSIFICATION", "分级分类", "识别敏感数据并执行分类分级"),
            new ModuleDefinition("ASSET", "数据资产", "盘点、确权、评价与运营数据资产"),
            new ModuleDefinition("ISSUE", "问题整改", "分派数据问题、复核与关闭"),
            new ModuleDefinition("ACCESS_AUDIT", "访问审计", "记录授权、查询、导出和异常访问")
        ); }
    public Map<String, WorkflowAction> actions() { return Collections.unmodifiableMap(actions); }
    public record ModuleDefinition(String code,String name,String description) {}
    public record WorkflowAction(String code,String label,List<String> from,String to,String requiredRole) {}
}
