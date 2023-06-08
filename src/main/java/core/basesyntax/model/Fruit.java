package core.basesyntax.model;

public enum Fruit {
    APPLE("apple"),
    BANANA("banana"),
    ORANGE("orange"),
    LEMON("lemon");

    private String fruitName;

    Fruit(String name) {
        this.fruitName = name;
    }

    public String getFruitName() {
        return fruitName;
    }

    public static Fruit getFruitByName(String fruitName) {
        for (Fruit fn : Fruit.values()) {
            if (fn.fruitName == fruitName) {
                return fn;
            }
        }
        throw new RuntimeException("Can't find Fruit by name " + fruitName);
    }
}
