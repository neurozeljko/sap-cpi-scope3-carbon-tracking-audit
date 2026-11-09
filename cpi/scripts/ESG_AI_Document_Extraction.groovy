package cpi.scripts

import com.sap.gateway.ip.core.customdev.util.Message
import groovy.json.JsonSlurper

Message processData(Message message) {
    def reader = message.getBody(java.io.Reader)
    def jsonSlurper = new JsonSlurper()
    def payload = jsonSlurper.parse(reader)

    String supplierId = payload.supplierId ?: "SUPPLIER-GLOBAL-ES-01"
    String invoiceNumber = payload.invoiceNumber ?: "INV-ESG-2026-99"
    def aiExtractedData = payload.aiCoreExtractedEntities ?: [:]

    double reportedEmissionsTCO2e = (aiExtractedData.scope3EmissionsTCO2e ?: 0.0) as Double
    double netMaterialWeightTonnes = (aiExtractedData.totalMaterialWeightTonnes ?: 1.0) as Double
    double recycledContentRatio = (aiExtractedData.recycledMaterialPercentage ?: 0.0) as Double
    double baselineEmissionsBenchmark = (payload.industryBenchmarkTCO2e ?: 2.45) as Double

    if (reportedEmissionsTCO2e <= 0.0 || netMaterialWeightTonnes <= 0.0) {
        throw new IllegalArgumentException("ESG_AUDIT_FAULT: Zero or negative emission/material values extracted.")
    }

    double emissionIntensityPerTonne = reportedEmissionsTCO2e / netMaterialWeightTonnes
    boolean isGreenwashingAnomaly = (emissionIntensityPerTonne < (baselineEmissionsBenchmark * 0.25)) && (recycledContentRatio < 50.0)

    message.setProperty("Prop_Supplier_ID", supplierId)
    message.setProperty("Prop_Invoice_Number", invoiceNumber)
    message.setProperty("Prop_Reported_Emissions", reportedEmissionsTCO2e)
    message.setProperty("Prop_Material_Weight", netMaterialWeightTonnes)
    message.setProperty("Prop_Emission_Intensity", emissionIntensityPerTonne.round(4))
    message.setProperty("Prop_Recycled_Ratio", recycledContentRatio)
    message.setProperty("Prop_Greenwashing_Risk", isGreenwashingAnomaly.toString())

    return message
}
