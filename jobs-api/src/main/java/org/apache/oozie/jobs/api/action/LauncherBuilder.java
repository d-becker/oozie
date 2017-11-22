package org.apache.oozie.jobs.api.action;

import org.apache.oozie.jobs.api.ModifyOnce;

public class LauncherBuilder implements Builder<Launcher> {
    private final ModifyOnce<Long> memoryMb;
    private final ModifyOnce<Long> vCores;
    private final ModifyOnce<String> queue;
    private final ModifyOnce<String> sharelib;
    private final ModifyOnce<String> viewAcl;
    private final ModifyOnce<String> modifyAcl;

    public LauncherBuilder() {
        this.memoryMb = new ModifyOnce<>();
        this.vCores = new ModifyOnce<>();
        this.queue = new ModifyOnce<>();
        this.sharelib = new ModifyOnce<>();
        this.viewAcl = new ModifyOnce<>();
        this.modifyAcl = new ModifyOnce<>();
    }

    @Override
    public Launcher build() {
        return new Launcher(memoryMb.get(),
                vCores.get(),
                queue.get(),
                sharelib.get(),
                viewAcl.get(),
                modifyAcl.get());
    }

    public LauncherBuilder withMemoryMb(final long memoryMb) {
        this.memoryMb.set(memoryMb);
        return this;
    }

    public LauncherBuilder withVCores(final long vCores) {
        this.vCores.set(vCores);
        return this;
    }

    public LauncherBuilder withQueue(final String queue) {
        this.queue.set(queue);
        return this;
    }

    public LauncherBuilder withSharelib(final String sharelib) {
        this.sharelib.set(sharelib);
        return this;
    }

    public LauncherBuilder withViewAcl(final String viewAcl) {
        this.viewAcl.set(viewAcl);
        return this;
    }

    public LauncherBuilder withModifyAcl(final String modifyAcl) {
        this.modifyAcl.set(modifyAcl);
        return this;
    }
}
