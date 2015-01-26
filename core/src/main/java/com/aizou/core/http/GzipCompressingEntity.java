package com.aizou.core.http;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.entity.HttpEntityWrapper;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;

import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Created by rjm on 2015/1/26.
 */
public class GzipCompressingEntity extends HttpEntityWrapper {

    private static final String GZIP_CODEC = "gzip";
//	private static final int DEFAULT_BUFFER_SIZE = 1024; // this is also the maximum chunk size

    public GzipCompressingEntity(final HttpEntity entity) {
        super(entity);
    }

    @Override
    public Header getContentEncoding() {
        return new BasicHeader(HTTP.CONTENT_ENCODING, GZIP_CODEC);
    }

    @Override
    public long getContentLength() {
        return -1;
    }

    @Override
    public boolean isChunked() {
        // force content chunking
        return true;
    }

    @Override
    public void writeTo(final OutputStream outstream) throws IOException {
        if (outstream == null) {
            throw new IllegalArgumentException("Output stream may not be null");
        }
        GZIPOutputStream gzip = new GZIPOutputStream(outstream);
        wrappedEntity.writeTo(gzip);
        gzip.finish();
    }
}
