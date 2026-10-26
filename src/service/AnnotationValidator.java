package service;

import model.annotation.MaxLength;
import model.annotation.Positive;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class AnnotationValidator {

    public static String[] validate(Object obj) {

        List<String> errors = new ArrayList<>();

        Class<?> currentClass = obj.getClass();

        while (currentClass != null) {

            Field[] fields = currentClass.getDeclaredFields();

            for (Field field : fields) {

                field.setAccessible(true);

                try {

                    if (field.isAnnotationPresent(Positive.class)) {

                        long value =
                                ((Number) field.get(obj)).longValue();

                        Positive annotation =
                                field.getAnnotation(Positive.class);

                        if (value <= 0) {
                            errors.add(
                                    field.getName() + ": "
                                            + annotation.message()
                            );
                        }
                    }

                    if (field.isAnnotationPresent(MaxLength.class)) {

                        String value =
                                (String) field.get(obj);

                        MaxLength annotation =
                                field.getAnnotation(MaxLength.class);

                        if (value != null &&
                                value.length() > annotation.value()) {

                            errors.add(
                                    field.getName()
                                            + ": maximum length is "
                                            + annotation.value()
                            );
                        }
                    }

                } catch (IllegalAccessException e) {

                    errors.add(
                            "Cannot access field: "
                                    + field.getName()
                    );
                }
            }

            currentClass = currentClass.getSuperclass();
        }

        return errors.toArray(new String[0]);
    }
}