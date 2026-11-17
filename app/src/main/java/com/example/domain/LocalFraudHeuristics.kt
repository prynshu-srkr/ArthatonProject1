package com.example.domain

import com.example.model.FraudAssessmentResult
import kotlin.math.max
import kotlin.math.min

object LocalFraudHeuristics {

    // 1. PIN / OTP / Password / CVV Credential Traps
    private val pinKeywordRegex = Regex(
        """\b(upi\s*pin|mpin|4-digit\s*pin|6-digit\s*pin|enter\s*pin|enter\s*your\s*pin|approve\s*&\s*enter|share\s*otp|enter\s*otp|secret\s*pin|cvv|atm\s*pin|password)\b""",
        RegexOption.IGNORE_CASE
    )
    private val receiveLureRegex = Regex(
        """\b(receive|receiving|cashback|refund|reward|subsidy|prize|claim|win|won|winner|bonus|advance|lottery|gift|voucher|jackpot)\b""",
        RegexOption.IGNORE_CASE
    )
    private val collectReqRegex = Regex(
        """\b(collect\s*request|request\s*from|pay\s*request|requested\s*money|payment\s*request|approve\s*request|decline\s*or\s*approve)\b""",
        RegexOption.IGNORE_CASE
    )

    // 2. Lottery / Prize / Cashback / Free Money Lures
    private val lotteryRegex = Regex(
        """\b(lottery|jackpot|lucky\s*draw|won\s*rs|won\s*₹|win\s*rs|win\s*₹|cashback\s*of\s*rs|reward\s*of\s*rs|unclaimed\s*reward|congratulations\s*you\s*have\s*won|claim\s*your\s*(prize|reward|cashback|refund))\b""",
        RegexOption.IGNORE_CASE
    )

    // 3. Suspicious UPI IDs (VPAs)
    private val vpaRegex = Regex("""([a-zA-Z0-9.\-_]{2,40}@[a-zA-Z]{2,15})""")
    private val suspiciousVpaWords = Regex(
        """(cashback|refund|reward|lottery|pmcare|olx|army|kyc|care|support|helpline|prize|flipkart|amazon|winner|bonus|verify|officer)""",
        RegexOption.IGNORE_CASE
    )

    // 4. Urgency, Account Block, Suspension, Disconnection Threats
    private val urgencyRegex = Regex(
        """\b(immediately|tonight|within\s*\d+\s*(mins|minutes|hours|hrs)|11:59\s*pm|9:30\s*pm|expires\s*in|urgent|urgently|suspended|blocked|block|deactivated|deactivate|disconnected|disconnect|frozen|freeze|terminated|closed\s*today|expire\s*today|last\s*warning|final\s*notice|action\s*required)\b""",
        RegexOption.IGNORE_CASE
    )

    // 5. Fake Authority, KYC, Tax, Customs, Police, Digital Arrest
    private val authorityRegex = Regex(
        """\b(pan\s*kyc|kyc\s*update|update\s*kyc|pending\s*kyc|complete\s*your\s*kyc|kyc\s*expired|kyc\s*verification|aadhaar\s*update|electricity\s*power|electricity\s*bill|electricity\s*office|power\s*cut|yono|income\s*tax|tax\s*refund|customs\s*parcel|fedex\s*parcel|illegal\s*parcel|digital\s*arrest|cbi|cyber\s*police|narcotics|rbi\s*notice|court\s*notice|challan|traffic\s*echallan)\b""",
        RegexOption.IGNORE_CASE
    )

    // 6. Links, Bare Domains, Shortened URLs & Click Bait
    private val urlRegex = Regex(
        """(https?://[^\s]+|www\.[^\s]+|\b[a-z0-9\-]+\.(com|in|org|net|xyz|top|online|site|info|link|click|club|live|me|ly|gl|gy|gd|apk)(/[^\s]*)?)""",
        RegexOption.IGNORE_CASE
    )
    private val shortLinkRegex = Regex(
        """\b(bit\.ly|tinyurl\.com|rb\.gy|is\.gd|t\.me|cutt\.ly|shorturl|ngrok|\.apk|\.xyz|\.top|\.click|\.link)\b""",
        RegexOption.IGNORE_CASE
    )
    private val clickActionRegex = Regex(
        """\b(click\s*here|click\s*below|click\s*the\s*link|tap\s*here|visit\s*link|open\s*link|verify\s*here|update\s*here|login\s*here|download\s*app)\b""",
        RegexOption.IGNORE_CASE
    )

    // 7. Remote Access / Screen-Sharing Apps
    private val remoteAppRegex = Regex(
        """\b(anydesk|teamviewer|quicksupport|rustdesk|airmirror|ultraviewer|\.apk\b|install\s*support\s*app)\b""",
        RegexOption.IGNORE_CASE
    )

    // 8. Unverified Callback / Personal Mobile Numbers (excludes 1800/1860 toll-free and 12-digit UPI Ref numbers)
    private val mobileInTextRegex = Regex("""(?<!\d)(?:\+?91[\s\-]?)?[6-9]\d{4}[\s\-]?\d{5}(?!\d)""")
    private val callbackPhraseRegex = Regex(
        """\b(contact\s*our\s*officer|call\s*immediately|call\s*our\s*helpline|customer\s*care\s*no|whatsapp\s*now|whatsapp\s*us)\b""",
        RegexOption.IGNORE_CASE
    )

    // 9. Part-Time Job / Telegram Task / Crypto / Investment Scams
    private val jobTaskRegex = Regex(
        """\b(part-time|part\s*time\s*job|work\s*from\s*home|wfh|liking\s*youtube|youtube\s*videos|google\s*maps\s*review|daily\s*income|telegram\s*receptionist|earn\s*rs|earn\s*₹|\d+%\s*return|double\s*your\s*money|prepaid\s*task|vip\s*task|stock\s*trading\s*group)\b""",
        RegexOption.IGNORE_CASE
    )

    // 10. Fake Credit Alert ("sent by mistake" / "return the money")
    private val creditMistakeRegex = Regex(
        """\b(sent\s*by\s*mistake|wrongly\s*transferred|return\s*the\s*money|send\s*back|refund\s*to)\b""",
        RegexOption.IGNORE_CASE
    )

    // 11. Personal Mobile Number Impersonating a Bank
    private val claimsBankInBodyRegex = Regex(
        """\b(dear\s*(hdfc|sbi|icici|axis|kotak|pnb|bob|canara|union|bank|customer|consumer|user))\b""",
        RegexOption.IGNORE_CASE
    )
    private val personalMobileSenderRegex = Regex("""^(?:\+?91[\s\-]?)?[6-9]\d{4}[\s\-]?\d{5}$""")

    // 12. Honeytrap, Friendship Club SMS Typos, Romance Scam, Sextortion
    private val romanceWordsRegex = Regex(
        """\b(honey|darling|sweetheart|babe|baby|jaanu|jaan|handsome|beautiful|sexy|hot\s*chat|lonely|feel\s*alone|are\s*(u|you)\s*alone|alone\s*tonight|need\s*pa[r]?tner|pa[r]?tner|fr[ie]*nd\s*s[h]?ip|frndsip|frndshp|dosti|private\s*(video|call|chat|photos|pics)|video\s*call\s*me|nude|webcam|dating|friendship\s*club|escort|call\s*girl|bhabhi|aunty|girl\s*friend|gf\s*service|night\s*service|sugar\s*(mommy|daddy)|marry\s*you|love\s*you|my\s*love|kiss\s*you|hug\s*you|soulmate|secret\s*relationship|meet\s*me\s*alone|horny|romant[a-z]*|call\s+[a-z]{3,12}\s+to\s+fr[a-z]*)\b""",
        RegexOption.IGNORE_CASE
    )
    private val blackmailRegex = Regex(
        """\b(recorded\s*your\s*video|leak\s*your\s*(video|photos|pics)|upload\s*on\s*youtube|send\s*to\s*your\s*(family|friends|contacts)|viral\s*your\s*video|delete\s*the\s*footage|pay\s*to\s*delete|compromised\s*video)\b""",
        RegexOption.IGNORE_CASE
    )
    private val moneyOrGiftAskRegex = Regex(
        """\b(send\s*me\s*(money|rs|₹|cash|gift)|need\s*(urgent\s*)?(money|help|cash)|customs\s*fee|gift\s*parcel|hospital\s*bill|recharge\s*my|pay\s*for\s*my|flight\s*ticket|registration\s*fee|membership\s*fee|joining\s*fee|transfer\s*rs|gpay\s*me|phonepe\s*me|paytm\s*me)\b""",
        RegexOption.IGNORE_CASE
    )
    private val fakeTemplateHookRegex = Regex(
        """\b(thank\s*you\s*for\s*choosing\s*us|appreciate\s*your\s*continued\s*support|if\s*any\s*query\s*call)\b""",
        RegexOption.IGNORE_CASE
    )

    // 13. Safe Bank Alert Checks
    private val historicalDateRegex = Regex(
        """\b(\d{1,2}(st|nd|rd|th)?[\s/\-]*(jan|feb|mar|apr|may|jun|jul|aug|sep|oct|nov|dec|november)|\d{1,2}[/\-]\d{1,2}[/\-]\d{2,4})\b""",
        RegexOption.IGNORE_CASE
    )
    private val standardBankAlertRegex = Regex(
        """\b(credited|debited|spent|withdrawn|transferred|a/c|acct|account\s*xx|upi\s*ref|ref\s*no|utr|avl\s*bal|available\s*bal|clear\s*bal|imps|neft|rtgs|sent\s*rs\.?\s*\d+|paid\s*rs\.?\s*\d+)\b""",
        RegexOption.IGNORE_CASE
    )
    private val maskedAccountOrRefRegex = Regex(
        """\b(a/c\s*[xX*]+\d+|acct\s*[xX*]+\d+|upi\s*ref\s*no|avl\s*bal|available\s*bal|1800\d{6,7}|1860\d{6,7})\b""",
        RegexOption.IGNORE_CASE
    )

    private val amountExtractRegex = Regex("""(?:rs\.?|inr|₹)\s*[\d,]+(?:\.\d{1,2})?""", RegexOption.IGNORE_CASE)
    private val electricityScamRegex = Regex(
        """\b(electricity|power\s*cut|bill\s*unpaid|meter|disconnection|disconnect\s*tonight|electricity\s*officer)\b""",
        RegexOption.IGNORE_CASE
    )
    private val kycOrBlockRegex = Regex(
        """\b(pan\s*kyc|kyc\s*update|update\s*kyc|pending\s*kyc|complete\s*your\s*kyc|kyc\s*expired|kyc\s*verification|aadhaar\s*update|yono|account\s*(blocked|suspended|frozen|deactivated))\b""",
        RegexOption.IGNORE_CASE
    )
    private val digitalArrestOrCustomsRegex = Regex(
        """\b(customs\s*parcel|fedex\s*parcel|illegal\s*parcel|digital\s*arrest|cbi|cyber\s*police|narcotics|rbi\s*notice|court\s*notice|challan|traffic\s*echallan|income\s*tax)\b""",
        RegexOption.IGNORE_CASE
    )

    /**
     * Returns true when a message is clearly a legitimate bank debit/credit notification
     * (e.g. contains masked A/c number, UPI Ref No, Avl Bal, official 1800 toll-free number,
     * and has zero phishing links, zero PIN/OTP requests, and zero collect requests).
     */
    fun isVerifiedSafeBankAlert(rawText: String, sender: String = ""): Boolean {
        val text = rawText.trim()
        if (text.isEmpty()) return false
        val hasStandardBankMarkers = standardBankAlertRegex.containsMatchIn(text) &&
            maskedAccountOrRefRegex.containsMatchIn(text)
        val hasAnyThreat = pinKeywordRegex.containsMatchIn(text) ||
            collectReqRegex.containsMatchIn(text) ||
            receiveLureRegex.containsMatchIn(text) ||
            lotteryRegex.containsMatchIn(text) ||
            urgencyRegex.containsMatchIn(text) ||
            authorityRegex.containsMatchIn(text) ||
            urlRegex.containsMatchIn(text) ||
            shortLinkRegex.containsMatchIn(text) ||
            clickActionRegex.containsMatchIn(text) ||
            remoteAppRegex.containsMatchIn(text) ||
            romanceWordsRegex.containsMatchIn(text) ||
            blackmailRegex.containsMatchIn(text) ||
            personalMobileSenderRegex.containsMatchIn(sender.trim())

        return hasStandardBankMarkers && !hasAnyThreat
    }

    fun evaluate(rawText: String, sender: String = ""): FraudAssessmentResult {
        val text = rawText.trim()
        val cleanSender = sender.trim()
        val flags = mutableListOf<String>()
        var score = 10

        val hasPinKeyword = pinKeywordRegex.containsMatchIn(text)
        val hasReceiveClaim = receiveLureRegex.containsMatchIn(text)
        val isCollectReq = collectReqRegex.containsMatchIn(text)

        if (hasPinKeyword && hasReceiveClaim) {
            score += 82
            flags.add("Fake UPI Collect Request")
            flags.add("Asks for PIN/OTP to Receive Money")
        } else if (isCollectReq && hasReceiveClaim) {
            score += 75
            flags.add("Deceptive Collect Request")
        } else if (hasPinKeyword) {
            score += 65
            flags.add("Requests Secret UPI PIN or OTP")
        } else if (isCollectReq) {
            score += 45
            flags.add("Unverified UPI Collect Request")
        }

        if (lotteryRegex.containsMatchIn(text)) {
            score += 65
            flags.add("Fake Lottery / Reward Lure")
        }

        val vpaMatches = vpaRegex.findAll(text).map { it.value }.toList()
        for (vpa in vpaMatches) {
            if (suspiciousVpaWords.containsMatchIn(vpa)) {
                score += 45
                flags.add("Suspicious UPI ID ($vpa)")
                break
            }
        }

        val hasUrgency = urgencyRegex.containsMatchIn(text)
        if (hasUrgency) {
            score += 38
            flags.add("Urgency / Account Block Threat")
        }

        val hasAuthority = authorityRegex.containsMatchIn(text)
        if (hasAuthority) {
            score += 42
            flags.add("Fake Authority / KYC Impersonation")
        }

        val hasUrl = urlRegex.containsMatchIn(text)
        if (shortLinkRegex.containsMatchIn(text)) {
            score += 50
            flags.add("High-Risk Shortened / APK Link")
        } else if (hasUrl) {
            score += 38
            flags.add("Suspicious External Link")
        }

        val hasClickAction = clickActionRegex.containsMatchIn(text)
        if (hasClickAction) {
            score += 25
            if (!flags.contains("Suspicious External Link") && !flags.contains("High-Risk Shortened / APK Link")) {
                flags.add("Phishing Call-to-Action")
            }
        }

        val isRemoteAppScam = remoteAppRegex.containsMatchIn(text)
        if (isRemoteAppScam) {
            score += 65
            flags.add("Screen-Sharing / Malware App Trap")
        }

        val hasMobileNumberInText = mobileInTextRegex.containsMatchIn(text)
        val hasCallbackPhrase = callbackPhraseRegex.containsMatchIn(text)
        if ((hasMobileNumberInText || hasCallbackPhrase) && (hasUrgency || hasAuthority || hasReceiveClaim)) {
            score += 32
            flags.add("Unverified Callback Phone Number")
        }

        val isJobTaskScam = jobTaskRegex.containsMatchIn(text)
        if (isJobTaskScam) {
            score += 75
            flags.add("Fake Job / Task Investment Scam")
        }

        val isFakeCreditReversal = creditMistakeRegex.containsMatchIn(text) &&
            (hasUrl || isCollectReq || hasMobileNumberInText)
        if (isFakeCreditReversal) {
            score += 65
            flags.add("Fake Credit / Reversal Scam")
        }

        if (claimsBankInBodyRegex.containsMatchIn(text) && personalMobileSenderRegex.containsMatchIn(cleanSender)) {
            score += 30
            flags.add("Bank Alert Sent from Personal Mobile Number")
        }

        val isHoneytrapRomance = romanceWordsRegex.containsMatchIn(text)
        val isSextortion = blackmailRegex.containsMatchIn(text)
        val hasFakeTemplateHook = fakeTemplateHookRegex.containsMatchIn(text)

        if (isSextortion) {
            score += 88
            flags.add("Sextortion / Video Blackmail Scam")
            flags.add("Threatens to Leak Private Media")
        }

        if (isHoneytrapRomance && (
                moneyOrGiftAskRegex.containsMatchIn(text) ||
                    hasUrl ||
                    isCollectReq ||
                    hasReceiveClaim ||
                    hasMobileNumberInText ||
                    hasFakeTemplateHook
                )
        ) {
            score += 85
            flags.add("Honeytrap / Friendship Club Scam")
            if (hasMobileNumberInText) {
                flags.add("Suspicious Callback Number Trap")
            }
            if (hasFakeTemplateHook) {
                flags.add("Disguised SMS Template Lure")
            }
        } else if (isHoneytrapRomance) {
            score += 75
            flags.add("Honeytrap / Romance Lure")
            flags.add("Unsolicited Flirtatious / Dating Bait")
        }

        if (!isHoneytrapRomance && hasFakeTemplateHook && hasMobileNumberInText) {
            score += 68
            flags.add("Hijacked SMS Template Scam")
            flags.add("Unverified Personal Callback Number")
        }

        val hasHistoricalDate = historicalDateRegex.containsMatchIn(text)
        val isStandardBankAlert = standardBankAlertRegex.containsMatchIn(text)

        if (flags.isEmpty() && (isStandardBankAlert || hasHistoricalDate)) {
            score = 5
        }

        if (flags.size >= 2 && score < 75) {
            score = 78 + min(18, flags.size * 4)
        } else if (flags.size == 1 && isHoneytrapRomance && score < 72) {
            score = 75
        }

        score = min(99, max(4, score))

        val threatLevel = when {
            score >= 70 -> "High Risk / Scam"
            score >= 35 -> "Suspicious"
            else -> "Safe"
        }

        val detectedAmount = amountExtractRegex.find(text)?.value
        val detectedPhone = mobileInTextRegex.find(text)?.value
        val detectedLink = urlRegex.find(text)?.value
        val detectedVpa = vpaMatches.firstOrNull()

        val isElectricityScam = electricityScamRegex.containsMatchIn(text)
        val isKycOrAccountBlock = kycOrBlockRegex.containsMatchIn(text)
        val isDigitalArrestOrCustoms = digitalArrestOrCustomsRegex.containsMatchIn(text)
        val isLotteryRewardScam = lotteryRegex.containsMatchIn(text)

        val explanation: String
        val explanationHi: String

        when {
            isSextortion -> {
                explanation = "VIDEO BLACKMAIL / SEXTORTION ALERT: Do NOT pay ${detectedAmount ?: "any money"} or reply to the blackmailer. Scammers use morphed videos and threats to create panic and extort money repeatedly. Take screenshots, block the contact immediately, and report anonymously at 1930 or cybercrime.gov.in."
                explanationHi = "वीडियो ब्लैकमेल / सेक्सटॉर्शन अलर्ट: घबराकर ${detectedAmount ?: "कोई भी पैसा"} न भेजें और ठग को जवाब न दें। साइबर अपराधी एडिट किए गए वीडियो दिखाकर डराते हैं और बार-बार पैसे मांगते हैं। तुरंत नंबर ब्लॉक करें और 1930 या cybercrime.gov.in पर गोपनीय शिकायत दर्ज करें।"
            }
            isHoneytrapRomance -> {
                val phoneDetail = if (detectedPhone != null) " (especially the number $detectedPhone)" else ""
                val phoneDetailHi = if (detectedPhone != null) " (विशेषकर नंबर $detectedPhone पर)" else ""
                explanation = "HONEYTRAP / FRIENDSHIP CLUB SCAM: This message uses emotional or romantic bait (\"friendship/partner\") disguised inside an SMS template. Do NOT call back$phoneDetail or pay any registration/meeting fees. Fraudsters use these numbers to trap victims into paying advance fees or video-call extortion."
                explanationHi = "हनीट्रैप / फ्रेंडशिप क्लब स्कैम: यह संदेश अकेलेपन या दोस्ती (\"Patner / Frndsip\") का झांसा देकर आपको फंसाने के लिए भेजा गया है। इस नंबर$phoneDetailHi पर कॉल न करें और कोई रजिस्ट्रेशन या मीटिंग शुल्क न दें। ठग इसके ज़रिए पैसे ऐंठते हैं या ब्लैकमेल करते हैं।"
            }
            isRemoteAppScam -> {
                explanation = "REMOTE ACCESS / MALWARE TRAP: Never download ${detectedLink ?: "any .APK file"} or install screen-sharing apps like AnyDesk, TeamViewer, or QuickSupport. Scammers use these tools to watch your phone screen live and steal your OTPs and bank balance."
                explanationHi = "रिमोट एक्सेस / मैलवेयर धोखाधड़ी: कभी भी ${detectedLink ?: "कोई .APK फ़ाइल"} डाउनलोड न करें और AnyDesk या TeamViewer जैसे स्क्रीन-शेयरिंग ऐप इंस्टॉल न करें। ठग इनके ज़रिए आपके फोन की स्क्रीन देखकर OTP और बैंक बैलेंस चुरा लेते हैं।"
            }
            hasPinKeyword || (isCollectReq && hasReceiveClaim) -> {
                val vpaDetail = if (detectedVpa != null) " from UPI ID \"$detectedVpa\"" else ""
                val amtDetail = if (detectedAmount != null) " of $detectedAmount" else ""
                explanation = "FAKE UPI COLLECT TRAP: You are being tricked into approving a payment request$amtDetail$vpaDetail. Entering your UPI PIN or OTP will DEDUCT money from your bank account—you never enter a PIN to receive cashback, refunds, or payments. Decline and block the request immediately."
                explanationHi = "नकली UPI कलेक्ट रिक्वेस्ट: आपको ${if (detectedAmount != null) "$detectedAmount के " else ""}कैशबैक या रिफंड के नाम पर पैसे भेजने की रिक्वेस्ट भेजी गई है। याद रखें, UPI पिन डालने से आपके खाते से पैसे कटते हैं, आते नहीं हैं। इस रिक्वेस्ट को तुरंत Decline (अस्वीकार) करें।"
            }
            isElectricityScam -> {
                val callNote = if (detectedPhone != null) " Do NOT call the fake officer number ($detectedPhone)." else ""
                val callNoteHi = if (detectedPhone != null) " दिए गए नकली अधिकारी नंबर ($detectedPhone) पर बिल्कुल कॉल न करें।" else ""
                explanation = "ELECTRICITY DISCONNECTION SCAM: Power companies never threaten tonight disconnection via personal mobile numbers or WhatsApp links.$callNote Check your bill status only on your state electricity board's official portal or UPI biller section."
                explanationHi = "बिजली कनेक्शन काटने का नकली संदेश: बिजली विभाग कभी भी निजी मोबाइल नंबर से आज रात लाइन काटने की धमकी नहीं देता।$callNoteHi अपने बिल की जांच केवल आधिकारिक सरकारी वेबसाइट या भरोसेमंद UPI ऐप के बिल सेक्शन में ही करें।"
            }
            isDigitalArrestOrCustoms -> {
                explanation = "FAKE POLICE / CUSTOMS / DIGITAL ARREST SCAM: Law enforcement, CBI, Customs, and RBI never conduct \"digital arrests\" on video calls or demand money (${detectedAmount ?: "fines"}) to clear parcels or cases. Disconnect immediately and report the number on 1930."
                explanationHi = "नकली पुलिस / कस्टम / डिजिटल अरेस्ट स्कैम: पुलिस, CBI, कस्टम या RBI कभी भी कॉल या वीडियो कॉल पर 'डिजिटल अरेस्ट' नहीं करते और न ही केस रफा-दफा करने के लिए पैसे (${detectedAmount ?: "जुर्माना"}) मांगते हैं। तुरंत कॉल काटें और 1930 पर रिपोर्ट करें।"
            }
            isJobTaskScam -> {
                explanation = "PART-TIME JOB / TASK INVESTMENT SCAM: Offers promising easy daily earnings (${detectedAmount ?: "high payouts"}) for liking YouTube videos, rating Google Maps, or completing prepaid Telegram tasks are 100% fraudulent. They pay small amounts first and then freeze your large deposits."
                explanationHi = "पार्ट-टाइम जॉब / टेलीग्राम टास्क स्कैम: घर बैठे यूट्यूब वीडियो लाइक करने, रिव्यू देने या प्रीपेड टास्क के बदले रोज़ाना मोटी कमाई (${detectedAmount ?: "आसान मुनाफ़ा"}) का लालच देने वाले संदेश 100% ठगी हैं। इसमें कभी भी पैसा निवेश न करें।"
            }
            isKycOrAccountBlock -> {
                val linkNote = if (detectedLink != null) " Do NOT open the link \"$detectedLink\"." else ""
                val linkNoteHi = if (detectedLink != null) " \"$detectedLink\" लिंक को बिल्कुल न खोलें।" else ""
                explanation = "FAKE BANK KYC / ACCOUNT BLOCK PHISHING: Your bank will never block your account suddenly or ask you to update PAN/Aadhaar KYC via an SMS link.$linkNote If you have doubts, open your official banking app directly or visit your branch."
                explanationHi = "नकली बैंक KYC / खाता ब्लॉक फ़िशिंग: कोई भी बैंक SMS लिंक भेजकर अचानक खाता बंद करने की धमकी नहीं देता और न ही लिंक पर PAN/Aadhaar मांगता है।$linkNoteHi केवल अपने बैंक के आधिकारिक ऐप का ही उपयोग करें।"
            }
            isLotteryRewardScam -> {
                val linkVisit = if (detectedLink != null) " Do not visit \"$detectedLink\"." else ""
                val linkVisitHi = if (detectedLink != null) " \"$detectedLink\" पर क्लिक न करें।" else ""
                explanation = "FAKE LOTTERY / REWARD SCAM: Messages claiming you won ${detectedAmount ?: "a lucky draw or unclaimed cashback"} are bait to steal your banking credentials or charge fake processing fees.$linkVisit Ignore and delete this message."
                explanationHi = "नकली लॉटरी / इनाम का झांसा: आपको ${detectedAmount ?: "लॉटरी, इनाम या कैशबैक"} जीतने का लालच देकर आपकी बैंक जानकारी चुराने या प्रोसेसिंग फीस ठगने की कोशिश की जा रही है।$linkVisitHi इस संदेश को तुरंत डिलीट करें।"
            }
            isFakeCreditReversal -> {
                explanation = "FAKE CREDIT / REVERSAL SCAM: Fraudsters send a fake SMS pretending ${detectedAmount ?: "money"} was credited to your account by mistake and ask you to return it. Always check your actual bank account balance inside your UPI/bank app before sending a single rupee."
                explanationHi = "नकली क्रेडिट / पैसे वापस मांगने का स्कैम: ठग पहले नकली SMS भेजते हैं कि आपके खाते में गलती से ${detectedAmount ?: "पैसे"} आ गए हैं और फिर उसे वापस मांगते हैं। किसी को भी पैसे लौटाने से पहले अपने बैंक ऐप में असली बैलेंस ज़रूर जांचें।"
            }
            !isHoneytrapRomance && hasFakeTemplateHook && hasMobileNumberInText -> {
                explanation = "HIJACKED SMS TEMPLATE SCAM: Scammers reused a corporate template (\"Thank you for choosing us\") and inserted a personal mobile number (${detectedPhone ?: "callback number"}) to trick you into calling a fake support desk. Do not call this number."
                explanationHi = "नकली कंपनी SMS टेम्पलेट स्कैम: ठगों ने कंपनी के टेम्पलेट (\"Thank you for choosing us\") का गलत इस्तेमाल करके उसमें अपना निजी मोबाइल नंबर (${detectedPhone ?: "कॉल नंबर"}) डाल दिया है ताकि आप उन्हें कॉल करें। इस नंबर पर कॉल न करें।"
            }
            hasUrl || hasClickAction -> {
                val extLink = if (detectedLink != null) " ($detectedLink)" else ""
                explanation = "SUSPICIOUS PHISHING LINK DETECTED: This message tries to get you to open an unverified external link$extLink. Clicking unknown links in SMS/WhatsApp can lead to fake banking pages or malware downloads. Delete this message safely."
                explanationHi = "संदिग्ध फ़िशिंग लिंक: यह संदेश आपको एक अनजान बाहरी लिंक$extLink खोलने के लिए उकसा रहा है। ऐसे लिंक पर क्लिक करने से आपका डेटा चोरी हो सकता है। इस लिंक को न खोलें।"
            }
            threatLevel == "High Risk / Scam" || threatLevel == "Suspicious" -> {
                val flagsList = if (flags.isNotEmpty()) " (${flags.joinToString(", ")})" else ""
                explanation = "CAUTION — SUSPICIOUS FINANCIAL MESSAGE: We detected manipulation indicators$flagsList in this text. Do not share OTPs, PINs, or personal details, and verify any claim independently with the official organization."
                explanationHi = "सावधान — संदिग्ध संदेश: इस संदेश में धोखाधड़ी के संकेत$flagsList पाए गए हैं। किसी भी अनजान व्यक्ति के साथ अपना OTP, पिन या निजी जानकारी साझा न करें।"
            }
            isStandardBankAlert -> {
                explanation = "SAFE TRANSACTION RECORD: This is a standard bank/UPI notification${if (detectedAmount != null) " for $detectedAmount" else ""}. It contains no phishing links, no account suspension threats, and does not ask for your secret UPI PIN or OTP."
                explanationHi = "सुरक्षित बैंक/UPI सूचना: यह ${if (detectedAmount != null) "$detectedAmount के " else ""}लेन-देन का एक सामान्य और सुरक्षित बैंक अलर्ट है। इसमें कोई संदिग्ध लिंक, खाता बंद करने की धमकी या UPI पिन/OTP की मांग नहीं है।"
            }
            else -> {
                explanation = "NO FRAUD INDICATORS FOUND: We scanned this statement and found no phishing links, fake collect requests, urgency traps, or honeytrap lures. Still, never share your OTP or UPI PIN with anyone."
                explanationHi = "कोई धोखाधड़ी संकेत नहीं मिला: इस संदेश की जांच में कोई फ़िशिंग लिंक, नकली पेमेंट रिक्वेस्ट या ठगी का जाल नहीं पाया गया है। फिर भी सतर्क रहें और अपना OTP या UPI पिन किसी को न बताएं।"
            }
        }

        return FraudAssessmentResult(
            riskScore = score,
            threatLevel = threatLevel,
            redFlags = flags.distinct(),
            explanation = explanation,
            explanationHi = explanationHi
        )
    }
}
