package fitnessapp.fitnesspartner.controller;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

public class SfcPacket {
    private Map<String, String> data;
    private String sfp;  // Service Function Path
    private boolean authenticated;

    public SfcPacket(Map<String, String> data) {
        this.data = data;
    }

    public Map<String, String> getData() {
        return data;
    }

    public void setData(Map<String, String> data) {
        this.data = data;
    }

    public String getSfp() {
        return sfp;
    }

    public void setSfp(String sfp) {
        this.sfp = sfp;
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    public void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
    }
}
