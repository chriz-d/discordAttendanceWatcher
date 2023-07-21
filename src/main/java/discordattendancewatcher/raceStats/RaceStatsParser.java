package discordattendancewatcher.raceStats;

public class RaceStatsParser {
   public static void parse(String url, String outputPath) {
      Process p;
      try {
         p = Runtime.getRuntime().exec(new String[] { ".venv/bin/python", "src/main/python/screenshot.py", url, outputPath});
         p.waitFor();
         p.destroy();
      } catch (Exception e) {
         e.printStackTrace();
      }
   }
}
