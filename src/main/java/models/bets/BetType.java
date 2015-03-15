package main.java.models.bets;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum BetType {

    OW1, OW2, W1, W2, D, X1, W1W2, X2, F1, F2, G1, G2, GX, TG, TL, iT1G, iT1L, iT2G, iT2L, S;

    public boolean getReturnFundsStatus() {
        return false;
    }

    public String getFullName(BetType v) {

        String name = "";

        switch (v) {
            case W1:
                name = "First team win (without draw)";
                break;
            case W2:
                name = "Second team win (without draw)";
                break;
            case OW1:
                name = "First team win (with draw)";
                break;
            case OW2:
                name = "Second team win (with draw)";
                break;
            case D:
                name = "Draw";
                break;
            case X1:
                name = "First team win or draw";
                break;
            case X2:
                name = "Second team win or draw";
                break;
            case W1W2:
                name = "First team win or second team win, no draw";
                break;
            case F1:
                name = "Asian fora of first team equal";
                break;
            case F2:
                name = "Asian fora of second team equal";
                break;
            case G1:
                name = "Euro fora of first team equal";
                break;
            case G2:
                name = "Euro fora of second team equal";
                break;
            case TG:
                name = "Total of match is greater than";
                break;
            case TL:
                name = "Total of match lower than";
                break;
            case iT1G:
                name = "Invidual total of first team greater than";
                break;
            case iT1L:
                name = "Individual total of first team lower than";
                break;
            case iT2G:
                name = "Invidual total of second team greater than";
                break;
            case iT2L:
                name = "Individual total of second team lower than";
                break;
            case S:
                name = "Score of the match";
                break;
        }

        return name;
    }

    public static Map<String, List<BetType>> getStacks() {
        Map<String, List<BetType>> res = new HashMap();

        List<BetType> stack1 = new ArrayList();
        stack1.add(OW1);
        stack1.add(OW2);
        res.put("OW1-OW2", stack1);

        List<BetType> stack2 = new ArrayList();
        stack2.add(W1);
        stack2.add(D);
        stack2.add(W2);
        res.put("W1-D-W2", stack2);

        List<BetType> stack3 = new ArrayList();
        stack3.add(X1);
        stack3.add(W1W2);
        stack3.add(X2);
        res.put("X1-W1W2-X2", stack3);

        List<BetType> stack4 = new ArrayList();
        stack4.add(S);
        res.put("S-x-y", stack4);

        List<BetType> stack5 = new ArrayList();
        stack5.add(F1);
        stack5.add(F2);
        res.put("F1-F2", stack5);

        List<BetType> stack6 = new ArrayList();
        stack6.add(G1);
        stack6.add(GX);
        stack6.add(G2);
        res.put("G1-GX-G2", stack6);

        List<BetType> stack7 = new ArrayList();
        stack7.add(TG);
        stack7.add(TL);
        stack7.add(iT1G);
        stack7.add(iT1L);
        stack7.add(iT2G);
        stack7.add(iT2L);
        res.put("TG-TL-iT1G-iT1L-iT2G-iT2L", stack7);

        return res;
    }
}
