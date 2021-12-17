package com.github.jonathanlalou.kafkabasic.batch;

import org.springframework.stereotype.Component;

@Component
public class GhardaiaHelper {
    public static final String INPUT_FOLDER = "./src/main/resources/text/";

    public Character hebrew2Latin(Character input) {
        return switch (input) {
            /*־ */
            case '\u05BE' -> null;
            /*׀ */
            case '\u05C0' -> null;
            /*׃ */
            case '\u05C3' -> null;
            /*׆ */
            case '\u05C6' -> 'N';    // TODO
            /*א */
            case '\u05D0' -> 'A';
            /*ב */
            case '\u05D1' -> 'B';
            /*ג */
            case '\u05D2' -> 'G';
            /*ד */
            case '\u05D3' -> 'D';
            /*ה */
            case '\u05D4' -> 'E';
            /*ו */
            case '\u05D5' -> 'U';
            /*ז */
            case '\u05D6' -> 'Z';
            /*ח */
            case '\u05D7' -> 'H';
            /*ט */
            case '\u05D8' -> 'T';
            /*י */
            case '\u05D9' -> 'I';
            /*ך */
            case '\u05DA' -> 'K';// endy
            /*כ */
            case '\u05DB' -> 'K';
            /*ל */
            case '\u05DC' -> 'L';
            /*ם */
            case '\u05DD' -> 'M';// endy
            /*מ */
            case '\u05DE' -> 'M';
            /*ן */
            case '\u05DF' -> 'N';// endy
            /*נ */
            case '\u05E0' -> 'N';
            /*ס */
            case '\u05E1' -> 'S';
            /*ע */
            case '\u05E2' -> 'O';
            /*ף */
            case '\u05E3' -> 'P';// endy
            /*פ */
            case '\u05E4' -> 'P';
            /*ץ */
            case '\u05E5' -> 'C';//endy
            /*צ */
            case '\u05E6' -> 'C';
            /*ק */
            case '\u05E7' -> 'Q';
            /*ר */
            case '\u05E8' -> 'R';
            /*ש */
            case '\u05E9' -> 'W';
            /*ת */
            case '\u05EA' -> 'X';
            /*װ */
            case '\u05F0' -> null;
            /*ױ */
            case '\u05F1' -> null;
            /*ײ */
            case '\u05F2' -> null;
            /*׳ */
            case '\u05F3' -> null;
            /*״  */
            case '\u05F4' -> null;
            default -> null;
        };
    }

    public Boolean isFinal(Character input) {
        return switch (input) {
            case '\u05DA', '\u05DD', '\u05DF', '\u05E3', '\u05E5' -> true;
            default -> false;
        };
    }
}
