package model;

import java.util.ArrayList;

public class History {

    private ArrayList<GameRecord> recordList;

    public History() {
        this.recordList = new ArrayList<>();
    }

    public void addRecord(GameRecord record) {
        this.recordList.add(record);
    }

    // Manos de un usuario, de la mas reciente a la mas antigua
    public ArrayList<GameRecord> getRecordsByUser(String username) {
        ArrayList<GameRecord> records = new ArrayList<>();
        for (int i = this.recordList.size() - 1; i >= 0; i--) {
            if (this.recordList.get(i).getUsername().equalsIgnoreCase(username)) {
                records.add(this.recordList.get(i));
            }
        }
        return records;
    }

    public ArrayList<GameRecord> getRecordList() {
        return this.recordList;
    }
}
