package com.onepiece.gpgaming.core.email

import java.io.ByteArrayInputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import javax.activation.DataSource


class HTMLDataSource(private val html: String) : DataSource {

    override fun getInputStream(): InputStream {
        return ByteArrayInputStream(html.toByteArray());
    }

    override fun getOutputStream(): OutputStream {
        throw IOException("This DataHandler cannot write HTML");

    }

    override fun getContentType(): String {
        return "text/html"
    }

    override fun getName(): String {
        return "HTMLDataSource"
    }
}
