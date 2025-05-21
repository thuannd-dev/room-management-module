package data;

import common.env.Constants;
import core.entities.Feast;
import core.interfaces.IFeast;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class FeastDAO implements IFeast {

    private final List<Feast> FEAST_LIST = new ArrayList<>();
    private final FileManager FILE_MANAGER;

    public FeastDAO(String fileName) throws Exception {
        this.FILE_MANAGER = new FileManager(fileName);
        loadData();
    }

    @Override
    public final void loadData() throws Exception {
        String feastCode, feastName, ingredients;
        double price;
        try {
            FEAST_LIST.clear();
            List<String> feastData = FILE_MANAGER.readDataFromFile();
            feastData.remove(0);
            for (String e : feastData) {
                List<String> feastS = Arrays.asList(e.split(","));
                feastCode = feastS.get(0).trim();
                feastName = feastS.get(1).trim();
                price = Double.parseDouble(feastS.get(2).trim());
                ingredients = feastS.get(3).trim();
                Feast feast = new Feast(feastCode, feastName, price, ingredients);
                FEAST_LIST.add(feast);
                Constants.FEAST_CODE_LIST.add(feastCode);
                if (FEAST_LIST.isEmpty()) {
                    throw new Exception("Customer list is empty.");
                }
            }
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    @Override
    public List<Feast> getFeasts() {
        Collections.sort(
                FEAST_LIST,
                (e1, e2) -> Double.compare(e1.getPrice(), e2.getPrice())
        );
        return FEAST_LIST;
    }

    @Override
    public Feast getFeastById(String id) throws Exception {
        if (FEAST_LIST.isEmpty()) {
            getFeasts();
        }
        Feast feast = FEAST_LIST
                .stream()
                .filter(
                        e -> e.getFeastCode().equals(id)
                )
                .findFirst()
                .orElse(null);
        return feast;
    }

}
