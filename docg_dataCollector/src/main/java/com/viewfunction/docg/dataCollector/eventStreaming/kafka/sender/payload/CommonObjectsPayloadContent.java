package com.viewfunction.docg.dataCollector.eventStreaming.kafka.sender.payload;

import java.nio.ByteBuffer;

public class CommonObjectsPayloadContent {

    private CommonObjectsPayloadContentType includingContent;
    private boolean textContentEncoded;
    private String textContentEncodeAlgorithm;
    private ByteBuffer binaryContent;
    private String textContent;

    public boolean getTextContentEncoded() {
        return textContentEncoded;
    }

    public void setTextContentEncoded(boolean textContentEncoded) {
        this.textContentEncoded = textContentEncoded;
    }

    public String getTextContentEncodeAlgorithm() {
        return textContentEncodeAlgorithm;
    }

    public void setTextContentEncodeAlgorithm(String textContentEncodeAlgorithm) {
        this.textContentEncodeAlgorithm = textContentEncodeAlgorithm;
    }

    public ByteBuffer getBinaryContent() {
        return binaryContent;
    }

    public void setBinaryContent(ByteBuffer binaryContent) {
        this.binaryContent = binaryContent;
    }

    public String getTextContent() {
        return textContent;
    }

    public void setTextContent(String textContent) {
        this.textContent = textContent;
    }

    public CommonObjectsPayloadContentType getIncludingContent() {
        return includingContent;
    }

    public void setIncludingContent(CommonObjectsPayloadContentType includingContent) {
        this.includingContent = includingContent;
    }
}
