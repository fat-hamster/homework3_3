package Client;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class LocalHistory {
    FileWriter fileWriter;
    ReverseLineReader lineReader;
    File file;
    private final String FILENAME = "history.txt";

    public LocalHistory() {
        file = new File(FILENAME);
        if(!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                System.out.println("Невозможно создать файл: " + file.getAbsolutePath());
            }
        }
        try {
            fileWriter = new FileWriter(file, true);
            Date dateNow = new Date();
            SimpleDateFormat formatForDateNow = new SimpleDateFormat("E dd.MM.yyyy 'и время' hh:mm:ss a zzz");
            fileWriter.write(formatForDateNow.format(dateNow) + '\n');
            fileWriter.flush();
            lineReader = new ReverseLineReader(file, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeHistory(String str) {
        try {
            fileWriter.write(str + '\n');
            fileWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String readHistory(int rows) {
        List<String> history = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        String str;
        for (int i = 0; i < rows; i++) {
            try {
                if((str = lineReader.readLine()) == null) {
                    break;
                }
                history.add(str + '\n');
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(history.isEmpty()) {
            return "";
        }
        Collections.reverse(history);
        for (String s : history) {
            sb.append(s);
        }
        return sb.toString();
    }

    public void close() {
        try {
            fileWriter.flush();
            fileWriter.close();
            lineReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}