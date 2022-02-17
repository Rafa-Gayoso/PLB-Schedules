package dao.interfaces;

import java.util.List;

public interface Dao<T>{
    List<T> getEntities();
    void insertEntity(T entity);
    void updateEntity(T entity);
    void deleteEntity(T entity);
    T getEntityById(int id);
}
