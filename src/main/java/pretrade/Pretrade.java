package pretrade;

import java.util.ArrayList;

public class Pretrade {

    private long order_number;
    private long timestamp;
    private int microsecond;
    private float price;

    public ArrayList<Amend> getNextValues() {
        return nextValues;
    }

    public void addNextValue(Amend v) {
        nextValues.add(v);
    }

    private ArrayList<Amend> nextValues;

    public Pretrade() {
        nextValues = new ArrayList<>();
    }

    private int next_microsecond;
    public int getMicrosecond() {
        return microsecond;
    }

    public void setMicrosecond(int microsecond) {
        this.microsecond = microsecond;
    }

    private int volume;

    public int getNext_microsecond() {
        return next_microsecond;
    }

    public void setNext_microsecond(int next_microsecond) {
        this.next_microsecond = next_microsecond;
    }

    private String side;
    private String transaction_type;

    private long algo_id;

    private long next_timestamp;
    private String next_transaction_type;

    public long getOrder_number() {
        return order_number;
    }

    public void setOrder_number(long order_number) {
        this.order_number = order_number;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }

    public String getSide() {
        return side;
    }

    public void setSide(String side) {
        this.side = side;
    }

    public String getTransaction_type() {
        return transaction_type;
    }

    public void setTransaction_type(String transaction_type) {
        this.transaction_type = transaction_type;
    }

    public long getAlgo_id() {
        return algo_id;
    }

    public void setAlgo_id(long algo_id) {
        this.algo_id = algo_id;
    }

    public long getNext_timestamp() {
        return next_timestamp;
    }

    public void setNext_timestamp(long next_timestamp) {
        this.next_timestamp = next_timestamp;
    }

    public String getNext_transaction_type() {
        return next_transaction_type;
    }

    public void setNext_transaction_type(String next_transaction_type) {
        this.next_transaction_type = next_transaction_type;
    }

    public int getNext_volume() {
        return next_volume;
    }

    public void setNext_volume(int next_volume) {
        this.next_volume = next_volume;
    }

    private int next_volume;

}
