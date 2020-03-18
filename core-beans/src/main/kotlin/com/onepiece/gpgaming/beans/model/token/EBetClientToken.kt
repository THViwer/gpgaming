package com.onepiece.gpgaming.beans.model.token

data class EBetClientToken(

        val apiPath: String = "http://gpgaming88myr.ebet.im:8888/api",

        val gameUrl: String = "http://47.90.11.101:8882/?infoUrl=h5/59123c",

        val channelId: String = "517",

        val privateKey: String = "MIIBVAIBADANBgkqhkiG9w0BAQEFAASCAT4wggE6AgEAAkEAhMsqN/wRiQghV+6owkXqiZwQhJECE0CkiW0sUHh2HAV9X82kUkSaB29Vcsfo1T8OjVKBt8mqP7g71tdTsJo9/wIDAQABAkEAgMYHteyyEIGa3AurCWIFk9aMyhFYwtS/+iitfYejeX2MnEoK/XSy3fIl9HK6Q0IZzBR+UWwASfWn607O8iOhuQIhANKJCWNXakh5G9S6+EGG2P+MaEiym9It46dHG8nKoZeNAiEAoXhbSqdJUQii6fFLPOQcYTmhQGFF5KVI1CdcVjOTMrsCIFRfRrvhFF7m6hhetY7NE7mV81Tu/zND4K/w91nvMsMhAiARKlFSK1yA79EZ25vq+jkGIPFCfdTHTMbkPtbzFwKD9wIgTzows0YBF67eqawHDL+nImjZr4Lj/yJ/9pMxyQ3fLM4=",

        val publicKey: String = "MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAITLKjf8EYkIIVfuqMJF6omcEISRAhNApIltLFB4dhwFfV/NpFJEmgdvVXLH6NU/Do1SgbfJqj+4O9bXU7CaPf8CAwEAAQ==",

        val currency: String = "MYR"


) : ClientToken


