<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="3.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:cbam="urn:eu:cbam:v1:declaration"
    exclude-result-prefixes="xsl">

    <xsl:output method="xml" indent="yes" encoding="UTF-8"/>

    <xsl:template match="/">
        <cbam:CarbonBorderAdjustmentDeclaration>
            <cbam:Header>
                <cbam:DeclarantIdentifier>ORG-GLOBAL-CORP-1010</cbam:DeclarantIdentifier>
                <cbam:ReportingQuarter>2026-Q4</cbam:ReportingQuarter>
                <cbam:SubmissionTimestamp><xsl:value-of select="current-dateTime()"/></cbam:SubmissionTimestamp>
            </cbam:Header>
            <cbam:SupplyChainInstallations>
                <cbam:Installation>
                    <cbam:SupplierID><xsl:value-of select="//SupplierId"/></cbam:SupplierID>
                    <cbam:DirectAndIndirectEmbeddedEmissionsTCO2e>
                        <xsl:value-of select="//TotalTCO2e"/>
                    </cbam:DirectAndIndirectEmbeddedEmissionsTCO2e>
                    <cbam:TotalMaterialNetWeightTonnes>
                        <xsl:value-of select="//MaterialWeightTonnes"/>
                    </cbam:TotalMaterialNetWeightTonnes>
                    <cbam:SpecificCarbonIntensity>
                        <xsl:value-of select="//CarbonIntensityTCO2ePerTonne"/>
                    </cbam:SpecificCarbonIntensity>
                    <cbam:CryptographicAuditHash>
                        <xsl:value-of select="//AuditSealSHA256"/>
                    </cbam:CryptographicAuditHash>
                </cbam:Installation>
            </cbam:SupplyChainInstallations>
        </cbam:CarbonBorderAdjustmentDeclaration>
    </xsl:template>

</xsl:stylesheet>
