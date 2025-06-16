package model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class PieceLoader {
    public static List<PiecePersonnalisee> chargerDepuisJson(String chemin) {
        List<PiecePersonnalisee> pieces = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(chemin))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line.trim());
            }
            String json = sb.toString();
            // Supposons que le JSON est un tableau d'objets
            json = json.substring(1, json.length() - 1); // retire [ et ]
            String[] objets = json.split("\\},\\s*\\{");
            for (String obj : objets) {
                String o = obj;
                if (!o.startsWith("{")) o = "{" + o;
                if (!o.endsWith("}")) o = o + "}";
                String name = extraireString(o, "\"name\":");
                int unicode = extraireInt(o, "\"unicode\":");
                String imagePath = extraireString(o, "\"imagePath\":");
                boolean isWhite = extraireBool(o, "\"isWhite\":");
                int startRow = extraireInt(o, "\"startRow\":");
                int startCol = extraireInt(o, "\"startCol\":");
                String type = extraireString(o, "\"type\":");
                boolean isKing = extraireBool(o, "\"isKing\":");
                List<int[]> movePattern = extraireMovePattern(o);
                pieces.add(new PiecePersonnalisee(name, unicode, imagePath, startRow, startCol, isWhite, type, isKing, movePattern));
            }
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement des pièces personnalisées : " + e.getMessage());
        }
        return pieces;
    }

    private static String extraireString(String obj, String key) {
        int idx = obj.indexOf(key);
        if (idx == -1) return "";
        int start = obj.indexOf('"', idx + key.length()) + 1;
        int end = obj.indexOf('"', start);
        return obj.substring(start, end);
    }
    private static int extraireInt(String obj, String key) {
        int idx = obj.indexOf(key);
        if (idx == -1) return 0;
        int start = obj.indexOf(':', idx) + 1;
        int end = obj.indexOf(',', start);
        if (end == -1) end = obj.indexOf('}', start);
        return Integer.parseInt(obj.substring(start, end).replaceAll("[^0-9-]", "").trim());
    }
    private static boolean extraireBool(String obj, String key) {
        int idx = obj.indexOf(key);
        if (idx == -1) return false;
        int start = obj.indexOf(':', idx) + 1;
        int end = obj.indexOf(',', start);
        if (end == -1) end = obj.indexOf('}', start);
        return obj.substring(start, end).replaceAll("[^a-zA-Z]", "").trim().equalsIgnoreCase("true");
    }
    private static List<int[]> extraireMovePattern(String obj) {
        List<int[]> pattern = new ArrayList<>();
        int idx = obj.indexOf("\"movePattern\":");
        if (idx == -1) return pattern;
        int start = obj.indexOf('[', idx);
        int end = obj.indexOf(']', start);
        String arr = obj.substring(start + 1, end);
        String[] couples = arr.split("\\],\\s*\\[|\\], \\[");
        for (String couple : couples) {
            String[] nums = couple.replace("[", "").replace("]", "").split(",");
            if (nums.length == 4) {
                try {
                    int a = Integer.parseInt(nums[0].trim());
                    int b = Integer.parseInt(nums[1].trim());
                    int c = Integer.parseInt(nums[2].trim());
                    int d = (nums[3].trim().equalsIgnoreCase("true") || nums[3].trim().equals("1")) ? 1 : 0;
                    pattern.add(new int[]{a, b, c, d});
                } catch (Exception ignored) {}
            } else if (nums.length == 3) {
                try {
                    int a = Integer.parseInt(nums[0].trim());
                    int b = Integer.parseInt(nums[1].trim());
                    int c = Integer.parseInt(nums[2].trim());
                    pattern.add(new int[]{a, b, c});
                } catch (Exception ignored) {}
            } else if (nums.length == 2) {
                try {
                    int a = Integer.parseInt(nums[0].trim());
                    int b = Integer.parseInt(nums[1].trim());
                    pattern.add(new int[]{a, b, 1}); // compatibilité ancienne version
                } catch (Exception ignored) {}
            }
        }
        return pattern;
    }
}
