package br.gov.acrimat.utils;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

@Named
@RequestScoped
public class MethodMapUtils implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	public Map<String, Method> createSetMethodMap(final Class<?> clazz) {
        final Map<String, Method> methodMap = new HashMap<>();
        getAllMethods(clazz)
                .filter(MethodMapUtils::isSetterMethod)
                .forEach(method -> methodMap.put(getAttributeName(method.getName()), method));
        return methodMap;
    }

	private static Stream<Method> getAllMethods(final Class<?> clazz) {
        return Optional.ofNullable(clazz)
                .map(Class::getMethods)
                .map(Arrays::stream)
                .orElseGet(Stream::empty);
    }

    private static boolean isSetterMethod(final Method method) {
        final String methodName = method.getName();
        return methodName.startsWith("set") && method.getParameterCount() == 1 && void.class.equals(method.getReturnType());
    }

    private static String getAttributeName(final String methodName) {
        return Character.toLowerCase(methodName.charAt(3)) + methodName.substring(4);
    }

}
