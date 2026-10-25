package repositories;

import java.util.ArrayList;
import model.GameRecord;
import model.History;
import persistence.JsonRepository;

public class HistoryRepository {


    private JsonRepository<History> repository;
    private History myHistory;

    public HistoryRepository() {
        this.repository = new JsonRepository<>("src/data/history.json", History.class);
        this.myHistory = this.repository.readFile();
        if (this.myHistory == null) {
            this.myHistory = new History();
        }
    }

    public void save() {
        this.repository.writeFile(this.myHistory);
    }

    public void addRecord(GameRecord record) {
        this.myHistory.addRecord(record);
        this.save();
    }

    public ArrayList<GameRecord> getRecordsByUser(String username) {
        return this.myHistory.getRecordsByUser(username);
    }
}
