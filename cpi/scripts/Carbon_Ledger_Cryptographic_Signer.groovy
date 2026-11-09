package cpi.scripts

import com.sap.gateway.ip.core.customdev.util.Message
import java.security.MessageDigest

Message processData(Message message) {
    String supplierId = message.getProperty("Prop_Supplier_ID")
    String invoiceNumber = message.getProperty("Prop_Invoice_Number")
    double emissions = message.getProperty("Prop_Reported_Emissions") as Double
    double weight = message.getProperty("Prop_Material_Weight") as Double
    String correlationId = message.getProperty("Prop_Correlation_ID") ?: UUID.randomUUID().toString()

    String auditString = "${supplierId}|${invoiceNumber}|${emissions}|${weight}|${correlationId}"
    MessageDigest digest = MessageDigest.getInstance("SHA-256")
    byte[] hashBytes = digest.digest(auditString.getBytes("UTF-8"))
    
    StringBuilder hexString = new StringBuilder()
    for (byte b : hashBytes) {
        String hex = Integer.toHexString(0xff & b)
        if (hex.length() == 1) hexString.append('0')
        hexString.append(hex)
    }
    String cryptographicAuditSeal = hexString.toString()

    message.setProperty("Prop_Cryptographic_Audit_Seal", cryptographicAuditSeal)
    message.setHeader("X-Audit-Seal-SHA256", cryptographicAuditSeal)
    return message
}
