package Ir.optimization;

import Ir.IRModule;

public interface Pass {
    String getName();

    interface IRPass extends Pass {
        void run(IRModule m);
    }
}
