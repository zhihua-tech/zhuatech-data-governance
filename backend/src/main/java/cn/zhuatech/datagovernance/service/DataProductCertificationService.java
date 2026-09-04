/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.datagovernance.service;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class DataProductCertificationService {
    public Result assess(Request request) {
        var blockers = new ArrayList<String>();
        var actions = new ArrayList<String>();
        if (request.productId() == null || request.productId().isBlank()) blockers.add("数据产品编号不能为空");
        if (!request.qualityThresholdMet()) blockers.add("数据质量阈值未达标");
        if (!request.piiClassified()) blockers.add("个人信息与敏感数据未分级");
        if (!request.accessPolicyApproved()) blockers.add("数据访问策略未批准");
        if (!request.sourceContractsValid()) blockers.add("上游数据契约无效");
        if (!request.auditReady()) blockers.add("数据产品认证证据不完整");
        if (!request.ownerAssigned()) actions.add("指定数据产品责任人");
        if (!request.glossaryLinked()) actions.add("关联业务术语与口径");
        if (!request.lineageComplete()) actions.add("补齐端到端数据血缘");
        if (!request.retentionDefined()) actions.add("定义数据保留与销毁规则");
        if (!request.stewardApproved()) actions.add("取得数据管理员批准");
        var decision = !blockers.isEmpty() ? Decision.BLOCKED : actions.isEmpty() ? Decision.CERTIFY : Decision.REMEDIATE;
        return new Result(decision, List.copyOf(blockers), List.copyOf(actions));
    }

    public enum Decision { CERTIFY, REMEDIATE, BLOCKED }
    public record Request(String productId, boolean ownerAssigned, boolean glossaryLinked,
                          boolean lineageComplete, boolean qualityThresholdMet, boolean piiClassified,
                          boolean accessPolicyApproved, boolean retentionDefined,
                          boolean sourceContractsValid, boolean stewardApproved, boolean auditReady) {}
    public record Result(Decision decision, List<String> blockers, List<String> actions) {}
}
