/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.datagovernance.service;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

class DataProductCertificationServiceTest {
    private final DataProductCertificationService service = new DataProductCertificationService();

    @Test void certifiesTrustedDataProduct() {
        var result = service.assess(new DataProductCertificationService.Request("DP-100", true, true, true,
                true, true, true, true, true, true, true));
        assertThat(result.decision()).isEqualTo(DataProductCertificationService.Decision.CERTIFY);
    }

    @Test void routesMetadataGapsToRemediation() {
        var result = service.assess(new DataProductCertificationService.Request("DP-101", false, false, false,
                true, true, true, false, true, false, true));
        assertThat(result.actions()).hasSize(5);
        assertThat(result.decision()).isEqualTo(DataProductCertificationService.Decision.REMEDIATE);
    }

    @Test void blocksUntrustedDataProduct() {
        var result = service.assess(new DataProductCertificationService.Request("", false, false, false,
                false, false, false, false, false, false, false));
        assertThat(result.blockers()).hasSize(6);
        assertThat(result.decision()).isEqualTo(DataProductCertificationService.Decision.BLOCKED);
    }
}
