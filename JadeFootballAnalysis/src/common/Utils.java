package common;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import jade.wrapper.AgentContainer;
import jade.wrapper.ControllerException;
import org.jinq.orm.stream.JinqStream;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.util.List;

public class Utils
{
    public static <T> ArrayList toList(T[] arr)
    {
        return new ArrayList<>(Arrays.asList(arr));
    }

    public static ArrayList<String> toList(String[] arr)
    {
        return new ArrayList<>(Arrays.asList(arr));
    }

    public static JinqStream<Integer> jinqStreamRange(Integer firstNumber, Integer numberOfElements)
    {
        List<Integer> ints = new ArrayList<>();
        for (int i = firstNumber; i < firstNumber + numberOfElements; i++)
            ints.add(i);
        return JinqStream.from(ints);
    }

    public static Integer[] toIntArray(List<Integer> list)
    {
        return list.toArray(new Integer[0]);
    }

    public static <T> JinqStream arrToJinqStream(T[] arr)
    {
        return JinqStream.from(new ArrayList<>(Arrays.asList(arr)));
    }

    public static JinqStream<String[]> arr2DToJinqStream(String[][] arr)
    {
        return JinqStream.from(new ArrayList<>(Arrays.asList(arr)));
    }

    public static <T> JinqStream<T[]> arr2DToJinqStream(T[][] arr)
    {
        return JinqStream.from(new ArrayList<>(Arrays.asList(arr)));
    }

    public static JinqStream<Component> arrToJinqStream(Component[] arr)
    {
        return JinqStream.from(new ArrayList<>(Arrays.asList(arr)));
    }

    public static JinqStream<String> arrToJinqStream(String[] arr)
    {
        return JinqStream.from(new ArrayList<>(Arrays.asList(arr)));
    }

    public static JinqStream<Integer> arrToJinqStream(Integer[] arr)
    {
        return JinqStream.from(new ArrayList<>(Arrays.asList(arr)));
    }

    public static JinqStream<Map<String, String>> toJinqStream(List<Map<String, String>> lhmap)
    {
        return JinqStream.from(new ArrayList<>(lhmap));
    }

    public static JinqStream<Map<String, Object>> toJinqStreamLso(List<Map<String, Object>> lhmap)
    {
        return JinqStream.from(new ArrayList<>(lhmap));
    }

    public static String[][] toJagged(List<List<String>> list)
    {
        return list.stream()
            .map(l -> l.toArray(new String[0]))
            .toArray(String[][]::new);
    }

    public static <T> List<List<T>> to2DList(T[][] arr)
    {
        List<List<T>> list = new ArrayList<>();
        for (T[] subArr : arr)
        {
            List<T> subList = new ArrayList<>();
            Collections.addAll(subList, subArr);
            list.add(subList);
        }
        return list;
    }

    public static List<List<String>> to2DList(String[][] arr)
    {
        List<List<String>> list = new ArrayList<>();
        for (String[] subArr : arr)
        {
            List<String> subList = new ArrayList<>();
            Collections.addAll(subList, subArr);
            list.add(subList);
        }
        return list;
    }

    public static boolean isInt(String str)
    {
        try
        {
            Integer.parseInt(str);
        }
        catch (NumberFormatException nfe)
        {
            return false;
        }
        return true;
    }

    public static Integer toInt(Object o)
    {
        return Utils.toInt(Utils.round(Double.parseDouble(o.toString())));
    }

    public static Integer toInt(Double d)
    {
        return Integer.parseInt(Integer.valueOf(Utils.round(d).intValue()).toString());
    }

    public static Double round(Double d, Integer decimals)
    {
        Double pow = Math.pow(10, decimals);
        return (double)Math.round(d * pow) / pow;
    }

    public static Double round(Double d)
    {
        return round(d, 0);
    }

    /**
     * Scans all classes accessible from the context class loader which belong to the given package and subpackages.
     *
     * @param packageName The base package
     * @return The classes
     * @throws ClassNotFoundException
     * @throws IOException
     */
    public static Class[] getClasses(String packageName) throws ClassNotFoundException, IOException, URISyntaxException
    {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        assert classLoader != null;
        String path = packageName.replace('.', '/');
        Enumeration<URL> resources = classLoader.getResources(path);
        List<File> dirs = new ArrayList<>();
        while (resources.hasMoreElements())
        {
            URL resource = resources.nextElement();
            dirs.add(new File(resource.toURI()));
        }
        ArrayList<Class> classes = new ArrayList<>();
        for (File directory : dirs)
        {
            classes.addAll(findClasses(directory, packageName));
        }
        return classes.toArray(new Class[classes.size()]);
    }

    /**
     * Recursive method used to find all classes in a given directory and subdirs.
     *
     * @param directory   The base directory
     * @param packageName The package name for classes found inside the base directory
     * @return The classes
     * @throws ClassNotFoundException
     */
    public static List<Class> findClasses(File directory, String packageName) throws ClassNotFoundException
    {
        List<Class> classes = new ArrayList<>();
        if (!directory.exists())
        {
            return classes;
        }
        File[] files = directory.listFiles();
        for (File file : files)
        {
            if (file.isDirectory())
            {
                assert !file.getName().contains(".");
                classes.addAll(findClasses(file, packageName + "." + file.getName()));
            }
            else if (file.getName().endsWith(".class"))
            {
                classes.add(Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6)));
            }
        }
        return classes;
    }

    public static void sizeToContent(JFrame jFrame) // content pane musi być ustawiony
    {
        jFrame.pack();
        Container contentPane = jFrame.getContentPane();
        Integer leftCornerX = Utils.toInt(Utils.arrToJinqStream(contentPane.getComponents()).sortedBy(c -> c.getBounds().getMinX()).toList().get(0).getBounds().getMinX());
        Integer leftCornerY = Utils.toInt(Utils.arrToJinqStream(contentPane.getComponents()).sortedBy(c -> c.getBounds().getMinY()).toList().get(0).getBounds().getMinY());
        Integer rightCornerX = Utils.toInt(Utils.arrToJinqStream(contentPane.getComponents()).sortedDescendingBy(c -> c.getBounds().getMaxX()).toList().get(0).getBounds().getMaxX());
        Integer rightCornerY = Utils.toInt(Utils.arrToJinqStream(contentPane.getComponents()).sortedDescendingBy(c -> c.getBounds().getMaxY()).toList().get(0).getBounds().getMaxY());

        Dimension size = new Dimension(rightCornerX + leftCornerX, rightCornerY + leftCornerY);
        contentPane.setPreferredSize(size);
        contentPane.setSize(size);

        Insets insets = jFrame.getInsets();
        Dimension jframeSize = new Dimension(rightCornerX + leftCornerX + insets.left + insets.right, rightCornerY + leftCornerY + insets.top + insets.bottom);
        jFrame.setPreferredSize(jframeSize);
        jFrame.setSize(jframeSize);
    }

    public static boolean containsAny(String str, String[] strs)
    {
        for (String s : strs)
            if (str.contains(s))
                return true;
        return false;
    }

    public static boolean containsAgent(AgentContainer ac, String agentName)
    {
        try
        {
            ac.getAgent(agentName);
        }
        catch (ControllerException ignored)
        {
            return false;
        }

        return true;
    }

    private static JsonParser parser = new JsonParser();

    public static Object jsonParse(String json)
    {
        if (!Utils.containsAny(json, new String[] { "{", "[" }))
            return json.replaceAll("\"", "");
        JsonElement jEl = parser.parse(json);

        if (jEl.isJsonObject())
        {
            JsonObject jObj = jEl.getAsJsonObject();
            Set<Map.Entry<String, JsonElement>> keyVals = jObj.entrySet();
            HashMap<String, Object> hMap = new HashMap<>();

            for (Map.Entry<String, JsonElement> kvp : keyVals)
            {
                String key = kvp.getKey();
                JsonElement value = kvp.getValue();
                hMap.put(key, jsonParse(value.toString()));
            }

            return hMap;
        }
        else if (jEl.isJsonArray())
        {
            JsonArray jArr = jEl.getAsJsonArray();
            ArrayList<Object> arr = new ArrayList<>();
            for (JsonElement el : jArr)
                arr.add(jsonParse(el.toString()));

            return arr;
        }
        return null;
    }

    public static void enableControls(Component[] components)
    {
        for (Component c : components)
            c.setEnabled(true);
    }

    public static void disableControls(Component[] components)
    {
        for (Component c : components)
            c.setEnabled(false);
    }

    public static void enableControls(Container container, String[] controlNames)
    {
        Utils.arrToJinqStream(container.getComponents()).where(c -> Utils.containsAny(c.getName(), controlNames))
            .forEach(c -> c.setEnabled(true));
    }

    public static void disableControls(Container container, String[] controlNames)
    {
        Utils.arrToJinqStream(container.getComponents()).where(c -> Utils.containsAny(c.getName(), controlNames))
            .forEach(c -> c.setEnabled(false));
    }

    public static String[] split(final String str)
    {
        return split(str, null, -1);
    }

    public static String[] split(final String str, final char separatorChar)
    {
        return splitWorker(str, separatorChar, false);
    }

    public static String[] split(final String str, final String separatorChars)
    {
        return splitWorker(str, separatorChars, -1, false);
    }

    public static String[] split(final String str, final String separatorChars, final int max)
    {
        return splitWorker(str, separatorChars, max, false);
    }

    private static String[] splitWorker(final String str, final char separatorChar, final boolean preserveAllTokens)
    {
        if (str == null)
        {
            return null;
        }
        final int len = str.length();
        if (len == 0)
        {
            return new String[0];
        }
        final List<String> list = new ArrayList<>();
        int i = 0, start = 0;
        boolean match = false;
        boolean lastMatch = false;
        while (i < len)
        {
            if (str.charAt(i) == separatorChar)
            {
                if (match || preserveAllTokens)
                {
                    list.add(str.substring(start, i));
                    match = false;
                    lastMatch = true;
                }
                start = ++i;
                continue;
            }
            lastMatch = false;
            match = true;
            i++;
        }
        if (match || preserveAllTokens && lastMatch)
        {
            list.add(str.substring(start, i));
        }
        return list.toArray(new String[list.size()]);
    }

    private static String[] splitWorker(final String str, final String separatorChars, final int max, final boolean preserveAllTokens)
    {
        if (str == null)
        {
            return null;
        }
        final int len = str.length();
        if (len == 0)
        {
            return new String[0];
        }
        final List<String> list = new ArrayList<>();
        int sizePlus1 = 1;
        int i = 0, start = 0;
        boolean match = false;
        boolean lastMatch = false;
        if (separatorChars == null)
        {
            // Null separator means use whitespace
            while (i < len)
            {
                if (Character.isWhitespace(str.charAt(i)))
                {
                    if (match || preserveAllTokens)
                    {
                        lastMatch = true;
                        if (sizePlus1++ == max)
                        {
                            i = len;
                            lastMatch = false;
                        }
                        list.add(str.substring(start, i));
                        match = false;
                    }
                    start = ++i;
                    continue;
                }
                lastMatch = false;
                match = true;
                i++;
            }
        }
        else if (separatorChars.length() == 1)
        {
            // Optimise 1 character case
            final char sep = separatorChars.charAt(0);
            while (i < len)
            {
                if (str.charAt(i) == sep)
                {
                    if (match || preserveAllTokens)
                    {
                        lastMatch = true;
                        if (sizePlus1++ == max)
                        {
                            i = len;
                            lastMatch = false;
                        }
                        list.add(str.substring(start, i));
                        match = false;
                    }
                    start = ++i;
                    continue;
                }
                lastMatch = false;
                match = true;
                i++;
            }
        }
        else
        {
            // standard case
            while (i < len)
            {
                if (separatorChars.indexOf(str.charAt(i)) >= 0)
                {
                    if (match || preserveAllTokens)
                    {
                        lastMatch = true;
                        if (sizePlus1++ == max)
                        {
                            i = len;
                            lastMatch = false;
                        }
                        list.add(str.substring(start, i));
                        match = false;
                    }
                    start = ++i;
                    continue;
                }
                lastMatch = false;
                match = true;
                i++;
            }
        }
        if (match || preserveAllTokens && lastMatch)
        {
            list.add(str.substring(start, i));
        }
        return list.toArray(new String[list.size()]);
    }

    public static Integer sum(Integer[] intArr)
    {
        Integer sum = 0;
        for (Integer i : intArr)
            sum += i;
        return sum;
    }
}
