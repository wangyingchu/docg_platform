package com.viewfunction.docg.dataCompute.applicationCapacity.dataCompute.commandProcessor;

import com.beust.jcommander.Parameter;

import java.util.ArrayList;
import java.util.List;

public class UnitdsCommandOptions {

    @Parameter
    private List<String> parameters = new ArrayList<>();

    @Parameter(names = "-fn", description = "Data Slice name filter(name start with filter)")
    private String startSliceName;

    public String getStartSliceName() {
        return startSliceName;
    }
}