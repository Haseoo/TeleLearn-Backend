package kielce.tu.weaii.telelearn;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

@EqualsAndHashCode
@ToString
public class MultipartFileMock implements MultipartFile {
    public static final String MOCK_NAME = "mock";
    public static final String ORIGINAL_MOCK_NAME = "originalMockName";
    public static final String CONTENT_TYPE_MOCK = "contentTypeMock";

    private byte[] content;

    public MultipartFileMock(byte[] content) {
        this.content = content;
    }

    @Override
    public String getName() {
        return MOCK_NAME;
    }

    @Override
    public String getOriginalFilename() {
        return ORIGINAL_MOCK_NAME;
    }

    @Override
    public String getContentType() {
        return CONTENT_TYPE_MOCK;
    }

    @Override
    public boolean isEmpty() {
        return content != null && content.length != 0;
    }

    @Override
    public long getSize() {
        return content.length;
    }

    @Override
    public byte[] getBytes() throws IOException {
        return content;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return new ByteArrayInputStream(content);
    }

    @Override
    public void transferTo(File file) throws IOException, IllegalStateException {

    }
}
