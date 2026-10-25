package model;

import java.util.ArrayList;

public class Users {

    private ArrayList<User> userList;

    public Users() {
        this.userList = new ArrayList<>();
    }

    public void addUser(User user) {
        int newId = 1;
        if (!this.userList.isEmpty()) {
            newId = this.userList.get(this.userList.size() - 1).getId() + 1;
        }
        user.setId(newId);
        this.userList.add(user);
    }

    public User findByName(String username) {
        for (int i = 0; i < this.userList.size(); i++) {
            if (this.userList.get(i).getUsername().equalsIgnoreCase(username)) {
                return this.userList.get(i);
            }
        }
        return null;
    }

    public ArrayList<User> getUserList() {
        return this.userList;
    }
}
