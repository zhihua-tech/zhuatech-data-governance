/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.datagovernance.deep;

import jakarta.persistence.*;
import java.time.*;

/**
 * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
 */
@Entity @Table(name="dg_data_assets",uniqueConstraints=@UniqueConstraint(columnNames={"organizationCode","assetCode"}))
class DataAsset {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) Long id; @Version long version;
 @Column(nullable=false,length=40) String organizationCode; @Column(nullable=false,length=50) String assetCode;
 @Column(nullable=false,length=120) String name; @Column(nullable=false,length=60) String dataDomain;
 @Column(nullable=false,length=50) String owner; @Column(nullable=false,length=30) String classification;
 @Column(nullable=false,length=20) String status="DRAFT"; double qualityScore; double lineageCoverage;
 LocalDateTime createdAt; LocalDateTime updatedAt;
 /**
  * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
  */
 protected DataAsset(){} /**
                          * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
                          */
DataAsset(String org,String code,String name,String domain,String owner,String classification){organizationCode=org;assetCode=code;this.name=name;dataDomain=domain;this.owner=owner;this.classification=classification;}
 /**
  * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
  */
 @PrePersist void created(){createdAt=updatedAt=LocalDateTime.now();}/**
                                                                      * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
                                                                      */
@PreUpdate void updated(){updatedAt=LocalDateTime.now();}
}

/**
 * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
 */
@Entity @Table(name="dg_quality_rules",uniqueConstraints=@UniqueConstraint(columnNames={"assetId","ruleCode"}))
class QualityRule {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) Long id; @Column(nullable=false) Long assetId;
 @Column(nullable=false,length=40) String ruleCode; @Column(nullable=false,length=40) String ruleType;
 @Column(nullable=false,length=200) String expressionText; double thresholdValue; boolean active=true;
 LocalDateTime createdAt;
 /**
  * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
  */
 protected QualityRule(){} /**
                            * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
                            */
QualityRule(Long assetId,String code,String type,String expression,double threshold){this.assetId=assetId;ruleCode=code;ruleType=type;expressionText=expression;thresholdValue=threshold;createdAt=LocalDateTime.now();}
}

/**
 * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
 */
@Entity @Table(name="dg_quality_runs",uniqueConstraints=@UniqueConstraint(columnNames="runNo"))
class QualityRun {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) Long id; @Column(nullable=false) Long assetId;
 @Column(nullable=false,length=50) String runNo; long totalRows; long failedRows; double score;
 @Column(nullable=false,length=20) String result; LocalDateTime executedAt;
 /**
  * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
  */
 protected QualityRun(){} /**
                           * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
                           */
QualityRun(Long assetId,String no,long total,long failed,double score,String result){this.assetId=assetId;runNo=no;totalRows=total;failedRows=failed;this.score=score;this.result=result;executedAt=LocalDateTime.now();}
}

/**
 * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
 */
@Entity @Table(name="dg_governance_issues",uniqueConstraints=@UniqueConstraint(columnNames="issueNo"))
class GovernanceIssue {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) Long id; @Version long version; @Column(nullable=false) Long assetId;
 @Column(nullable=false,length=50) String issueNo; @Column(nullable=false,length=20) String severity;
 @Column(nullable=false,length=300) String description; @Column(length=50) String assignee;
 @Column(nullable=false,length=20) String status="OPEN"; LocalDate dueDate; @Column(length=300) String resolution;
 LocalDateTime createdAt; LocalDateTime resolvedAt;
 /**
  * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
  */
 protected GovernanceIssue(){} /**
                                * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
                                */
GovernanceIssue(Long assetId,String no,String severity,String description,LocalDate due){this.assetId=assetId;issueNo=no;this.severity=severity;this.description=description;dueDate=due;createdAt=LocalDateTime.now();}
}

/**
 * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
 */
@Entity @Table(name="dg_lineage_edges",uniqueConstraints=@UniqueConstraint(columnNames={"organizationCode","sourceCode","targetCode"}))
class LineageEdge {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) Long id; @Column(nullable=false,length=40) String organizationCode;
 @Column(nullable=false,length=50) String sourceCode; @Column(nullable=false,length=50) String targetCode;
 @Column(nullable=false,length=120) String transformation; boolean validated; LocalDateTime createdAt;
 /**
  * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
  */
 protected LineageEdge(){} /**
                            * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
                            */
LineageEdge(String org,String source,String target,String transformation){organizationCode=org;sourceCode=source;targetCode=target;this.transformation=transformation;createdAt=LocalDateTime.now();}
}
