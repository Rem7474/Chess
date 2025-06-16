package model;

import java.util.List;

public class PiecePersonnaliseeJson {
    public String name;
    public int unicode;
    public String imagePath;
    public boolean isWhite;
    public int startRow;
    public int startCol;
    public String type;
    public boolean isKing;
    public List<List<Integer>> movePattern; // chaque pattern : [dL, dC, maxDist]
}
