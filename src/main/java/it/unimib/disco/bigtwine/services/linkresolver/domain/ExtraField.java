package it.unimib.disco.bigtwine.services.linkresolver.domain;

public class ExtraField {
    private String valuePath;
    private String saveAs;

    public ExtraField() {
    }

    public String getValuePath() {
        return valuePath;
    }

    public ExtraField setValuePath(String valuePath) {
        this.valuePath = valuePath;
        return this;
    }

    public String getSaveAs() {
        return saveAs;
    }

    public ExtraField setSaveAs(String saveAs) {
        this.saveAs = saveAs;
        return this;
    }
}
