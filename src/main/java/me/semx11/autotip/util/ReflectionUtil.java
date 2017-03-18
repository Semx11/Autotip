package me.semx11.autotip.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for Reflection.
 *
 * @author Semx11
 */
public class ReflectionUtil {

    private static Map<String, Class<?>> loadedClasses = new HashMap<>();
    private static Map<Class<?>, Map<Class<?>[], Constructor<?>>> loadedConstructors = new HashMap<>();
    private static Map<Class<?>, Map<String, Method>> loadedMethods = new HashMap<>();
    private static Map<Class<?>, Map<String, Field>> loadedFields = new HashMap<>();
    private static Map<Class<?>, Map<String, Enum<?>>> loadedEnums = new HashMap<>();

    public static Class<?> getClazz(String className) {
        if (loadedClasses.containsKey(className)) {
            return loadedClasses.get(className);
        }

        try {
            Class clazz = Class.forName(className);
            loadedClasses.put(className, clazz);
            return clazz;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Constructor<?> getConstructor(Class<?> clazz, Class<?>... params) {
        if (!loadedConstructors.containsKey(clazz)) {
            loadedConstructors.put(clazz, new HashMap<>());
        }

        Map<Class<?>[], Constructor<?>> clazzConstructors = loadedConstructors.get(clazz);

        if (clazzConstructors.containsKey(params)) {
            clazzConstructors.get(params);
        }

        Constructor<?> constructor = null;
        try {
            constructor = clazz.getConstructor(params);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        clazzConstructors.put(params, constructor);
        loadedConstructors.put(clazz, clazzConstructors);
        return constructor;
    }

    public static Method getMethod(Class<?> clazz, String methodName, Class<?>... params) {
        if (!loadedMethods.containsKey(clazz)) {
            loadedMethods.put(clazz, new HashMap<>());
        }

        Map<String, Method> clazzMethods = loadedMethods.get(clazz);

        if (clazzMethods.containsKey(methodName)) {
            return clazzMethods.get(methodName);
        }

        Method method = null;
        try {
            method = clazz.getMethod(methodName, params);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        clazzMethods.put(methodName, method);
        loadedMethods.put(clazz, clazzMethods);
        return method;
    }

    public static Field getField(Class<?> clazz, String fieldName) {
        if (!loadedFields.containsKey(clazz)) {
            loadedFields.put(clazz, new HashMap<>());
        }

        Map<String, Field> clazzFields = loadedFields.get(clazz);

        if (clazzFields.containsKey(fieldName)) {
            return clazzFields.get(fieldName);
        }

        Field field = null;
        try {
            field = clazz.getField(fieldName);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

        clazzFields.put(fieldName, field);
        loadedFields.put(clazz, clazzFields);
        return field;
    }

    public static Enum<?> getEnum(Class<?> clazz, String enumName) {
        if (!loadedEnums.containsKey(clazz)) {
            loadedEnums.put(clazz, new HashMap<>());
        }

        Map<String, Enum<?>> clazzEnums = loadedEnums.get(clazz);

        if (clazzEnums.containsKey(enumName.toUpperCase())) {
            return clazzEnums.get(enumName.toUpperCase());
        }

        if (clazz.getEnumConstants().length == 0) {
            return null;
        }

        Enum<?> theEnum = null;
        for (Object o : clazz.getEnumConstants()) {
            Enum<?> anEnum = (Enum<?>) o;
            clazzEnums.put(anEnum.name(), anEnum);
            if (anEnum.name().equalsIgnoreCase(enumName)) {
                theEnum = anEnum;
            }
        }
        loadedEnums.put(clazz, clazzEnums);
        return theEnum;
    }
}
