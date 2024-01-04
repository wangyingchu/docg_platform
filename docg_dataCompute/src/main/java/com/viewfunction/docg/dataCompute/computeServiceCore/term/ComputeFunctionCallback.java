package com.viewfunction.docg.dataCompute.computeServiceCore.term;

import java.io.Serializable;

public interface ComputeFunctionCallback extends Serializable {

    public void onPrepare();

    public void onFinish();

    public void onRun();
}
