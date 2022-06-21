package ru.ignat.lw;

public class MostPopularVoucher {
    int id,count;

    public MostPopularVoucher(int id, int count) {
        this.id = id;
        this.count = count;
    }

    public int getId() {
        return id;
    }

    public int getCount() {
        return count;
    }
}
