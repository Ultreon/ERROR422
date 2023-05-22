package me.qboi.mods.err422.event;

public class DamageWorldEvent extends Event {
    public DamageWorldEvent(EventHandler handler) {
        super(15, 25, handler);
    }

    @Override
    @SuppressWarnings("CallToThreadRun")
    public boolean trigger() {
        final WorldDamageEventThread thread = new WorldDamageEventThread(this.handler);
        thread.run();  // Dumb developer forgot to call Thread.start() instead of Thread.run()
        return true;
    }
}
