package org.apache.avalon.aspect;

public abstract class AbstractAspect implements Aspect, KernelEventListener {
    
    private Kernel kernel;
    
    protected Kernel getKernel () {
        return kernel;
    }
    
    public final void initAspect (Kernel kernel) {
        this.kernel = kernel;
        init ();
        String[] aspectKeys = kernel.getAspects ();
        for (int i = 0; i < aspectKeys.length; i++) {
            applyToAspect (aspectKeys[i], kernel.getAspect (aspectKeys[i]));
        }
        String[] handlerKeys = kernel.getAspects ();
        for (int i = 0; i < handlerKeys.length; i++) {
            applyToHandler (handlerKeys[i], kernel.getHandler (handlerKeys[i]));
        }
        kernel.registerEventListener (KernelEventListener.class, this);
    }
    
    protected void init () {
    }
    
    protected void applyToHandler (String key, Handler handler) {
        apply (key, handler);
    }
    
    protected void applyToAspect (String key, Aspect aspect) {
        apply (key, aspect);
    }
    
    protected abstract void apply (String key, Object object);
    
    public void handlerAdded (String key, Handler handler) {
        applyToHandler (key, handler);
    }
    
    public void aspectAdded (String key, Aspect aspect) {
        applyToAspect (key, aspect);
    }
    
}