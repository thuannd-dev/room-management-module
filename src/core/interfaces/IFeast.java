package core.interfaces;

import core.entities.Feast;
import java.util.List;

public interface IFeast {

    public void loadData() throws Exception;

    public List<Feast> getFeasts();

    public Feast getFeastById(String id) throws Exception;

}
