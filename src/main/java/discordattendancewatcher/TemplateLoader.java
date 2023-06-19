package discordattendancewatcher;

import java.io.FileReader;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileNotFoundException;

public class TemplateLoader {
    
    public static String template;
    
    public static String loadTemplate(String path) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(path));
            template = new String();
            String line;
            while((line = reader.readLine()) != null) {
                template += line + "\n";
            }
            reader.close();
            return template;
        } catch (FileNotFoundException e) {
            System.out.println("Template not found.");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
    
}
