package cpi.scripts

import com.sap.gateway.ip.core.customdev.util.Message
import groovy.json.JsonOutput

Message processData(Message message) {
    String supplierId = message.getProperty("Prop_Supplier_ID")
    String invoiceNumber = message.getProperty("Prop_Invoice_Number")
    double emissions = message.getProperty("Prop_Reported_Emissions") as Double
    double intensity = message.getProperty("Prop_Emission_Intensity") as Double
    String correlationId = message.getProperty("Prop_Correlation_ID") ?: UUID.randomUUID().toString()

    def quarantinePayload = [
        greenwashingAuditQuarantine: [
            incidentTrackingId: correlationId,
            supplierId: supplierId,
            invoiceReference: invoiceNumber,
            reportedEmissionsTCO2e: emissions,
            calculatedIntensity: intensity,
            quarantineReason: "REPORTED_METRICS_VIOLATE_PHYSICAL_BENCHMARKS_POSSIBLE_UNDERREPORTING",
            auditAction: "SUSPEND_SUPPLIER_ESG_CERTIFICATE_AND_TRIGGER_FORENSIC_INSPECTION",
            quarantineTimestamp: new Date().format("yyyy-MM-dd'T'HH:mm:ss'Z'")
        ]
    ]

    message.setBody(JsonOutput.prettyPrint(JsonOutput.toJson(quarantinePayload)))
    message.setHeader("Content-Type", "application/json")
    message.setHeader("X-ESG-Audit-Status", "QUARANTINED_GREENWASHING_ALERT")
    return message
}
