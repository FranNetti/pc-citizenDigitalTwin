package it.unibo.citizenDigitalTwin.util;

public final class BackHelper {

    private final static BackHelper INSTANCE = new BackHelper();

    public static BackHelper getInstance() {
        return INSTANCE;
    }

    public interface BackListener{
        boolean onBackClick();
    }

    private BackListener listener;

    private BackHelper(){}

    public void setListener(final BackListener listener) {
        this.listener = listener;
    }

    public void clearListener(){
        this.listener = null;
    }

    public BackListener getListener() {
        return listener;
    }
}
