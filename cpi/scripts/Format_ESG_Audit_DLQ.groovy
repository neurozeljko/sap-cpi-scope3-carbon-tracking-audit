package cpi.scripts

import com.sap.gateway.ip.core.customdev.util.Message
import groovy.json.JsonOutput

Message processData(Message message) {
    def exception = message.getProperty("CamelExceptionCaught")
    String supplierId = message.getProperty("Prop_Supplier_ID") ?: "UNKNOWN_SUPPLIER"
    String correlationId = message.getHeader("SAP_MplCorrelationId", String) ?: UUID.randomUUID().toString()
    String errorMsg = exception ? exception.getMessage() : "ESG Scope 3 processing failure."

    def dlqPayload = [
        esgAuditDeadLetterQueue: [
            incidentTrackingId: correlationId,
            supplierId: supplierId,
            auditRiskLevel: "REGULATORY_NON_COMPLIANCE_RISK",
            failureReason: errorMsg,
            remediation: "STORE_IN_LEGAL_HOLD_DLQ_FOR_REPROCESSING",
            timestamp: new Date().format("yyyy-MM-dd'T'HH:mm:ss'Z'")
        ]
    ]

    message.setBody(JsonOutput.prettyPrint(JsonOutput.toJson(dlqPayload)))
    message.setHeader("Content-Type", "application/json")
    message.setHeader("X-ESG-Audit-Status", "QUARANTINED_IN_DLQ")
    return message
}
