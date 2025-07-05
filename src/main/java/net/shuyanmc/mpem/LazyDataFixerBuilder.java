package net.shuyanmc.mpem;

import com.mojang.datafixers.DataFixerBuilder;
import com.mojang.datafixers.DataFixerUpper;

import java.util.concurrent.Executor;

public class LazyDataFixerBuilder extends DataFixerBuilder implements LazyDataFixerBuilderl {
    private static final Executor NO_OP_EXECUTOR = command -> {};

    public LazyDataFixerBuilder(int dataVersion) {
        super(dataVersion);
    }

    @Override
    public void build(Executor executor) {
        // 替换 Executor 逻辑，但不直接重写 build()
        DataFixerUpper build;
        build(NO_OP_EXECUTOR);
        return ;
    }
}