package org.apache.oozie.jobs.api.action;

public class Launcher {
    private final long memoryMb;
    private final long vCcores;
    private final String queue;
    private final String sharelib;
    private final String viewAcl;
    private final String modifyAcl;

    public Launcher(final long memoryMb,
                    final long vCcores,
                    final String queue,
                    final String sharelib,
                    final String viewAcl,
                    final String modifyAcl) {
        this.memoryMb = memoryMb;
        this.vCcores = vCcores;
        this.queue = queue;
        this.sharelib = sharelib;
        this.viewAcl = viewAcl;
        this.modifyAcl = modifyAcl;
    }

    public long getMemoryMb() {
        return memoryMb;
    }

    public long getVCores() {
        return vCcores;
    }

    public String getQueue() {
        return queue;
    }

    public String getSharelib() {
        return sharelib;
    }

    public String getViewAcl() {
        return viewAcl;
    }

    public String getModifyAcl() {
        return modifyAcl;
    }
}
