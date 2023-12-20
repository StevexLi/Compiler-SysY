package Ir.optimization;

import Ir.IRModule;

import java.util.ArrayList;
import java.util.List;

import static Ir.optimization.OptimizationLevel.BranchOptimization;

public class PassModule {
    private static final PassModule INSTANCE = new PassModule();

    private final List<Pass.IRPass> irPasses = new ArrayList<>();

    public static PassModule getInstance() {
        return INSTANCE;
    }

    public PassModule() {
//        if (Config.GVNGCM) {
//            irPasses.add(new GVNGCM());
//        }
        if (BranchOptimization) {
            irPasses.add(new BranchOptimization());
        }
    }
    public void runIRPasses() {
        irPasses.forEach(p -> p.run(IRModule.getInstance()));
    }
}
