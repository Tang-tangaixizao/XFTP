package Entity;

public class LinuxFileInfo {
    private String linuxFileName;
    private String linuxFileType;
    private String linuxFileSize;
    private String linuxFileDate;
    private String linuxFileAttr;
    private String linuxFileOwner;

    public void setLinuxFileAttr(String linuxFileAttr) {
        this.linuxFileAttr = linuxFileAttr;
    }

    public String getLinuxFileAttr() {
        return linuxFileAttr;
    }

    public void setLinuxFileName(String linuxFileName) {
        this.linuxFileName = linuxFileName;
    }

    public void setLinuxFileType(String linuxFileType) {
        this.linuxFileType = linuxFileType;
    }

    public void setLinuxFileSize(String linuxFileSize) {
        this.linuxFileSize = linuxFileSize;
    }

    public void setLinuxFileDate(String linuxFileDate) {
        this.linuxFileDate = linuxFileDate;
    }

    public void setLinuxFileOwner(String linuxFileOwner) {
        this.linuxFileOwner = linuxFileOwner;
    }

    public String getLinuxFileName() {
        return linuxFileName;
    }

    public String getLinuxFileType() {
        return linuxFileType;
    }

    public String getLinuxFileSize() {
        return linuxFileSize;
    }

    public String getLinuxFileDate() {
        return linuxFileDate;
    }

    public String getLinuxFileOwner() {
        return linuxFileOwner;
    }

}
