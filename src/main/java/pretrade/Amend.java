package pretrade;

public class Amend {


    private long timestamp;
    private int microsecond;
    private String transaction_type;
    private int volume;

    public Amend(long timestamp, int microsecond, String transaction_type, int volume) {
        this.timestamp = timestamp;
        this.microsecond = microsecond;
        this.transaction_type = transaction_type;
        this.volume = volume;
    }

    public int getMicrosecond() {
        return microsecond;
    }

    public void setMicrosecond(int microsecond) {
        this.microsecond = microsecond;
    }

    public String getTransaction_type() {
        return transaction_type;
    }

    public void setTransaction_type(String transaction_type) {
        this.transaction_type = transaction_type;
    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
