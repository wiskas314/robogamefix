package gui.state;

import log.Logger;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * Мэнеджер сохранения и восстановления состояний
 */
public class ApplicationStateManager {
    private final File configFile;
    private final List<RegisteredComponent> components =new ArrayList<>();

    /**
     * обертка для хранения пары префикс компонент
     */
    private class RegisteredComponent{
        final String prefix;
        final Stateful component;

        /**
         * создает новую запись компонента
         */
        RegisteredComponent(String prefix, Stateful component){
            this.prefix=prefix;
            this.component=component;
        }
    }

    /**
     * Создает мэнеджер и путь к файлу
     */
    public ApplicationStateManager(){
        String home = System.getProperty("user.home");
        String surnameDir = "yurovitskiy";
        Path homePath = Path.of(home);
        Path dirPath = homePath.resolve(surnameDir);
        try {
            Files.createDirectories(dirPath);
        } catch (IOException e) {
            Logger.error("Не удалось создать директорию для конфигурации: " + e.getMessage());
        }
        configFile = dirPath.resolve("state.cfg").toFile();
    }

    /**
     *Регистрируем компоненты
     */
    public void register(String prefix, Stateful component){
        components.add(new RegisteredComponent(prefix,component));
    }

    /**
     * Сохраняет состояние всех зарегестрированных компонентов в файл
     */
    public void save(){
        Map<String,String> stateMap = new HashMap<>();
        for(RegisteredComponent rc : components){
            StateStorage storage = new StateStorage(stateMap,rc.prefix);
            rc.component.saveState(storage);
        }
        saveToFile(stateMap);
    }

    /**
     * Восстанавливает состояние всех зарегестрированных компонентов
     */
    public void restore(){
        Map<String,String> stateMap = loadFromFile();
        for(RegisteredComponent rc:components){
            StateStorage storage = new StateStorage(stateMap,rc.prefix);
            rc.component.restoreState(storage);
        }
    }

    /**
     * Сохраняет данные в файл
     */
    private void saveToFile(Map<String, String> map)
    {
        try (PrintWriter writer = new PrintWriter(new FileWriter(configFile)))
        {
            for (Map.Entry<String, String> e : map.entrySet())
                writer.println(e.getKey() + "=" + e.getValue());
        }
        catch (IOException e)
        {
            Logger.error("Не удалось сохранить состояние: " + e.getMessage());
        }
    }

    /**
     * Востанавливает данные из файла
     */
    private Map<String,String> loadFromFile(){
        Map<String,String> map = new HashMap<>();
        if(!configFile.exists()) return map;

        try(BufferedReader reader = new BufferedReader(new FileReader(configFile))){
            String line;
            while ((line = reader.readLine()) !=null){
                line=line.trim();
                if (line.isEmpty()) continue;
                int eq = line.indexOf('=');
                if (eq>0){
                    String key = line.substring(0,eq).trim();
                    String value = line.substring(eq+1).trim();
                    map.put(key,value);
                }
            }
        } catch (IOException e) {
            Logger.error("Не удалось загрузить состояние: " + e.getMessage());
        }
        return map;
    }
}
