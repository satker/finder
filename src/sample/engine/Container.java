package sample.engine;
/*
Хранилище результатов
 */
public class Container{
    private volatile String name = "";

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    private volatile int id = 0;

    public Container(int id, String name) {
        this.id = id;
        this.name = name;
    }
    public Container(){
    }
}
