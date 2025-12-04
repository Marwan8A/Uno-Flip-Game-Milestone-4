package UNO;

import javax.swing.*;
import java.awt.*;
import java.io.Serializable;

/**
 * Represents a single Uno card, which has a color, type, and possibly a value.
 * numeric cards have a value (1 to 9), while action and wild cards do not.
 *
 * @author Noor Karabala, Abdullah Hasan, Marwan Alakhras, Yousif Al-Satari
 * @version 4.0 -   Milestone 4
 */

public class Card implements Serializable {

    private final CardColor lightColor;
    private final CardType lightType;
    private final CardColor darkColor;
    private final CardType darkType;
    private final int lightValue;
    private final int darkValue;
    private final int lightScore;
    private final int darkScore;
    private CardSide side;

    /**
     * Creates a UNO Flip card with light and dark side properties.
     *
     * @param lightColor the color of the light side
     * @param lightType  the type of the light side
     * @param darkColor  the color of the dark side
     * @param darkType   the type of the dark side
     * @param lightValue the numeric value on the light side, or -1 if not a number card
     * @param darkValue  the numeric value on the dark side, or -1 if not a number card
     */
    public Card(CardColor lightColor, CardType lightType, CardColor darkColor,
                CardType darkType, int lightValue, int darkValue) {
        this.lightColor = lightColor;
        this.lightType = lightType;
        this.darkColor = darkColor;
        this.darkType = darkType;
        this.lightValue = lightValue;
        this.darkValue = darkValue;
        this.side = CardSide.LIGHT;

        this.lightScore = (this.lightType == CardType.NUMBER) ? lightValue : lightType.getScore();
        this.darkScore = (this.darkType == CardType.NUMBER) ? darkValue : darkType.getScore();

    }

    /**
     * Gets the color of the currently active side.
     *
     * @return the active side color
     */
    public CardColor getColor() { return (this.side == CardSide.LIGHT) ? this.lightColor : this.darkColor; }

    /**
     * Gets the type of the currently active side.
     *
     * @return the active side type
     */
    public CardType getType() { return (this.side == CardSide.LIGHT) ? this.lightType : this.darkType; }

    /**
     * Gets the numeric value of the currently active side.
     *
     * @return the active side value, or -1 if not a number card
     */
    public int getValue() {return (this.side == CardSide.LIGHT) ? this.lightValue : this.darkValue; }

    /**
     * Gets the score value of the currently active side.
     *
     * @return the active side score
     */
    public int getScore() { return (this.side == CardSide.LIGHT) ? this.lightScore : this.darkScore; }

    /**
     * Returns the card as a string.
     * Example: "RED 5" or "WILD_DRAW_FOUR".
     *
     * @return the card description as text
     */
    @Override
    public String toString() {
        CardColor color = getColor();
        CardType type = getType();
        int value = getValue();

        return (type == CardType.NUMBER)
                ? color + " " + value
                : (type == CardType.WILD || type == CardType.WILD_DRAW_TWO)
                ? type.toString()
                : color + " " + type;
    }

    /**
     * Changes values to Strings.
     *
     * @param num the number to change to String.
     * @return String representation of num.
     */
    private String numberToWord(int num) {
        return switch (num) {
            case 1 -> "one";
            case 2 -> "two";
            case 3 -> "three";
            case 4 -> "four";
            case 5 -> "five";
            case 6 -> "six";
            case 7 -> "seven";
            case 8 -> "eight";
            case 9 -> "nine";
            default -> throw new IllegalArgumentException("Invalid number card: " + num);
        };
    }

    /**
     * Attaches images to cards.
     *
     * @return the image of the card
     */
    public ImageIcon getImageIcon() {
        String path;

        if (side == CardSide.LIGHT) {

            switch (lightType) {

                case NUMBER -> {
                    String word = numberToWord(lightValue);
                    path = "/UNO/unoCards/LightSideCards/" + word + "/" + lightColor.toString().toLowerCase() + ".png";
                }

                case SKIP -> {
                    path = "/UNO/unoCards/LightSideCards/skip/" + lightColor.toString().toLowerCase() + ".png";
                }

                case REVERSE -> {
                    path = "/UNO/unoCards/LightSideCards/reverse/" + lightColor.toString().toLowerCase() + ".png";
                }

                case DRAW_ONE -> {
                    path = "/UNO/unoCards/LightSideCards/draw_one/" + lightColor.toString().toLowerCase() + ".png";
                }

                case FLIP -> {
                    path = "/UNO/unoCards/LightSideCards/flip/" + lightColor.toString().toLowerCase() + ".png";
                }

                case WILD -> {
                    path = "/UNO/unoCards/LightSideCards/wild/wild.png";
                }

                case WILD_DRAW_TWO -> {
                    path = "/UNO/unoCards/LightSideCards/wild_draw_two_cards/wild_draw_two.png";
                }

                default -> throw new IllegalStateException("Unexpected type: " + lightType);
            }

        }
        else { // DARK SIDE

            switch (darkType) {

                case NUMBER -> {
                    String word = numberToWord(darkValue);
                    path = "/UNO/unoCards/DarkSideCards/" + word + "/" + darkColor.toString().toLowerCase() + ".png";
                }

                case SKIP_EVERYONE -> {
                    path = "/UNO/unoCards/DarkSideCards/skip_everyone/" + darkColor.toString().toLowerCase() + ".png";
                }

                case REVERSE -> {
                    path = "/UNO/unoCards/DarkSideCards/reverse/" + darkColor.toString().toLowerCase() + ".png";
                }

                case DRAW_FIVE -> {
                    path = "/UNO/unoCards/DarkSideCards/draw_five/" + darkColor.toString().toLowerCase() + ".png";
                }

                case FLIP -> {
                    path = "/UNO/unoCards/DarkSideCards/flip/" + darkColor.toString().toLowerCase() + ".png";
                }

                case WILD -> {
                    path = "/UNO/unoCards/DarkSideCards/wild/wild.png";
                }

                case WILD_DRAW_COLOR -> {
                    path = "/UNO/unoCards/DarkSideCards/wild_draw_color/wild_draw_color.png";
                }

                default -> throw new IllegalStateException("Unexpected type: " + darkType);
            }
        }

        java.net.URL imgURL = getClass().getResource(path);

        if (imgURL == null) {
            System.err.println("ERROR: Could not find image: " + path);
            return null;
        }

        ImageIcon icon = new ImageIcon(imgURL);
        Image scaled = icon.getImage().getScaledInstance(120, 180, Image.SCALE_SMOOTH);
        return new ImageIcon(scaled);
    }

    /**
     * Flips the card between its light and dark sides.
     */
    public void flipCard() {
        this.side = (this.side == CardSide.LIGHT) ? CardSide.DARK : CardSide.LIGHT;
    }

    /**
     * Gets the currently active side of this card.
     *
     * @return the active card side
     */
    public CardSide getSide() {
        return this.side;
    }

    /**
     * Converts this card to an XML representation.
     *
     * @param indent the indentation to apply to each XML line
     * @return a string containing this card encoded as XML
     */
    public String toXML(String indent) {
        StringBuilder sb = new StringBuilder();

        sb.append(indent).append("<card>\n");

        sb.append(indent).append("  <side>").append(this.getSide()).append("</side>\n");

        sb.append(indent).append("  <lightColor>").append(lightColor).append("</lightColor>\n");
        sb.append(indent).append("  <lightType>").append(lightType).append("</lightType>\n");
        sb.append(indent).append("  <lightValue>").append(lightValue).append("</lightValue>\n");

        sb.append(indent).append("  <darkColor>").append(darkColor).append("</darkColor>\n");
        sb.append(indent).append("  <darkType>").append(darkType).append("</darkType>\n");
        sb.append(indent).append("  <darkValue>").append(darkValue).append("</darkValue>\n");

        sb.append(indent).append("</card>\n");

        return sb.toString();
    }
}