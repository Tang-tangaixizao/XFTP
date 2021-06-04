package Entity;

public class FileInfo {
    private String fileName;
    private String fileDate;
    private String fileSize;

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setFileDate(String fileDate) {
        this.fileDate = fileDate;
    }

    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }

    public String getFileName() {
        return fileName;
    }

    public String getFileDate() {
        return fileDate;
    }

    public String getFileSize() {
        return fileSize;
    }
}
