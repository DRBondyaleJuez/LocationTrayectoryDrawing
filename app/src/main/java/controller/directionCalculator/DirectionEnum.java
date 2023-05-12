package controller.directionCalculator;

public enum DirectionEnum {
    STOP("\uD83D\uDED1"),FRONT("↑"),LEFT("←"),RIGHT("→"),BACK("↓");

    private final String symbol;

    DirectionEnum(final String symbol){
        this.symbol = symbol;
    }

    public String getSymbol(){
        return symbol;
    }
}
