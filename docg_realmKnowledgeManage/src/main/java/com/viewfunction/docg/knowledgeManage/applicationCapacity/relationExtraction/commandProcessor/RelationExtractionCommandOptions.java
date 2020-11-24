package com.viewfunction.docg.knowledgeManage.applicationCapacity.relationExtraction.commandProcessor;

import com.beust.jcommander.Parameter;

import java.util.ArrayList;
import java.util.List;

public class RelationExtractionCommandOptions {

    @Parameter
    private List<String> parameters = new ArrayList<>();

    @Parameter(names = "-multimode", description = "Allow run multi relation extractor instance")
    private boolean multimode = false;

    @Parameter(names = "-linker", description = "relation linker id")
    private String linkerId;

    @Parameter(names = "-extraction", description = "relation extraction logic id")
    private String extractionId;

    public boolean isMultimode() {
        return multimode;
    }

    public String getLinkerId() {
        return linkerId;
    }

    public String getExtractionId() {
        return extractionId;
    }
}
