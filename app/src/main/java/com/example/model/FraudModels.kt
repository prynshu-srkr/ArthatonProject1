package com.example.model

data class FraudAssessmentResult(
    val riskScore: Int,
    val threatLevel: String, // "Safe", "Suspicious", or "High Risk / Scam"
    val redFlags: List<String>,
    val explanation: String,
    val explanationHi: String
)

data class SampleScenario(
    val key: String,
    val label: String,
    val dotColorHex: Long,
    val text: String,
    val sender: String,
    val channel: String
)

data class GoldenRuleSlide(
    val index: Int,
    val badgeText: String,
    val bodyPrefix: String,
    val boldHighlight: String,
    val bodySuffix: String,
    val secondaryHighlight: String = "",
    val tailText: String = "",
    val themeType: SlideThemeType
)

enum class SlideThemeType {
    AMBER, TEAL, ROSE
}

data class EmergencyStep(
    val number: Int,
    val titleEn: String,
    val bodyEn: String,
    val bodyHi: String
)

object KavachStaticData {
    val CHANNELS = listOf(
        "SMS Text Message",
        "UPI App Collect Popup",
        "WhatsApp / Telegram Forward",
        "Email Statement Alert"
    )

    val SAMPLE_SCENARIOS = listOf(
        SampleScenario(
            key = "upi_collect",
            label = "Fake UPI Collect",
            dotColorHex = 0xFFF43F5E, // Rose-500
            text = "Congratulations! You have won Rs. 4,500 Flipkart Festive Cashback reward. Payment request sent from cashback.reward@ybl. Approve collect request & enter your 4-digit UPI PIN immediately to receive Rs. 4,500 in your bank account.",
            sender = "cashback.reward@ybl",
            channel = "UPI App Collect Popup"
        ),
        SampleScenario(
            key = "electricity_cut",
            label = "Electricity Cut Tonight",
            dotColorHex = 0xFFF59E0B, // Amber-500
            text = "Dear Consumer, Your Electricity power will be disconnected tonight at 9:30 PM from electricity office because your previous month bill was not updated. Please immediately call our electricity officer at 8923456712 to update bill.",
            sender = "+91 8923456712",
            channel = "SMS Text Message"
        ),
        SampleScenario(
            key = "honeytrap_sms",
            label = "Honeytrap / Friendship SMS",
            dotColorHex = 0xFFEC4899, // Pink-500
            text = "Dear Are u Feel Alone Need Patner?, Thank you for choosing us! . We appreciate your continued support. If any query call DIYA to Frndsip 8170069963 AMAL KUMAR",
            sender = "8170069963",
            channel = "SMS Text Message"
        ),
        SampleScenario(
            key = "fake_kyc",
            label = "Fake Bank KYC Link",
            dotColorHex = 0xFFF97316, // Orange-500
            text = "URGENT: Dear SBI Customer, your Bank Account and YONO access will be blocked today as your PAN KYC is expired. Click here http://bit.ly/sbi-kyc-verify-now to update your KYC immediately and avoid suspension.",
            sender = "+91 9812345670",
            channel = "SMS Text Message"
        ),
        SampleScenario(
            key = "safe_bank",
            label = "Safe Bank Debit/Credit Alert",
            dotColorHex = 0xFF10B981, // Emerald-500
            text = "Dear Customer, Rs. 850.00 debited from A/c XX4821 on 07-Nov-26 to VPA metro.recharge@okaxis (UPI Ref No 631118492014). Avl Bal: Rs. 14,230.50. Not you? Call 18002586161 - HDFC Bank",
            sender = "VM-HDFCBK",
            channel = "SMS Text Message"
        )
    )

    val GOLDEN_RULE_SLIDES = listOf(
        GoldenRuleSlide(
            index = 0,
            badgeText = "Golden UPI Rule #1 • Collect Requests",
            bodyPrefix = "You ",
            boldHighlight = "NEVER",
            bodySuffix = " need to enter your UPI PIN or scan a QR code to receive money. Entering your PIN always deducts money from your bank account.",
            themeType = SlideThemeType.AMBER
        ),
        GoldenRuleSlide(
            index = 1,
            badgeText = "Golden UPI Rule #2 • KYC & APK Phishing",
            bodyPrefix = "Banks and NPCI ",
            boldHighlight = "never ask you to update PAN/KYC",
            bodySuffix = " or unblock accounts via SMS/WhatsApp links or screen-sharing apps like AnyDesk.",
            themeType = SlideThemeType.TEAL
        ),
        GoldenRuleSlide(
            index = 2,
            badgeText = "Golden UPI Rule #3 • Golden Hour Reporting",
            bodyPrefix = "Victim of a UPI or banking fraud? Call the National Cyber Crime Helpline at ",
            boldHighlight = "1930",
            bodySuffix = " or report at ",
            secondaryHighlight = "cybercrime.gov.in",
            tailText = " within the golden hour to freeze stolen funds.",
            themeType = SlideThemeType.ROSE
        )
    )

    val EMERGENCY_STEPS = listOf(
        EmergencyStep(
            number = 1,
            titleEn = "Turn OFF Mobile Data & Wi-Fi (Airplane Mode)",
            bodyEn = "If you clicked an unknown link or installed an .apk / AnyDesk app, cut internet access immediately so scammers cannot read your SMS OTPs.",
            bodyHi = "• यदि आपने किसी लिंक पर क्लिक किया है या कोई ऐप डाउनलोड किया है, तो तुरंत मोबाइल डेटा और Wi-Fi बंद करें।"
        ),
        EmergencyStep(
            number = 2,
            titleEn = "Dial 1930 Within the \"Golden Hour\" to Freeze Money",
            bodyEn = "Keep the 12-digit UPI Reference / UTR number and screenshot ready. Reporting within 15–60 minutes lets Cyber Police freeze the scammer's bank account before withdrawal.",
            bodyHi = "• पैसे कटने पर तुरंत 1930 पर कॉल करें और UTR नंबर बताएं ताकि ठग के बैंक खाते में रकम फ्रीज़ की जा सके।"
        ),
        EmergencyStep(
            number = 3,
            titleEn = "Block Your UPI ID & Change MPIN / NetBanking Password",
            bodyEn = "Open your official bank app (or call your bank's official toll-free number on the back of your debit card) to temporarily block UPI and cards.",
            bodyHi = "• अपने बैंक ऐप से UPI ID को अस्थायी रूप से ब्लॉक करें और अपना UPI पिन/पासवर्ड बदल लें।"
        ),
        EmergencyStep(
            number = 4,
            titleEn = "Save Evidence (Do Not Delete the Message)",
            bodyEn = "Take screenshots of the SMS/WhatsApp message, sender number, UPI ID, and bank debit alert for your 1930 cybercrime complaint.",
            bodyHi = "• सबूत के तौर पर SMS, फोन नंबर और ट्रांज़ैक्शन का स्क्रीनशॉट सुरक्षित रखें, संदेश को डिलीट न करें।"
        )
    )
}
