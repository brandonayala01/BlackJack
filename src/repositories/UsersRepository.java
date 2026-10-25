package repositories;

import model.User;
import model.Users;
import persistence.JsonRepository;

public class UsersRepository {

    // Creamos el repositorio
    private JsonRepository<Users> repository;
    private Users myUsers;

    public UsersRepository() {
        this.repository = new JsonRepository<>("src/data/users.json", Users.class);
        this.myUsers = this.repository.readFile();
        if (this.myUsers == null) {
            this.myUsers = new Users();
        }
    }

    public void save() {
        this.repository.writeFile(this.myUsers);
    }

    public Users getMyUsers() {
        return this.myUsers;
    }

    public void addUser(User user) {
        this.myUsers.addUser(user);
        this.save();
    }
}
