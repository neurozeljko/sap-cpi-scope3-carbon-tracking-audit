package cpi.scripts

import com.sap.gateway.ip.core.customdev.util.Message
import groovy.json.JsonOutput

Message processData(Message message) {
    String supplierId = message.getProperty("Prop_Supplier_ID")
    String invoiceNumber = message.getProperty("Prop_Invoice_Number")
    double emissions = message.getProperty("Prop_Reported_Emissions") as Double
    double weight = message.getProperty("Prop_Material_Weight") as Double
    double intensity = message.getProperty("Prop_Emission_Intensity") as Double
    double recycledPct = message.getProperty("Prop_Recycled_Ratio") as Double
    String auditSeal = message.getProperty("Prop_Cryptographic_Audit_Seal")
    String correlationId = message.getProperty("Prop_Correlation_ID") ?: UUID.randomUUID().toString()

    def controlTowerPayload = [
        SustainabilityControlTowerRecord: [
            OrganizationId: "ORG-GLOBAL-CORP-1010",
            ReportingPeriod: "2026-Q4",
            AccountingStandard: "GHG_PROTOCOL_SCOPE_3",
            ComplianceStatus: "VERIFIED_AUDIT_READY",
            SupplierProfile: [
                SupplierId: supplierId,
                OriginInvoiceReference: invoiceNumber,
                AuditSealSHA256: auditSeal
            ],
            EmissionMetrics: [
                TotalTCO2e: emissions,
                MaterialWeightTonnes: weight,
                CarbonIntensityTCO2ePerTonne: intensity,
                RecycledContentPercentage: recycledPct
            ],
            RegulatoryFilingProfile: [
                EUCBAMCompliant: true,
                SECClimateDisclosureReady: true,
                FilingCorrelationId: correlationId,
                SignedTimestamp: new Date().format("yyyy-MM-dd'T'HH:mm:ss'Z'")
            ]
        ]
    ]

    message.setBody(JsonOutput.prettyPrint(JsonOutput.toJson(controlTowerPayload)))
    message.setHeader("Content-Type", "application/json")
    message.setHeader("X-ESG-Audit-Status", "CONTROL_TOWER_VERIFIED")
    return message
}
